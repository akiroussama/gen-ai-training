package fr.formation.poc.mobilite_bancaire;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DossierMobiliteRepository extends JpaRepository<DossierMobilite, Long> {

    Optional<DossierMobilite> findByIdDossier(String idDossier);

    List<DossierMobilite> findByCodeClient(String codeClient);
}
