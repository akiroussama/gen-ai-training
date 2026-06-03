# AGENTS.md — POC Titres (le « README pour agents IA »)

> **Standard ouvert** (agents.md / agentskills.io) lu nativement par **GitHub Copilot,
> Claude Code, Cursor, Codex…** C'est la **source canonique** des conventions de ce
> dépôt : tout assistant IA lit ce fichier avant d'agir. Le
> `.github/copilot-instructions.md` en reprend l'essentiel pour Copilot — garder les
> deux alignés (ou faire pointer l'un vers l'autre).

## Le projet
POC pédagogique « Titres » — **fictif, zéro donnée réelle**. 4 filières : valeurs
mobilières (ordres de bourse), épargne salariale, épargne bancaire (DAT), mobilité bancaire.
Stack : **Java 17 · Spring Boot 3.2.5** (`jakarta.*`) · **JSF 2.3** (écran legacy) ·
**Angular 17** (front) · H2 · Maven.

## Build / test / run
- Backend : `mvn clean verify` (surefire + failsafe + JaCoCo). Unitaires : `mvn test`.
  Couverture : ouvrir `target/site/jacoco/index.html`.
- Lancer : `mvn spring-boot:run` (profil dev, port 8080 ; console H2 sur `/h2-console`).
- Front : `cd angular-front && npm install && npm test && npm start` (port 4200, proxy `/api`).
- e2e Playwright : `cd e2e && npm ci && npx playwright test` (objectif : 5/5 vert).

## Conventions (règles dures)
- **Argent = `BigDecimal`**, JAMAIS `double`/`float`. Arrondis `setScale(2, RoundingMode.HALF_UP)`.
- **Accès données** : `findById(id).orElseThrow(() -> new XxxIntrouvableException(id))`, JAMAIS `.get()`.
- **Pas de concaténation** dans une requête (injection SQL) : requêtes paramétrées / dérivées Spring Data.
- **Injection par constructeur** (pas `@Autowired` sur champ). `@Transactional` sur les méthodes de service qui mutent puis persistent.
- **Logs SLF4J** ; JAMAIS de donnée sensible en clair (RIB, IBAN, identifiant client).
- **Noms métier en français** (`executerOrdre`, `calculerAgios`).

## Tests
- JUnit 5 + Mockito + AssertJ. Un test = un comportement ; nom = comportement ; `@DisplayName` en français.
- **Assertion qui mord** : valeur exacte ou exception attendue
  (`assertThatThrownBy(...).isInstanceOf(...).hasMessageContaining("...")`). Montants : `isEqualByComparingTo`, jamais `isEqualTo`.
- Dépôt mocké (`@Mock` / `@InjectMocks`), pas de vraie base en unitaire.
- **INTERDIT** : `assertNotNull` seul, `verify(...)` sans assertion sur le résultat, **supprimer/affaiblir un test** pour le faire passer.

## Dettes intentionnelles
Ce dépôt pédagogique contient des dettes techniques **volontaires**, à
**découvrir et corriger pendant les ateliers**. Elles ne sont **pas listées
ici** : ce serait livrer la réponse. Les conventions ci-dessus définissent le
code *propre* ; tout écart constaté dans le code est une dette candidate.

## Garde-fous agent
- `git commit` **avant** toute boucle agentique ; un commit vert par étape de refactor.
- Ne JAMAIS supprimer ni affaiblir un test pour verdir. **Relire le diff des tests en premier.**
- Refactor = **comportement inchangé** (tests verts à l'identique).
