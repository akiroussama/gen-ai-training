package fr.formation.poc.mobilite_bancaire;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/mobilite")
public class DossierMobiliteController {

    private final DossierMobiliteRepository repository;
    private final MobiliteOrchestrator orchestrator;

    public DossierMobiliteController(DossierMobiliteRepository repository,
                                     MobiliteOrchestrator orchestrator) {
        this.repository = repository;
        this.orchestrator = orchestrator;
    }

    @GetMapping
    public List<DossierMobiliteDto> lister(@RequestParam(required = false) String codeClient) {
        List<DossierMobilite> dossiers = (codeClient == null)
                ? repository.findAll()
                : repository.findByCodeClient(codeClient);
        return dossiers.stream().map(DossierMobiliteDto::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DossierMobiliteDto> detail(@PathVariable Long id) {
        Optional<DossierMobilite> d = repository.findById(id);
        return d.map(dos -> ResponseEntity.ok(DossierMobiliteDto.from(dos)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DossierMobiliteDto creer(@RequestParam String codeClient,
                                    @RequestParam String ribAncien,
                                    @RequestParam String ribNouveau) {
        return DossierMobiliteDto.from(
                orchestrator.creerDossier(codeClient, ribAncien, ribNouveau));
    }

    @PostMapping("/{id}/operations")
    public DossierMobiliteDto ajouterOperation(@PathVariable Long id,
                                               @RequestParam String libelle,
                                               @RequestParam String beneficiaire,
                                               @RequestParam BigDecimal montant,
                                               @RequestParam TypeOperation type) {
        return DossierMobiliteDto.from(
                orchestrator.ajouterOperation(id, libelle, beneficiaire, montant, type));
    }

    @PostMapping("/{id}/transferer")
    public DossierMobiliteDto lancerTransfert(@PathVariable Long id) {
        return DossierMobiliteDto.from(orchestrator.lancerTransfert(id));
    }
}
