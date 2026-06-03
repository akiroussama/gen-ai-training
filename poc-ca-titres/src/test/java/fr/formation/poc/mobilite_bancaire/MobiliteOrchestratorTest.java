package fr.formation.poc.mobilite_bancaire;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MobiliteOrchestratorTest {

    @Mock
    private DossierMobiliteRepository repository;

    @InjectMocks
    private MobiliteOrchestrator orchestrator;

    @Test
    @DisplayName("creerDossier génère un idDossier MOB-* et statut INITIE")
    void creerDossier_genereIdEtStatutInitie() {
        when(repository.save(any(DossierMobilite.class))).thenAnswer(inv -> inv.getArgument(0));

        DossierMobilite dossier = orchestrator.creerDossier(
                "CLI-MOB-001", "FR7630003000401234567890144", "FR7610107001234567890123450");

        assertThat(dossier.getIdDossier()).startsWith("MOB-");
        assertThat(dossier.getStatut()).isEqualTo(StatutMobilite.INITIE);
        assertThat(dossier.getCodeClient()).isEqualTo("CLI-MOB-001");
        assertThat(dossier.getDateCreation()).isNotNull();
    }

    @Test
    @DisplayName("ajouterOperation rattache l'op au dossier et la persiste EN_ATTENTE")
    void ajouterOperation_rattache_correctement() {
        DossierMobilite dossier = nouveauDossier();
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(dossier));
        when(repository.save(any(DossierMobilite.class))).thenAnswer(inv -> inv.getArgument(0));

        DossierMobilite avecOp = orchestrator.ajouterOperation(
                1L, "EDF mensuel", "EDF", new BigDecimal("85.20"), TypeOperation.PRELEVEMENT);

        assertThat(avecOp.getOperations()).hasSize(1);
        assertThat(avecOp.getOperations().get(0).getStatutTransfert())
                .isEqualTo(StatutTransfert.EN_ATTENTE);
        assertThat(avecOp.getOperations().get(0).getDossier()).isSameAs(avecOp);
    }

    @Test
    @DisplayName("lancerTransfert passe toutes les opérations à TRANSFERE et statut dossier TERMINE")
    void lancerTransfert_quandTout_OK_dossierEstTermine() {
        DossierMobilite dossier = nouveauDossier();
        OperationRecurrente op1 = new OperationRecurrente();
        op1.setStatutTransfert(StatutTransfert.EN_ATTENTE);
        dossier.ajouterOperation(op1);
        OperationRecurrente op2 = new OperationRecurrente();
        op2.setStatutTransfert(StatutTransfert.EN_ATTENTE);
        dossier.ajouterOperation(op2);

        when(repository.findById(1L)).thenReturn(java.util.Optional.of(dossier));
        when(repository.save(any(DossierMobilite.class))).thenAnswer(inv -> inv.getArgument(0));

        DossierMobilite resultat = orchestrator.lancerTransfert(1L);

        assertThat(resultat.getStatut()).isEqualTo(StatutMobilite.TERMINE);
        assertThat(resultat.getOperations())
                .allMatch(o -> o.getStatutTransfert() == StatutTransfert.TRANSFERE);
    }

    private DossierMobilite nouveauDossier() {
        DossierMobilite d = new DossierMobilite();
        d.setId(1L);
        d.setIdDossier("MOB-ABC12345");
        d.setCodeClient("CLI-MOB-001");
        d.setRibAncien("FR7630003000401234567890144");
        d.setRibNouveau("FR7610107001234567890123450");
        d.setStatut(StatutMobilite.INITIE);
        d.setDateCreation(LocalDateTime.now());
        return d;
    }
}
