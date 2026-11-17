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

## Roteiro de teste (Insomnia)

Todas as requisições passam pelo **Gateway** (porta fixa 8082), então não é preciso saber a porta dos serviços. No Insomnia, crie uma requisição, escolha o método, cole a URL e, quando houver Body, selecione **JSON** e cole o conteúdo. Execute na ordem.

## Idosos (idoso-service)

### 1. Listar idosos

**GET**

```
http://localhost:8082/idoso-service/api/v1/idosos
```

Esperado: 200 com os 3 idosos da carga inicial

### 2. Buscar idoso por id

**GET**

```
http://localhost:8082/idoso-service/api/v1/idosos/1
```

Esperado: 200, `statusDia` = `AGUARDANDO`

### 3. Criar idoso

**POST**

```
http://localhost:8082/idoso-service/api/v1/idosos
```

Body (JSON):

```json
{
  "nome": "Antonio Ferreira",
  "dataNascimento": "1948-05-20",
  "telefone": "(11) 95555-4444",
  "horarioAviso": "09:30:00",
  "tempoEsperaMinutos": 60
}
```

Esperado: 201 com o header `Location` e `statusDia` = `AGUARDANDO` (anote o `id`, será o 4)

### 4. Atualizar idoso

**PUT**

```
http://localhost:8082/idoso-service/api/v1/idosos/4
```

Body (JSON):

```json
{
  "nome": "Antonio Ferreira Lima",
  "dataNascimento": "1948-05-20",
  "telefone": "(11) 95555-4444",
  "horarioAviso": "10:00:00",
  "tempoEsperaMinutos": 90
}
```

Esperado: 200 com os dados atualizados

### 5. Excluir idoso

**DELETE**

```
http://localhost:8082/idoso-service/api/v1/idosos/4
```

Esperado: 204 sem corpo

### 6. Idoso inexistente

**GET**

```
http://localhost:8082/idoso-service/api/v1/idosos/999
```

Esperado: 404

### 7. Validação (corpo inválido)

**POST**

```
http://localhost:8082/idoso-service/api/v1/idosos
```

Body (JSON):

```json
{
  "nome": "",
  "dataNascimento": "2999-01-01",
  "telefone": "",
  "horarioAviso": "09:00:00",
  "tempoEsperaMinutos": 0
}
```

Esperado: 400 com a lista de erros por campo

## Check-ins e comunicação síncrona (checkin-service)

### 8. Listar check-ins

**GET**

```
http://localhost:8082/checkin-service/api/v1/checkins
```

Esperado: 200 com os 3 check-ins da carga inicial

### 9. Buscar check-in por id

**GET**

```
http://localhost:8082/checkin-service/api/v1/checkins/1
```

Esperado: 200

### 10. Check-in "Estou bem" (altera o status via Feign)

**POST**

```
http://localhost:8082/checkin-service/api/v1/checkins
```

Body (JSON):

```json
{
  "idosoId": 1,
  "tipo": "ESTOU_BEM",
  "observacao": "Tudo certo"
}
```

Esperado: 201 com o header `Location`

### 11. Conferir que o status do idoso 1 mudou

**GET**

```
http://localhost:8082/idoso-service/api/v1/idosos/1
```

Esperado: 200, `statusDia` = `BEM`

### 12. Check-in "Preciso de ajuda" (altera o status via Feign)

**POST**

```
http://localhost:8082/checkin-service/api/v1/checkins
```

Body (JSON):

```json
{
  "idosoId": 3,
  "tipo": "PRECISO_AJUDA",
  "observacao": "Caí no banheiro"
}
```

Esperado: 201 com o header `Location`

### 13. Conferir que o status do idoso 3 mudou

**GET**

```
http://localhost:8082/idoso-service/api/v1/idosos/3
```

Esperado: 200, `statusDia` = `PEDIU_AJUDA`

### 14. Check-in de idoso inexistente

**POST**

```
http://localhost:8082/checkin-service/api/v1/checkins
```

Body (JSON):

```json
{
  "idosoId": 999,
  "tipo": "ESTOU_BEM"
}
```

Esperado: 404, nada é gravado

### 15. Atualizar check-in

**PUT**

```
http://localhost:8082/checkin-service/api/v1/checkins/1
```

Body (JSON):

```json
{
  "observacao": "Conferido pela família",
  "resolvido": true
}
```

Esperado: 200

### 16. Excluir check-in

**DELETE**

```
http://localhost:8082/checkin-service/api/v1/checkins/1
```

Esperado: 204 sem corpo

## Circuit Breaker + Fallback

Pare o `idoso-service` (Ctrl+C no terminal dele) e repita a requisição **10**. O checkin-service responde **503** com mensagem amigável (fallback) e nada é gravado. Ao subir o idoso-service novamente (e aguardar o registro no Eureka), a requisição volta a funcionar.

## Dados iniciais (Flyway)

- `idoso`: 3 idosos (ids 1, 2 e 3)
- `checkin`: 3 check-ins de exemplo

## Estrutura do repositório

```
eureka-server/   api-gateway/   idoso-service/   checkin-service/   docker-compose.yml   README.md
```

Cada microsserviço segue as camadas: `controller`, `service`, `repository`, `entity`, `dto.request`, `dto.response`, `exception`.
