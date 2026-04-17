import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      // Proxy /api -> Web backend (cusapi) port 8081
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false,
      }
    }
  }
})
