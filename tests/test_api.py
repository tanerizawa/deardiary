import sys
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.pool import StaticPool
import pytest

sys.path.append("app/backend_api")

from app.main import app
from app import models
from app.database import Base, get_db


@pytest.fixture
def client():
    engine = create_engine(
        "sqlite://",
        connect_args={"check_same_thread": False},
        poolclass=StaticPool,
    )
    TestingSessionLocal = sessionmaker(
        autocommit=False, autoflush=False, bind=engine, future=True
    )
    Base.metadata.create_all(bind=engine)

    def override_get_db():
        db = TestingSessionLocal()
        try:
            yield db
        finally:
            db.close()

    app.dependency_overrides[get_db] = override_get_db
    with TestClient(app) as c:
        yield c


def test_create_and_get_entries(client):
    response = client.post(
        "/entries/",
        json={
            "content": "hello",
            "mood": "Senang",
            "timestamp": 1,
            "activities": ["A"],
        },
    )
    assert response.status_code == 201
    data = response.json()
    assert data["content"] == "hello"
    assert data["activities"] == ["A"]

    resp = client.get("/entries/")
    assert resp.status_code == 200
    assert len(resp.json()) == 1


def test_mood_stats(client):
    client.post(
        "/entries/",
        json={"content": "hi", "mood": "Sedih", "timestamp": 2, "activities": []},
    )
    client.post(
        "/entries/",
        json={"content": "hey", "mood": "Sedih", "timestamp": 3, "activities": []},
    )
    resp = client.get("/stats/")
    assert resp.status_code == 200
    assert resp.json()["stats"] == {"Sedih": 2}
