package fr.formation.poc.valeurs_mobilieres;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Ordre de bourse passé par un client Titres.
 * Note : ancien comportement (avant 2025) utilisait isin sur 11 caractères.
 * Désormais ISIN normalisé sur 12 caractères selon ISO 6166.
 */
@Entity
@Table(name = "ordre_bourse")
public class OrdreBourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_client", length = 50)
    private String codeClient;

    @Column(name = "code_isin", length = 12)
    private String codeIsin;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private SensOrdre sens;

    private Integer quantite;

    @Column(name = "cours_limite", precision = 12, scale = 4)
    private BigDecimal coursLimite;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatutOrdre statut;

    @Column(name = "frais_percus", precision = 12, scale = 4)
    private BigDecimal fraisPercus;

    @Column(name = "agios_percus", precision = 12, scale = 4)
    private BigDecimal agiosPercus;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_execution")
    private LocalDateTime dateExecution;

    public OrdreBourse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeClient() {
        return codeClient;
    }

    public void setCodeClient(String codeClient) {
        this.codeClient = codeClient;
    }

    public String getCodeIsin() {
        return codeIsin;
    }

    public void setCodeIsin(String codeIsin) {
        this.codeIsin = codeIsin;
    }

    public SensOrdre getSens() {
        return sens;
    }

    public void setSens(SensOrdre sens) {
        this.sens = sens;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getCoursLimite() {
        return coursLimite;
    }

    public void setCoursLimite(BigDecimal coursLimite) {
        this.coursLimite = coursLimite;
    }

    public StatutOrdre getStatut() {
        return statut;
    }

    public void setStatut(StatutOrdre statut) {
        this.statut = statut;
    }

    public BigDecimal getFraisPercus() {
        return fraisPercus;
    }

    public void setFraisPercus(BigDecimal fraisPercus) {
        this.fraisPercus = fraisPercus;
    }

    public BigDecimal getAgiosPercus() {
        return agiosPercus;
    }

    public void setAgiosPercus(BigDecimal agiosPercus) {
        this.agiosPercus = agiosPercus;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateExecution() {
        return dateExecution;
    }

    public void setDateExecution(LocalDateTime dateExecution) {
        this.dateExecution = dateExecution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrdreBourse that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
