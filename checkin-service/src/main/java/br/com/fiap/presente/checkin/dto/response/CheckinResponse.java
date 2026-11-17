package br.com.fiap.presente.checkin.dto.response;


import br.com.fiap.presente.checkin.entity.Checkin;
import br.com.fiap.presente.checkin.entity.TipoCheckin;

import java.time.LocalDateTime;

public record CheckinResponse(
        Long id,
        Long idosoId,
        TipoCheckin tipo,
        LocalDateTime dataHora,
        String observacao,
        boolean resolvido) {

    public static CheckinResponse from(Checkin c) {
        return new CheckinResponse(c.getId(), c.getIdosoId(), c.getTipo(), c.getDataHora(),
                c.getObservacao(), c.isResolvido());
    }
}
