package fr.formation.poc.epargne_salariale;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/avoirs-salariaux")
public class AvoirSalarialController {

    private final AvoirSalarialRepository repository;
    private final ArbitrageService arbitrageService;

    public AvoirSalarialController(AvoirSalarialRepository repository,
                                   ArbitrageService arbitrageService) {
        this.repository = repository;
        this.arbitrageService = arbitrageService;
    }

    @GetMapping
    public List<AvoirSalarialDto> lister(@RequestParam(required = false) String codeBeneficiaire) {
        List<AvoirSalarial> avoirs = (codeBeneficiaire == null)
                ? repository.findAll()
                : repository.findByCodeBeneficiaire(codeBeneficiaire);
        return avoirs.stream().map(AvoirSalarialDto::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvoirSalarialDto> detail(@PathVariable Long id) {
        Optional<AvoirSalarial> a = repository.findById(id);
        return a.map(av -> ResponseEntity.ok(AvoirSalarialDto.from(av)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/arbitrer")
    public AvoirSalarialDto arbitrer(@PathVariable Long id,
                                     @RequestParam String fondsCible,
                                     @RequestParam BigDecimal montant) {
        return AvoirSalarialDto.from(
                arbitrageService.effectuerArbitrage(id, fondsCible, montant));
    }
}
