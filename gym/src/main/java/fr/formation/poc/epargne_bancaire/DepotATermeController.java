package fr.formation.poc.epargne_bancaire;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/dats")
public class DepotATermeController {

    private final DepotATermeRepository repository;
    private final DatService datService;

    public DepotATermeController(DepotATermeRepository repository, DatService datService) {
        this.repository = repository;
        this.datService = datService;
    }

    @GetMapping
    public List<DepotATermeDto> lister(@RequestParam(required = false) String codeClient) {
        List<DepotATerme> dats = (codeClient == null)
                ? repository.findAll()
                : repository.findByCodeClient(codeClient);
        return dats.stream().map(DepotATermeDto::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepotATermeDto> detail(@PathVariable Long id) {
        Optional<DepotATerme> d = repository.findById(id);
        return d.map(dat -> ResponseEntity.ok(DepotATermeDto.from(dat)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{codeClient}/interets")
    public Map<String, BigDecimal> simulerInteretsParClient(@PathVariable String codeClient) {
        return datService.simulerInteretsParClient(codeClient);
    }

    @PostMapping("/{id}/cloturer-anticipe")
    public DepotATermeDto cloturerAnticipe(@PathVariable Long id) {
        return DepotATermeDto.from(datService.cloturerAnticipe(id));
    }
}
