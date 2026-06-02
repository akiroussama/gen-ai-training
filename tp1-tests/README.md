# TP1 — Tests unitaires + couverture sur `OrdreBourseService`

Durée : ~35 minutes.  
Format : **solo** (chacun son environnement, sa suite de tests, sa couverture).  
Terrain : le POC fil rouge (gym), classe `OrdreBourseService` — **aucun test fourni, vous partez de zéro**.

## Objectif

Avec l'assistant (Copilot), **générer une suite de tests JUnit** complète sur
`OrdreBourseService.executerOrdre(...)`, **atteindre une cible de couverture
mesurée par JaCoCo**, et surtout **savoir repérer un test vert qui ne prouve rien**.

À la fin, vous saurez dire : *« j'ai une suite verte, une couverture chiffrée, et
chaque test échouerait si le code se cassait. »*

## Le code à tester (fourni)

`OrdreBourseService.executerOrdre(Long id)` consolide plusieurs règles métier :

- charge l'ordre (`repository.findById`) ;
- refuse un ordre **BLOQUÉ** (lève une exception) ;
- est **idempotent** sur un ordre déjà **EXÉCUTÉ** (renvoie l'état tel quel) ;
- calcule des **frais de courtage par paliers** sur le montant brut ;
- calcule des **agios** si l'ordre traîne au-delà de **30 jours** ;
- passe le statut à **EXÉCUTÉ** et persiste.

Le dépôt (`OrdreBourseRepository`) se **mocke** (Mockito) : pas de base de données.

## Les cas que votre suite doit couvrir

À vous de les écrire (l'assistant aide, mais c'est vous qui jugez). Au minimum :

1. **Nominal** : un ordre `RECU` passe `EXECUTE`, avec des frais positifs.
2. **Bloqué** : un ordre `BLOQUE` lève une exception (message explicite).
3. **Idempotent** : un ordre déjà `EXECUTE` est renvoyé tel quel (frais inchangés).
4. **Paliers de frais** (montant brut = cours × quantité) :
   - brut < 1 000 → forfait fixe ;
   - 1 000 ≤ brut < 10 000 → pourcentage A ;
   - brut ≥ 10 000 → pourcentage B, avec un **plancher**.
5. **Agios** : au-delà de 30 jours, les agios sont **non nuls** (et nuls en deçà).
6. **Identifiant inconnu** : comportement quand l'ordre n'existe pas.

> **Piège à débusquer vous-mêmes** : un des paliers contient un **plancher qui
> n'est jamais atteignable** (branche morte). JaCoCo la montrera **non couverte**
> — et c'est **normal**. Saurez-vous expliquer pourquoi aucun test ne peut la couvrir ?

## Le piège central : le test vert qui ne prouve rien

L'assistant pond **très souvent** un test parfaitement vert mais **creux** :

- un `verify(...)` sans la moindre assertion sur le résultat ;
- un `assertNotNull(...)` qui passe quoi qu'il arrive ;
- un test sans assertion du tout.

**Règle d'or** : pour chaque test généré, demandez-vous *« qu'est-ce que ce test
ferait réellement échouer ? »*. Si la réponse est **rien**, il est décoratif → **poubelle**.

## Comment mesurer (JaCoCo)

```bash
mvn test
# puis ouvrez le rapport HTML :
#   target/site/jacoco/index.html
```

Visez une **couverture de branche** élevée sur `executerOrdre` — pas un vague
« ça a l'air testé ». Un faux test gonfle le pourcentage **sans rien prouver** :
le chiffre ne vaut que si chaque test a une assertion qui **mord**.

## Les 5 réflexes (gate de fin)

Avant de considérer un test « bon » :

1. ça **compile** ?
2. les tests sont **verts** sous vos yeux ?
3. les imports / API existent vraiment (pas hallucinés) ?
4. ça respecte vos conventions (`copilot-instructions.md`) ?
5. **je comprends chaque ligne** — et chaque test échouerait si le code se cassait ?

Si non au 5 → on ne garde pas, on redemande à l'assistant d'expliquer.

## Critères de réussite

- La suite **compile** et passe au **vert**.
- **Couverture de branche cible atteinte** sur `executerOrdre` (rapport JaCoCo).
- **Chaque test a une assertion qui mord** (aucun test décoratif).
- Vous savez **nommer** la branche non couvrable (le plancher mort) et pourquoi.

## Message à retenir

L'IA accélère l'écriture des tests, mais **la couverture n'est pas la preuve**.
C'est l'**assertion qui mord** qui protège votre code en prod. Le bon réflexe :
faire générer, puis **juger** — jeter ce qui ne prouve rien.
