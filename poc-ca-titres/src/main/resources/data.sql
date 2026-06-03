-- Jeu de données fictif POC Titres
-- AUCUNE donnée réelle. Tous noms / IBAN / RIB / numéros sont inventés.

-- ============================================================
-- Filière Valeurs Mobilières
-- ============================================================

INSERT INTO ordre_bourse (id, code_client, code_isin, sens, quantite, cours_limite, statut, date_creation)
VALUES
 (1, 'CLI-0001', 'FR0000131104', 'ACHAT',  10, 65.50, 'RECU',     '2026-05-20T09:15:00'),
 (2, 'CLI-0001', 'FR0000120271', 'VENTE',   5, 175.20, 'EXECUTE', '2026-05-21T10:30:00'),
 (3, 'CLI-0002', 'FR0000125338', 'ACHAT', 100,  9.85,  'BLOQUE',  '2026-05-22T14:45:00'),
 (4, 'CLI-0003', 'FR0010220475', 'ACHAT',  20, 89.10,  'RECU',    '2026-05-23T11:00:00'),
 (5, 'CLI-0002', 'FR0000120628', 'VENTE',  50, 21.40,  'EXECUTE', '2026-05-23T16:20:00');

-- ============================================================
-- Filière Épargne Salariale (avoirs + arbitrages)
-- ============================================================

INSERT INTO avoir_salarial (id, code_beneficiaire, id_entreprise, code_fonds, montant_investi, date_valeur, statut)
VALUES
 (1, 'BEN-0001', 'ENT-FICTIVE-A', 'FONDS-MONETAIRE',  5200.00,  '2024-09-15', 'DISPONIBLE'),
 (2, 'BEN-0001', 'ENT-FICTIVE-A', 'FONDS-EQUILIBRE', 12500.50,  '2023-12-01', 'DISPONIBLE'),
 (3, 'BEN-0002', 'ENT-FICTIVE-B', 'FONDS-DYNAMIQUE', 28750.00,  '2024-03-20', 'BLOQUE'),
 (4, 'BEN-0003', 'ENT-FICTIVE-A', 'FONDS-OBLIGATAIRE', 4100.00, '2025-01-10', 'DISPONIBLE');

-- ============================================================
-- Filière Épargne Bancaire (DAT)
-- ============================================================

INSERT INTO depot_a_terme (id, numero, code_client, montant, duree_mois, taux_annuel, date_ouverture, date_echeance, statut)
VALUES
 (1, 'DAT-2024-001', 'CLI-0010', 20000.00, 24, 0.0325, '2024-05-15', '2026-05-15', 'ACTIF'),
 (2, 'DAT-2024-002', 'CLI-0010',  5000.00, 12, 0.0280, '2024-11-01', '2025-11-01', 'ACTIF'),
 (3, 'DAT-2025-001', 'CLI-0011', 50000.00, 36, 0.0350, '2025-02-20', '2028-02-20', 'ACTIF'),
 (4, 'DAT-2023-007', 'CLI-0012', 10000.00, 12, 0.0220, '2023-04-10', '2024-04-10', 'ECHU');

-- ============================================================
-- Filière Mobilité Bancaire (Facilit)
-- ============================================================

INSERT INTO dossier_mobilite (id, id_dossier, code_client, rib_ancien, rib_nouveau, statut, date_creation)
VALUES
 (1, 'MOB-AB12CD34', 'CLI-MOB-001', 'FR7630003000401234567890144', 'FR7610107001234567890123450', 'INITIE',  '2026-05-22T10:00:00'),
 (2, 'MOB-EF56GH78', 'CLI-MOB-002', 'FR7630004001501234567890175', 'FR7611315002234567890123451', 'EN_COURS','2026-05-25T09:30:00');

INSERT INTO operation_recurrente (id, dossier_id, libelle, beneficiaire, montant, type, statut_transfert)
VALUES
 (1, 1, 'EDF mensuel',          'EDF',                    85.20, 'PRELEVEMENT', 'EN_ATTENTE'),
 (2, 1, 'Salaire',              'EMPLOYEUR-FICTIF-SA',  2750.00, 'VIREMENT',    'EN_ATTENTE'),
 (3, 2, 'Abonnement telecom',   'TELECOM-FICTIF',         45.99, 'PRELEVEMENT', 'TRANSFERE'),
 (4, 2, 'Loyer',                'BAILLEUR-FICTIF-SARL', 1200.00, 'PRELEVEMENT', 'EN_ATTENTE');
