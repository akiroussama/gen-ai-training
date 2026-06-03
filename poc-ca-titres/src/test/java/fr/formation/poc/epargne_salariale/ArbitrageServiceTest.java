package fr.formation.poc.epargne_salariale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArbitrageServiceTest {

    @Mock
    private AvoirSalarialRepository repository;

    @InjectMocks
    private ArbitrageService service;

    private AvoirSalarial avoir;

    @BeforeEach
    void initAvoir() {
        avoir = new AvoirSalarial();
        avoir.setId(1L);
        avoir.setCodeBeneficiaire("BEN-0001");
        avoir.setIdEntreprise("ENT-0001");
        avoir.setCodeFonds("FONDS-MONETAIRE");
        avoir.setMontantInvesti(new BigDecimal("10000.00"));
        avoir.setDateValeur(LocalDate.now().minusMonths(6));
        avoir.setStatut(StatutAvoir.DISPONIBLE);
    }

    @Test
    @DisplayName("Arbitrage nominal : statut passe ARBITRAGE_EN_COURS, frais 0.5% appliqués")
    void effectuerArbitrage_quandNominal_appliqueFraisEtChangeStatut() {
        when(repository.findById(1L)).thenReturn(Optional.of(avoir));
        when(repository.save(any(AvoirSalarial.class))).thenAnswer(inv -> inv.getArgument(0));

        AvoirSalarial resultat = service.effectuerArbitrage(1L, "FONDS-ACTIONS", new BigDecimal("1000.00"));

        assertThat(resultat.getStatut()).isEqualTo(StatutAvoir.ARBITRAGE_EN_COURS);
        assertThat(resultat.getCodeFonds()).isEqualTo("FONDS-ACTIONS");
        // 10000 - 1000 - (1000 * 0.005) = 8995.00
        assertThat(resultat.getMontantInvesti()).isEqualByComparingTo(new BigDecimal("8995.00"));
    }

    @Test
    @DisplayName("Arbitrage sur avoir BLOQUE lève RuntimeException")
    void effectuerArbitrage_quandBloque_leveException() {
        avoir.setStatut(StatutAvoir.BLOQUE);
        when(repository.findById(1L)).thenReturn(Optional.of(avoir));

        assertThatThrownBy(() ->
                service.effectuerArbitrage(1L, "FONDS-ACTIONS", new BigDecimal("1000")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("bloqué");
    }

    @Test
    @DisplayName("Arbitrage en dessous du seuil 100 EUR lève RuntimeException")
    void effectuerArbitrage_quandMontantInsuffisant_leveException() {
        when(repository.findById(1L)).thenReturn(Optional.of(avoir));

        assertThatThrownBy(() ->
                service.effectuerArbitrage(1L, "FONDS-ACTIONS", new BigDecimal("50")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("100 EUR");
    }

    @Test
    @DisplayName("Arbitrage sur id inconnu lève NoSuchElementException (dette D7-bis récurrente)")
    void effectuerArbitrage_quandIdInconnu_jetteNoSuchElement() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.effectuerArbitrage(999L, "FONDS-ACTIONS", new BigDecimal("500")))
                .isInstanceOf(NoSuchElementException.class);
    }
}
