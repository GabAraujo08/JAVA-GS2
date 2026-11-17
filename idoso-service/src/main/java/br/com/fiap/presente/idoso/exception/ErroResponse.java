package br.com.fiap.presente.idoso.exception;


import java.time.LocalDateTime;
import java.util.List;

public record ErroResponse(LocalDateTime timestamp, int status, String mensagem, List<String> erros) {

    public static ErroResponse of(int status, String mensagem, List<String> erros) {
        return new ErroResponse(LocalDateTime.now(), status, mensagem, erros);
    }
}
