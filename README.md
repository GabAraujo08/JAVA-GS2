# PRESENTE – Global Solution FIAP 2026.2 (Microsserviços)

> "Um toque por dia. Uma família em paz."

## Integrantes

| Nome completo                  | RM     |
|--------------------------------|--------|
| Gabriel Araujo da Silva Brito  | 558802 |
| Arthur Brugnerotto Cerqueira   | 558836 |
| Giovana Santos Silva           | 558873 |

## Problema

O Censo 2022 do IBGE contou mais de 5,6 milhões de idosos morando sozinhos no Brasil. Se um deles passa mal ou cai, pode ficar horas ou dias sem ajuda, e a família vive com a dúvida "será que está tudo bem?". As soluções existentes costumam ser caras ou complicadas demais. (ODS 3, 10 e 11)

## Solução

Todo dia o idoso toca em um botão "Estou bem" (ou "Preciso de ajuda"). Este repositório contém o backend em microsserviços do PRESENTE:

- **idoso-service**: cadastro dos idosos (nome, nascimento, telefone, horário do aviso, tempo de espera) e o **status do dia** (`BEM`, `AGUARDANDO`, `SEM_RESPOSTA`, `PEDIU_AJUDA`).
- **checkin-service**: registra os check-ins/pedidos de ajuda. Ao criar um check-in, chama o **idoso-service por OpenFeign** (síncrono) para **alterar o status do idoso**: `ESTOU_BEM` → `BEM` e `PRECISO_AJUDA` → `PEDIU_AJUDA`.

## Arquitetura

```
                     ┌───────────────┐
 cliente ──────────► │  api-gateway  │ :8082
                     └───────┬───────┘
                             │ (descobre via Eureka)
        ┌────────────────────┴──────────────────┐
        ▼                                       ▼
┌────────────────┐   OpenFeign + LoadBalancer  ┌────────────────┐
│ checkin-service│ ──────────────────────────► │ idoso-service  │
│ (porta aleatória)  Circuit Breaker + Fallback│ (porta aleatória)
└───────┬────────┘                             └───────┬────────┘
        ▼                                              ▼
  MySQL: presente_checkin                       MySQL: presente_idoso
        └──────────── Eureka Server :8761 ─────────────┘
```

| Módulo           | Porta   | Tecnologia                                   |
|------------------|---------|----------------------------------------------|
| eureka-server    | 8761    | Spring Cloud Netflix Eureka                  |
| api-gateway      | 8082    | Spring Cloud Gateway                         |
| idoso-service    | aleatória | Java 25, Spring Boot 4.0.9, JPA, Flyway, MySQL |
| checkin-service  | aleatória | Java 25, Spring Boot 4.0.9, JPA, Flyway, MySQL, OpenFeign, Resilience4j |

Cada microsserviço usa **banco próprio** (`presente_idoso` e `presente_checkin`), sem compartilhar tabelas. Os bancos e as tabelas são criados pelo Flyway (migrations `V1` = estrutura, `V2` = carga inicial).

## Pré-requisitos

- Java 25
- Docker (com Docker Compose)
- Maven não precisa estar instalado: use o `mvnw` de cada módulo (`mvnw.cmd` no Windows)

## Como executar (ambiente limpo)

Todos os comandos a partir da raiz do repositório. Use um terminal para cada item.

**1. MySQL (Docker)** – porta 3306 precisa estar livre:

```bash
docker compose up -d
```

Aguarde ficar `healthy` (`docker compose ps`). Usuário `root`, senha `root`. Os bancos são criados automaticamente pelos microsserviços.

**2. Eureka Server (8761):**

```bash
cd eureka-server
./mvnw spring-boot:run
```

**3. API Gateway (8082):**

```bash
cd api-gateway
./mvnw spring-boot:run
```

**4. idoso-service (porta aleatória):**

```bash
cd idoso-service
./mvnw spring-boot:run
```

**5. checkin-service (porta aleatória):**

```bash
cd checkin-service
./mvnw spring-boot:run
```

**6. (Opcional) Segunda instância de um serviço**, para ver o Load Balancer: rode novamente o comando do passo 4 ou 5 em outro terminal. Como a porta é aleatória, não há conflito. (No IntelliJ: *Allow multiple instances*.)

Confira em http://localhost:8761 que `IDOSO-SERVICE`, `CHECKIN-SERVICE` e `API-GATEWAY` aparecem registrados. A porta real de cada instância aparece ali e no log (`Tomcat started on port ...`).

## Swagger / OpenAPI

Cada serviço expõe o Swagger UI na sua porta aleatória:

```
http://localhost:<porta-do-servico>/swagger-ui/index.html
```

(use a porta mostrada no dashboard do Eureka ou no log).

## Endpoints (versão 1)

Pelo Gateway: `http://localhost:8082/<nome-do-servico>/api/v1/...`

**idoso-service** – `/api/v1/idosos`

| Método | Rota                     | Descrição                     | Retorno |
|--------|--------------------------|-------------------------------|---------|
| GET    | `/api/v1/idosos`         | Lista                         | 200 |
| GET    | `/api/v1/idosos/{id}`    | Busca por id                  | 200 / 404 |
| POST   | `/api/v1/idosos`         | Cria (status inicial `AGUARDANDO`) | 201 + Location / 400 |
| PUT    | `/api/v1/idosos/{id}`    | Atualiza                      | 200 / 400 / 404 |
| DELETE | `/api/v1/idosos/{id}`    | Exclui                        | 204 / 404 |
| PUT    | `/api/v1/idosos/{id}/status` | Altera status (usado pelo checkin-service via Feign) | 200 / 400 / 404 |

**checkin-service** – `/api/v1/checkins`

| Método | Rota                      | Descrição                     | Retorno |
|--------|---------------------------|-------------------------------|---------|
| GET    | `/api/v1/checkins`        | Lista                         | 200 |
| GET    | `/api/v1/checkins/{id}`   | Busca por id                  | 200 / 404 |
| POST   | `/api/v1/checkins`        | Registra check-in **e altera o status do idoso via Feign** | 201 + Location / 400 / 404 / 503 |
| PUT    | `/api/v1/checkins/{id}`   | Atualiza observação/resolvido | 200 / 400 / 404 |
| DELETE | `/api/v1/checkins/{id}`   | Exclui                        | 204 / 404 |

## Roteiro de teste rápido

Troque `8082` pela porta do Gateway se necessário (padrão 8082).

```bash
# 1) Idoso 1 começa AGUARDANDO
curl http://localhost:8082/idoso-service/api/v1/idosos/1

# 2) Check-in "Estou bem" -> checkin-service chama o idoso-service via Feign
curl -i -X POST http://localhost:8082/checkin-service/api/v1/checkins \
  -H "Content-Type: application/json" \
  -d '{"idosoId":1,"tipo":"ESTOU_BEM","observacao":"Tudo certo"}'

# 3) Status do idoso 1 agora é BEM
curl http://localhost:8082/idoso-service/api/v1/idosos/1

# 4) Pedido de ajuda -> status vira PEDIU_AJUDA
curl -X POST http://localhost:8082/checkin-service/api/v1/checkins \
  -H "Content-Type: application/json" \
  -d '{"idosoId":3,"tipo":"PRECISO_AJUDA","observacao":"Caí no banheiro"}'

# 5) Idoso inexistente -> 404
curl -i -X POST http://localhost:8082/checkin-service/api/v1/checkins \
  -H "Content-Type: application/json" \
  -d '{"idosoId":999,"tipo":"ESTOU_BEM"}'
```

**Testando Circuit Breaker + Fallback:** pare o `idoso-service` e repita o passo 2. O checkin-service responde **503** com mensagem amigável (fallback) em vez de erro genérico, e nada é gravado.

## Dados iniciais (Flyway)

- `idoso`: 3 idosos (ids 1, 2 e 3)
- `checkin`: 3 check-ins de exemplo

## Estrutura do repositório

```
eureka-server/   api-gateway/   idoso-service/   checkin-service/   docker-compose.yml   README.md
```

Cada microsserviço segue as camadas: `controller`, `service`, `repository`, `entity`, `dto.request`, `dto.response`, `exception`.
