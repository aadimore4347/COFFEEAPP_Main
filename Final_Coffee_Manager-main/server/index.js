import express from "express";
import cors from "cors";
export function createServer() {
    const app = express();
    // CORS configuration for development
    app.use(cors({
        origin: [
            "http://localhost:5173",
            "http://localhost:3000"
        ],
        credentials: true
    }));
    app.use(express.json());
    // Health check endpoint
    app.get("/api/health", (req, res)=>{
        res.json({
            status: "ok",
            message: "Development server running",
            mode: "development-proxy"
        });
    });
    // Catch-all for API routes - returns 501 Not Implemented
    // This tells the frontend to use standalone mode
    app.use("/api/*", (req, res)=>{
        res.status(501).json({
            error: "Not Implemented",
            message: "Backend not connected - using frontend standalone mode",
            endpoint: req.originalUrl,
            method: req.method
        });
    });
    return app;
}
// Only start server if run directly (not when imported by Vite)
if (import.meta.url === `file://${process.argv[1]}`) {
    const app = createServer();
    const PORT = process.env.PORT || 8080;
    app.listen(PORT, ()=>{
        console.log(`🚀 Development server running on http://localhost:${PORT}`);
        console.log(`📡 API endpoints available at http://localhost:${PORT}/api`);
        console.log(`💡 Backend integration ready - just implement the API endpoints!`);
    });
}
