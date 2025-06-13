import os
import logging
from dotenv import load_dotenv
from openai import OpenAI

# Memuat variabel dari file .env ke dalam environment
load_dotenv()

logger = logging.getLogger(__name__)


def get_openrouter_client() -> OpenAI:
    """
    Mengambil API key dari environment, memeriksanya, dan menginisialisasi
    klien OpenRouter OpenAI.
    """
    api_key = os.getenv("OPENROUTER_API_KEY")

    if api_key:
        logger.debug(
            "OpenRouter API key loaded (length: %d, prefix: %s, suffix: %s)",
            len(api_key),
            api_key[:4],
            api_key[-4:],
        )
    else:
        logger.debug("OPENROUTER_API_KEY not found in environment")

    # Logika ini tetap sama. Jika kunci tidak ada, program akan berhenti.
    # Ini adalah pengaman jika terjadi kesalahan.
    if not api_key:
        raise RuntimeError(
            "Kunci API 'OPENROUTER_API_KEY' tidak ada di environment. "
            "Pastikan file .env Anda benar dan berada di direktori yang tepat."
        )

    # Mengembalikan klien yang sudah diinisialisasi
    return OpenAI(base_url="https://openrouter.ai/api/v1", api_key=api_key)
