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
    console.log("\n🔄 Falling back to Node.js mock server...");
    console.log("Starting development mock server instead...\n");

    // Start the mock server as fallback
    const mockServer = spawn("node", ["mock-server.js"], {
      stdio: "inherit",
      cwd: __dirname,
    });

    mockServer.on("error", (mockError) => {
      console.error("Failed to start mock server:", mockError.message);
      console.log("\nTo fix this issue:");
      console.log("1. Install pip: apt install python3-pip (on Ubuntu/Debian)");
      console.log(
        "2. Install dependencies: cd app/backend_api && pip install -r requirements.txt",
      );
      console.log(
        "3. Or install manually: pip install fastapi uvicorn sqlalchemy pydantic",
      );
      process.exit(1);
    });

    // Handle graceful shutdown for mock server
    process.on("SIGINT", () => {
      console.log("Shutting down mock server...");
      mockServer.kill("SIGINT");
      process.exit(0);
    });

    process.on("SIGTERM", () => {
      console.log("Shutting down mock server...");
      mockServer.kill("SIGTERM");
      process.exit(0);
    });
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
