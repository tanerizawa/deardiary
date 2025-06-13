import os
import json
import logging
import requests
import httpx
from typing import List


class MissingAPIKeyError(RuntimeError):
    """Raised when the OPENROUTER_API_KEY is not configured."""


class NetworkError(RuntimeError):
    """Raised when a network error occurs talking to OpenRouter."""


class InvalidResponseError(RuntimeError):
    """Raised when OpenRouter returns an invalid JSON payload."""

from . import schemas


async def caption_image_with_openrouter(image_url: str) -> str:
    """Return a text description of the image using OpenRouter."""

    api_key = os.getenv("OPENROUTER_API_KEY")
    if not api_key:
        raise MissingAPIKeyError("Missing OPENROUTER_API_KEY")

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

    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json",
    }

    try:
        async with httpx.AsyncClient(timeout=10) as client:
            response = await client.post(
                "https://openrouter.ai/api/v1/chat/completions",
                headers=headers,
                json=payload,
            )
        response.raise_for_status()
        data = response.json()
        return data["choices"][0]["message"]["content"]
    except Exception as e:
        raise RuntimeError(f"Error from OpenRouter API: {e}")


def generate_articles_with_openrouter(text: str) -> List[schemas.ArticleResponse]:
    """Request OpenRouter API to create article titles and summaries."""

    api_key = os.getenv("OPENROUTER_API_KEY")
    if not api_key:
        raise MissingAPIKeyError("Missing OPENROUTER_API_KEY")

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

    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json",
    }

    try:
        response = requests.post(
            "https://openrouter.ai/api/v1/chat/completions",
            headers=headers,
            json=payload,
            timeout=10,
        )
        response.raise_for_status()
    except requests.RequestException as e:
        raise NetworkError(f"Error communicating with OpenRouter: {e}") from e

    try:
        data = response.json()
        text_resp = data["choices"][0]["message"]["content"]
        try:
            articles = json.loads(text_resp)
        except json.JSONDecodeError as e:
            logging.error("OpenRouter raw response: %s", text_resp)
            raise InvalidResponseError(
                "Invalid JSON in OpenRouter response"
            ) from e
        return [schemas.ArticleResponse(**a) for a in articles]
    except Exception as e:
        raise InvalidResponseError(f"Malformed response from OpenRouter: {e}") from e

