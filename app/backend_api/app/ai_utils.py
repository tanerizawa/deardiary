import asyncio
import os
import json
import logging
from typing import List


class MissingAPIKeyError(RuntimeError):
    """Raised when the OPENROUTER_API_KEY is not configured."""


class NetworkError(RuntimeError):
    """Raised when a network error occurs talking to OpenRouter."""


class InvalidResponseError(RuntimeError):
    """Raised when OpenRouter returns an invalid JSON payload."""


from . import schemas
from .openrouter_client import get_openrouter_client


async def caption_image_with_openrouter(image_url: str) -> str:
    """Return a text description of the image using OpenRouter."""

    try:
        client = get_openrouter_client()
    except RuntimeError as e:
        raise MissingAPIKeyError(str(e)) from e

    payload = {
        "model": "google/gemini-2.0-flash-exp:free",
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
        data = await asyncio.to_thread(
            client.chat.completions.create,
            **payload,
        )
        return data.choices[0].message.content
    except Exception as e:
        raise RuntimeError(f"Error from OpenRouter API: {e}")


def generate_articles_with_openrouter(text: str) -> List[schemas.ArticleResponse]:
    """Request OpenRouter API to create article titles and summaries."""

    try:
        client = get_openrouter_client()
    except RuntimeError as e:
        raise MissingAPIKeyError(str(e)) from e

    payload = {
        "model": "google/gemini-2.0-flash-exp:free",
        "messages": [
            {
                "role": "user",
                "content": (
                    "Buat tiga judul artikel beserta ringkasan singkat dalam format JSON "
                    "[{'title': 'Judul', 'summary': 'Ringkasan'}] berdasarkan teks berikut.\n"
                    + text
                ),
            }
        ],
    }

    try:
        data = client.chat.completions.create(**payload)
        text_resp = data.choices[0].message.content
        try:
            articles = json.loads(text_resp)
        except json.JSONDecodeError as e:
            logging.error("OpenRouter raw response: %s", text_resp)
            raise InvalidResponseError("Invalid JSON in OpenRouter response") from e
        return [schemas.ArticleResponse(**a) for a in articles]
    except Exception as e:
        raise InvalidResponseError(f"Malformed response from OpenRouter: {e}") from e
