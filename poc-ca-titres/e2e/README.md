# e2e Playwright — équivalent déterministe (migration Selenium → Playwright)

Support de l'atelier J3 : on **montre** d'abord la flakiness du Selenium, puis on
migre vers Playwright **déterministe** sur le même parcours.

## Le contraste à montrer

| | Selenium flaky (`src/test/.../OrdreBourseSeleniumFlakyIT.java`) | Playwright (`ordre-bourse.spec.ts`) |
|---|---|---|
| Attente | `Thread.sleep(400)` (course) | auto-wait par locator |
| Assertions | one-shot après le sleep | web-first (`expect(...).toHaveText`), ré-essaient |
| Locators | positionnels (`div.filtre > *:nth-child(3)`) | par rôle / label (`getByRole`, `getByLabel`) |
| Tests | état partagé + ordre imposé | indépendants |
| 5 lancements | rouge / vert **aléatoire** | **5/5 vert** (sans `retries`) |

## Pré-requis (mêmes que le Selenium)

1. Backend Spring sur :8080 — dans `poc-titres/` : `mvn spring-boot:run`
2. Angular sur :4200 (proxy `/api`) — dans `angular-front/` : `npm start`
3. Navigateur Chromium Playwright installé (fait par le devcontainer, sinon :
   `npx playwright install chromium`)

## Lancer

```bash
cd e2e
npm ci                      # ou npm install
npx playwright install chromium   # si pas déjà fait par le devcontainer
npx playwright test         # le parcours, déterministe
npm run test:5x             # 5 fois de suite -> 5/5 vert
npx playwright show-report  # rapport + traces
```

## Démo flakiness Selenium (avant la migration)

Dans `poc-titres/`, retirer `@Disabled` de `OrdreBourseSeleniumFlakyIT`, puis :

```bash
for i in 1 2 3 4 5; do mvn -q verify -Dit.test=OrdreBourseSeleniumFlakyIT; done
```

On observe le rouge/vert aléatoire (sleep trop court, sélecteur fragile,
dépendance à l'ordre). C'est l'argument qui vend Playwright.

> Astuce codegen : `npx playwright codegen http://localhost:4200` génère des
> locators robustes en cliquant — utile pour migrer un scénario existant.
