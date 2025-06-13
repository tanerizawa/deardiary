from .openrouter_client import get_openrouter_client


def analyze_text(text: str) -> str:
    """Analyze text sentiment using OpenRouter."""

    client = get_openrouter_client()

    payload = {
        "model": "google/gemini-2.0-flash-exp:free",
        "messages": [
            {
                "role": "user",
                "content": f"Analyze the sentiment of the following text and respond with a short sentence. Text: {text}",
            }
        ],
    }

    try:
        data = client.chat.completions.create(**payload)
        return data.choices[0].message.content
    except Exception as e:
        raise RuntimeError(f"Error from OpenRouter API: {e}")
