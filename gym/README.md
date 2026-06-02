# POC Titres — fil rouge formation IA générative

POC pédagogique support de la formation 3 jours « IA générative pour
les développeurs » destinée aux équipes Titres.

**Statut** : POC fil rouge prêt pour le format 3 jours. Stack complète :
backend 4 filières + profils dev/prod + CORS + JSF war buildable +
Angular standalone (routing + 2 écrans + login fictif) + proxy.conf +
Selenium e2e placeholder. Les enrichissements restants sont optionnels.

## Scope

Application reproduisant le métier Titres simplifié sur 4 filières :

| Filière | Statut |
|---|---|
| **Valeurs mobilières** (ordres de bourse) | ✅ COMPLET (session 1) |
| **Épargne salariale** (avoirs + arbitrages) | ✅ COMPLET (session 2) |
| **Épargne bancaire** (DAT) | ✅ COMPLET (session 2) |
| **Mobilité bancaire** (workflow Facilit) | ✅ COMPLET (session 2) |

> **Anti-fuite** : aucune donnée client réelle. Tous codes,
> ISIN, IBAN, noms sont fictifs. Le POC sert uniquement aux
> exercices de formation et aux démos formateur.

## Stack technique

- Java 17
- Spring Boot 3.2.x (web + data-jpa + validation)
- H2 in-memory (mode PostgreSQL pour réalisme SQL)
- JUnit 5 + Mockito (tests backend)
- JSF / PrimeFaces (écran legacy, dossier séparé)
- Angular 17+ (écran moderne, session 2)
- Selenium WebDriver e2e (session 2)
- Maven 3.9+

## Build et lancement

### Backend Spring Boot

```bash
# Build + tests (surefire + failsafe)
mvn clean verify

# Lancement par défaut (profil dev)
mvn spring-boot:run

# Profil prod (placeholder, à compléter avec vraie DB en prod)
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Accès
# - API REST     : http://localhost:8080/api/v1/ordres-bourse
# - H2 console   : http://localhost:8080/h2-console  (profil dev uniquement)
#                  URL: jdbc:h2:mem:poccatitres, user: sa, pas de pwd
```

### Front Angular

```bash
cd angular-front
npm install                  # première fois ~1-2 min
npm start                    # dev server :4200 avec proxy /api → :8080
npm test                     # specs Jasmine
```

Le `proxy.conf.json` forward les requêtes `/api/*` du :4200 vers le
backend :8080 — pas besoin d'activer CORS côté navigateur en dev.
La CORS Spring (`CorsConfig.java`) est tout de même configurée pour
les cas où le front serait servi depuis une autre origine
(staging, prod).

### Connexion (démo)

L'authentification est **fictive** (atelier sécurité O27 + vibe checks).
N'importe quel identifiant + mot de passe non vide est accepté.
Voir `DETTE-TECH-INTENTIONNELLE.md` D54-D56.

## Endpoints (4 filières)

### Valeurs mobilières
| Méthode | URL | Description |
|---|---|---|
| GET    | `/api/v1/ordres-bourse`         | Liste tous les ordres |
| GET    | `/api/v1/ordres-bourse/{id}`    | Détail ordre |
| POST   | `/api/v1/ordres-bourse`         | Création ordre |
| POST   | `/api/v1/ordres-bourse/{id}/executer` | Exécution avec calcul frais + agios |

### Épargne salariale
| Méthode | URL | Description |
|---|---|---|
| GET    | `/api/v1/avoirs-salariaux`              | Liste des avoirs (filtre `?codeBeneficiaire=`) |
| GET    | `/api/v1/avoirs-salariaux/{id}`         | Détail avoir |
| POST   | `/api/v1/avoirs-salariaux/{id}/arbitrer?fondsCible=&montant=` | Arbitrage avec frais 0.5% |

### Épargne bancaire (DAT)
| Méthode | URL | Description |
|---|---|---|
| GET    | `/api/v1/dats`                                 | Liste DATs (filtre `?codeClient=`) |
| GET    | `/api/v1/dats/{id}`                            | Détail DAT |
| GET    | `/api/v1/dats/client/{codeClient}/interets`    | Simulation intérêts cumulés par DAT actif du client |
| POST   | `/api/v1/dats/{id}/cloturer-anticipe`          | Clôture anticipée avec pénalité 50 % sur intérêts |

### Mobilité bancaire (Facilit)
| Méthode | URL | Description |
|---|---|---|
| GET    | `/api/v1/mobilite`                  | Liste des dossiers |
| GET    | `/api/v1/mobilite/{id}`             | Détail dossier (+ opérations récurrentes) |
| POST   | `/api/v1/mobilite?codeClient=&ribAncien=&ribNouveau=` | Création dossier (statut INITIE) |
| POST   | `/api/v1/mobilite/{id}/operations?libelle=&beneficiaire=&montant=&type=` | Ajout opération récurrente |
| POST   | `/api/v1/mobilite/{id}/transferer`  | Lancement transfert (statut EN_COURS → TERMINE/ECHEC) |

## Dette technique intentionnelle

Le POC contient **volontairement** des dettes techniques destinées aux
ateliers J3 :
- **Atelier O27 Détection dette tech** : stagiaires identifient les
  problèmes assistés par Copilot
- **Atelier vibe checks** : reconnaissance d'anti-patterns
- **Atelier M7 Migration JSF→Angular** : l'écran JSF a 3 responsabilités
  mélangées à refactorer

Le détail des dettes intentionnelles est dans
[`docs/DETTE-TECH-INTENTIONNELLE.md`](docs/DETTE-TECH-INTENTIONNELLE.md)
(lecture formateur uniquement, à ne pas distribuer aux stagiaires).

## Arborescence

```
poc-titres/
├── pom.xml                              Maven config Spring Boot 3.2
├── README.md                            ce fichier
├── .gitignore
├── docs/
│   └── DETTE-TECH-INTENTIONNELLE.md     pour le formateur
├── ci/
│   ├── Jenkinsfile                      template Jenkins
│   └── .gitlab-ci.yml                   template GitLab CI
├── src/
│   ├── main/
│   │   ├── java/fr/formation/poc/
│   │   │   ├── PocTitresApplication.java
│   │   │   └── valeurs_mobilieres/      filière complète session 1
│   │   └── resources/
│   │       ├── application.yml
│   │       └── data.sql                 jeu fictif
│   └── test/
│       └── java/fr/formation/poc/valeurs_mobilieres/
├── jsf-legacy/                          écran JSF legacy (war séparé futur)
└── angular-front/                       placeholder session 2
```

## Référence

- PV phase 2 : `../../docs/2026-05-28-pv-phase2-objectifs.md`
- Checklist pré-formation : `../../docs/2026-05-28-checklist-pre-formation.md`

## Modules complémentaires (session 3 livrée)

### JSF legacy (`jsf-legacy/`)
Module war buildable : `cd jsf-legacy && mvn clean package`.
Stack Java 11 + JSF 2.3 + PrimeFaces 13 + `javax.*` namespace.
Déploiement manuel Tomcat 9 / WildFly. Voir `jsf-legacy/README.md`.

### Angular front (`angular-front/`)
Angular 17 standalone (~12 fichiers source) + Jasmine specs.
Build : `cd angular-front && npm install && npm test && npm start`.
Le composant `ListeOrdresComponent` est la **cible attendue de la
migration JSF→Angular** (atelier M7). Voir `angular-front/README.md`.

### Selenium e2e (`src/test/java/.../OrdreBourseSeleniumIT.java`)
Test WebDriver placeholder, **@Disabled par défaut** (nécessite
ChromeDriver + Angular sur :4200 + backend sur :8080).
Activation décrite dans la JavaDoc du test. Sert atelier M6.

## Suite (session 4)

- Profil `dev` / `prod` séparés (`application-{dev,prod}.yml`)
- `proxy.conf.json` côté Angular pour mapper `/api` → `:8080`
- CORS configuré côté Spring (correctif dette tech D18)
- Test Selenium e2e activé contre POC déployé (CI Docker)
- Authentification fictive simple (login form)
- 2e + 3e écrans Angular (épargne, mobilité)
- CI Jenkinsfile et `.gitlab-ci.yml` enrichis avec stage Angular (npm)
