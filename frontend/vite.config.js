import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
import defaultSettings from './src/settings.js'

const __dirname = path.dirname(fileURLToPath(import.meta.url))

function resolve(dir) {
  return path.resolve(__dirname, dir)
}

const port = process.env.port || process.env.npm_config_port || 8081 // dev port

// All configuration item explanations can be find in https://vite.dev/config/
export default defineConfig({
  base: '/',
  plugins: [
    vue({
      template: {
        compilerOptions: {
          // keep the whitespace between elements
          whitespace: 'preserve'
        }
      }
    }),
    // svg sprite: src/icons/svg/*.svg -> <svg-icon icon-class="name" />
    createSvgIconsPlugin({
      iconDirs: [resolve('src/icons/svg')],
      symbolId: 'icon-[name]'
    }),
    {
      // replace __APP_TITLE__ in index.html with settings.title
      name: 'html-title',
      transformIndexHtml: html => html.replace(/__APP_TITLE__/g, defaultSettings.title)
    }
  ],
  resolve: {
    alias: {
      '@': resolve('src')
    },
    // keep extension-less imports of .vue files (e.g. import Layout from '@/layout')
    extensions: ['.mjs', '.js', '.mts', '.ts', '.jsx', '.tsx', '.json', '.vue']
  },
  css: {
    preprocessorOptions: {
      scss: {
        // the styles still use @import (sass): silence the deprecation warning until they are converted to @use
        silenceDeprecations: ['import']
      }
    }
  },
  server: {
    port: Number(port),
    open: true
  },
  build: {
    outDir: resolve('../src/main/resources/static'),
    emptyOutDir: true,
    sourcemap: false,
    chunkSizeWarningLimit: 1200, // element-plus (~1.1MB) and xlsx are big
    rollupOptions: {
      output: {
        manualChunks: {
          // split Element Plus into a single package
          'chunk-element-plus': ['element-plus']
        }
      }
    }
  },
  test: {
    environment: 'jsdom',
    include: ['tests/**/*.{test,spec}.js']
  }
})
