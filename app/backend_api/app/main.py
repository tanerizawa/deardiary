# app/main.py

from fastapi import FastAPI, Depends, HTTPException, status, Response
from sqlalchemy.orm import Session
from typing import List
from dotenv import load_dotenv
import logging

# Load environment variables
load_dotenv()

# Import internal modules
from . import models, schemas, crud, openrouter
from .database import engine, get_db
from .ai_utils import (
    caption_image_with_openrouter,
    generate_articles_with_openrouter,
    chat_with_openrouter,
    MissingAPIKeyError,
    NetworkError,
    InvalidResponseError,
)

# Initialize FastAPI application
app = FastAPI(
    title="Diary Depresiku API",
    description="API untuk mencatat suasana hati, menganalisis emosi, dan mendapatkan saran artikel berbasis AI.",
    version="1.0.0",
)

# Create tables if not exist
models.Base.metadata.create_all(bind=engine)

# -------------------------
# AUTENTIKASI
# -------------------------

@app.post("/register/", status_code=status.HTTP_201_CREATED)
async def register_user(user: schemas.UserCreate, db: Session = Depends(get_db)):
    """Registrasi pengguna baru"""
    if crud.get_user_by_email(db, user.email):
        raise HTTPException(status_code=400, detail="Email sudah terdaftar")
    crud.create_user(db, user)
    return {"message": "User created"}


@app.post("/login/", response_model=schemas.Token)
async def login_user(user: schemas.UserLogin, db: Session = Depends(get_db)):
    """Login dan autentikasi pengguna"""
    if not crud.authenticate_user(db, user.email, user.password):
        raise HTTPException(status_code=400, detail="Email atau password salah")
    return {"token": "dummy"}  # Ganti dengan sistem token nyata jika perlu

# -------------------------
# ENTRI DIARY
# -------------------------

@app.post("/entries/", response_model=schemas.DiaryEntryResponse, status_code=201)
async def create_diary_entry(entry: schemas.DiaryEntryCreate, db: Session = Depends(get_db)):
    """Menyimpan entri suasana hati harian"""
    try:
        db_entry = crud.create_diary_entry(db, entry)
        return schemas.DiaryEntryResponse.model_validate(db_entry)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Gagal menyimpan entri: {str(e)}")


@app.get("/entries/", response_model=List[schemas.DiaryEntryResponse])
async def list_diary_entries(skip: int = 0, limit: int = 100, db: Session = Depends(get_db)):
    """Menampilkan seluruh entri diary"""
    entries = crud.get_diary_entries(db, skip=skip, limit=limit)
    return [schemas.DiaryEntryResponse.model_validate(e) for e in entries]


@app.get("/entries/{entry_id}", response_model=schemas.DiaryEntryResponse)
async def get_diary_entry(entry_id: int, db: Session = Depends(get_db)):
    """Menampilkan satu entri diary berdasarkan ID"""
    entry = crud.get_diary_entry(db, entry_id)
    if entry is None:
        raise HTTPException(status_code=404, detail="Entri tidak ditemukan")
    return schemas.DiaryEntryResponse.model_validate(entry)

# -------------------------
# ANALISIS EMOSI (AI)
# -------------------------

@app.post("/analyze/", response_model=schemas.AnalyzeResponse)
def analyze_entry(request: schemas.AnalyzeRequest):
    """Menganalisis teks untuk mendeteksi suasana hati menggunakan OpenRouter"""
    try:
        result = openrouter.analyze_text(request.text)
        return {"analysis": result}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Gagal menganalisis teks: {e}")

# -------------------------
# CHAT AI
# -------------------------

@app.post("/chat/")
def chat(request: schemas.ChatRequest):
    """Kirim pesan pengguna ke OpenRouter dan terima balasan teks."""
    try:
        result = chat_with_openrouter(request.text, history=request.history, mood=request.mood)
        return Response(content=result, media_type="text/plain")
    except MissingAPIKeyError as e:
        raise HTTPException(status_code=500, detail=f"API Key tidak ditemukan: {str(e)}")
    except NetworkError as e:
        raise HTTPException(status_code=502, detail=f"OpenRouter error: {str(e)}")
    except InvalidResponseError as e:
        raise HTTPException(status_code=502, detail=f"OpenRouter error: {str(e)}")

# -------------------------
# ARTIKEL OTOMATIS (AI)
# -------------------------

@app.post("/articles/", response_model=List[schemas.ArticleResponse])
def generate_articles(request: schemas.ArticleRequest):
    """Menyarankan artikel berdasarkan isi jurnal atau emosi pengguna"""
    try:
        return generate_articles_with_openrouter(request.text)
    except MissingAPIKeyError as e:
        raise HTTPException(status_code=500, detail=f"API Key tidak ditemukan: {str(e)}")
    except (NetworkError, InvalidResponseError) as e:
        raise HTTPException(status_code=502, detail=f"OpenRouter error: {str(e)}")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Gagal menghasilkan artikel: {str(e)}")

# -------------------------
# CAPTION GAMBAR (AI)
# -------------------------

@app.post("/openrouter_caption/", response_model=schemas.OpenRouterCaptionResponse)
async def caption_image(request: schemas.OpenRouterCaptionRequest):
    """Menghasilkan deskripsi gambar menggunakan AI"""
    try:
        caption = await caption_image_with_openrouter(request.image_url)
        return {"caption": caption}
    except MissingAPIKeyError as e:
        raise HTTPException(status_code=500, detail=f"API Key tidak ditemukan: {str(e)}")
    except NetworkError as e:
        raise HTTPException(status_code=502, detail=f"OpenRouter error: {str(e)}")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Gagal membuat caption: {e}")

# -------------------------
# STATISTIK MOOD
# -------------------------

@app.get("/stats/", response_model=schemas.MoodStatsResponse)
async def get_mood_stats(db: Session = Depends(get_db)):
    """Menghitung statistik suasana hati dari seluruh entri"""
    stats = crud.get_mood_stats(db)
    return {"stats": stats}
