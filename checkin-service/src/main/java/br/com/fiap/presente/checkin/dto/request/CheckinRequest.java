package br.com.fiap.presente.checkin.dto.request;


import br.com.fiap.presente.checkin.entity.TipoCheckin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CheckinRequest(
        @NotNull Long idosoId,
        @NotNull TipoCheckin tipo,
        @Size(max = 255) String observacao) {
}
