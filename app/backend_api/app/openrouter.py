from .openrouter_client import get_openrouter_client
import json

def analyze_text(text: str) -> str:
    """Analyze text sentiment using OpenRouter."""

    client = get_openrouter_client()

    # Payload tetap sama, karena prompt Anda sudah benar dalam meminta kalimat sederhana.
    payload = {
        "model": "deepseek/deepseek-chat-v3-0324:free",
        "messages": [
            {
                "role": "user",
                "content": f"Analyze the sentiment of the following text and respond with a short sentence. Text: {text}",
            }
        ],
    }

    try:
        data = client.chat.completions.create(**payload)

        # Ekstrak konten respons teks dari model.
        # Ini akan berupa kalimat seperti "The sentiment is positive."
        response_text = data.choices[0].message.content

        # Langsung kembalikan respons teks dari model karena prompt meminta kalimat sederhana.
        # Tidak perlu mencoba parsing JSON yang rumit dan tidak perlu.
        return response_text

    except Exception as e:
        # Menangkap error lain dari OpenRouter API atau Python
        print(f"ERROR: Kesalahan API OpenRouter atau pemrosesan: {e}")
        # Kembalikan pesan error umum, agar klien tidak mendapatkan detail internal
        return "Gagal menganalisis sentimen: Terjadi kesalahan tidak terduga."

