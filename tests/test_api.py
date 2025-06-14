import sys
import os
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.pool import StaticPool
import pytest
import httpx

os.environ["SQLALCHEMY_DATABASE_URL"] = "sqlite:///:memory:"
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


def test_analyze_entry(client, monkeypatch):
    class MockResp:
        def __init__(self, content="Positif"):
            self.choices = [
                type("Choice", (), {"message": type("M", (), {"content": content})()})
            ]

    class MockClient:
        def __init__(self, resp):
            self.chat = type(
                "Chat",
                (),
                {
                    "completions": type(
                        "Comp", (), {"create": lambda self, **kwargs: resp}
                    )()
                },
            )()

    monkeypatch.setenv("OPENROUTER_API_KEY", "dummy")
    monkeypatch.setattr(
        "app.openrouter.get_openrouter_client", lambda: MockClient(MockResp())
    )
    monkeypatch.setattr(
        "app.openrouter_client.get_openrouter_client", lambda: MockClient(MockResp())
    )
    resp = client.post("/analyze/", json={"text": "saya senang"})
    assert resp.status_code == 200
    assert resp.json()["analysis"] == "Positif"


def test_openrouter_articles(client, monkeypatch):
    class MockResp:
        def __init__(self):
            self.choices = [
                type(
                    "Choice",
                    (),
                    {
                        "message": type(
                            "M", (), {"content": '[{"title": "A", "summary": "B"}]'}
                        )()
                    },
                )
            ]

    class MockClient:
        def __init__(self, resp):
            self.chat = type(
                "Chat",
                (),
                {
                    "completions": type(
                        "Comp", (), {"create": lambda self, **kwargs: resp}
                    )()
                },
            )()

    monkeypatch.setenv("OPENROUTER_API_KEY", "dummy")
    monkeypatch.setattr(
        "app.ai_utils.get_openrouter_client",
        lambda: MockClient(MockResp()),
    )
    monkeypatch.setattr(
        "app.openrouter_client.get_openrouter_client", lambda: MockClient(MockResp())
    )
    resp = client.post("/articles/", json={"text": "hi"})
    assert resp.status_code == 200
    assert resp.json() == [{"title": "A", "summary": "B"}]


def test_openrouter_articles_error(client, monkeypatch):
    class MockClient:
        def __init__(self):
            self.chat = type(
                "Chat",
                (),
                {
                    "completions": type(
                        "Comp",
                        (),
                        {
                            "create": lambda self, **kwargs: (_ for _ in ()).throw(
                                Exception("bad")
                            )
                        },
                    )()
                },
            )()

    monkeypatch.setenv("OPENROUTER_API_KEY", "dummy")
    monkeypatch.setattr("app.ai_utils.get_openrouter_client", lambda: MockClient())
    monkeypatch.setattr(
        "app.openrouter_client.get_openrouter_client", lambda: MockClient()
    )
    monkeypatch.setattr("app.openrouter.get_openrouter_client", lambda: MockClient())
    resp = client.post("/articles/", json={"text": "hi"})
    assert resp.status_code == 502


def test_openrouter_articles_missing_key(client, monkeypatch):
    monkeypatch.delenv("OPENROUTER_API_KEY", raising=False)
    resp = client.post("/articles/", json={"text": "hi"})
    assert resp.status_code == 500


def test_openrouter_articles_bad_json(client, monkeypatch):
    class BadResp:
        def __init__(self):
            self.choices = [
                type("C", (), {"message": type("M", (), {"content": "not-json"})()})
            ]

    class MockClient:
        def __init__(self):
            self.chat = type(
                "Chat",
                (),
                {
                    "completions": type(
                        "Comp", (), {"create": lambda self, **kwargs: BadResp()}
                    )()
                },
            )()

    monkeypatch.setenv("OPENROUTER_API_KEY", "dummy")
    monkeypatch.setattr("app.ai_utils.get_openrouter_client", lambda: MockClient())
    monkeypatch.setattr(
        "app.openrouter_client.get_openrouter_client",
        lambda: MockClient(),
    )
    resp = client.post("/articles/", json={"text": "hi"})
    assert resp.status_code == 502


def test_openrouter_caption(client, monkeypatch):
    class MockResp:
        def __init__(self):
            self.choices = [
                type("C", (), {"message": type("M", (), {"content": "A boardwalk"})()})
            ]

    class MockClient:
        def __init__(self):
            self.chat = type(
                "Chat",
                (),
                {
                    "completions": type(
                        "Comp", (), {"create": lambda self, **kw: MockResp()}
                    )()
                },
            )()

    monkeypatch.setenv("OPENROUTER_API_KEY", "dummy")
    monkeypatch.setattr("app.ai_utils.get_openrouter_client", lambda: MockClient())
    monkeypatch.setattr(
        "app.openrouter_client.get_openrouter_client",
        lambda: MockClient(),
    )
    resp = client.post(
        "/openrouter_caption/",
        json={"image_url": "http://example.com/img.jpg"},
    )
    assert resp.status_code == 200
    assert resp.json()["caption"] == "A boardwalk"


def test_openrouter_caption_error(client, monkeypatch):
    class MockClient:
        def __init__(self):
            self.chat = type(
                "Chat",
                (),
                {
                    "completions": type(
                        "Comp",
                        (),
                        {
                            "create": lambda self, **kw: (_ for _ in ()).throw(
                                Exception("bad")
                            )
                        },
                    )()
                },
            )()

    monkeypatch.setenv("OPENROUTER_API_KEY", "dummy")
    monkeypatch.setattr("app.ai_utils.get_openrouter_client", lambda: MockClient())
    monkeypatch.setattr(
        "app.openrouter_client.get_openrouter_client",
        lambda: MockClient(),
    )
    resp = client.post(
        "/openrouter_caption/",
        json={"image_url": "http://example.com/img.jpg"},
    )
    assert resp.status_code == 500


def test_openrouter_analyze(client, monkeypatch):
    class MockResp:
        def __init__(self, content="Positif"):
            self.choices = [
                type("C", (), {"message": type("M", (), {"content": content})()})
            ]

    class MockClient:
        def __init__(self):
            self.chat = type(
                "Chat",
                (),
                {
                    "completions": type(
                        "Comp", (), {"create": lambda self, **kw: MockResp()}
                    )()
                },
            )()

    monkeypatch.setenv("OPENROUTER_API_KEY", "dummy")
    monkeypatch.setattr("app.ai_utils.get_openrouter_client", lambda: MockClient())
    monkeypatch.setattr(
        "app.openrouter_client.get_openrouter_client", lambda: MockClient()
    )
    monkeypatch.setattr("app.openrouter.get_openrouter_client", lambda: MockClient())
    resp = client.post("/analyze/", json={"text": "hello"})
    assert resp.status_code == 200
    assert resp.json() == {"analysis": "Positif"}


def test_openrouter_analyze_error(client, monkeypatch):
    class MockClient:
        def __init__(self):
            self.chat = type(
                "Chat",
                (),
                {
                    "completions": type(
                        "Comp",
                        (),
                        {
                            "create": lambda self, **kw: (_ for _ in ()).throw(
                                Exception("bad")
                            )
                        },
                    )()
                },
            )()

    monkeypatch.setenv("OPENROUTER_API_KEY", "dummy")
    monkeypatch.setattr("app.openrouter.get_openrouter_client", lambda: MockClient())
    monkeypatch.setattr(
        "app.openrouter_client.get_openrouter_client", lambda: MockClient()
    )
    resp = client.post("/analyze/", json={"text": "hello"})
    assert resp.status_code == 500
