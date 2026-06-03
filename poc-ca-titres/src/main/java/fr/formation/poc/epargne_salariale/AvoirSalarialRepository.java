package fr.formation.poc.epargne_salariale;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvoirSalarialRepository extends JpaRepository<AvoirSalarial, Long> {

    List<AvoirSalarial> findByCodeBeneficiaire(String codeBeneficiaire);

    List<AvoirSalarial> findByIdEntreprise(String idEntreprise);
}
