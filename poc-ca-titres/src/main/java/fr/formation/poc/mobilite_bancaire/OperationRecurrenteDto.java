package fr.formation.poc.mobilite_bancaire;

import java.math.BigDecimal;

public class OperationRecurrenteDto {

    private Long id;
    private String libelle;
    private String beneficiaire;
    private BigDecimal montant;
    private TypeOperation type;
    private StatutTransfert statutTransfert;

    public static OperationRecurrenteDto from(OperationRecurrente o) {
        OperationRecurrenteDto dto = new OperationRecurrenteDto();
        dto.id = o.getId();
        dto.libelle = o.getLibelle();
        dto.beneficiaire = o.getBeneficiaire();
        dto.montant = o.getMontant();
        dto.type = o.getType();
        dto.statutTransfert = o.getStatutTransfert();
        return dto;
    }

    public Long getId() { return id; }
    public String getLibelle() { return libelle; }
    public String getBeneficiaire() { return beneficiaire; }
    public BigDecimal getMontant() { return montant; }
    public TypeOperation getType() { return type; }
    public StatutTransfert getStatutTransfert() { return statutTransfert; }
}
