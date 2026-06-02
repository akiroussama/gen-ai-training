package fr.formation.poc.epargne_bancaire;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepotATermeRepository extends JpaRepository<DepotATerme, Long> {

    List<DepotATerme> findByCodeClient(String codeClient);

    List<DepotATerme> findByStatut(StatutDat statut);
}
