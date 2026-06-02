package fr.formation.poc.mobilite_bancaire;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "dossier_mobilite")
public class DossierMobilite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_dossier", length = 50)
    private String idDossier;

    @Column(name = "code_client", length = 50)
    private String codeClient;

    @Column(name = "rib_ancien", length = 34)
    private String ribAncien;

    @Column(name = "rib_nouveau", length = 34)
    private String ribNouveau;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatutMobilite statut;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "dossier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OperationRecurrente> operations = new ArrayList<>();

    public DossierMobilite() {
    }

    public void ajouterOperation(OperationRecurrente op) {
        op.setDossier(this);
        this.operations.add(op);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIdDossier() { return idDossier; }
    public void setIdDossier(String idDossier) { this.idDossier = idDossier; }
    public String getCodeClient() { return codeClient; }
    public void setCodeClient(String codeClient) { this.codeClient = codeClient; }
    public String getRibAncien() { return ribAncien; }
    public void setRibAncien(String ribAncien) { this.ribAncien = ribAncien; }
    public String getRibNouveau() { return ribNouveau; }
    public void setRibNouveau(String ribNouveau) { this.ribNouveau = ribNouveau; }
    public StatutMobilite getStatut() { return statut; }
    public void setStatut(StatutMobilite statut) { this.statut = statut; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public List<OperationRecurrente> getOperations() { return operations; }
    public void setOperations(List<OperationRecurrente> operations) { this.operations = operations; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DossierMobilite that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
