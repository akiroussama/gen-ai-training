export type StatutAvoir = 'DISPONIBLE' | 'BLOQUE' | 'ARBITRAGE_EN_COURS' | 'CLOTURE';

export interface AvoirSalarialDto {
  id: number;
  codeBeneficiaire: string;
  idEntreprise: string;
  codeFonds: string;
  montantInvesti: number;
  dateValeur: string;
  statut: StatutAvoir;
}
