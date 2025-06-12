const { spawn } = require("child_process");
const path = require("path");
const fs = require("fs");

console.log("Starting FastAPI development server...");

function findPython() {
  const { execSync } = require("child_process");
  try {
    execSync("python3 --version", { stdio: "pipe" });
    return "python3";
  } catch (error) {
    try {
      execSync("python --version", { stdio: "pipe" });
      const output = execSync("python --version", { encoding: "utf8" });
      if (output.includes("Python 3")) {
        return "python";
      }
    } catch (error2) {
      console.error("Python 3 not found. Please install Python 3.");
      process.exit(1);
    }
  }
}

function startFastAPI() {
  const python = findPython();
  const backendPath = path.join(__dirname, "app", "backend_api");

  if (!fs.existsSync(backendPath)) {
    console.error("Backend directory not found:", backendPath);
    process.exit(1);
  }

  // Check if main.py exists
  const mainPath = path.join(backendPath, "app", "main.py");
  if (!fs.existsSync(mainPath)) {
    console.error("main.py not found in app/backend_api/app/");
    process.exit(1);
  }

  console.log("Starting FastAPI server on http://0.0.0.0:8000");
  console.log("Backend path:", backendPath);

  // Try uvicorn first, then fallback to python -m uvicorn
  const uvicornArgs = [
    "app.main:app",
    "--reload",
    "--host",
    "0.0.0.0",
    "--port",
    "8000",
  ];

  let uvicornProcess;

  try {
    // Try direct uvicorn command first
    uvicornProcess = spawn("uvicorn", uvicornArgs, {
      cwd: backendPath,
      stdio: "inherit",
      env: { ...process.env, PYTHONPATH: backendPath },
    });
  } catch (error) {
    console.log("uvicorn command not found, trying python -m uvicorn...");
    // Fallback to python -m uvicorn
    uvicornProcess = spawn(python, ["-m", "uvicorn", ...uvicornArgs], {
      cwd: backendPath,
      stdio: "inherit",
      env: { ...process.env, PYTHONPATH: backendPath },
    });
  }

  uvicornProcess.on("error", (error) => {
    console.error("Failed to start FastAPI server:", error.message);
    console.log("Make sure uvicorn is installed:");
    console.log("cd app/backend_api && pip install uvicorn fastapi");
    process.exit(1);
  });

  uvicornProcess.on("close", (code) => {
    console.log(`FastAPI server process exited with code ${code}`);
  });

  // Handle graceful shutdown
  process.on("SIGINT", () => {
    console.log("Shutting down FastAPI server...");
    uvicornProcess.kill("SIGINT");
    process.exit(0);
  });

  process.on("SIGTERM", () => {
    console.log("Shutting down FastAPI server...");
    uvicornProcess.kill("SIGTERM");
    process.exit(0);
  });
}

if (require.main === module) {
  startFastAPI();
}

module.exports = { startFastAPI };
