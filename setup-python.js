const { spawn, execSync } = require("child_process");
const fs = require("fs");
const path = require("path");

console.log("Setting up Python backend dependencies...");

// Check if Python 3 is available
function checkPython() {
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

// Check if pip is available
function checkPip() {
  try {
    execSync("pip3 --version", { stdio: "pipe" });
    return "pip3";
  } catch (error) {
    try {
      execSync("pip --version", { stdio: "pipe" });
      return "pip";
    } catch (error2) {
      console.log("pip not found, attempting to install...");
      try {
        const python = checkPython();
        execSync(`${python} -m ensurepip --user`, { stdio: "inherit" });
        return "pip3";
      } catch (error3) {
        console.error("Failed to install pip. Please install pip manually.");
        process.exit(1);
      }
    }
  }
}

function installDependencies() {
  const python = checkPython();
  const pip = checkPip();

  const backendPath = path.join(__dirname, "app", "backend_api");
  const requirementsPath = path.join(backendPath, "requirements.txt");

  if (!fs.existsSync(requirementsPath)) {
    console.error("requirements.txt not found in app/backend_api/");
    process.exit(1);
  }

  console.log("Installing Python dependencies...");
  try {
    // Try to install with --user flag first
    execSync(`cd ${backendPath} && ${pip} install --user -r requirements.txt`, {
      stdio: "inherit",
      cwd: backendPath,
    });
    console.log("Python dependencies installed successfully!");
  } catch (error) {
    console.log("Trying alternative installation method...");
    try {
      execSync(
        `cd ${backendPath} && ${python} -m pip install --user -r requirements.txt`,
        {
          stdio: "inherit",
          cwd: backendPath,
        },
      );
      console.log("Python dependencies installed successfully!");
    } catch (error2) {
      console.error("Failed to install Python dependencies:", error2.message);
      console.log(
        "Please manually run: cd app/backend_api && pip install -r requirements.txt",
      );
    }
  }
}

if (require.main === module) {
  installDependencies();
}

module.exports = { checkPython, checkPip, installDependencies };
