# app/main.py: Aplikasi FastAPI dan endpoint-endpoint API
from fastapi import FastAPI, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List

from .ai_utils import (
    analyze_with_gemini,
    caption_image_with_openrouter,
    generate_articles_with_gemini,
)

from . import crud
from . import models, schemas, openrouter
from .database import engine, get_db

app = FastAPI(
    title="Diary Depresiku API",
    description="API untuk menyimpan dan mengelola entri diary, serta fitur analisis mood.",
    version="0.1.0",
)

models.Base.metadata.create_all(bind=engine)




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


@app.post("/openrouter_analyze/", response_model=schemas.AnalyzeResponse)
def openrouter_analyze_endpoint(request: schemas.AnalyzeRequest):
    """Analyze text using the OpenRouter API."""

    try:
        result = openrouter.analyze_text(request.text)
        return {"analysis": result}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to analyze text: {e}")


@app.get("/stats/", response_model=schemas.MoodStatsResponse)
async def read_mood_stats_endpoint(db: Session = Depends(get_db)):
    stats = crud.get_mood_stats(db)
    return {"stats": stats}
