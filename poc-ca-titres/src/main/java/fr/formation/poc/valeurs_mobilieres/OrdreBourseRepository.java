package fr.formation.poc.valeurs_mobilieres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdreBourseRepository extends JpaRepository<OrdreBourse, Long> {

    List<OrdreBourse> findByCodeClient(String codeClient);

    List<OrdreBourse> findByStatut(StatutOrdre statut);

    /**
     * Recherche d'ordres par sens + statut.
     * NOTE : ancienne implementation avec concaténation SQL retirée
     * Phase 2 — utilisation de paramètres nommés JPQL.
     */
    @Query("SELECT o FROM OrdreBourse o WHERE o.sens = :sens AND o.statut = :statut")
    List<OrdreBourse> findBySensEtStatut(@Param("sens") SensOrdre sens,
                                         @Param("statut") StatutOrdre statut);
}
