import asyncio
import json
import logging
import re
from typing import List

from . import schemas
from .openrouter_client import get_openrouter_client


# === Custom Error Classes ===


class MissingAPIKeyError(RuntimeError):
    """Raised when the OPENROUTER_API_KEY is not configured."""


class NetworkError(RuntimeError):
    """Raised when a network error occurs while communicating with OpenRouter."""


class InvalidResponseError(RuntimeError):
    """Raised when OpenRouter returns an invalid or unexpected response."""


# === Helper Functions ===


def extract_json_from_markdown(text: str) -> str:
    """Return JSON payload from an OpenRouter response.

    The helper first looks for a fenced code block of the form `````json```, but
    falls back to returning the entire string when the block is not found.  This
    mirrors the behaviour expected in the unit tests where the API may respond
    with raw JSON without markdown formatting.

    Args:
        text: Raw response text from OpenRouter.

    Returns:
        The JSON portion of the response without any markdown syntax.

    Raises:
        InvalidResponseError: If no JSON payload could be extracted.
    """
    match = re.search(r"```json\s*(.*?)\s*```", text, re.DOTALL)
    if match:
        return match.group(1).strip()

    # If the response is plain JSON, return it as-is so the caller can attempt
    # to parse it.
    stripped = text.strip()
    if stripped.startswith("[") or stripped.startswith("{"):
        return stripped

    raise InvalidResponseError("Tidak ditemukan blok JSON dalam respons OpenRouter.")


# === OpenRouter Functionalities ===


async def caption_image_with_openrouter(image_url: str) -> str:
    """
    Generates a caption for an image using the OpenRouter API.

    Args:
        image_url (str): The URL of the image to describe.

    Returns:
        str: A textual description of the image.

    Raises:
        MissingAPIKeyError: If API key is not set.
        RuntimeError: If OpenRouter API fails to return valid result.
    """
    try:
        client = get_openrouter_client()
    except RuntimeError as e:
        raise MissingAPIKeyError(str(e)) from e

    payload = {
        "model": "deepseek/deepseek-chat-v3-0324:free",
        "messages": [
            {
                "role": "user",
                "content": [
                    {"type": "text", "text": "What is in this image?"},
                    {"type": "image_url", "image_url": {"url": image_url}},
                ],
            }
        ],
    }

    try:
        data = await asyncio.to_thread(client.chat.completions.create, **payload)
        return data.choices[0].message.content
    except Exception as e:
        raise RuntimeError(f"Error from OpenRouter API: {e}") from e


def generate_articles_with_openrouter(text: str) -> List[schemas.ArticleResponse]:
    """
    Requests the OpenRouter API to generate three article titles and summaries in JSON format.

    Args:
        text (str): The user input to base article ideas on.

    Returns:
        List[schemas.ArticleResponse]: Parsed list of article suggestions.

    Raises:
        MissingAPIKeyError: If API key is missing.
        InvalidResponseError: If response is malformed or not JSON.
    """
    try:
        client = get_openrouter_client()
    except RuntimeError as e:
        raise MissingAPIKeyError(str(e)) from e

    payload = {
        "model": "deepseek/deepseek-chat-v3-0324:free",
        "messages": [
            {
                "role": "user",
                "content": (
                    "Buat tiga judul artikel beserta ringkasan singkat dalam format JSON "
                    "[{'title': 'Judul', 'summary': 'Ringkasan'}] tanpa tambahan penjelasan. "
                    "Balas hanya dengan JSON.\n" + text
                ),
            }
        ],
    }

    try:
        data = client.chat.completions.create(**payload)
        text_resp = data.choices[0].message.content

        try:
            json_str = extract_json_from_markdown(text_resp)
            articles = json.loads(json_str)
        except Exception as e:
            logging.error("[OpenRouter JSON Parsing Error] Raw response: %s", text_resp)
            raise InvalidResponseError(
                "Invalid JSON format in OpenRouter response."
            ) from e

        return [schemas.ArticleResponse(**a) for a in articles]

    except Exception as e:
        raise InvalidResponseError(f"Malformed response from OpenRouter: {e}") from e


def chat_with_openrouter(text: str, history: str | None = None, mood: str | None = None) -> str:
    """Interact with OpenRouter to craft a follow-up question for the user.

    The helper performs two requests:
    1. Ask OpenRouter to analyze the user's message (optionally considering
       ``history`` and ``mood``) and respond with a JSON payload containing
       ``issue``, ``technique`` and ``tone``.
    2. Use those values to request a final textual reply.

    Parameters
    ----------
    text: str
        The latest user message.
    history: str | None
        Previous chat history sent by the client.
    mood: str | None
        Current user mood if provided.

    Returns
    -------
    str
        The final response text from OpenRouter.

    Raises
    ------
    MissingAPIKeyError
        If the API key is not configured.
    InvalidResponseError
        If OpenRouter returns malformed or unexpected data.
    """

    try:
        client = get_openrouter_client()
    except RuntimeError as e:
        raise MissingAPIKeyError(str(e)) from e

    base_prompt = (
        "Identifikasi masalah utama pengguna dan sarankan teknik coping dalam"
        " format JSON seperti {'issue': '', 'technique': '', 'tone': ''}. "
        "Balas hanya dengan JSON."
    )

    user_text = text
    if history:
        user_text += f"\nRiwayat: {history}"
    if mood:
        user_text += f"\nMood: {mood}"

    payload_first = {
        "model": "deepseek/deepseek-chat-v3-0324:free",
        "messages": [
            {"role": "user", "content": base_prompt + "\n" + user_text}
        ],
    }

    try:
        first = client.chat.completions.create(**payload_first)
        raw = first.choices[0].message.content
        json_str = extract_json_from_markdown(raw)
        info = json.loads(json_str)
        issue = info.get("issue")
        technique = info.get("technique")
        tone = info.get("tone")
        if not all(isinstance(v, str) for v in (issue, technique, tone)):
            raise InvalidResponseError("Missing keys in OpenRouter response")
    except InvalidResponseError:
        raise
    except Exception as e:
        logging.error("[OpenRouter JSON Parsing Error] Raw response: %s", raw)
        raise InvalidResponseError(f"Malformed response from OpenRouter: {e}") from e

    second_prompt = (
        f"Dengan nada {tone}, buat pertanyaan singkat mengenai {issue} "
        f"dan anjurkan {technique}."
    )

    payload_second = {
        "model": "deepseek/deepseek-chat-v3-0324:free",
        "messages": [{"role": "user", "content": second_prompt}],
    }

    try:
        second = client.chat.completions.create(**payload_second)
        return second.choices[0].message.content
    except Exception as e:
        raise InvalidResponseError(f"Malformed response from OpenRouter: {e}") from e
