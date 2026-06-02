export type SensOrdre = 'ACHAT' | 'VENTE';
export type StatutOrdre = 'RECU' | 'BLOQUE' | 'EXECUTE' | 'REJETE';

export interface OrdreBourseDto {
  id: number;
  codeClient: string;
  codeIsin: string;
  sens: SensOrdre;
  quantite: number;
  coursLimite: number;
  statut: StatutOrdre;
  fraisPercus: number | null;
  agiosPercus: number | null;
  dateCreation: string;
  dateExecution: string | null;
}
