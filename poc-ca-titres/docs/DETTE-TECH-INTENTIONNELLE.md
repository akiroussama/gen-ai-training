>>> CORRIGE - NE PAS DISTRIBUER <<<

# Dette technique intentionnelle — POC Titres

> **Lecture formateur uniquement.** Ne pas distribuer aux stagiaires.
> Ils doivent découvrir ces dettes par eux-mêmes en atelier.

Ce document liste les dettes techniques **volontairement plantées**
dans le POC pour servir les ateliers de la formation J3 :

- **Atelier O27 — Détection dette tech sur POC** (1h, nouveau v3)
- **Atelier M8 — Anti-drawback code IA défectueux** (vibe checks étendu)
- **Atelier M7 — Migration JSF → Angular** (DossierBean = base)
- **Démo cas 4 J3 — Code review JSF legacy assistée**

Chaque dette est référencée par fichier:ligne approximative + sévérité
+ correctif attendu.

---

## Backend Spring Boot (`src/main/java/.../valeurs_mobilieres/`)

### OrdreBourse.java (entité)

| # | Dette | Sévérité | Correctif attendu |
|---|---|---|---|
| D1 | JavaDoc obsolète mentionne « ancien comportement avant 2025 » | Faible | Réécrire JavaDoc reflet actuel |
| D2 | Aucune annotation `@NotNull`, `@NotBlank`, `@Size` sur champs | Moyen | Ajouter Bean Validation + intégrer Spring Validation au controller |
| D3 | Pas de `@PrePersist` pour `dateCreation` auto | Faible | Ajouter `@PrePersist` qui set `LocalDateTime.now()` |
| D4 | Méthode `setId(Long)` publique sur entité JPA | Moyen | Restreindre setId au framework (constructor ou package-private) |

### OrdreBourseRepository.java

| # | Dette | Sévérité | Correctif attendu |
|---|---|---|---|
| D5 | JavaDoc évoque ancienne concat SQL — vestige historique non nettoyé | Faible | Retirer la note ou la déplacer en CHANGELOG |
| D6 | Pas de méthode paginée pour `findAll()` (impact perf si volume) | Moyen | Ajouter `Page<OrdreBourse> findAll(Pageable p)` côté controller |

### OrdreBourseService.java — **dette la plus dense**

| # | Dette | Sévérité | Correctif attendu |
|---|---|---|---|
| D7 | `repository.findById(id).get()` sans `isPresent()` — jette `NoSuchElementException` au lieu d'exception métier | **Élevé** | Créer `OrdreNonTrouveException extends RuntimeException` + `@ResponseStatus(404)` + `orElseThrow(() -> new OrdreNonTrouveException(id))` |
| D8 | Magic numbers : `1000`, `10000`, `5.00`, `0.015`, `0.012`, `50.00`, `30`, `0.04`, `365` | **Élevé** | Extraire en `BareméFrais` ou `@ConfigurationProperties` |
| D9 | Méthode `executerOrdre()` fait 4 choses (chargement + validation + calcul frais + calcul agios + persistance) sur 70+ lignes | **Élevé** | Extraire `calculerFrais()`, `calculerAgios()`, `validerEtatExecution()` |
| D10 | `try { } catch (RuntimeException) { throw e; } catch (Exception)` — pattern catch-and-rethrow inutile | Moyen | Retirer le try/catch (laisser propager) OU créer wrapper exception métier |
| D11 | `throw new RuntimeException("...")` — exception sans typage métier | **Élevé** | Créer `OrdreBoursierException` ou hiérarchie métier |
| D12 | Aucun logging (SLF4J absent du fichier) | Moyen | Ajouter `private static final Logger log = LoggerFactory.getLogger(...)` + logs INFO/WARN aux moments clés |
| D13 | Couplage fort : Service appelle Repository en direct sans interface domaine | Faible | Considérer pattern Hexagonal (port/adapter) |
| D14 | Pas de transaction explicite (`@Transactional` absent) | Moyen | Ajouter `@Transactional` sur les méthodes mutantes |

### OrdreBourseController.java

| # | Dette | Sévérité | Correctif attendu |
|---|---|---|---|
| D15 | `@Valid` absent sur `@RequestBody` de `creer()` — pas de validation entrée | Moyen | Ajouter `@Valid` + Bean Validation sur OrdreBourse (lié D2) |
| D16 | Service ET Repository injectés — controller accède directement au repo (bypass service) | Moyen | Déplacer `findById` en `service.detail(id)` |
| D17 | Pas de gestion globale d'exceptions (`@ControllerAdvice` absent) | Moyen | Créer `GlobalExceptionHandler` mappant `OrdreNonTrouveException`→404, `IllegalArgumentException`→400, etc. |
| D18 | Pas de CORS configuré (Angular ne pourra pas appeler en local) | Élevé | Ajouter `@CrossOrigin` ou config `WebMvcConfigurer` |
| D19 | Pas de Swagger/OpenAPI exposé | Faible | Ajouter dependency `springdoc-openapi-starter-webmvc-ui` |

### Tests

| # | Dette | Sévérité | Correctif attendu |
|---|---|---|---|
| D20 | `OrdreBourseControllerIT` utilise `@SpringBootTest` (lourd) au lieu de `@WebMvcTest` ciblé | Faible | Si pas besoin du contexte complet, switcher en `@WebMvcTest(OrdreBourseController.class)` |
| D21 | Test « id inconnu jette `NoSuchElementException` » documente le bug au lieu de le corriger | Moyen | Une fois D7 corrigé, ce test doit asserter `OrdreNonTrouveException` |
| D22 | Aucun test ne vérifie le calcul agios précisément (seulement « positive ») | Moyen | Ajouter `@ParameterizedTest` avec valeurs attendues précises (BigDecimal `compareTo`, pas `equals`) |

### Configuration

| # | Dette | Sévérité | Correctif attendu |
|---|---|---|---|
| D23 | `data.sql` insère avec id explicite alors que colonne IDENTITY → conflit séquence H2 potentiel | Moyen | Retirer la colonne `id` des INSERT (autogen) OU utiliser sequence générée |
| D24 | Pas de profiles (`dev` / `prod`) — config unique | Faible | Créer `application-dev.yml` / `application-prod.yml` |
| D25 | Pas de chiffrement de secrets (pas de Vault, env vars) — H2 in-memory donc OK pour POC, mais à mentionner en transposition prod | Faible | Doc README + slide formation |

---

## JSF Legacy (`jsf-legacy/`)

### DossierBean.java — **dette mélangée intentionnelle**

| # | Dette | Sévérité | Correctif attendu |
|---|---|---|---|
| D26 | **3 responsabilités mélangées** (commenté dans le code) : persistance + métier + formatage UI | **Élevé** | Extraire `OrdreBourseService` Spring (réutiliser le backend !) + `OrdreBourseConverter` JSF |
| D27 | **SQL injection JPQL** : `codeClient` concaténé dans `jpql += "AND o.codeClient = '" + codeClient + "'"` | **Critique** | Utiliser paramètres nommés : `em.createQuery(jpql).setParameter("codeClient", codeClient)` |
| D28 | `@PostConstruct rechercher()` appelle JPA avant initialisation user inputs → comportement aléatoire | Moyen | Initialiser seulement les listes + déclencher rechercher() au clic bouton |
| D29 | `NumberFormat` instance field — pas thread-safe sous JSF @ViewScoped (OK ici mais bad practice) | Faible | Créer Converter JSF dédié, ou static ThreadLocal |
| D30 | Cast `(List<Object[]>)` unchecked sans vérif structure | Moyen | Mapper proprement via TypedQuery<OrdreBourse> |
| D31 | Inner class `DossierVue` avec champs `public` mutables | Moyen | Encapsuler avec getters/setters privés, ou utiliser record Java |
| D32 | `@ManagedBean` deprecated (JSF 2.3+) | Faible | Migrer vers CDI `@Named` + `@ViewScoped` (faces.view) |
| D33 | Aucun test (zéro test sur DossierBean) | **Élevé** | Couverture nulle, refactor risqué |

### valeurs-mobilieres.xhtml

| # | Dette | Sévérité | Correctif attendu |
|---|---|---|---|
| D34 | Pas de validation client (PrimeFaces validators absents) | Moyen | Ajouter `<f:validateLength>` + messages d'erreur |
| D35 | Bouton commandButton ne déclenche pas de feedback utilisateur (pas de growl/toast) | Faible | Ajouter `<p:growl>` après action |
| D36 | Pas d'i18n (libellés en dur français) | Faible | Externaliser dans `messages.properties` |

---

## Couverture par atelier

### Atelier O27 — Détection dette tech (J3 AM, 1h)

**Cible** : 5 dettes identifiées par binôme assisté par Copilot.

Suggestions de découpage par binôme (si 2-3 binômes) :
- Binôme A : OrdreBourseService D7, D8, D9, D11, D14 (les plus visibles)
- Binôme B : DossierBean D26, D27 (sécurité), D29, D30, D33
- Binôme C : Controller D15, D16, D17, D18, D20 (architecture)

### Atelier M7 — Migration JSF → Angular (J2 PM, 1h)

**Cible** : extraire la logique métier du DossierBean (D26) vers un
composant Angular qui consomme l'API REST `/api/v1/ordres-bourse` du
backend Spring (déjà existant).

Le formateur fournit le squelette Angular, le binôme :
1. Identifie les 3 responsabilités du DossierBean
2. Mappe responsabilité métier → appel API
3. Mappe responsabilité formatage → pipe Angular
4. Construit le composant + test Jasmine basique

### Atelier M8 — Anti-drawback code IA défectueux (J3 PM, 30 min)

**Cible** : utiliser la checklist 5 réflexes sur le code de
OrdreBourseService généré par IA. Identifier D7 (NPE), D8 (magic),
D11 (exception générique), D14 (transaction), D15 (validation).

### Démo cas 4 J3 — Code review JSF legacy assistée

**Cible formateur** : démontrer comment Copilot Chat aide à
identifier D26 (3 responsabilités) et propose un plan de refactor
en 5-8 étapes atomiques.

---

## Total dettes : 36

- **Critique** : 1 (D27 SQL injection)
- **Élevé** : 8 (D7, D8, D9, D11, D18, D26, D33)
- **Moyen** : 18
- **Faible** : 9

---

## Session 2 — Dettes dans les 3 filières ajoutées

### ArbitrageService.java (Épargne Salariale)

| # | Dette | Sévérité | Correctif attendu |
|---|---|---|---|
| D38 | NPE D7-bis : `repository.findById(idAvoir).get()` sans `isPresent()` | **Élevé** | Pattern récurrent, même correctif que D7 (`OrdreNonTrouveException` étendu en `AvoirNonTrouveException`) |
| D39 | Magic numbers : seuil min `100`, palier `5000`, frais `0.005`, plafond `25.00` | **Élevé** | Extraire `BareméArbitrage` ou `@ConfigurationProperties` |
| D40 | Pas de `@Transactional` sur `effectuerArbitrage()` qui mute l'avoir + (à terme) crée une trace d'arbitrage | Moyen | Ajouter `@Transactional` |
| D41 | Validation `montantArbitre > 0` absente — un montant 0 passe les checks | Moyen | Ajouter `@NotNull @Positive` ou check explicite |

### DatService.java (Épargne Bancaire)

| # | Dette | Sévérité | Correctif attendu |
|---|---|---|---|
| D42 | NPE D7-bis (récurrent) sur `cloturerAnticipe()` | **Élevé** | Idem D7 / D38 |
| D43 | **Simulation N+1** : `simulerInteretsParClient()` fait `findAll()` puis filtre Java sur codeClient + statut au lieu d'une requête SQL ciblée | **Élevé** | Ajouter `findByCodeClientAndStatut(codeClient, ACTIF)` dans le Repository et l'appeler |
| D44 | Magic numbers : pénalité `0.5` (50%), diviseur année `365` | Moyen | Externaliser (`PENALITE_RETRAIT_ANTICIPE`, `JOURS_PAR_AN`) |
| D45 | Note historique au-dessus de `simulerInteretsParClient` justifie le N+1 « à des fins de comparaison pédagogique » — comment dans le code de prod | Faible | Retirer le commentaire ou le déplacer dans un fichier d'historique séparé |
| D46 | Pas de calcul jour ouvré (week-end / fériés ignorés) | Faible | Implémenter avec `LocalDate.datesUntil` + filtre `DayOfWeek` ou bibliothèque jour ouvré |

### MobiliteOrchestrator.java (Mobilité Bancaire)

| # | Dette | Sévérité | Correctif attendu |
|---|---|---|---|
| D47 | NPE D7-bis × 2 : sur `ajouterOperation()` et `lancerTransfert()` | **Élevé** | Idem D7 / D38 / D42 |
| D48 | **Logging de RIB en clair** dans `creerDossier()` : `log.info(... ribAncien + " → " + ribNouveau)` — donnée sensible exposée dans les logs | **Critique** | Masquer (`****0144` au lieu du RIB complet) OU ne logger que l'id dossier OU passer en `log.debug` filtré par appender ne sortant pas en prod |
| D49 | **Exception swallowed** dans `lancerTransfert()` : `catch (Exception e) { log.error(...) }` puis continue la boucle sans propager — un transfert qui plante ne fait pas échouer le dossier (autre que via le compteur `toutesTransferees`) | **Élevé** | Soit propager `MobiliteException`, soit marquer explicitement `op.setStatutTransfert(StatutTransfert.ECHEC)` dans le catch (corrige le calcul `toutesTransferees`) |
| D50 | Pas de `@Transactional` sur `lancerTransfert()` qui mute N opérations + le dossier | **Élevé** | Ajouter `@Transactional` |
| D51 | `transfererOperation()` est un no-op (juste set statut TRANSFERE) — pas d'appel réel à un système externe | Faible (par construction POC) | Documenter que c'est un mock pour démo, ajouter retry/circuit breaker quand prod |
| D52 | `idDossier` généré par `UUID.randomUUID().substring(0,8)` — risque collision sur grand volume + non préfixé par filiale | Moyen | Utiliser un séquenceur métier (ex `MOB-2026-000001`) avec contrainte unique |
| D53 | `creerDossier()` ne valide pas le format RIB (longueur 27 FR, contrôle de clé) | Moyen | Ajouter validateur RIB côté Bean Validation (`@Pattern` ou `@RIB`) |

---

## Total dettes v2 (sessions 1+2) : 53

- **Critique** : 2 (D27 SQL injection JPQL, D48 logging RIB sensible)
- **Élevé** : 14 (D7, D8, D9, D11, D18, D26, D33, D38, D39, D42, D43, D47, D49, D50)
- **Moyen** : 26
- **Faible** : 11

### Pattern récurrent fort : NPE D7

Le pattern `repository.findById(id).get()` apparaît dans **4 services**
(OrdreBourseService, ArbitrageService, DatService, MobiliteOrchestrator
× 2). Excellent support pour l'atelier J3 « reconnaître un anti-pattern
qui se répète » + atelier M9 « copilot-instructions.md anti-amnésie »
(la règle « toujours utiliser orElseThrow » à ajouter dans le fichier
context partagé évite la re-génération du même anti-pattern par
Copilot).

---

## Session 4 — Dettes ajoutées (Angular auth + config)

### AuthService Angular (`auth/auth.service.ts`)

| # | Dette | Sévérité | Correctif attendu |
|---|---|---|---|
| D54 | **Authentification fictive boolean localStorage** : pas de validation serveur, pas de token JWT, pas d'expiration | **Critique** (mais isolé démo) | Backend `/api/auth/login` qui renvoie JWT signé + interceptor HTTP qui pose le `Authorization: Bearer ...` + refresh token |
| D55 | localStorage lisible par tout JS = vulnérable XSS | Élevé | Préférer httpOnly cookie (mais nécessite backend session) |
| D56 | `connecter()` accepte n'importe quel login/password non vide | Critique (démo) | Endpoint REST `/api/auth/login` + AuthenticationManager Spring Security |

### Configuration CORS (`common/CorsConfig.java`) — **D18 RESOLUE**

D18 « pas de CORS configuré » de la session 1 est désormais résolue.
Reste 1 mini-dette résiduelle :

| # | Dette | Sévérité | Correctif attendu |
|---|---|---|---|
| D57 | `application-prod.yml` fournit fallback `jdbc:h2:mem:` même en profil prod (via `${DB_URL:...}`) — silencieux | Élevé | Retirer le fallback : `${DB_URL}` sans valeur → fail-fast si env var absente en prod |

### Profil prod (`application-prod.yml`)

| # | Dette | Sévérité | Correctif attendu |
|---|---|---|---|
| D58 | Pas d'actuator endpoints exposés pour health checks (`/actuator/health`, `/actuator/info`) | Moyen | Ajouter `spring-boot-starter-actuator` + config exposition prudente |
| D59 | Pas de monitoring metrics (Micrometer) | Moyen | Idem actuator + Micrometer + Prometheus en prod |

### Routing Angular

| # | Dette | Sévérité | Correctif attendu |
|---|---|---|---|
| D60 | Pas de lazy loading sur les modules (tous les composants chargés au bootstrap) | Moyen | Utiliser `loadComponent` pour avoirs/dats/mobilite — gain perf significatif quand l'app grossit |

---

## Total dettes v3 (sessions 1+2+3+4) : 60

- **Critique** : 4 (D27 SQL injection JPQL, D48 logging RIB, D54 auth fictive, D56 login validation)
- **Élevé** : 16
- **Moyen** : 29
- **Faible** : 11

D18 marqué RÉSOLU (CORS configuré). Pattern récurrent NPE D7 toujours actif sur 4 services backend.

---

## Session 5 — Dettes à ajouter (planifié)

- JSF war : pas de CSRF token, session sans timeout, pas de filtre sécurité
- Angular : subscription RxJS jamais désabonnée → memory leak
- Validation form Angular côté client uniquement (server-side absent)
- Component template avec logique métier inline (`{{ a * 0.015 }}`)
- Test Selenium qui dépend de l'ordre d'exécution (état partagé)
- `Thread.sleep(2000)` au lieu de `WebDriverWait`
- Sélecteurs CSS fragiles
- Pas de rate limiting backend
- Pas d'audit trail métier

---

## Stratégie d'enrichissement (sessions suivantes)

À ajouter quand on construit les 3 autres filières (épargne salariale,
épargne bancaire, mobilité bancaire) :

- Doublonner D7 (NPE) dans chaque service → atelier reconnaissance
  pattern récurrent
- Ajouter 1 vraie dette de perf (N+1 select JPA) dans 1 filière
- Ajouter 1 dette de sécurité (logging mot de passe en clair)
  → atelier sensibilisation

Une fois Angular ajouté :
- Subscription RxJS jamais désabonnée → memory leak
- Validation form côté client uniquement (server-side absent)
- Component template avec logique métier (`{{ a * 0.015 }}`)

Une fois Selenium ajouté :
- Test e2e qui dépend de l'ordre (état partagé)
- Sleep `Thread.sleep(2000)` au lieu de `WebDriverWait`
- Sélecteurs CSS fragiles (`.btn-primary:nth-child(3)`)

---

*Fichier maintenu à jour à chaque ajout de dette intentionnelle.
Version 1 — session 1 POC — 2026-05-28.*
