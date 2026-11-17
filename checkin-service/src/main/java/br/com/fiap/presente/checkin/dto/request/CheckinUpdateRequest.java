package br.com.fiap.presente.checkin.dto.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CheckinUpdateRequest(
        @Size(max = 255) String observacao,
        @NotNull Boolean resolvido) {
}
