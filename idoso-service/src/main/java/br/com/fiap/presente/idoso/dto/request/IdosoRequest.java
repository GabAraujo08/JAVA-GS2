package br.com.fiap.presente.idoso.dto.request;


import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public record IdosoRequest(
        @NotBlank @Size(max = 120) String nome,
        @NotNull @Past LocalDate dataNascimento,
        @NotBlank @Size(max = 20) String telefone,
        @NotNull LocalTime horarioAviso,
        @NotNull @Min(1) Integer tempoEsperaMinutos) {
}
