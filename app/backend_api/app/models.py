# app/models.py: Definisi model ORM untuk tabel diary entries
from sqlalchemy import Column, Integer, String, BigInteger # Penting: Import BigInteger
from .database import Base

# ORM model untuk entri diary
class DiaryEntry(Base):
    # Nama tabel di database. Harus konsisten dengan 'tableName' di Android DiaryEntry.kt
    __tablename__ = "diary_entries"

    id = Column(Integer, primary_key=True, index=True)

    # Nama kolom untuk isi diary. Harus konsisten dengan 'content' di Android dan schemas.py.
    content = Column(String, nullable=False, index=True) # Menambahkan index juga untuk pencarian

    # Nama kolom untuk mood.
    mood = Column(String, nullable=False, index=True) # Menambahkan index

    # Nama kolom untuk timestamp.
    # Menggunakan BigInteger agar dapat menyimpan nilai Long dari Android/Kotlin.
    # Harus konsisten dengan 'timestamp: int' di schemas.py dan 'creationTimestamp: Long' di DiaryEntry.kt.
    timestamp = Column(BigInteger, nullable=False, index=True) # Menambahkan index