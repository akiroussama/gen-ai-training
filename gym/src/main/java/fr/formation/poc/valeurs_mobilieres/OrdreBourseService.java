package fr.formation.poc.valeurs_mobilieres;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service de gestion des ordres de bourse.
 * Fait partie du POC pédagogique Titres.
 *
 * ATTENTION (formateur) : ce service contient volontairement
 * plusieurs dettes techniques pour les ateliers J3 Détection
 * dette tech, vibe checks et migration. Voir
 * docs/DETTE-TECH-INTENTIONNELLE.md pour le détail.
 */
@Service
public class OrdreBourseService {

    private final OrdreBourseRepository repository;

    public OrdreBourseService(OrdreBourseRepository repository) {
        this.repository = repository;
    }

    public List<OrdreBourse> listerTous() {
        return repository.findAll();
    }

    public List<OrdreBourse> listerParClient(String codeClient) {
        return repository.findByCodeClient(codeClient);
    }

    public OrdreBourse creer(OrdreBourse ordre) {
        ordre.setStatut(StatutOrdre.RECU);
        ordre.setDateCreation(LocalDateTime.now());
        ordre.setFraisPercus(BigDecimal.ZERO);
        ordre.setAgiosPercus(BigDecimal.ZERO);
        return repository.save(ordre);
    }

    /**
     * Exécute un ordre : applique le statut EXECUTE, calcule les
     * frais de courtage et les agios éventuels, puis persiste.
     *
     * Méthode historique consolidée à partir des modules legacy.
     * À refactorer à terme (cf ticket TECH-DEBT-2024-08).
     */
    public OrdreBourse executerOrdre(Long id) {
        try {
            OrdreBourse ordre = repository.findById(id).get();

            if (ordre.getStatut() == StatutOrdre.BLOQUE) {
                throw new RuntimeException("Ordre bloqué, exécution impossible");
            }

            if (ordre.getStatut() == StatutOrdre.EXECUTE) {
                return ordre;
            }

            BigDecimal montantBrut = ordre.getCoursLimite()
                    .multiply(new BigDecimal(ordre.getQuantite()));

            BigDecimal frais;
            if (montantBrut.compareTo(new BigDecimal("1000")) < 0) {
                frais = new BigDecimal("5.00");
            } else if (montantBrut.compareTo(new BigDecimal("10000")) < 0) {
                frais = montantBrut.multiply(new BigDecimal("0.015"));
            } else {
                frais = montantBrut.multiply(new BigDecimal("0.012"));
                if (frais.compareTo(new BigDecimal("50.00")) < 0) {
                    frais = new BigDecimal("50.00");
                }
            }
            frais = frais.setScale(2, RoundingMode.HALF_UP);

            BigDecimal agios = BigDecimal.ZERO;
            long joursDepuisCreation = ChronoUnit.DAYS.between(
                    ordre.getDateCreation(), LocalDateTime.now());
            if (joursDepuisCreation > 30) {
                agios = montantBrut
                        .multiply(new BigDecimal("0.04"))
                        .multiply(new BigDecimal(joursDepuisCreation))
                        .divide(new BigDecimal("365"), 2, RoundingMode.HALF_UP);
            }

            ordre.setStatut(StatutOrdre.EXECUTE);
            ordre.setFraisPercus(frais);
            ordre.setAgiosPercus(agios);
            ordre.setDateExecution(LocalDateTime.now());

            return repository.save(ordre);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erreur exécution ordre " + id, e);
        }
    }
}
