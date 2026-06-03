package fr.formation.poc.mobilite_bancaire;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "operation_recurrente")
public class OperationRecurrente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dossier_id")
    private DossierMobilite dossier;

    @Column(length = 200)
    private String libelle;

    @Column(length = 200)
    private String beneficiaire;

    @Column(precision = 12, scale = 2)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TypeOperation type;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_transfert", length = 20)
    private StatutTransfert statutTransfert;

    public OperationRecurrente() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public DossierMobilite getDossier() { return dossier; }
    public void setDossier(DossierMobilite dossier) { this.dossier = dossier; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getBeneficiaire() { return beneficiaire; }
    public void setBeneficiaire(String beneficiaire) { this.beneficiaire = beneficiaire; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public TypeOperation getType() { return type; }
    public void setType(TypeOperation type) { this.type = type; }
    public StatutTransfert getStatutTransfert() { return statutTransfert; }
    public void setStatutTransfert(StatutTransfert statutTransfert) { this.statutTransfert = statutTransfert; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OperationRecurrente that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
