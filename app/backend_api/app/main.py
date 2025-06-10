# app/main.py: Aplikasi FastAPI dan endpoint-endpoint API
from fastapi import FastAPI, Depends, HTTPException, status # Import status dan HTTPException
from sqlalchemy.orm import Session
from typing import List # Untuk tipe hint List

from backend_api import crud # Tambahkan import crud
from . import models, schemas
from .database import engine, get_db

# Membuat objek FastAPI
app = FastAPI(
    title="Diary Depresiku API", # Judul aplikasi di Swagger UI
    description="API untuk menyimpan dan mengelola entri diary, serta fitur analisis mood.",
    version="0.1.0",
)

# Membuat semua tabel di database (jika belum ada)
# Ini harus dipanggil sekali saat aplikasi startup
models.Base.metadata.create_all(bind=engine)

# Endpoint untuk membuat entri diary baru
@app.post(
    "/entries/", # Menggunakan trailing slash agar konsisten dengan Android
    response_model=schemas.DiaryEntryResponse, # Menggunakan schema respons yang benar
    status_code=status.HTTP_201_CREATED, # Menggunakan kode status 201 Created
    summary="Membuat entri diary baru",
    description="Menyimpan entri diary baru ke database, termasuk isi, mood, dan timestamp."
)
async def create_diary_entry_endpoint( # Nama fungsi yang lebih deskriptif
        entry: schemas.DiaryEntryCreate, # Schema Pydantic untuk request body
        db: Session = Depends(get_db) # Dependency injection untuk sesi database
):
    # Memanggil fungsi CRUD untuk membuat entri di database
    # Menggunakan properti 'content' dan 'timestamp' sesuai dengan schemas dan models
    db_entry = crud.create_diary_entry(db=db, entry=entry)
    return db_entry # Akan otomatis dikonversi ke schema DiaryEntryResponse

# Endpoint untuk mendapatkan semua entri diary
@app.get(
    "/entries/",
    response_model=List[schemas.DiaryEntryResponse], # Mengembalikan daftar DiaryEntryResponse
    summary="Mendapatkan semua entri diary",
    description="Mengambil daftar semua entri diary yang tersimpan, diurutkan berdasarkan timestamp terbaru."
)
async def read_diary_entries_endpoint(
        skip: int = 0, # Parameter query untuk pagination
        limit: int = 100, # Parameter query untuk pagination
        db: Session = Depends(get_db)
):
    entries = crud.get_diary_entries(db=db, skip=skip, limit=limit)
    return entries

# Endpoint untuk mendapatkan entri diary berdasarkan ID
@app.get(
    "/entries/{entry_id}", # Endpoint dengan path parameter
    response_model=schemas.DiaryEntryResponse,
    summary="Mendapatkan entri diary berdasarkan ID",
    description="Mengambil satu entri diary berdasarkan ID uniknya."
)
async def read_diary_entry_by_id_endpoint(
        entry_id: int, # Path parameter
        db: Session = Depends(get_db)
):
    db_entry = crud.get_diary_entry(db=db, entry_id=entry_id)
    if db_entry is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Entry not found")
    return db_entry


# Endpoint untuk analisis AI terhadap teks diary
@app.post(
    "/analyze",
    response_model=schemas.AnalyzeResponse,
    summary="Menganalisis teks diary",
    description="Melakukan analisis sederhana terhadap teks diary untuk mendeteksi mood."
)
def analyze_entry_endpoint( # Nama fungsi yang lebih deskriptif
        request: schemas.AnalyzeRequest
):
    text = request.text
    # Logika dummy: menentukan analisis berdasar kemunculan kata (hanya contoh placeholder)
    analysis_result = "Mood netral"
    if any(word in text.lower() for word in ["sedih", "marah", "cemas", "depresi", "frustasi"]):
        analysis_result = "Mood terdeteksi negatif"
    elif any(word in text.lower() for word in ["senang", "bahagia", "gembira", "ceria", "optimis"]):
        analysis_result = "Mood terdeteksi positif"

    # Mengembalikan hasil analisis dummy
    return {"analysis": analysis_result}