import os
import requests


def analyze_text(text: str) -> str:
    """Analyze text sentiment using OpenRouter."""

    api_key = os.getenv("OPENROUTER_API_KEY")
    if not api_key:
        raise RuntimeError("Missing OPENROUTER_API_KEY")

    payload = {
        "model": "google/gemini-2.0-flash-exp:free",
        "messages": [
            {
                "role": "user",
                "content": f"Analyze the sentiment of the following text and respond with a short sentence. Text: {text}",
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
