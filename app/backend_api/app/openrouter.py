from .openrouter_client import get_openrouter_client
import json

def analyze_text(text: str) -> str:
    """Analyze text sentiment using OpenRouter."""

    client = get_openrouter_client()

    payload = {
        "model": "deepseek/deepseek-chat-v3-0324:free", # Model Anda
        "messages": [
            {
                "role": "user",
                "content": f"Analyze the sentiment of the following text and respond with a short sentence. Text: {text}",
            }
        ],
    }

    try:
        data = client.chat.completions.create(**payload)
        raw_response_content = data.choices[0].message.content

        # Coba dulu mengurai sebagai JSON (untuk format daftar artikel)
        try:
            parsed_data = json.loads(raw_response_content)

            if isinstance(parsed_data, list) and len(parsed_data) > 0:
                sentiment_result = parsed_data[0].get("summary", "Ringkasan tidak ditemukan.")
                return f"Analisis sentimen: {sentiment_result}"
            else:
                # Jika itu JSON tapi bukan format daftar yang diharapkan
                print(f"DEBUG: Data yang diurai adalah JSON tapi bukan daftar artikel yang diharapkan: {parsed_data}")
                # Untuk saat ini, kita kembalikan konten mentahnya saja, asumsikan itu mungkin teks sentimen
                return raw_response_content
        except json.JSONDecodeError:
            # Jika parsing JSON gagal, berarti ini adalah teks biasa.
            # INILAH TEMPAT DI MANA KITA SEKARANG LANGSUNG MENGEMBALIKAN SENTIMENNYA!
            print(f"DEBUG: Konten respons bukan JSON, akan dianggap sebagai teks biasa: '{raw_response_content}'")
            return raw_response_content # <-- Ini akan mengembalikan "The sentiment is negative."

    except Exception as e:
        # Menangkap error lain dari OpenRouter API atau Python
        print(f"ERROR: Kesalahan API OpenRouter atau pemrosesan: {e}")
        # Kembalikan pesan error umum, agar klien tidak mendapatkan detail internal
        return "Gagal menganalisis sentimen: Terjadi kesalahan tidak terduga."