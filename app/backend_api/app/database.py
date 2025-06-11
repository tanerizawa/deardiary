# app/database.py: Inisialisasi koneksi database dan session SQLAlchemy
from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Session # Import Session untuk tipe hint

# URL database. Untuk SQLite, ini adalah path ke file database.
# Database akan dibuat di root direktori aplikasi FastAPI.
# Catatan: "sqlite:///./diary.db" berarti file diary.db ada di direktori yang sama dengan tempat Anda menjalankan server.
SQLALCHEMY_DATABASE_URL = "sqlite:///./diary.db"

# create_engine membuat instance engine database.
# - connect_args={"check_same_thread": False}: Diperlukan untuk SQLite
#   saat berinteraksi dengan multiple thread (seperti di FastAPI), untuk menghindari error.
# - future=True: Mengaktifkan mode kompatibilitas SQLAlchemy 2.0 untuk perilaku yang lebih modern.
engine = create_engine(
    SQLALCHEMY_DATABASE_URL,
    connect_args={"check_same_thread": False},
    future=True # Mengaktifkan SQLAlchemy 2.0 style
)

# Base adalah kelas dasar tempat model database kita akan mewarisi.
# Ini adalah bagian dari deklarasi ORM SQLAlchemy.
Base = declarative_base()

# sessionmaker adalah "pabrik" untuk objek Session.
# - autocommit=False: Perubahan tidak langsung disimpan ke DB secara otomatis.
# - autoflush=False: Objek tidak langsung di-flush ke DB.
# - bind=engine: Mengikat Session ke engine database kita.
# - future=True: Mengaktifkan mode kompatibilitas SQLAlchemy 2.0 untuk Session.
SessionLocal = sessionmaker(
    autocommit=False,
    autoflush=False,
    bind=engine,
    future=True # Mengaktifkan SQLAlchemy 2.0 style
)

# Dependency: fungsi untuk mendapatkan sesi database per permintaan.
# Ini akan digunakan oleh FastAPI untuk setiap request yang membutuhkan akses database.
def get_db():
    db: Session = SessionLocal() # Memberikan tipe hint untuk objek db
    try:
        yield db # Mengembalikan sesi database dan menjaga agar tetap terbuka
    finally:
        db.close() # Menutup sesi setelah request selesai (penting!)