import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5173,
    proxy: {
      // 前端请求 /api/** 转发到后端 8081（8080 被本机参考系统占用，DMS 后端常驻 8081）
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true
      }
    }
  }
})
