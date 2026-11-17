package br.com.fiap.presente.idoso.dto.request;


import br.com.fiap.presente.idoso.entity.StatusDia;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull StatusDia status) {
}
