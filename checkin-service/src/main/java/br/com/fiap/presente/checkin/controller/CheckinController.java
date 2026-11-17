package br.com.fiap.presente.checkin.controller;


import br.com.fiap.presente.checkin.dto.request.CheckinRequest;
import br.com.fiap.presente.checkin.dto.request.CheckinUpdateRequest;
import br.com.fiap.presente.checkin.dto.response.CheckinResponse;
import br.com.fiap.presente.checkin.service.CheckinService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/checkins")
public class CheckinController {

    private final CheckinService service;

    public CheckinController(CheckinService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CheckinResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckinResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /** Registra o check-in e atualiza o status do idoso no idoso-service (OpenFeign). */
    @PostMapping
    public ResponseEntity<CheckinResponse> criar(@RequestBody @Valid CheckinRequest request) {
        CheckinResponse criado = service.criar(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CheckinResponse> atualizar(@PathVariable Long id,
                                                     @RequestBody @Valid CheckinUpdateRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
