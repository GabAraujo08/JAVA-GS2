package br.com.fiap.presente.checkin.client;


import br.com.fiap.presente.checkin.client.dto.IdosoClientResponse;
import br.com.fiap.presente.checkin.client.dto.StatusClientRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Cliente OpenFeign do idoso-service. O nome "idoso-service" e resolvido pelo Eureka
 * e balanceado pelo LoadBalancer (sem URL fixa).
 */
@FeignClient(name = "idoso-service", path = "/api/v1/idosos", fallbackFactory = IdosoClientFallbackFactory.class)
public interface IdosoClient {

    @GetMapping("/{id}")
    IdosoClientResponse buscarPorId(@PathVariable("id") Long id);

    @PutMapping("/{id}/status")
    IdosoClientResponse atualizarStatus(@PathVariable("id") Long id, @RequestBody StatusClientRequest request);
}
