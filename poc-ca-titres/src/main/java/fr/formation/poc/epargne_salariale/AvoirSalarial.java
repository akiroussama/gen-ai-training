package fr.formation.poc.epargne_salariale;

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
@Table(name = "avoir_salarial")
public class AvoirSalarial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_beneficiaire", length = 50)
    private String codeBeneficiaire;

    @Column(name = "id_entreprise", length = 50)
    private String idEntreprise;

    @Column(name = "code_fonds", length = 50)
    private String codeFonds;

    @Column(name = "montant_investi", precision = 14, scale = 4)
    private BigDecimal montantInvesti;

    @Column(name = "date_valeur")
    private LocalDate dateValeur;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private StatutAvoir statut;

    public AvoirSalarial() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodeBeneficiaire() { return codeBeneficiaire; }
    public void setCodeBeneficiaire(String codeBeneficiaire) { this.codeBeneficiaire = codeBeneficiaire; }
    public String getIdEntreprise() { return idEntreprise; }
    public void setIdEntreprise(String idEntreprise) { this.idEntreprise = idEntreprise; }
    public String getCodeFonds() { return codeFonds; }
    public void setCodeFonds(String codeFonds) { this.codeFonds = codeFonds; }
    public BigDecimal getMontantInvesti() { return montantInvesti; }
    public void setMontantInvesti(BigDecimal montantInvesti) { this.montantInvesti = montantInvesti; }
    public LocalDate getDateValeur() { return dateValeur; }
    public void setDateValeur(LocalDate dateValeur) { this.dateValeur = dateValeur; }
    public StatutAvoir getStatut() { return statut; }
    public void setStatut(StatutAvoir statut) { this.statut = statut; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AvoirSalarial that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
