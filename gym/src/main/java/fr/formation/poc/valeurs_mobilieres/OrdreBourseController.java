package fr.formation.poc.valeurs_mobilieres;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/ordres-bourse")
public class OrdreBourseController {

    private final OrdreBourseService service;
    private final OrdreBourseRepository repository;

    public OrdreBourseController(OrdreBourseService service, OrdreBourseRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @GetMapping
    public List<OrdreBourseDto> lister(@RequestParam(required = false) String codeClient) {
        List<OrdreBourse> ordres = (codeClient == null)
                ? service.listerTous()
                : service.listerParClient(codeClient);
        return ordres.stream().map(OrdreBourseDto::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdreBourseDto> detail(@PathVariable Long id) {
        Optional<OrdreBourse> ordre = repository.findById(id);
        return ordre.map(o -> ResponseEntity.ok(OrdreBourseDto.from(o)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    public OrdreBourseDto creer(@RequestBody OrdreBourse ordre) {
        return OrdreBourseDto.from(service.creer(ordre));
    }

    @PostMapping("/{id}/executer")
    public OrdreBourseDto executer(@PathVariable Long id) {
        return OrdreBourseDto.from(service.executerOrdre(id));
    }
}
