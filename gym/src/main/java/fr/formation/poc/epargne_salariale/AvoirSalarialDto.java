package fr.formation.poc.epargne_salariale;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AvoirSalarialDto {

    private Long id;
    private String codeBeneficiaire;
    private String idEntreprise;
    private String codeFonds;
    private BigDecimal montantInvesti;
    private LocalDate dateValeur;
    private StatutAvoir statut;

    public static AvoirSalarialDto from(AvoirSalarial e) {
        AvoirSalarialDto d = new AvoirSalarialDto();
        d.id = e.getId();
        d.codeBeneficiaire = e.getCodeBeneficiaire();
        d.idEntreprise = e.getIdEntreprise();
        d.codeFonds = e.getCodeFonds();
        d.montantInvesti = e.getMontantInvesti();
        d.dateValeur = e.getDateValeur();
        d.statut = e.getStatut();
        return d;
    }

    public Long getId() { return id; }
    public String getCodeBeneficiaire() { return codeBeneficiaire; }
    public String getIdEntreprise() { return idEntreprise; }
    public String getCodeFonds() { return codeFonds; }
    public BigDecimal getMontantInvesti() { return montantInvesti; }
    public LocalDate getDateValeur() { return dateValeur; }
    public StatutAvoir getStatut() { return statut; }
}
