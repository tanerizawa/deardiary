# Development Setup for Diary Depresiku

## Current Status ✅

The development environment is now working with a **Node.js mock server** that provides all the API endpoints needed for the Android app development.

### What's Running

- **Mock API Server**: `http://localhost:8000`
- **Available Endpoints**:
  - `GET /` - API info
  - `GET /entries/` - Get all diary entries
  - `POST /entries/` - Create new diary entry
  - `GET /entries/{id}` - Get diary entry by ID
  - `POST /analyze` - Analyze diary text for mood
  - `GET /stats/` - Get mood statistics

## Quick Start

```bash
npm install    # Setup dependencies and Python environment check
npm run dev    # Start the development server (falls back to mock server)
```

## Available Scripts

```bash
npm install           # Install dependencies and check Python setup
npm run dev           # Start FastAPI server (falls back to mock server)
npm run dev:mock      # Start mock server directly
npm run build         # Build Android app using Gradle
npm run test          # Run Python tests (requires Python setup)
```

## Mock Server Features

The current mock server provides:

- ✅ **Full API compatibility** with the FastAPI specification
- ✅ **CORS support** for Android app development
- ✅ **Simple mood analysis** (same logic as Python version)
- ✅ **In-memory data storage** (resets on restart)
- ✅ **Same endpoints** as the full FastAPI server

## Transitioning to Full Python FastAPI Server

To use the complete Python FastAPI server with database support:

### Option 1: Install Python Dependencies Locally

```bash
# Install pip (Ubuntu/Debian)
sudo apt update && sudo apt install python3-pip

# Install Python dependencies
cd app/backend_api
pip install -r requirements.txt

# Run FastAPI server directly
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

### Option 2: Use Docker (Recommended for Production)

```bash
# Create Dockerfile for backend
cd app/backend_api
docker build -t diary-backend .
docker run -p 8000:8000 diary-backend
```

### Option 3: Use Python Virtual Environment

```bash
# Create virtual environment
python3 -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate

# Install dependencies
cd app/backend_api
pip install -r requirements.txt

# Run server
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

## Android App Configuration

The Android app is configured to connect to `http://10.0.2.2:8000/` (Android emulator localhost) as defined in `app/build.gradle.kts`:

```kotlin
buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8000/\"")
```

For physical devices or different setups, update this URL accordingly.

## Project Structure

```
├── app/
│   ├── backend_api/           # Python FastAPI backend
│   │   ├── app/
│   │   │   ├── main.py        # FastAPI application
│   │   │   ├── models.py      # Database models
│   │   │   ├── schemas.py     # Pydantic schemas
│   │   │   └── crud.py        # Database operations
│   │   └── requirements.txt   # Python dependencies
│   └── src/main/              # Android app source
├── mock-server.js             # Node.js mock server (current)
├── run-fastapi.js             # FastAPI runner with fallback
├── setup-python.js            # Python environment setup
└── package.json               # Node.js dependencies and scripts
```

## Troubleshooting

### Mock Server Issues

- Check if port 8000 is available
- Restart with `npm run dev:mock`

### Python FastAPI Issues

- Ensure Python 3.7+ is installed
- Install pip: `sudo apt install python3-pip`
- Check Python path: `which python3`
- Install dependencies manually: `cd app/backend_api && pip install fastapi uvicorn sqlalchemy pydantic`

### Android Connection Issues

- Update `BASE_URL` in `app/build.gradle.kts`
- For emulator: use `http://10.0.2.2:8000/`
- For physical device: use your computer's IP address

## Next Steps

1. **Continue Android development** using the mock server
2. **Set up Python environment** when database persistence is needed
3. **Deploy to production** using Docker or cloud services
4. **Add authentication** and other advanced features

The mock server provides everything needed for initial Android app development and testing!
