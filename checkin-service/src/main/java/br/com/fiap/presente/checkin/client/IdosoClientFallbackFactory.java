package br.com.fiap.presente.checkin.client;


import br.com.fiap.presente.checkin.client.dto.IdosoClientResponse;
import br.com.fiap.presente.checkin.client.dto.StatusClientRequest;
import br.com.fiap.presente.checkin.exception.RecursoNaoEncontradoException;
import br.com.fiap.presente.checkin.exception.ServicoIndisponivelException;
import feign.FeignException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * Fallback do Circuit Breaker: se o idoso-service responder 404, devolve 404 ao cliente;
 * para qualquer outra falha (servico fora do ar, timeout, circuito aberto) devolve 503.
 */
@Component
public class IdosoClientFallbackFactory implements FallbackFactory<IdosoClient> {

    @Override
    public IdosoClient create(Throwable cause) {
        return new IdosoClient() {
            @Override
            public IdosoClientResponse buscarPorId(Long id) {
                throw traduzir(cause, id);
            }

            @Override
            public IdosoClientResponse atualizarStatus(Long id, StatusClientRequest request) {
                throw traduzir(cause, id);
            }
        };
    }

    private RuntimeException traduzir(Throwable cause, Long id) {
        if (cause instanceof FeignException fe && fe.status() == 404) {
            return new RecursoNaoEncontradoException("Idoso nao encontrado: " + id);
        }
        return new ServicoIndisponivelException(
                "Servico de idosos indisponivel no momento. Tente novamente em instantes.");
    }
}
