CREATE TABLE checkin (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    idoso_id   BIGINT       NOT NULL,
    tipo       VARCHAR(20)  NOT NULL,
    data_hora  DATETIME     NOT NULL,
    observacao VARCHAR(255) NULL,
    resolvido  BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
