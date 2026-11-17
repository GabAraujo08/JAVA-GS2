CREATE TABLE idoso (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    nome                 VARCHAR(120) NOT NULL,
    data_nascimento      DATE         NOT NULL,
    telefone             VARCHAR(20)  NOT NULL,
    horario_aviso        TIME         NOT NULL,
    tempo_espera_minutos INT          NOT NULL,
    status_dia           VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
