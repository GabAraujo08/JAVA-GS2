package br.com.fiap.presente.checkin.service;


import br.com.fiap.presente.checkin.client.IdosoClient;
import br.com.fiap.presente.checkin.client.dto.StatusClientRequest;
import br.com.fiap.presente.checkin.dto.request.CheckinRequest;
import br.com.fiap.presente.checkin.dto.request.CheckinUpdateRequest;
import br.com.fiap.presente.checkin.dto.response.CheckinResponse;
import br.com.fiap.presente.checkin.entity.Checkin;
import br.com.fiap.presente.checkin.entity.TipoCheckin;
import br.com.fiap.presente.checkin.exception.RecursoNaoEncontradoException;
import br.com.fiap.presente.checkin.repository.CheckinRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckinService {

    private final CheckinRepository repository;
    private final IdosoClient idosoClient;

    public CheckinService(CheckinRepository repository, IdosoClient idosoClient) {
        this.repository = repository;
        this.idosoClient = idosoClient;
    }

    @Transactional(readOnly = true)
    public List<CheckinResponse> listar() {
        return repository.findAll().stream().map(CheckinResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CheckinResponse buscarPorId(Long id) {
        return CheckinResponse.from(buscarEntidade(id));
    }

    /**
     * Registra o check-in e, por comunicacao sincrona (OpenFeign), altera o status do dia do idoso
     * no idoso-service: ESTOU_BEM -> BEM e PRECISO_AJUDA -> PEDIU_AJUDA.
     * As chamadas remotas acontecem antes de gravar, entao se falharem nada e salvo.
     */
    @Transactional
    public CheckinResponse criar(CheckinRequest request) {
        idosoClient.buscarPorId(request.idosoId());

        String novoStatus = request.tipo() == TipoCheckin.ESTOU_BEM ? "BEM" : "PEDIU_AJUDA";
        idosoClient.atualizarStatus(request.idosoId(), new StatusClientRequest(novoStatus));

        Checkin checkin = new Checkin();
        checkin.setIdosoId(request.idosoId());
        checkin.setTipo(request.tipo());
        checkin.setObservacao(request.observacao());
        checkin.setDataHora(LocalDateTime.now());
        checkin.setResolvido(request.tipo() == TipoCheckin.ESTOU_BEM);
        return CheckinResponse.from(repository.save(checkin));
    }

    @Transactional
    public CheckinResponse atualizar(Long id, CheckinUpdateRequest request) {
        Checkin checkin = buscarEntidade(id);
        checkin.setObservacao(request.observacao());
        checkin.setResolvido(request.resolvido());
        return CheckinResponse.from(repository.save(checkin));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Checkin buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Check-in nao encontrado: " + id));
    }
}
