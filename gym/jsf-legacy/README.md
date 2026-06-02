# JSF legacy — écran Valeurs Mobilières

Module legacy de démonstration buildable en war.

## Pourquoi ce module séparé

L'application Spring Boot principale est moderne (REST + JPA).
Cet écran JSF / PrimeFaces représente l'**existant legacy** des
équipes Titres et sert :

- **Atelier M7 — Migration JSF → composant Angular** (J2 PM v2)
  Le stagiaire reçoit `DossierBean.java` + `valeurs-mobilieres.xhtml`
  et doit produire le composant Angular équivalent.
- **Atelier O27 — Détection dette tech** (J3 AM v2)
  Le `DossierBean` mélange 3 responsabilités à identifier.
- **Démo cas 4 J3** — Code review JSF legacy assistée par Copilot.

## Stack legacy reproduite

- JSF 2.3 (Mojarra)
- PrimeFaces 13.x
- Java 8/11 compatible (downgrade depuis Java 17 si besoin Wildfly)
- `@ManagedBean` + `@ViewScoped` (deprecated mais représentatif)

## Build WAR

Module désormais structurellement complet :

```bash
cd jsf-legacy
mvn clean package        # produit target/poc-titres-jsf-legacy.war
```

Le war se déploie ensuite manuellement dans un Tomcat 9 ou WildFly.
**Non testé localement** : le user / formateur valide en pré-formation.

Sessions futures :
- Tomcat embedded ou Cargo plugin pour `mvn tomcat-run`
- Login fictif + filtres sécurité
- Intégration REST avec backend Spring (CORS configuré côté Spring)
- Page d'accueil multi-filière

Pour la formation, **on peut aussi se contenter de lire le code
uniquement** (démo formateur + base atelier M7) sans déployer.

## Dette technique volontaire dans DossierBean

Le bean mélange 3 responsabilités à séparer :

1. **Logique métier** (calcul frais, calcul agios) — devrait être
   dans un Service Spring
2. **Formatage UI** (NumberFormat, conversion String) — devrait
   être dans un Converter JSF ou côté template Angular
3. **Persistance directe** (JDBC ou EntityManager inline) —
   devrait être dans un Repository

Détail dans `../docs/DETTE-TECH-INTENTIONNELLE.md`.
