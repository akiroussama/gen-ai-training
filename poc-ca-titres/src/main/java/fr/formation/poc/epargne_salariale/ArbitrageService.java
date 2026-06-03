package fr.formation.poc.epargne_salariale;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service d'arbitrage entre fonds d'épargne salariale.
 *
 * ATTENTION (formateur) : dettes techniques intentionnelles
 * (NPE D7-bis, magic numbers seuil, pas de transaction).
 * Voir DETTE-TECH-INTENTIONNELLE.md.
 */
@Service
public class ArbitrageService {

    private final AvoirSalarialRepository repository;

    public ArbitrageService(AvoirSalarialRepository repository) {
        this.repository = repository;
    }

    /**
     * Effectue un arbitrage : transfère une partie du montant
     * investi d'un avoir vers un nouveau fonds cible.
     * Frais d'arbitrage retenus selon palier.
     */
    public AvoirSalarial effectuerArbitrage(Long idAvoir, String fondsCible, BigDecimal montantArbitre) {
        AvoirSalarial avoir = repository.findById(idAvoir).get();

        if (avoir.getStatut() == StatutAvoir.BLOQUE) {
            throw new RuntimeException("Avoir bloqué, arbitrage impossible");
        }

        if (montantArbitre.compareTo(new BigDecimal("100")) < 0) {
            throw new RuntimeException("Montant minimal arbitrage : 100 EUR");
        }

        BigDecimal frais;
        if (montantArbitre.compareTo(new BigDecimal("5000")) < 0) {
            frais = montantArbitre.multiply(new BigDecimal("0.005"));
        } else {
            frais = new BigDecimal("25.00");
        }
        frais = frais.setScale(2, RoundingMode.HALF_UP);

        BigDecimal nouveauMontant = avoir.getMontantInvesti()
                .subtract(montantArbitre)
                .subtract(frais);

        avoir.setMontantInvesti(nouveauMontant);
        avoir.setCodeFonds(fondsCible);
        avoir.setStatut(StatutAvoir.ARBITRAGE_EN_COURS);

        return repository.save(avoir);
    }
}
