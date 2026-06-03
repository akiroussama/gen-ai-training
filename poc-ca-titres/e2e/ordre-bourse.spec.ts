// >>> CORRIGE - NE PAS DISTRIBUER <<<
// Demo formateur de la migration Selenium -> Playwright (parcours deja migre).
// NE PAS livrer au gym stagiaire : c'est la solution finie. La version stagiaire
// (e2e/ordre-bourse.spec.ts dans partage-stagiaires/) est un squelette TODO.
import { test, expect } from '@playwright/test';

/**
 * Équivalent Playwright du parcours Selenium (OrdreBourseSeleniumIT), mais
 * DÉTERMINISTE. Les trois péchés du flaky sont corrigés :
 *   - SIN 1 (Thread.sleep) -> auto-wait : chaque locator attend tout seul ;
 *     les web-first assertions (expect(...).toBeVisible / toHaveText) ré-essaient
 *     jusqu'au timeout. Zéro sleep.
 *   - SIN 2 (sélecteurs positionnels) -> locators robustes par rôle / label.
 *   - SIN 3 (état partagé / ordre) -> chaque test est indépendant (page neuve).
 *
 * Lancer 5 fois : `npm run test:5x` -> 5/5 vert, sans retries.
 */

test('nominal : la liste des ordres se charge', async ({ page }) => {
  await page.goto('/');

  const lignes = page.locator('cat-liste-ordres tbody tr');
  await expect(lignes.first()).toBeVisible(); // auto-wait, pas de sleep
  expect(await lignes.count()).toBeGreaterThan(0);
});

test('filtre : la recherche par code client filtre les résultats', async ({ page }) => {
  await page.goto('/');

  await page.getByLabel('Code client').fill('CLI-0001');
  await page.getByRole('button', { name: 'Rechercher' }).click();

  const clients = page.locator('cat-liste-ordres tbody tr td:nth-child(2)');
  await expect(clients.first()).toHaveText('CLI-0001'); // web-first : ré-essaie

  const tous = await clients.allTextContents();
  expect(tous.every((t) => t.trim() === 'CLI-0001')).toBeTruthy();
});
