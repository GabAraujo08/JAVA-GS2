package br.com.fiap.presente.idoso.dto.response;


import br.com.fiap.presente.idoso.entity.Idoso;
import br.com.fiap.presente.idoso.entity.StatusDia;

import java.time.LocalDate;
import java.time.LocalTime;

public record IdosoResponse(
        Long id,
        String nome,
        LocalDate dataNascimento,
        String telefone,
        LocalTime horarioAviso,
        Integer tempoEsperaMinutos,
        StatusDia statusDia) {

    public static IdosoResponse from(Idoso i) {
        return new IdosoResponse(i.getId(), i.getNome(), i.getDataNascimento(), i.getTelefone(),
                i.getHorarioAviso(), i.getTempoEsperaMinutos(), i.getStatusDia());
    }
}
