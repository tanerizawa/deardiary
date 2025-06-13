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
    """
    Extracts JSON content from a markdown-style block formatted with triple backticks.

    Args:
        text (str): The raw response text that may include JSON wrapped in markdown.

    Returns:
        str: Cleaned JSON string without markdown syntax.

    Raises:
        InvalidResponseError: If JSON block is not found in the expected format.
    """
    match = re.search(r"```json\s*(.*?)\s*```", text, re.DOTALL)
    if match:
        return match.group(1).strip()
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
            raise InvalidResponseError("Invalid JSON format in OpenRouter response.") from e

        return [schemas.ArticleResponse(**a) for a in articles]

    except Exception as e:
        raise InvalidResponseError(f"Malformed response from OpenRouter: {e}") from e
