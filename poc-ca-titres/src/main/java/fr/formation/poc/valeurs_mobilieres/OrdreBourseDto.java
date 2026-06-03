package fr.formation.poc.valeurs_mobilieres;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrdreBourseDto {

    private Long id;
    private String codeClient;
    private String codeIsin;
    private SensOrdre sens;
    private Integer quantite;
    private BigDecimal coursLimite;
    private StatutOrdre statut;
    private BigDecimal fraisPercus;
    private BigDecimal agiosPercus;
    private LocalDateTime dateCreation;
    private LocalDateTime dateExecution;

    public static OrdreBourseDto from(OrdreBourse entity) {
        OrdreBourseDto dto = new OrdreBourseDto();
        dto.id = entity.getId();
        dto.codeClient = entity.getCodeClient();
        dto.codeIsin = entity.getCodeIsin();
        dto.sens = entity.getSens();
        dto.quantite = entity.getQuantite();
        dto.coursLimite = entity.getCoursLimite();
        dto.statut = entity.getStatut();
        dto.fraisPercus = entity.getFraisPercus();
        dto.agiosPercus = entity.getAgiosPercus();
        dto.dateCreation = entity.getDateCreation();
        dto.dateExecution = entity.getDateExecution();
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodeClient() { return codeClient; }
    public void setCodeClient(String codeClient) { this.codeClient = codeClient; }

    public String getCodeIsin() { return codeIsin; }
    public void setCodeIsin(String codeIsin) { this.codeIsin = codeIsin; }

    public SensOrdre getSens() { return sens; }
    public void setSens(SensOrdre sens) { this.sens = sens; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }

    public BigDecimal getCoursLimite() { return coursLimite; }
    public void setCoursLimite(BigDecimal coursLimite) { this.coursLimite = coursLimite; }

    public StatutOrdre getStatut() { return statut; }
    public void setStatut(StatutOrdre statut) { this.statut = statut; }

    public BigDecimal getFraisPercus() { return fraisPercus; }
    public void setFraisPercus(BigDecimal fraisPercus) { this.fraisPercus = fraisPercus; }

    public BigDecimal getAgiosPercus() { return agiosPercus; }
    public void setAgiosPercus(BigDecimal agiosPercus) { this.agiosPercus = agiosPercus; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateExecution() { return dateExecution; }
    public void setDateExecution(LocalDateTime dateExecution) { this.dateExecution = dateExecution; }
}
