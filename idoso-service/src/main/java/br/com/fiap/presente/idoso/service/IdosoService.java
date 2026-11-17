package br.com.fiap.presente.idoso.service;


import br.com.fiap.presente.idoso.dto.request.IdosoRequest;
import br.com.fiap.presente.idoso.dto.request.StatusRequest;
import br.com.fiap.presente.idoso.dto.response.IdosoResponse;
import br.com.fiap.presente.idoso.entity.Idoso;
import br.com.fiap.presente.idoso.entity.StatusDia;
import br.com.fiap.presente.idoso.exception.RecursoNaoEncontradoException;
import br.com.fiap.presente.idoso.repository.IdosoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IdosoService {

    private final IdosoRepository repository;

    public IdosoService(IdosoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<IdosoResponse> listar() {
        return repository.findAll().stream().map(IdosoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public IdosoResponse buscarPorId(Long id) {
        return IdosoResponse.from(buscarEntidade(id));
    }

    @Transactional
    public IdosoResponse criar(IdosoRequest request) {
        Idoso idoso = new Idoso();
        copiar(request, idoso);
        idoso.setStatusDia(StatusDia.AGUARDANDO);
        return IdosoResponse.from(repository.save(idoso));
    }

    @Transactional
    public IdosoResponse atualizar(Long id, IdosoRequest request) {
        Idoso idoso = buscarEntidade(id);
        copiar(request, idoso);
        return IdosoResponse.from(repository.save(idoso));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    /** Chamado pelo checkin-service (OpenFeign) para alterar o status do dia. */
    @Transactional
    public IdosoResponse atualizarStatus(Long id, StatusRequest request) {
        Idoso idoso = buscarEntidade(id);
        idoso.setStatusDia(request.status());
        return IdosoResponse.from(repository.save(idoso));
    }

    private Idoso buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Idoso nao encontrado: " + id));
    }

    private void copiar(IdosoRequest r, Idoso i) {
        i.setNome(r.nome());
        i.setDataNascimento(r.dataNascimento());
        i.setTelefone(r.telefone());
        i.setHorarioAviso(r.horarioAviso());
        i.setTempoEsperaMinutos(r.tempoEsperaMinutos());
    }
}
