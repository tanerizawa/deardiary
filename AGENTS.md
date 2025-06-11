## Start the FastAPI backend
From the project root, run:
```bash
cd app/backend_api
pip install -r requirements.txt
uvicorn app.main:app --reload
```

## General guidelines
- Run any configured formatters before committing code.
- Do **not** commit OS artifacts such as `.DS_Store` or `Thumbs.db`.
- Ensure every file ends with a trailing newline.
