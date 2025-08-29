import { defineConfig } from "vite";
import react from "@vitejs/plugin-react-swc";
import path from "path";
import { createServer } from "./server/index.js";
// https://vitejs.dev/config/
export default defineConfig(({ mode })=>({
        server: {
            host: "::",
            port: 5173,
            open: false
        },
        build: {
            outDir: "dist",
            sourcemap: mode === "development"
        },
        plugins: [
            react(),
            serverPlugin()
        ],
        resolve: {
            alias: {
                "@": path.resolve(__dirname, "./client")
            }
        },
        // Optimize dependencies for faster development
        optimizeDeps: {
            include: [
                "react",
                "react-dom",
                "react-router-dom"
            ]
        }
    }));
function serverPlugin() {
    return {
        name: "dev-server-plugin",
        apply: "serve",
        configureServer (server) {
            // Add development API server as middleware
            const app = createServer();
            // Mount the API server on /api routes
            server.middlewares.use(app);
        }
    };
}
