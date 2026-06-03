package fr.formation.poc.epargne_bancaire;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatServiceTest {

    @Mock
    private DepotATermeRepository repository;

    @InjectMocks
    private DatService service;

    private DepotATerme datActif;

    @BeforeEach
    void initDat() {
        datActif = new DepotATerme();
        datActif.setId(1L);
        datActif.setNumero("DAT-001");
        datActif.setCodeClient("CLI-0010");
        datActif.setMontant(new BigDecimal("10000.00"));
        datActif.setDureeMois(24);
        datActif.setTauxAnnuel(new BigDecimal("0.03"));
        datActif.setDateOuverture(LocalDate.now().minusDays(365));
        datActif.setDateEcheance(LocalDate.now().plusDays(365));
        datActif.setStatut(StatutDat.ACTIF);
    }

    @Test
    @DisplayName("calculerInterets pour 1 an plein à 3% sur 10000 → 300 EUR")
    void calculerInterets_quand1An_donne300() {
        BigDecimal interets = service.calculerInterets(datActif, LocalDate.now());
        assertThat(interets).isEqualByComparingTo(new BigDecimal("300.00"));
    }

    @Test
    @DisplayName("simulerInteretsParClient filtre par client et par statut ACTIF (dette : charge tout puis filtre mémoire)")
    void simulerInteretsParClient_filtre_correctement() {
        DepotATerme autreClient = new DepotATerme();
        autreClient.setId(2L);
        autreClient.setNumero("DAT-002");
        autreClient.setCodeClient("CLI-9999");
        autreClient.setMontant(new BigDecimal("5000"));
        autreClient.setTauxAnnuel(new BigDecimal("0.02"));
        autreClient.setDateOuverture(LocalDate.now().minusDays(365));
        autreClient.setStatut(StatutDat.ACTIF);

        DepotATerme echu = new DepotATerme();
        echu.setId(3L);
        echu.setNumero("DAT-003");
        echu.setCodeClient("CLI-0010");
        echu.setMontant(new BigDecimal("8000"));
        echu.setTauxAnnuel(new BigDecimal("0.025"));
        echu.setDateOuverture(LocalDate.now().minusDays(800));
        echu.setStatut(StatutDat.ECHU);

        when(repository.findAll()).thenReturn(List.of(datActif, autreClient, echu));

        Map<String, BigDecimal> resultat = service.simulerInteretsParClient("CLI-0010");

        assertThat(resultat).containsOnlyKeys("DAT-001");
        assertThat(resultat.get("DAT-001")).isPositive();
    }

    @Test
    @DisplayName("Clôture anticipée applique 50% de pénalité sur les intérêts")
    void cloturerAnticipe_appliquePenalite50Pourcent() {
        when(repository.findById(1L)).thenReturn(Optional.of(datActif));
        when(repository.save(any(DepotATerme.class))).thenAnswer(inv -> inv.getArgument(0));

        DepotATerme cloture = service.cloturerAnticipe(1L);

        assertThat(cloture.getStatut()).isEqualTo(StatutDat.CLOTURE_ANTICIPE);
        // intérêts bruts 300, 50% retenus = 150, capital 10000 → 10150
        assertThat(cloture.getMontant()).isEqualByComparingTo(new BigDecimal("10150.00"));
    }

    @Test
    @DisplayName("Clôture anticipée sur id inconnu lève NoSuchElementException (dette D7-bis récurrente)")
    void cloturerAnticipe_quandIdInconnu_jetteNoSuchElement() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cloturerAnticipe(999L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
