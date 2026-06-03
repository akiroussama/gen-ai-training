package fr.formation.poc.valeurs_mobilieres;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdreBourseServiceTest {

    @Mock
    private OrdreBourseRepository repository;

    @InjectMocks
    private OrdreBourseService service;

    private OrdreBourse ordreRecu;

    @BeforeEach
    void initOrdreParDefaut() {
        ordreRecu = new OrdreBourse();
        ordreRecu.setId(1L);
        ordreRecu.setCodeClient("CLI-0001");
        ordreRecu.setCodeIsin("FR0000131104");
        ordreRecu.setSens(SensOrdre.ACHAT);
        ordreRecu.setQuantite(10);
        ordreRecu.setCoursLimite(new BigDecimal("100.00"));
        ordreRecu.setStatut(StatutOrdre.RECU);
        ordreRecu.setDateCreation(LocalDateTime.now().minusDays(5));
    }

    @Test
    @DisplayName("creer() positionne le statut RECU et initialise les frais à zéro")
    void creer_quandNominal_initialiseLesChampsPardefaut() {
        OrdreBourse nouveau = new OrdreBourse();
        nouveau.setCodeClient("CLI-9999");
        nouveau.setCodeIsin("FR0000999999");
        nouveau.setSens(SensOrdre.VENTE);
        nouveau.setQuantite(5);
        nouveau.setCoursLimite(new BigDecimal("42.00"));

        when(repository.save(any(OrdreBourse.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdreBourse cree = service.creer(nouveau);

        assertThat(cree.getStatut()).isEqualTo(StatutOrdre.RECU);
        assertThat(cree.getDateCreation()).isNotNull();
        assertThat(cree.getFraisPercus()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(cree.getAgiosPercus()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("executerOrdre() nominal passe statut à EXECUTE et calcule les frais")
    void executerOrdre_quandRecu_passeEnExecuteAvecFrais() {
        when(repository.findById(1L)).thenReturn(Optional.of(ordreRecu));
        when(repository.save(any(OrdreBourse.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdreBourse execute = service.executerOrdre(1L);

        assertThat(execute.getStatut()).isEqualTo(StatutOrdre.EXECUTE);
        assertThat(execute.getDateExecution()).isNotNull();
        assertThat(execute.getFraisPercus()).isPositive();
    }

    @Test
    @DisplayName("executerOrdre() sur ordre BLOQUE lève RuntimeException")
    void executerOrdre_quandBloque_leveException() {
        ordreRecu.setStatut(StatutOrdre.BLOQUE);
        when(repository.findById(1L)).thenReturn(Optional.of(ordreRecu));

        assertThatThrownBy(() -> service.executerOrdre(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("bloqué");
    }

    @Test
    @DisplayName("executerOrdre() sur ordre déjà EXECUTE est idempotent et conserve l'état")
    void executerOrdre_quandDejaExecute_estIdempotent() {
        ordreRecu.setStatut(StatutOrdre.EXECUTE);
        ordreRecu.setFraisPercus(new BigDecimal("12.50"));
        when(repository.findById(1L)).thenReturn(Optional.of(ordreRecu));

        OrdreBourse retour = service.executerOrdre(1L);

        assertThat(retour.getStatut()).isEqualTo(StatutOrdre.EXECUTE);
        assertThat(retour.getFraisPercus()).isEqualByComparingTo(new BigDecimal("12.50"));
    }

    @ParameterizedTest(name = "frais({0} x {1}) = {2}")
    @CsvSource({
            // quantite, cours, frais attendus
            "10, 50.00, 5.00",      // 500 < 1000 → forfait 5
            "20, 100.00, 30.00",    // 2000 dans [1000,10000[ → 1.5%
            "50, 300.00, 180.00"    // 15000 > 10000 → 1.2% (180), supérieur au plancher 50
    })
    void executerOrdre_calculFrais_respecteLesPaliers(int quantite, String cours, String fraisAttendus) {
        ordreRecu.setQuantite(quantite);
        ordreRecu.setCoursLimite(new BigDecimal(cours));
        when(repository.findById(1L)).thenReturn(Optional.of(ordreRecu));
        when(repository.save(any(OrdreBourse.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdreBourse execute = service.executerOrdre(1L);

        assertThat(execute.getFraisPercus())
                .isEqualByComparingTo(new BigDecimal(fraisAttendus));
    }

    @Test
    @DisplayName("executerOrdre() après 30 jours calcule des agios non nuls")
    void executerOrdre_quandPlusDe30JoursDeRetard_calculeAgios() {
        ordreRecu.setDateCreation(LocalDateTime.now().minusDays(60));
        when(repository.findById(1L)).thenReturn(Optional.of(ordreRecu));
        when(repository.save(any(OrdreBourse.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdreBourse execute = service.executerOrdre(1L);

        assertThat(execute.getAgiosPercus()).isPositive();
    }

    /**
     * Ce test documente le COMPORTEMENT ACTUEL (dette technique) :
     * quand l'ordre n'existe pas, le service jette NoSuchElementException
     * via Optional.get() au lieu d'une exception métier propre.
     * À transformer en atelier J3 Détection dette tech.
     */
    @Test
    @DisplayName("executerOrdre() sur id inconnu lève NoSuchElementException (dette tech)")
    void executerOrdre_quandIdInconnu_jetteNoSuchElement() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.executerOrdre(999L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
