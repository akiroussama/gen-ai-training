# Angular front — POC Titres

Module Angular 17 standalone du POC. Sert :

- **Atelier M7 Migration JSF→Angular** (J2 PM v2) : `ListeOrdresComponent`
  est la **cible attendue** de la migration depuis `DossierBean.java` +
  `valeurs-mobilieres.xhtml` du module `jsf-legacy/`.
- **Atelier M6 Selenium e2e** (J3 PM v2) : cible UI testée par WebDriver
  (parcours « rechercher → exécuter » sur cet écran).
- **Démos formateur J3-J4** : front moderne consommant l'API Spring REST
  du backend POC.

## Stack

- Angular 17.3+ (standalone components, signals, inject API)
- TypeScript 5.4+
- HttpClient + RxJS Observable
- Tests : Jasmine + Karma (ChromeHeadless en CI)

## Pré-requis

- Node.js 18.19+ ou 20.11+
- npm 10+
- Chrome installé (pour `ng test`)

## Build et lancement

```bash
cd angular-front
npm install                # première fois (~1-2 min)

npm start                  # serveur dev :4200
                           # le proxy /api/* doit pointer sur :8080
                           # (à configurer via proxy.conf.json — session future)

npm test                   # tests Jasmine en ChromeHeadless
npm run build              # build production dans dist/
```

> **Note proxy** : par défaut Angular dev server sur :4200 ne forwarde
> pas /api/* vers le backend Spring :8080. Pour les démos, soit :
> 1. Configurer un `proxy.conf.json` côté Angular (à ajouter)
> 2. Lancer Chrome avec `--disable-web-security` (déconseillé)
> 3. Activer CORS côté Spring (dette tech D18 à corriger en atelier)
>
> Option 3 = cohérente avec l'atelier de détection dette tech.

## Structure

```
angular-front/
├── package.json
├── angular.json                config build Angular CLI
├── tsconfig*.json              TypeScript strict
├── .gitignore                  node_modules + dist
├── src/
│   ├── index.html
│   ├── main.ts                 bootstrapApplication standalone
│   ├── styles.css              theme CA (vert #006a4e)
│   └── app/
│       ├── app.component.ts    racine standalone
│       ├── app.config.ts       provideHttpClient
│       └── valeurs-mobilieres/
│           ├── ordre-bourse.model.ts        types stricts
│           ├── ordre-bourse.service.ts      HttpClient
│           ├── ordre-bourse.service.spec.ts 3 tests Jasmine
│           ├── liste-ordres.component.ts    standalone + signals
│           ├── liste-ordres.component.html
│           ├── liste-ordres.component.css
│           └── liste-ordres.component.spec.ts 3 tests
```

## Cible migration M7 JSF→Angular

| JSF legacy (DossierBean + xhtml) | Angular équivalent |
|---|---|
| `@ManagedBean @ViewScoped DossierBean` | `@Component standalone ListeOrdresComponent` |
| `@PersistenceContext EntityManager em` | `OrdreBourseService` (HttpClient REST) |
| JPQL `em.createQuery(jpql)` avec **concat SQL D27** | API REST `/api/v1/ordres-bourse?codeClient=` (paramétré) |
| `NumberFormat` instance field | `CurrencyPipe` Angular |
| `DateTimeFormatter` instance field | `DatePipe` Angular |
| Logic métier inline (calcul frais) | Déjà côté backend (Service Java) — front consomme |
| `<p:dataTable>` PrimeFaces | `<table>` HTML5 + `*ngFor` |
| `<p:inputText value=#{bean.codeClient}>` | `[(ngModel)]` |

**Trois responsabilités séparées Angular** (vs mélangées JSF) :
1. **Persistance** = `OrdreBourseService` (HttpClient REST)
2. **Métier** = côté backend Spring (déjà extrait)
3. **Formatage UI** = pipes Angular (`currency`, `date`)

## Tests (6 au total)

| Fichier | Tests |
|---|---|
| `ordre-bourse.service.spec.ts` | 3 (liste sans filtre, liste avec filtre, executer POST) |
| `liste-ordres.component.spec.ts` | 3 (création + ngOnInit, rafraichir filtré, executer + refresh) |

## Pourquoi pas de `proxy.conf.json` livré

Volontaire pour pédagogie : les stagiaires découvrent la nécessité
du proxy ou de CORS quand ils lancent `npm start` et voient les
erreurs réseau dans la console. Atelier M9 anti-amnésie : ajouter
la règle `proxy.conf.json` dans le `copilot-instructions.md` partagé.

## Dette technique future (session 4)

- Pas de routing (1 seule vue) — `provideRouter` à ajouter quand
  on aura plusieurs écrans (épargne salariale, mobilité, etc.)
- Pas de gestion d'état (NgRx ou Signals store) — accepté pour POC
- Pas de Storybook
- Pas de tests e2e Cypress / Playwright (Selenium côté backend pour POC)

## Référence

- Source de migration : `../jsf-legacy/src/main/java/.../DossierBean.java`
  + `../jsf-legacy/src/main/webapp/valeurs-mobilieres.xhtml`
- API backend : `../src/main/java/.../valeurs_mobilieres/OrdreBourseController.java`
- Plan formation : `../../plan-5-jours-v1.md` §v2 J2 PM atelier M7
