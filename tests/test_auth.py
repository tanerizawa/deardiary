import sys
import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.pool import StaticPool

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


def test_register_and_login(client):
    resp = client.post(
        "/register/", json={"email": "a@a.com", "password": "x", "name": "Alice"}
    )
    assert resp.status_code == 201

    resp = client.post("/login/", json={"email": "a@a.com", "password": "x"})
    assert resp.status_code == 200
    assert "token" in resp.json()


def test_login_fail(client):
    client.post("/register/", json={"email": "b@a.com", "password": "x", "name": "Bob"})
    resp = client.post("/login/", json={"email": "b@a.com", "password": "bad"})
    assert resp.status_code == 400
