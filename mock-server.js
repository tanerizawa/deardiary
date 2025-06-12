const http = require("http");
const url = require("url");
const fs = require("fs");
const path = require("path");

// Simple mock data storage
let diaryEntries = [
  {
    id: 1,
    content:
      "Hari ini saya merasa cukup baik. Cuaca cerah dan saya bisa jalan pagi.",
    mood: "positive",
    timestamp: new Date().toISOString(),
    created_at: new Date().toISOString(),
  },
];

let nextId = 2;

function sendResponse(res, statusCode, data) {
  res.writeHead(statusCode, {
    "Content-Type": "application/json",
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, OPTIONS",
    "Access-Control-Allow-Headers": "Content-Type",
  });
  res.end(JSON.stringify(data));
}

function sendError(res, statusCode, message) {
  sendResponse(res, statusCode, { detail: message });
}

const server = http.createServer((req, res) => {
  const parsedUrl = url.parse(req.url, true);
  const method = req.method;
  const pathname = parsedUrl.pathname;

  // Handle CORS preflight
  if (method === "OPTIONS") {
    res.writeHead(200, {
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, OPTIONS",
      "Access-Control-Allow-Headers": "Content-Type",
    });
    res.end();
    return;
  }

  console.log(`${method} ${pathname}`);

  // Routes
  if (pathname === "/entries/" && method === "GET") {
    // Get all diary entries
    const skip = parseInt(parsedUrl.query.skip) || 0;
    const limit = parseInt(parsedUrl.query.limit) || 100;
    const result = diaryEntries.slice(skip, skip + limit);
    sendResponse(res, 200, result);
  } else if (pathname === "/entries/" && method === "POST") {
    // Create new diary entry
    let body = "";
    req.on("data", (chunk) => {
      body += chunk.toString();
    });
    req.on("end", () => {
      try {
        const entryData = JSON.parse(body);
        const newEntry = {
          id: nextId++,
          content: entryData.content,
          mood: entryData.mood || "neutral",
          timestamp: entryData.timestamp || new Date().toISOString(),
          created_at: new Date().toISOString(),
        };
        diaryEntries.push(newEntry);
        sendResponse(res, 201, newEntry);
      } catch (error) {
        sendError(res, 400, "Invalid JSON data");
      }
    });
  } else if (pathname.startsWith("/entries/") && method === "GET") {
    // Get diary entry by ID
    const entryId = parseInt(pathname.split("/")[2]);
    const entry = diaryEntries.find((e) => e.id === entryId);
    if (entry) {
      sendResponse(res, 200, entry);
    } else {
      sendError(res, 404, "Entry not found");
    }
  } else if (pathname === "/analyze" && method === "POST") {
    // Analyze diary entry
    let body = "";
    req.on("data", (chunk) => {
      body += chunk.toString();
    });
    req.on("end", () => {
      try {
        const requestData = JSON.parse(body);
        const text = requestData.text || "";

        // Simple mood analysis (same logic as Python version)
        let analysis = "Mood netral";
        if (/sedih|marah|cemas|depresi|frustasi/i.test(text)) {
          analysis = "Mood terdeteksi negatif";
        } else if (/senang|bahagia|gembira|ceria|optimis/i.test(text)) {
          analysis = "Mood terdeteksi positif";
        }

        sendResponse(res, 200, { analysis });
      } catch (error) {
        sendError(res, 400, "Invalid JSON data");
      }
    });
  } else if (pathname === "/stats/" && method === "GET") {
    // Get mood statistics
    const stats = diaryEntries.reduce((acc, entry) => {
      acc[entry.mood] = (acc[entry.mood] || 0) + 1;
      return acc;
    }, {});
    sendResponse(res, 200, { stats });
  } else if (pathname === "/" && method === "GET") {
    // API info
    sendResponse(res, 200, {
      title: "Diary Depresiku API (Mock Server)",
      description: "Temporary Node.js mock server for development",
      version: "0.1.0-mock",
      note: "This is a development mock server. Please install Python dependencies for the full FastAPI server.",
    });
  } else {
    sendError(res, 404, "Not Found");
  }
});

const PORT = 8000;
const HOST = "0.0.0.0";

server.listen(PORT, HOST, () => {
  console.log(`🚀 Mock API server running on http://${HOST}:${PORT}`);
  console.log(`📝 Available endpoints:`);
  console.log(`   GET  /               - API info`);
  console.log(`   GET  /entries/       - Get all diary entries`);
  console.log(`   POST /entries/       - Create new diary entry`);
  console.log(`   GET  /entries/{id}   - Get diary entry by ID`);
  console.log(`   POST /analyze        - Analyze diary text`);
  console.log(`   GET  /stats/         - Get mood statistics`);
  console.log(``);
  console.log(`⚠️  This is a temporary mock server for development.`);
  console.log(`   To use the full FastAPI server:`);
  console.log(`   1. Install pip: apt install python3-pip`);
  console.log(
    `   2. Install dependencies: cd app/backend_api && pip install -r requirements.txt`,
  );
  console.log(
    `   3. Run: uvicorn app.main:app --reload --host 0.0.0.0 --port 8000`,
  );
});

// Graceful shutdown
process.on("SIGINT", () => {
  console.log("\n📋 Shutting down mock server...");
  server.close(() => {
    console.log("Mock server stopped");
    process.exit(0);
  });
});

process.on("SIGTERM", () => {
  console.log("\n📋 Shutting down mock server...");
  server.close(() => {
    console.log("Mock server stopped");
    process.exit(0);
  });
});
