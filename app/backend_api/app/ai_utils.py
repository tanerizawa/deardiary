import os
import json
import requests
from typing import List

from . import schemas


def analyze_with_gemini(text: str) -> str:
    api_key = os.getenv("GEMINI_API_KEY")
    if not api_key:
        raise RuntimeError("Missing GEMINI_API_KEY")
    url = (
        "https://generativelanguage.googleapis.com/v1beta/models/"
        f"gemini-pro:generateContent?key={api_key}"
    )
    payload = {
        "contents": [
            {
                "parts": [
                    {
                        "text": (
                            "Analisis mood untuk teks berikut secara singkat. "
                            "Balas hanya dengan kata Positif, Negatif, atau Netral.\n"
                            + text
                        )
                    }
                ]
            }
        ]
    }
    try:
        response = requests.post(url, json=payload, timeout=10)
        response.raise_for_status()
        data = response.json()
        result = data["candidates"][0]["content"]["parts"][0]["text"].lower()
        if "positif" in result:
            return "Mood terdeteksi positif"
        if "negatif" in result:
            return "Mood terdeteksi negatif"
        return "Mood netral"
    except Exception as e:
        raise RuntimeError(f"Error from Gemini API: {e}")


def caption_image_with_openrouter(image_url: str) -> str:
    """Return a text description of the image using OpenRouter."""

    api_key = os.getenv("OPENROUTER_API_KEY")
    if not api_key:
        raise RuntimeError("Missing OPENROUTER_API_KEY")

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
        response = requests.post(
            "https://openrouter.ai/api/v1/chat/completions",
            headers=headers,
            json=payload,
            timeout=10,
        )
        response.raise_for_status()
        data = response.json()
        return data["choices"][0]["message"]["content"]
    except Exception as e:
        raise RuntimeError(f"Error from OpenRouter API: {e}")


def generate_articles_with_gemini(text: str) -> List[schemas.GeminiArticleResponse]:
    """Request Gemini API to create article titles and summaries."""

    api_key = os.getenv("GEMINI_API_KEY")
    if not api_key:
        raise RuntimeError("Missing GEMINI_API_KEY")
    url = (
        "https://generativelanguage.googleapis.com/v1beta/models/"
        f"gemini-pro:generateContent?key={api_key}"
    )
    payload = {
        "contents": [
            {
                "parts": [
                    {
                        "text": (
                            "Buat tiga judul artikel beserta ringkasan singkat dalam format JSON "
                            "[{'title': 'Judul', 'summary': 'Ringkasan'}] berdasarkan teks berikut.\n"
                            + text
                        )
                    }
                ]
            }
        ]
    }
    try:
        response = requests.post(url, json=payload, timeout=10)
        response.raise_for_status()
        data = response.json()
        text_resp = data["candidates"][0]["content"]["parts"][0]["text"]
        articles = json.loads(text_resp)
        return [schemas.GeminiArticleResponse(**a) for a in articles]
    except Exception as e:
        raise RuntimeError(f"Error from Gemini API: {e}")

