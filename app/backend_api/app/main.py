# app/main.py: Aplikasi FastAPI dan endpoint-endpoint API
from fastapi import (
    FastAPI,
    Depends,
    HTTPException,
    status,
)  # Import status dan HTTPException
import os
import requests
from sqlalchemy.orm import Session
from typing import List, Dict  # Untuk tipe hint List dan Dict

from . import crud  # Import modul CRUD lokal
from . import models, schemas
from .database import engine, get_db

# Membuat objek FastAPI
app = FastAPI(
    title="Diary Depresiku API",  # Judul aplikasi di Swagger UI
    description="API untuk menyimpan dan mengelola entri diary, serta fitur analisis mood.",
    version="0.1.0",
)

# Membuat semua tabel di database (jika belum ada)
# Ini harus dipanggil sekali saat aplikasi startup
models.Base.metadata.create_all(bind=engine)


def analyze_with_gemini(text: str) -> str:
    """Send text to Gemini API and return mood description."""
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
    response = requests.post(url, json=payload, timeout=10)
    response.raise_for_status()
    data = response.json()
    result = data["candidates"][0]["content"]["parts"][0]["text"].lower()
    if "positif" in result:
        return "Mood terdeteksi positif"
    if "negatif" in result:
        return "Mood terdeteksi negatif"
    return "Mood netral"


@app.post("/register/", status_code=status.HTTP_201_CREATED)
async def register_user(user: schemas.UserCreate, db: Session = Depends(get_db)):
    if crud.get_user_by_email(db, user.email):
        raise HTTPException(status_code=400, detail="Email already registered")
    crud.create_user(db, user)
    return {"message": "User created"}


@app.post("/login/", response_model=schemas.Token)
async def login_user(user: schemas.UserLogin, db: Session = Depends(get_db)):
    if not crud.authenticate_user(db, user.email, user.password):
        raise HTTPException(status_code=400, detail="Invalid credentials")
    return {"token": "dummy"}


# Endpoint untuk membuat entri diary baru
@app.post(
    "/entries/",  # Menggunakan trailing slash agar konsisten dengan Android
    response_model=schemas.DiaryEntryResponse,  # Menggunakan schema respons yang benar
    status_code=status.HTTP_201_CREATED,  # Menggunakan kode status 201 Created
    summary="Membuat entri diary baru",
    description="Menyimpan entri diary baru ke database, termasuk isi, mood, dan timestamp.",
)
async def create_diary_entry_endpoint(  # Nama fungsi yang lebih deskriptif
    entry: schemas.DiaryEntryCreate,  # Schema Pydantic untuk request body
    db: Session = Depends(get_db),  # Dependency injection untuk sesi database
):
    # Memanggil fungsi CRUD untuk membuat entri di database
    # Menggunakan properti 'content' dan 'timestamp' sesuai dengan schemas dan models
    db_entry = crud.create_diary_entry(db=db, entry=entry)
    return schemas.DiaryEntryResponse.model_validate(db_entry)


# Endpoint untuk mendapatkan semua entri diary
@app.get(
    "/entries/",
    response_model=List[
        schemas.DiaryEntryResponse
    ],  # Mengembalikan daftar DiaryEntryResponse
    summary="Mendapatkan semua entri diary",
    description="Mengambil daftar semua entri diary yang tersimpan, diurutkan berdasarkan timestamp terbaru.",
)
async def read_diary_entries_endpoint(
    skip: int = 0,  # Parameter query untuk pagination
    limit: int = 100,  # Parameter query untuk pagination
    db: Session = Depends(get_db),
):
    entries = crud.get_diary_entries(db=db, skip=skip, limit=limit)
    return [schemas.DiaryEntryResponse.model_validate(e) for e in entries]


# Endpoint untuk mendapatkan entri diary berdasarkan ID
@app.get(
    "/entries/{entry_id}",  # Endpoint dengan path parameter
    response_model=schemas.DiaryEntryResponse,
    summary="Mendapatkan entri diary berdasarkan ID",
    description="Mengambil satu entri diary berdasarkan ID uniknya.",
)
async def read_diary_entry_by_id_endpoint(
    entry_id: int, db: Session = Depends(get_db)  # Path parameter
):
    db_entry = crud.get_diary_entry(db=db, entry_id=entry_id)
    if db_entry is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="Entry not found"
        )
    return schemas.DiaryEntryResponse.model_validate(db_entry)


# Endpoint untuk analisis AI terhadap teks diary
@app.post(
    "/analyze",
    response_model=schemas.AnalyzeResponse,
    summary="Menganalisis teks diary",
    description="Melakukan analisis sederhana terhadap teks diary untuk mendeteksi mood.",
)
def analyze_entry_endpoint(  # Nama fungsi yang lebih deskriptif
    request: schemas.AnalyzeRequest,
):
    try:
        analysis_result = analyze_with_gemini(request.text)
    except Exception:
        raise HTTPException(status_code=500, detail="Failed to analyze text")

    return {"analysis": analysis_result}


@app.get(
    "/stats/",
    response_model=schemas.MoodStatsResponse,
    summary="Statistik mood",
    description="Menghitung jumlah entri untuk tiap mood.",
)
async def read_mood_stats_endpoint(db: Session = Depends(get_db)):
    stats = crud.get_mood_stats(db)
    return {"stats": stats}
