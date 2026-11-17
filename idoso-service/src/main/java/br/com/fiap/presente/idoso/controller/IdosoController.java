package br.com.fiap.presente.idoso.controller;


import br.com.fiap.presente.idoso.dto.request.IdosoRequest;
import br.com.fiap.presente.idoso.dto.request.StatusRequest;
import br.com.fiap.presente.idoso.dto.response.IdosoResponse;
import br.com.fiap.presente.idoso.service.IdosoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/idosos")
public class IdosoController {

    private final IdosoService service;

    public IdosoController(IdosoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<IdosoResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IdosoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<IdosoResponse> criar(@RequestBody @Valid IdosoRequest request) {
        IdosoResponse criado = service.criar(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IdosoResponse> atualizar(@PathVariable Long id,
                                                   @RequestBody @Valid IdosoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    /** Endpoint consumido pelo checkin-service via OpenFeign. */
    @PutMapping("/{id}/status")
    public ResponseEntity<IdosoResponse> atualizarStatus(@PathVariable Long id,
                                                         @RequestBody @Valid StatusRequest request) {
        return ResponseEntity.ok(service.atualizarStatus(id, request));
    }
}
