import { URL, fileURLToPath } from 'node:url';
import { defineConfig, normalizePath } from 'vite';
import vue from '@vitejs/plugin-vue';
import { viteStaticCopy } from 'vite-plugin-static-copy';

const { getAbsoluteFSPath } = await import('swagger-ui-dist');
const swaggerUiPath = getAbsoluteFSPath();

const config = defineConfig({
  plugins: [
    vue(),
    viteStaticCopy({
      targets: [
        {
          src: [
            `${normalizePath(swaggerUiPath)}/*.{js,css,html,png}`,
            `!${normalizePath(swaggerUiPath)}/**/index.html`,
            normalizePath(fileURLToPath(new URL('./dist/axios.min.js', import.meta.resolve('axios/package.json')))),
            normalizePath(fileURLToPath(new URL('./src/main/webapp/swagger-ui/index.html', import.meta.url))),
          ],
          dest: 'swagger-ui',
        },
      ],
    }),
  ],
  root: fileURLToPath(new URL('./src/main/webapp/', import.meta.url)),
  publicDir: fileURLToPath(new URL('./target/classes/static/public', import.meta.url)),
  cacheDir: fileURLToPath(new URL('./target/.vite-cache', import.meta.url)),
  build: {
    emptyOutDir: true,
    outDir: fileURLToPath(new URL('./target/classes/static/', import.meta.url)),
    rollupOptions: {
      input: {
        app: fileURLToPath(new URL('./src/main/webapp/index.html', import.meta.url)),
      },
    },
  },
  resolve: {
    alias: {
      vue: '@vue/compat/dist/vue.esm-bundler.js',
      '@': fileURLToPath(new URL('./src/main/webapp/app/', import.meta.url)),
      '@content': fileURLToPath(new URL('./src/main/webapp/content/', import.meta.url)),
      '@assets': fileURLToPath(new URL('./src/main/webapp/assets/', import.meta.url)), // ✅ qo‘shildi
    },
  },
  define: {
    I18N_HASH: '"generated_hash"',
    SERVER_API_URL: '"http://13.53.190.127:7777"',
    APP_VERSION: `"${process.env.APP_VERSION ? process.env.APP_VERSION : 'DEV'}"`,
  },
  server: {
    host: true,
    port: 9000,
    proxy: {
      '/api': {
        target: 'http://13.53.190.127:7777',
        changeOrigin: true,
      },
      '/management': {
        target: 'http://13.53.190.127:7777',
        changeOrigin: true,
      },
      '/v3/api-docs': {
        target: 'http://13.53.190.127:7777',
        changeOrigin: true,
      },
    },
  },
});

// jhipster-needle-add-vite-config - JHipster will add custom config

export default config;
