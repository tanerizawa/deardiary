import os
from dotenv import load_dotenv
from openai import OpenAI

# Memuat variabel dari file .env ke dalam environment
load_dotenv()

def get_openrouter_client() -> OpenAI:
    """
    Mengambil API key dari environment, memeriksanya, dan menginisialisasi
    klien OpenRouter OpenAI.
    """
    api_key = os.getenv("OPENROUTER_API_KEY")

    # --- BLOK DEBUGGING ---
    # Kode berikut akan mencetak nilai API Key yang terbaca ke terminal Anda.
    # Ini membantu memastikan bahwa file .env Anda dimuat dengan benar.
    print("--- Memeriksa Kunci API OpenRouter ---")
    if api_key:
        print(f"DEBUG: Kunci API ditemukan. Panjang: {len(api_key)} karakter.")
        # Untuk keamanan, kita hanya akan menampilkan beberapa karakter pertama dan terakhir
        print(f"DEBUG: Kunci dimulai dengan '{api_key[:4]}' dan diakhiri dengan '{api_key[-4:]}'.")
    else:
        # Pesan ini akan muncul jika os.getenv() tidak menemukan variabelnya.
        print("DEBUG: KESALAHAN! Variabel OPENROUTER_API_KEY tidak ditemukan di environment.")
    print("------------------------------------")
    # --- AKHIR BLOK DEBUGGING ---

    # Logika ini tetap sama. Jika kunci tidak ada, program akan berhenti.
    # Ini adalah pengaman jika terjadi kesalahan.
    if not api_key:
        raise RuntimeError(
            "Kunci API 'OPENROUTER_API_KEY' tidak ada di environment. "
            "Pastikan file .env Anda benar dan berada di direktori yang tepat."
        )

    # Mengembalikan klien yang sudah diinisialisasi
    return OpenAI(
        base_url="https://openrouter.ai/api/v1",
        api_key=api_key
    )
