# app/main.py: Aplikasi FastAPI dan endpoint-endpoint API
from fastapi import FastAPI, Depends, HTTPException, status
import os
import json
import requests
from sqlalchemy.orm import Session
from typing import List

from . import crud
from . import models, schemas
from .database import engine, get_db

app = FastAPI(
    title="Diary Depresiku API",
    description="API untuk menyimpan dan mengelola entri diary, serta fitur analisis mood.",
    version="0.1.0",
)

models.Base.metadata.create_all(bind=engine)


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


@app.post(
    "/entries/",
    response_model=schemas.DiaryEntryResponse,
    status_code=status.HTTP_201_CREATED,
)
async def create_diary_entry_endpoint(
    entry: schemas.DiaryEntryCreate, db: Session = Depends(get_db)
):
    try:
        db_entry = crud.create_diary_entry(db=db, entry=entry)
        return schemas.DiaryEntryResponse.model_validate(db_entry)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to create entry: {str(e)}")


@app.get("/entries/", response_model=List[schemas.DiaryEntryResponse])
async def read_diary_entries_endpoint(
    skip: int = 0, limit: int = 100, db: Session = Depends(get_db)
):
    entries = crud.get_diary_entries(db=db, skip=skip, limit=limit)
    return [schemas.DiaryEntryResponse.model_validate(e) for e in entries]


@app.get("/entries/{entry_id}", response_model=schemas.DiaryEntryResponse)
async def read_diary_entry_by_id_endpoint(entry_id: int, db: Session = Depends(get_db)):
    db_entry = crud.get_diary_entry(db=db, entry_id=entry_id)
    if db_entry is None:
        raise HTTPException(status_code=404, detail="Entry not found")
    return schemas.DiaryEntryResponse.model_validate(db_entry)


@app.post("/analyze", response_model=schemas.AnalyzeResponse)
def analyze_entry_endpoint(request: schemas.AnalyzeRequest):
    try:
        analysis_result = analyze_with_gemini(request.text)
        return {"analysis": analysis_result}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to analyze text: {e}")


@app.post(
    "/gemini_articles/",
    response_model=List[schemas.GeminiArticleResponse],
)
def generate_articles_endpoint(request: schemas.GeminiArticleRequest):
    """Generate article ideas based on supplied text."""

    try:
        return generate_articles_with_gemini(request.text)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to generate articles: {e}")


@app.post("/openrouter_caption/", response_model=schemas.OpenRouterCaptionResponse)
def caption_image_endpoint(request: schemas.OpenRouterCaptionRequest):
    """Generate an image caption using OpenRouter."""

    try:
        caption = caption_image_with_openrouter(request.image_url)
        return {"caption": caption}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to caption image: {e}")


@app.get("/stats/", response_model=schemas.MoodStatsResponse)
async def read_mood_stats_endpoint(db: Session = Depends(get_db)):
    stats = crud.get_mood_stats(db)
    return {"stats": stats}
