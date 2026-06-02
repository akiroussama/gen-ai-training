# REAL DO — Entraîner SON mini-modèle CA Titres de A à Z

Durée : ~80 minutes.  
Format : **solo** (chacun son environnement, son modèle, son score).  
Outil : Python standard uniquement, sans LLM, sans scikit-learn, sans PyTorch.

## Le défi

Vous allez entraîner un petit modèle qui **classe une demande métier CA Titres
vers la bonne filière**, puis vous allez le **corriger** quand il se trompe, et
le **déployer** derrière un endpoint que vous appelez.

| Demande fictive | Filière attendue |
|---|---|
| « L'ordre d'achat sur ISIN FR0000120271 reste en attente » | Valeurs mobilières |
| « Un salarié veut arbitrer ses avoirs PEE vers un fonds monétaire » | Épargne salariale |
| « Le client demande une clôture anticipée de son dépôt à terme » | Épargne bancaire |
| « Les prélèvements récurrents doivent suivre le nouveau RIB » | Mobilité bancaire |

L'objectif n'est pas de fabriquer un LLM, ni de coder la rétropropagation à la
main (elle est **fournie**). L'objectif est de voir, de vos mains, **les 6 étapes
réelles** qui transforment des phrases en un service qui décide :

`data -> clean -> vectorisation -> entraînement -> correction par préférence -> déploiement`

## Pourquoi c'est intéressant

À la fin, vous pourrez dire : **j'ai entraîné un vrai modèle, je l'ai corrigé
sur ses erreurs, et je l'ai déployé.** Petit, mais complet :

- un jeu de données fictif et étiqueté ;
- une vectorisation bag-of-words ;
- un mini-réseau à une couche cachée + softmax ;
- une boucle d'entraînement (rétropropagation **fournie**) ;
- une **correction par préférence** (le principe de l'alignement, à l'échelle
  jouet — ce n'est **pas** du RLHF, c'est une correction supervisée ciblée) ;
- un **endpoint d'inférence** `infer(texte) -> filière`.

## Règle de confidentialité

Ne remplacez jamais les exemples par des tickets, logs, noms de clients, écrans
ou données Crédit Agricole réels. Le dataset fourni est fictif et suffisant.

## Ce qui est FOURNI vs ce que VOUS complétez

| Fourni (ne pas réécrire) | À compléter (les `TODO`) |
|---|---|
| lecture des données, découpage train/validation | `normaliser`, `tokeniser` (étape clean) |
| produit vecteur/matrice, ReLU | `construire_vocabulaire`, `vectoriser` (étape vecto) |
| **`pas_gradient` (la rétropropagation)**, `entrainer`, `predire`, `evaluer` | `softmax` |
| `servir` (le serveur HTTP de l'endpoint) | `corriger_par_preference` (étape 5) |
|  | `infer` (étape 6) |

## Déroulé (~80 min, solo)

### 0-5 — Lancer le starter
```bash
cd clients/credit-agricole-ia-gen/formation-3j-socle-comundi/ateliers/01-panorama-modeles/exercice/starter
python mini_modele_ca_titres.py
```
Le script s'arrête sur le premier `TODO`. C'est normal : vous allez le compléter.

### 5-10 — Lire les données
Ouvrez `demandes_ca_titres.csv` : repérez les 4 filières, les mots métier
récurrents, les cas ambigus.

### 10-25 — Clean + vectorisation
Complétez `normaliser`, `tokeniser`, `construire_vocabulaire`, `vectoriser`.
But : transformer une phrase en vecteur de nombres (le modèle ne voit pas le
français, il voit des positions).

### 25-35 — Lancer l'entraînement
Complétez `softmax`, puis **relancez**. La rétropropagation (`pas_gradient`,
`entrainer`) est fournie : regardez la **perte descendre** et la **précision de
validation** s'afficher. > 80 % = réussi, > 90 % = le moment de sourire.

### 35-50 — Étape 5 : correction par préférence
Le script affiche un **cas raté** (mauvaise filière, faible confiance).
Complétez `corriger_par_preference` (indice : réutilisez `pas_gradient` sur la
correction, quelques epochs). Relancez : le cas passe de **faux** à **corrigé**
sous vos yeux — c'est le *principe* de l'alignement, en miniature.

### 50-65 — Étape 6 : déploiement (endpoint)
Complétez `infer` (envelopper `predire` en dict JSON-able). Relancez : vos
appels `infer(...)` renvoient une filière. Bonus : `python mini_modele_ca_titres.py --serve`
ouvre un vrai endpoint, à appeler depuis un autre terminal :
```bash
curl -X POST http://localhost:8008/infer -d '{"texte":"passer un ordre d achat"}'
```

### 65-75 — Effet wow
Ajoutez vos phrases fictives dans `phrases_wow.txt`, testez l'endpoint sur des
formulations nouvelles. Notez une phrase qui marche, une qui trompe le modèle.

### 75-80 — Débrief
1. Qu'est-ce que le modèle a réellement appris ?
2. La correction par préférence, est-ce du RLHF ? (non — pourquoi ?)
3. Qu'est-ce qu'un LLM ajoute en plus de ce mini-modèle ?

## Critères de réussite
- Le script se lance sans erreur une fois les `TODO` complétés.
- Précision de validation > 80 %.
- La correction par préférence **fait changer** une prédiction ratée.
- L'endpoint `infer` (ou `--serve`) renvoie une filière sur une phrase nouvelle.

## Message à retenir
Un modèle IA n'est pas magique : il apprend des régularités dans des exemples,
on le corrige sur ses erreurs, on le déploie comme un service. Les grands
modèles utilisent la même intuition — avec beaucoup plus de données, de
paramètres, d'architecture, de calcul, et de garde-fous.
