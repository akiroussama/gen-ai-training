# -*- coding: utf-8 -*-
"""
REAL DO — Entraîner SON mini-modèle CA Titres de A à Z.

Vous complétez les TODO pour entraîner un petit réseau qui classe une demande
métier fictive dans la bonne filière, puis vous le CORRIGEZ par préférence et
vous le DÉPLOYEZ derrière un endpoint.

Règles : pas de LLM, pas de scikit-learn, pas de PyTorch, données fictives.

Ce qui est FOURNI (ne pas réécrire) : la lecture des données, le découpage,
le produit vecteur/matrice, et surtout la rétropropagation (`pas_gradient`,
`entrainer`) — le gradient est donné, concentrez-vous sur le FLUX et sur les
DEUX nouvelles étapes (préférence, déploiement).

Les 6 étapes :
  1. data  (fourni)   2. clean  (TODO)   3. vectorisation (TODO)
  4. entraînement (fourni)   5. correction par préférence (TODO)
  6. déploiement / endpoint (TODO)

Lancer : python mini_modele_ca_titres.py
"""
from __future__ import annotations

import csv
import json
import math
import random
import re
import sys
import unicodedata
from pathlib import Path

try:
    sys.stdout.reconfigure(encoding="utf-8")
except Exception:
    pass

RACINE = Path(__file__).parent
DATASET = RACINE / "demandes_ca_titres.csv"
PHRASES_WOW = RACINE / "phrases_wow.txt"

FILIERES = ["VALEURS_MOBILIERES", "EPARGNE_SALARIALE", "EPARGNE_BANCAIRE", "MOBILITE_BANCAIRE"]
STOPWORDS = {"le", "la", "les", "un", "une", "des", "du", "de", "d", "au", "aux", "a", "et",
             "ou", "sur", "pour", "par", "avec", "dans", "apres", "avant", "son", "sa", "ses",
             "en", "est", "reste"}


# === Étape 2 : CLEAN (À COMPLÉTER) ========================================
def normaliser(texte: str) -> str:
    """Retourne un texte minuscule, sans accents, apostrophes -> espaces."""
    # TODO 1 : passer en minuscules et remplacer les apostrophes par des espaces.
    # TODO 2 : retirer les accents (unicodedata.normalize "NFD" + filtrer les "Mn").
    raise NotImplementedError("TODO normaliser")


def tokeniser(texte: str) -> list[str]:
    """Découpe une phrase en tokens utiles."""
    # TODO 3 : normaliser puis extraire les mots avec re.findall(r"[a-z0-9]+", ...).
    # TODO 4 : retirer les stopwords et les mots de 1 caractère.
    raise NotImplementedError("TODO tokeniser")


# === Étape 1 : DATA (fourni) ==============================================
def charger_dataset() -> list[tuple[str, str]]:
    exemples: list[tuple[str, str]] = []
    with DATASET.open(encoding="utf-8-sig", newline="") as fichier:
        for ligne in csv.DictReader(fichier, delimiter=";"):
            exemples.append((ligne["texte"], ligne["filiere"]))
    random.Random(42).shuffle(exemples)
    return exemples


def decouper(exemples, ratio_validation: float = 0.20):
    n = int(len(exemples) * ratio_validation)
    return exemples[n:], exemples[:n]


# === Étape 3 : VECTORISATION (À COMPLÉTER) ================================
def construire_vocabulaire(exemples, taille_max: int = 180) -> dict[str, int]:
    """Construit un vocabulaire {token: index} des tokens les plus fréquents."""
    # TODO 5 : compter la fréquence de chaque token du jeu d'entraînement.
    # TODO 6 : garder les `taille_max` plus fréquents, renvoyer {token: index}.
    raise NotImplementedError("TODO construire_vocabulaire")


def vectoriser(texte: str, vocabulaire: dict[str, int]) -> list[float]:
    """Transforme une phrase en vecteur bag-of-words binaire."""
    # TODO 7 : créer un vecteur de zéros de taille len(vocabulaire).
    # TODO 8 : mettre 1.0 pour chaque token présent dans le vocabulaire.
    raise NotImplementedError("TODO vectoriser")


def relu(valeurs): return [max(0.0, v) for v in valeurs]


def softmax(logits):
    """Transforme des scores bruts en probabilités."""
    # TODO 9 : soustraire le max (stabilité), exponentielles, diviser par la somme.
    raise NotImplementedError("TODO softmax")


def init_matrice(lignes, colonnes, rng):
    return [[rng.uniform(-0.08, 0.08) for _ in range(colonnes)] for _ in range(lignes)]


def mat_vec(vecteur, matrice):
    sortie = [0.0] * len(matrice[0])
    for i, val in enumerate(vecteur):
        if val == 0.0:
            continue
        for j in range(len(sortie)):
            sortie[j] += val * matrice[i][j]
    return sortie


def ajouter_biais(valeurs, biais): return [v + biais[i] for i, v in enumerate(valeurs)]


# === Étape 4 : ENTRAÎNEMENT — rétropropagation FOURNIE (ne pas réécrire) ==
def pas_gradient(x, y: int, modele, taux: float) -> float:
    """Un pas de descente de gradient (le gradient est fourni). Renvoie la perte."""
    w1, b1, w2, b2 = modele["w1"], modele["b1"], modele["w2"], modele["b2"]
    tc, ts = len(b1), len(b2)
    z1 = ajouter_biais(mat_vec(x, w1), b1)
    h = relu(z1)
    logits = ajouter_biais(mat_vec(h, w2), b2)
    probabilites = softmax(logits)
    perte = -math.log(max(probabilites[y], 1e-12))
    dlogits = probabilites[:]
    dlogits[y] -= 1.0
    ancien_w2 = [ligne[:] for ligne in w2]
    for j in range(tc):
        for k in range(ts):
            w2[j][k] -= taux * h[j] * dlogits[k]
    for k in range(ts):
        b2[k] -= taux * dlogits[k]
    dh = [sum(ancien_w2[j][k] * dlogits[k] for k in range(ts)) for j in range(tc)]
    dz1 = [dh[j] if z1[j] > 0 else 0.0 for j in range(tc)]
    for i, val in enumerate(x):
        if val == 0.0:
            continue
        for j in range(tc):
            w1[i][j] -= taux * val * dz1[j]
    for j in range(tc):
        b1[j] -= taux * dz1[j]
    return perte


def entrainer(train, vocabulaire, epochs: int = 45, taille_cachee: int = 24, taux: float = 0.045):
    rng = random.Random(7)
    modele = {
        "w1": init_matrice(len(vocabulaire), taille_cachee, rng),
        "b1": [0.0] * taille_cachee,
        "w2": init_matrice(taille_cachee, len(FILIERES), rng),
        "b2": [0.0] * len(FILIERES),
        "vocabulaire": vocabulaire,
    }
    for epoch in range(1, epochs + 1):
        rng.shuffle(train)
        perte = sum(pas_gradient(vectoriser(t, vocabulaire), FILIERES.index(f), modele, taux)
                    for t, f in train)
        if epoch == 1 or epoch % 10 == 0:
            print(f"  epoch {epoch:02d} | perte moyenne = {perte / len(train):.4f}")
    return modele


def predire(texte: str, modele):
    x = vectoriser(texte, modele["vocabulaire"])
    z1 = ajouter_biais(mat_vec(x, modele["w1"]), modele["b1"])
    logits = ajouter_biais(mat_vec(relu(z1), modele["w2"]), modele["b2"])
    top = sorted(zip(FILIERES, softmax(logits)), key=lambda kv: kv[1], reverse=True)
    return top[0][0], top[0][1], top


def evaluer(exemples, modele) -> float:
    return sum(1 for t, attendu in exemples if predire(t, modele)[0] == attendu) / len(exemples)


# === Étape 5 : CORRECTION PAR PRÉFÉRENCE (À COMPLÉTER) ====================
# Principe de l'alignement, à l'échelle jouet : le modèle s'est trompé, on lui
# donne la bonne filière, on le réajuste. (Correction supervisée ciblée — PAS
# du RLHF.) Indice : réutilisez `pas_gradient` sur chaque correction, sur
# quelques epochs, avec un taux d'apprentissage un peu plus élevé.
def corriger_par_preference(modele, corrections, epochs: int = 12, taux: float = 0.05):
    # TODO 10 : pour `epochs` itérations, faire un `pas_gradient` sur chaque
    #           (texte, bonne_filiere) de `corrections`. Renvoyer le modèle.
    raise NotImplementedError("TODO corriger_par_preference")


# === Étape 6 : DÉPLOIEMENT — endpoint d'inférence (À COMPLÉTER) ===========
def infer(texte: str, modele) -> dict:
    """Enveloppe le modèle pour un appel 'service' : renvoie un dict JSON-able."""
    # TODO 11 : appeler predire et renvoyer {"texte", "filiere", "confiance"}.
    raise NotImplementedError("TODO infer")


def servir(modele, port: int = 8008):
    """FOURNI : sert infer() en HTTP. POST /infer {"texte": "..."}."""
    from http.server import BaseHTTPRequestHandler, HTTPServer

    class Handler(BaseHTTPRequestHandler):
        def do_POST(self):
            taille = int(self.headers.get("Content-Length", 0))
            corps = json.loads(self.rfile.read(taille) or b"{}")
            reponse = json.dumps(infer(corps.get("texte", ""), modele), ensure_ascii=False)
            self.send_response(200)
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.end_headers()
            self.wfile.write(reponse.encode("utf-8"))

        def log_message(self, *args):
            pass

    print(f"  endpoint prêt : POST http://localhost:{port}/infer")
    HTTPServer(("localhost", port), Handler).serve_forever()


def charger_phrases_wow() -> list[str]:
    return [l.strip() for l in PHRASES_WOW.read_text(encoding="utf-8").splitlines() if l.strip()]


def main():
    random.seed(42)
    print("[1] DATA")
    exemples = charger_dataset()
    train, validation = decouper(exemples)
    print(f"    {len(exemples)} demandes ({len(train)} entraînement / {len(validation)} validation)")

    print("[2-3] CLEAN + VECTORISATION")
    vocabulaire = construire_vocabulaire(train)
    print(f"    vocabulaire = {len(vocabulaire)} tokens")

    print("[4] ENTRAÎNEMENT")
    modele = entrainer(train, vocabulaire)
    print(f"    précision validation = {evaluer(validation, modele):.1%}")

    print("[5] CORRECTION PAR PRÉFÉRENCE")
    rates = [(t, att) for t, att in validation if predire(t, modele)[0] != att]
    cas, bonne = rates[0] if rates else ("transférer mon épargne salariale", "EPARGNE_SALARIALE")
    avant = predire(cas, modele)
    print(f"    cas raté : « {cas} »")
    print(f"    avant    : {avant[0]} ({avant[1]:.0%}) | attendu : {bonne}")
    corriger_par_preference(modele, [(cas, bonne)])
    apres = predire(cas, modele)
    print(f"    après    : {apres[0]} ({apres[1]:.0%})")

    print("[6] DÉPLOIEMENT (endpoint infer)")
    print("    " + json.dumps(infer("passer un ordre d'achat sur une action", modele), ensure_ascii=False))
    print("    (relancer avec --serve pour ouvrir le vrai endpoint HTTP)")
    if "--serve" in sys.argv:
        servir(modele)


if __name__ == "__main__":
    main()
