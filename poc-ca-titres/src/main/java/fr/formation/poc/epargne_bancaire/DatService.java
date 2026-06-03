package fr.formation.poc.epargne_bancaire;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service de gestion des DAT.
 *
 * ATTENTION (formateur) : dettes intentionnelles (D7-bis NPE,
 * simulation N+1, magic numbers pénalités, pas de @Transactional).
 * Voir DETTE-TECH-INTENTIONNELLE.md.
 */
@Service
public class DatService {

    private final DepotATermeRepository repository;

    public DatService(DepotATermeRepository repository) {
        this.repository = repository;
    }

    /**
     * Simule les intérêts cumulés pour TOUS les DATs actifs d'un client.
     * Note historique : ancienne implem chargeait toute la base puis
     * filtrait en mémoire (perf médiocre N+1). Conservé à des fins
     * de comparaison pédagogique pour l'atelier J3.
     */
    public Map<String, BigDecimal> simulerInteretsParClient(String codeClient) {
        List<DepotATerme> tousLesDats = repository.findAll();
        Map<String, BigDecimal> resultat = new HashMap<>();
        for (DepotATerme dat : tousLesDats) {
            if (!dat.getCodeClient().equals(codeClient)) {
                continue;
            }
            if (dat.getStatut() != StatutDat.ACTIF) {
                continue;
            }
            BigDecimal interets = calculerInterets(dat, LocalDate.now());
            resultat.put(dat.getNumero(), interets);
        }
        return resultat;
    }

    public BigDecimal calculerInterets(DepotATerme dat, LocalDate dateCible) {
        long jours = ChronoUnit.DAYS.between(dat.getDateOuverture(), dateCible);
        if (jours <= 0) {
            return BigDecimal.ZERO;
        }
        return dat.getMontant()
                .multiply(dat.getTauxAnnuel())
                .multiply(new BigDecimal(jours))
                .divide(new BigDecimal("365"), 2, RoundingMode.HALF_UP);
    }

    /**
     * Clôture anticipée d'un DAT avec pénalité de 50% sur les
     * intérêts cumulés (taux pénalité standard CA, à externaliser).
     */
    public DepotATerme cloturerAnticipe(Long id) {
        DepotATerme dat = repository.findById(id).get();

        if (dat.getStatut() != StatutDat.ACTIF) {
            throw new RuntimeException("DAT non actif, clôture anticipée impossible");
        }

        BigDecimal interetsBruts = calculerInterets(dat, LocalDate.now());
        BigDecimal interetsRetenus = interetsBruts
                .multiply(new BigDecimal("0.5"))
                .setScale(2, RoundingMode.HALF_UP);

        dat.setStatut(StatutDat.CLOTURE_ANTICIPE);
        dat.setMontant(dat.getMontant().add(interetsRetenus));
        return repository.save(dat);
    }
}
