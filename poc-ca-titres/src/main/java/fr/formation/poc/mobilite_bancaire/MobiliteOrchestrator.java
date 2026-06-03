package fr.formation.poc.mobilite_bancaire;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Orchestrateur de mobilité bancaire (équivalent Facilit CA).
 *
 * ATTENTION (formateur) : dettes intentionnelles :
 *  - Logging RIB en clair (donnée sensible)
 *  - Exception générique swallowed dans transferer()
 *  - Pas de @Transactional sur lancerTransfert() (atomicité absente)
 *  - Pas de retry / circuit breaker
 * Voir DETTE-TECH-INTENTIONNELLE.md.
 */
@Service
public class MobiliteOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(MobiliteOrchestrator.class);

    private final DossierMobiliteRepository repository;

    public MobiliteOrchestrator(DossierMobiliteRepository repository) {
        this.repository = repository;
    }

    public DossierMobilite creerDossier(String codeClient, String ribAncien, String ribNouveau) {
        DossierMobilite dossier = new DossierMobilite();
        dossier.setIdDossier("MOB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        dossier.setCodeClient(codeClient);
        dossier.setRibAncien(ribAncien);
        dossier.setRibNouveau(ribNouveau);
        dossier.setStatut(StatutMobilite.INITIE);
        dossier.setDateCreation(LocalDateTime.now());

        log.info("Création dossier mobilité {} client {} : ancien RIB {} → nouveau RIB {}",
                dossier.getIdDossier(), codeClient, ribAncien, ribNouveau);

        return repository.save(dossier);
    }

    public DossierMobilite ajouterOperation(Long idDossier, String libelle, String beneficiaire,
                                            java.math.BigDecimal montant, TypeOperation type) {
        DossierMobilite dossier = repository.findById(idDossier).get();

        OperationRecurrente op = new OperationRecurrente();
        op.setLibelle(libelle);
        op.setBeneficiaire(beneficiaire);
        op.setMontant(montant);
        op.setType(type);
        op.setStatutTransfert(StatutTransfert.EN_ATTENTE);

        dossier.ajouterOperation(op);
        return repository.save(dossier);
    }

    public DossierMobilite lancerTransfert(Long idDossier) {
        DossierMobilite dossier = repository.findById(idDossier).get();
        dossier.setStatut(StatutMobilite.EN_COURS);

        for (OperationRecurrente op : dossier.getOperations()) {
            try {
                transfererOperation(op, dossier);
            } catch (Exception e) {
                log.error("Erreur transfert operation {}", op.getId(), e);
            }
        }

        boolean toutesTransferees = dossier.getOperations().stream()
                .allMatch(o -> o.getStatutTransfert() == StatutTransfert.TRANSFERE);
        dossier.setStatut(toutesTransferees ? StatutMobilite.TERMINE : StatutMobilite.ECHEC);

        return repository.save(dossier);
    }

    private void transfererOperation(OperationRecurrente op, DossierMobilite dossier) {
        log.debug("Transfert op {} type {} de {} vers {}",
                op.getId(), op.getType(), dossier.getRibAncien(), dossier.getRibNouveau());

        op.setStatutTransfert(StatutTransfert.TRANSFERE);
    }
}
