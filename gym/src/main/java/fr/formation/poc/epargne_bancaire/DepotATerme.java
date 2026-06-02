package fr.formation.poc.epargne_bancaire;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "depot_a_terme")
public class DepotATerme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String numero;

    @Column(name = "code_client", length = 50)
    private String codeClient;

    @Column(precision = 14, scale = 2)
    private BigDecimal montant;

    @Column(name = "duree_mois")
    private Integer dureeMois;

    @Column(name = "taux_annuel", precision = 6, scale = 4)
    private BigDecimal tauxAnnuel;

    @Column(name = "date_ouverture")
    private LocalDate dateOuverture;

    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatutDat statut;

    public DepotATerme() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getCodeClient() { return codeClient; }
    public void setCodeClient(String codeClient) { this.codeClient = codeClient; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public Integer getDureeMois() { return dureeMois; }
    public void setDureeMois(Integer dureeMois) { this.dureeMois = dureeMois; }
    public BigDecimal getTauxAnnuel() { return tauxAnnuel; }
    public void setTauxAnnuel(BigDecimal tauxAnnuel) { this.tauxAnnuel = tauxAnnuel; }
    public LocalDate getDateOuverture() { return dateOuverture; }
    public void setDateOuverture(LocalDate dateOuverture) { this.dateOuverture = dateOuverture; }
    public LocalDate getDateEcheance() { return dateEcheance; }
    public void setDateEcheance(LocalDate dateEcheance) { this.dateEcheance = dateEcheance; }
    public StatutDat getStatut() { return statut; }
    public void setStatut(StatutDat statut) { this.statut = statut; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DepotATerme that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
