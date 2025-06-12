from sqlalchemy.orm import Session
from sqlalchemy import func
from typing import List, Optional, Dict
from passlib.context import CryptContext

from . import models, schemas


def create_diary_entry(
    db: Session, entry: schemas.DiaryEntryCreate
) -> models.DiaryEntry:
    """Create a new diary entry and persist it to the database."""
    db_entry = models.DiaryEntry(**entry.model_dump())
    db.add(db_entry)
    db.commit()
    db.refresh(db_entry)
    return db_entry


def get_diary_entries(
    db: Session, skip: int = 0, limit: int = 100
) -> List[models.DiaryEntry]:
    """Return a list of diary entries ordered by newest timestamp."""
    return (
        db.query(models.DiaryEntry)
        .order_by(models.DiaryEntry.timestamp.desc())
        .offset(skip)
        .limit(limit)
        .all()
    )


def get_diary_entry(db: Session, entry_id: int) -> Optional[models.DiaryEntry]:
    """Return a single diary entry by ID or None if not found."""
    return db.query(models.DiaryEntry).filter(models.DiaryEntry.id == entry_id).first()


def get_mood_stats(db: Session) -> Dict[str, int]:
    """Return counts of diary entries grouped by mood."""
    results = (
        db.query(models.DiaryEntry.mood, func.count(models.DiaryEntry.id))
        .group_by(models.DiaryEntry.mood)
        .all()
    )
    return {mood: count for mood, count in results}


pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")


def get_user_by_email(db: Session, email: str) -> Optional[models.User]:
    return db.query(models.User).filter(models.User.email == email).first()


def create_user(db: Session, user: schemas.UserCreate) -> models.User:
    hashed_password = pwd_context.hash(user.password)
    db_user = models.User(
        email=user.email, name=user.name, hashed_password=hashed_password
    )
    db.add(db_user)
    db.commit()
    db.refresh(db_user)
    return db_user


def authenticate_user(db: Session, email: str, password: str) -> bool:
    user = get_user_by_email(db, email)
    if not user:
        return False
    return pwd_context.verify(password, user.hashed_password)
