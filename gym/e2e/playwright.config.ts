import { defineConfig } from '@playwright/test';

/**
 * Config Playwright — équivalent déterministe du test Selenium.
 *
 * Pré-requis (mêmes que le Selenium) : backend Spring sur :8080 + Angular sur
 * :4200. `retries: 0` est VOLONTAIRE : l'auto-wait de Playwright rend le test
 * déterministe, on n'a pas besoin de masquer la flakiness par des retries.
 */
export default defineConfig({
  testDir: '.',
  timeout: 15_000,
  expect: { timeout: 5_000 },
  retries: 0,
  reporter: 'list',
  use: {
    baseURL: 'http://localhost:4200',
    headless: true,
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
  },
  projects: [{ name: 'chromium', use: { browserName: 'chromium' } }],
});
