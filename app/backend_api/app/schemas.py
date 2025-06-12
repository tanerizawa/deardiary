# app/schemas.py: Definisi Pydantic model untuk request dan response API
from pydantic import (
    BaseModel,
    Field,
    field_serializer,
    model_validator,
)  # Import Field jika ingin menambahkan validasi tambahan
from typing import Dict, List


# Schema dasar untuk entri diary (digunakan sebagai base class untuk request/response)
class DiaryEntryBase(BaseModel):
    # Menggunakan 'content' agar konsisten dengan model Android dan database
    content: str = Field(
        ..., min_length=1, max_length=5000
    )  # Contoh validasi: minimal 1 karakter, maks 5000

    # Menggunakan 'mood' dengan pilihan yang terbatas (enum bisa jadi pilihan lain)
    mood: str = Field(
        ..., pattern="^(Senang|Sedih|Cemas|Marah|Tersipu)$"
    )  # Contoh validasi: regex untuk pilihan mood

    # Menggunakan 'timestamp' sebagai integer (untuk Unix timestamp), konsisten dengan model Android dan database
    timestamp: (
        int  # Tipe data int di Python setara dengan Long di Kotlin untuk angka besar
    )

    activities: List[str] = Field(default_factory=list)


# Schema untuk pembuatan entri (dikirim dari client/Android, tanpa ID)
class DiaryEntryCreate(DiaryEntryBase):
    pass  # Semua field sama dengan DiaryEntryBase, karena ID dibuat otomatis oleh DB


# Schema untuk output entri (dikembalikan ke client/Android, termasuk ID)
class DiaryEntryResponse(
    DiaryEntryBase
):  # Mengganti 'Entry' menjadi 'DiaryEntryResponse'
    id: int  # ID entri dari database

    model_config = {
        "from_attributes": True,
    }

    @model_validator(mode="before")
    def _convert_activities(cls, data):
        if isinstance(data, dict) and isinstance(data.get("activities"), str):
            data["activities"] = (
                data["activities"].split("|") if data["activities"] else []
            )
        elif hasattr(data, "activities") and isinstance(data.activities, str):
            setattr(
                data,
                "activities",
                data.activities.split("|") if data.activities else [],
            )
        return data


# Schema untuk permintaan analisis AI (dummy)
class AnalyzeRequest(BaseModel):
    text: str = Field(..., min_length=1)  # Teks yang akan dianalisis


# Schema untuk respons analisis AI (dummy)
class AnalyzeResponse(BaseModel):
    analysis: str  # Hasil analisis (misal: "Mood terdeteksi positif")


class MoodStatsResponse(BaseModel):
    """Response schema for mood statistics."""

    stats: Dict[str, int]


class UserCreate(BaseModel):
    email: str
    password: str
    name: str


class UserLogin(BaseModel):
    email: str
    password: str


class Token(BaseModel):
    token: str
