# DearDiary

This repository contains an Android application and a small FastAPI backend used for storing diary entries.

## Android setup

1. Install **Android Studio** (AGP 8.1+).
2. Clone this repository and open the project in Android Studio.
3. Android Studio will automatically download the Gradle wrapper and all required dependencies.
4. Click **Run** or use `./gradlew assembleDebug` from the command line to build the app.
5. The Retrofit base URL is defined in `MyApplication.kt`. The default value points to `http://10.0.2.2:8000/` for an Android emulator. Adjust it if you run the backend elsewhere.

## FastAPI backend

1. Make sure **Python 3.10+** is installed.
2. Create a virtual environment and activate it:
   ```bash
   cd app/backend_api
   python -m venv venv
   source venv/bin/activate
   ```
3. Install the required packages:
   ```bash
   pip install -r requirements.txt
   ```
4. Start the development server from the `app/backend_api` directory:
   ```bash
   uvicorn app.main:app --reload
   ```
   The API will be available at `http://localhost:8000`.

The backend uses an SQLite database (`diary.db`) created automatically in the backend directory. There are no required environment variables.
