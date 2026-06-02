package fr.formation.poc.mobilite_bancaire;

import java.time.LocalDateTime;
import java.util.List;

public class DossierMobiliteDto {

    private Long id;
    private String idDossier;
    private String codeClient;
    private String ribAncien;
    private String ribNouveau;
    private StatutMobilite statut;
    private LocalDateTime dateCreation;
    private List<OperationRecurrenteDto> operations;

    public static DossierMobiliteDto from(DossierMobilite d) {
        DossierMobiliteDto dto = new DossierMobiliteDto();
        dto.id = d.getId();
        dto.idDossier = d.getIdDossier();
        dto.codeClient = d.getCodeClient();
        dto.ribAncien = d.getRibAncien();
        dto.ribNouveau = d.getRibNouveau();
        dto.statut = d.getStatut();
        dto.dateCreation = d.getDateCreation();
        dto.operations = d.getOperations().stream().map(OperationRecurrenteDto::from).toList();
        return dto;
    }

    public Long getId() { return id; }
    public String getIdDossier() { return idDossier; }
    public String getCodeClient() { return codeClient; }
    public String getRibAncien() { return ribAncien; }
    public String getRibNouveau() { return ribNouveau; }
    public StatutMobilite getStatut() { return statut; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public List<OperationRecurrenteDto> getOperations() { return operations; }
}
