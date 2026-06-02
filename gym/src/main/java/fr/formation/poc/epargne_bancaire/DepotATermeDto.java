package fr.formation.poc.epargne_bancaire;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DepotATermeDto {

    private Long id;
    private String numero;
    private String codeClient;
    private BigDecimal montant;
    private Integer dureeMois;
    private BigDecimal tauxAnnuel;
    private LocalDate dateOuverture;
    private LocalDate dateEcheance;
    private StatutDat statut;

    public static DepotATermeDto from(DepotATerme e) {
        DepotATermeDto d = new DepotATermeDto();
        d.id = e.getId();
        d.numero = e.getNumero();
        d.codeClient = e.getCodeClient();
        d.montant = e.getMontant();
        d.dureeMois = e.getDureeMois();
        d.tauxAnnuel = e.getTauxAnnuel();
        d.dateOuverture = e.getDateOuverture();
        d.dateEcheance = e.getDateEcheance();
        d.statut = e.getStatut();
        return d;
    }

    public Long getId() { return id; }
    public String getNumero() { return numero; }
    public String getCodeClient() { return codeClient; }
    public BigDecimal getMontant() { return montant; }
    public Integer getDureeMois() { return dureeMois; }
    public BigDecimal getTauxAnnuel() { return tauxAnnuel; }
    public LocalDate getDateOuverture() { return dateOuverture; }
    public LocalDate getDateEcheance() { return dateEcheance; }
    public StatutDat getStatut() { return statut; }
}
