from sqlalchemy.orm import Session
from sqlalchemy import func
from typing import List, Optional, Dict

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
