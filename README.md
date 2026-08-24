# PicPay Challenge

API REST que simula o fluxo de transferência de dinheiro entre usuários, inspirada no desafio backend do PicPay, desenvolvida em Java com Spring Boot.

## Visão geral

O PicPay Challenge é uma aplicação focada em demonstrar domínio de regras de negócio financeiras, com ênfase em:

- cadastro de usuários com validação de unicidade de e-mail e documento;
- dois tipos de usuário: comum e lojista, com regras de transação distintas;
- transferência de saldo entre usuários com validações de domínio;
- armazenamento seguro de senha com hash (BCrypt);
- respostas de erro padronizadas para cada regra violada;
- cobertura de testes unitários e de camada web para todos os cenários de negócio, incluindo os de falha.

A aplicação foi pensada como um projeto de portfólio para evidenciar raciocínio sobre regra de negócio e tratamento de exceções, mais do que uma API CRUD genérica.

## Stack tecnológica

- Java 21
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- Hibernate
- Spring Security (hash de senha com BCrypt)
- MySQL
- H2 (dependência de runtime, útil para testes locais)
- Maven
- Lombok
- MapStruct
- JUnit 5, Mockito (BDDMockito) e AssertJ

## Arquitetura

A aplicação segue uma arquitetura em camadas, com separação clara entre responsabilidades:

```text
┌─────────────────────────────┐
│         Controllers         │  Recebem e validam as requisições HTTP
├─────────────────────────────┤
│           Services          │  Contêm a lógica de negócio e as validações de domínio
├─────────────────────────────┤
│         Repositories        │  Acesso aos dados com Spring Data JPA
├─────────────────────────────┤
│       Entity / Playload     │  Modelos de persistência e DTOs de entrada/saída
├─────────────────────────────┤
│           Mapper            │  Converte entidades para DTOs e vice-versa (MapStruct)
├─────────────────────────────┤
│         MySQL / JPA         │  Persistência de usuários e transferências
└─────────────────────────────┘
```

### Estrutura do projeto

```text
src/
├── main/
│   ├── java/com/jonathan/picpay/
│   │   ├── Controllers/
│   │   │   ├── UserController.java
│   │   │   └── TransferController.java
│   │   ├── Playload/
│   │   │   ├── UserRequest.java
│   │   │   ├── UserResponse.java
│   │   │   ├── TransferRequest.java
│   │   │   └── TransferResponse.java
│   │   ├── Entity/
│   │   │   ├── User.java
│   │   │   ├── UserType.java
│   │   │   └── Transfer.java
│   │   ├── Exceptions/
│   │   │   ├── ExceptionDetail/
│   │   │   │   └── ExceptionsDetails.java
│   │   │   ├── NoBalanceException.java
│   │   │   ├── NotFoundException.java
│   │   │   ├── SelfTransactionException.java
│   │   │   ├── TypeNotSupportedForTransaction.java
│   │   │   └── UserAlreadyExistsException.java
│   │   ├── ExceptionHandler/
│   │   │   └── ExceptionsHandler.java
│   │   ├── Mappers/
│   │   │   ├── UserMapper.java
│   │   │   └── TransferMapper.java
│   │   ├── Repositories/
│   │   │   ├── UserRepository.java
│   │   │   └── TransferRepository.java
│   │   ├── Services/
│   │   │   ├── UserServices.java
│   │   │   └── TransferServices.java
│   │   ├── SecurityConfig/
│   │   │   └── SecurityConfig.java
│   │   └── PicpayApplication.java
│   └── resources/
│       └── application-example.yaml
├── test/
│   └── java/com/jonathan/picpay/
│       ├── Controllers/
│       │   ├── UserControllerTest.java
│       │   └── TransferControllerTest.java
│       ├── Services/
│       │   ├── UserServicesTest.java
│       │   └── TransferServicesTest.java
│       ├── Utils/
│       │   ├── UserRequestCreator.java
│       │   ├── UserResponseCreator.java
│       │   ├── TransferRequestCreator.java
│       │   └── TransferResponseCreator.java
│       └── PicpayApplicationTests.java
├── pom.xml
├── mvnw
└── mvnw.cmd
```

## Entidades e regras de negócio

### User

Campos da entidade:

- id
- firstName, lastName
- document (`@NotBlank`)
- email (`@Email`)
- userType (`COMMON` ou `SHOPKEEPER`)
- balance (`BigDecimal`)
- password (armazenada com hash BCrypt, nunca em texto puro)

Regras aplicadas na criação (`UserServices.create`):

- e-mail deve ser único — se já existir, lança `UserAlreadyExistsException`;
- documento (CPF/CNPJ) deve ser único — se já existir, lança `UserAlreadyExistsException`;
- a senha recebida no `UserRequest` é sempre codificada com `PasswordEncoder` (BCrypt) antes de persistir.

### UserType

Enum com dois valores, que direciona a regra de transferência:

- `COMMON` — pode enviar e receber transferências;
- `SHOPKEEPER` — só pode receber, não pode enviar.

### Transfer

Relaciona dois usuários (`userSend` e `userReceive`) e um valor (`amount`).

Regras de negócio aplicadas em `TransferServices.transfer` (método `validateTransfer`), nesta ordem:

1. **Tipo de usuário inválido para envio** — se `userSend` for `SHOPKEEPER`, lança `TypeNotSupportedForTransaction`. Lojista não pode enviar dinheiro.
2. **Transferência para si mesmo** — se `userSend.id` for igual a `userReceive.id`, lança `SelfTransactionException`.
3. **Valor inválido** — se `amount` for menor ou igual a zero, lança `NoBalanceException` (`"Balance must be Positive"`).
4. **Saldo zerado** — se o saldo do remetente for menor ou igual a zero, lança `NoBalanceException` (`"No balance in your account"`).
5. **Saldo insuficiente** — se `amount` for maior que o saldo disponível do remetente, lança `NoBalanceException` (`"No suficient balance for this transaction"`).

Se todas as validações passarem, o valor é debitado do remetente e creditado no destinatário dentro da mesma transação (`@Transactional`), e a operação só é persistida depois de todas as checagens — nenhuma escrita parcial acontece em caso de falha.

## Dependências e configuração

O projeto usa as dependências principais abaixo, definidas no `pom.xml`:

- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `spring-boot-starter-validation`
- `spring-boot-starter-webmvc-test` / `spring-boot-starter-data-jpa-test` / `spring-boot-starter-validation-test` (escopo de teste)
- `mysql-connector-j`
- `h2` (escopo runtime)
- `lombok`
- `mapstruct`
- `spring-boot-devtools`
- `spring-boot-h2console`

### Configuração de banco

O projeto não versiona `application.yaml` com credenciais reais — em vez disso, disponibiliza um arquivo de exemplo em `src/main/resources/application-example.yaml`:

```yaml
spring:
  application:
    name: picpay

  datasource:
    url: jdbc:mysql://localhost:3306/PicPayChallenge?createDatabaseIfNotExist=true
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
```

Antes de rodar a aplicação, copie esse arquivo para `application.yaml` no mesmo diretório e ajuste `username`/`password` para as suas credenciais locais do MySQL:

```bash
cp src/main/resources/application-example.yaml src/main/resources/application.yaml
```

Certifique-se de que o MySQL esteja ativo — o banco `PicPayChallenge` é criado automaticamente (`createDatabaseIfNotExist=true`).

## Como rodar a aplicação

### Pré-requisitos

- Java 21+
- Maven 3.8+
- MySQL 8+
- Git

### Passo a passo

#### 1) Clonar o repositório

```bash
git clone https://github.com/jonathanleao/transfer-picpay-challenge.git
cd transfer-picpay-challenge
```

#### 2) Configurar o banco

```bash
cp src/main/resources/application-example.yaml src/main/resources/application.yaml
```

Edite `username` e `password` no arquivo copiado com suas credenciais do MySQL local.

#### 3) Instalar dependências

```bash
./mvnw clean install
```

#### 4) Executar a aplicação

```bash
./mvnw spring-boot:run
```

A aplicação sobe em:

```text
http://localhost:8080
```

## Segurança

O projeto usa Spring Security, mas hoje limitado ao seguinte escopo:

- as senhas dos usuários são sempre armazenadas com hash `BCrypt` (`PasswordEncoder`), nunca em texto puro;
- o `SecurityFilterChain` desabilita CSRF e expõe autenticação HTTP Basic como mecanismo disponível;
- atualmente, as rotas `/users/**` e `/transfers/**` estão liberadas (`permitAll()`) — ou seja, não há autenticação obrigatória para consumir a API neste estado do projeto. Qualquer outra rota exigiria autenticação.

> Não há endpoint de login nem geração de token (JWT) implementados neste projeto — o foco atual é a camada de regra de negócio e validação, não a camada de autenticação.

## Endpoints da API

A base da API é:

```text
http://localhost:8080
```

| Método | Rota | Descrição |
|---|---|---|
| GET | `/users` | Lista todos os usuários cadastrados |
| POST | `/users` | Cria um novo usuário |
| POST | `/transfers` | Realiza uma transferência entre dois usuários |

### 1) Usuários

#### Listar usuários

```http
GET /users
```

Resposta (200 OK):

```json
[
  {
    "id": 1,
    "firstName": "Jonas",
    "lastName": "Leão",
    "email": "jonas@gmail.com",
    "userType": "COMMON",
    "balance": 100.00
  }
]
```

#### Criar usuário

```http
POST /users
Content-Type: application/json
```

Payload:

```json
{
  "firstName": "Jonas",
  "lastName": "Leão",
  "document": "12345678900",
  "email": "jonas@gmail.com",
  "userType": "COMMON",
  "balance": 100.00,
  "password": "senha123"
}
```

Resposta esperada (201 Created):

```json
{
  "id": 1,
  "firstName": "Jonas",
  "lastName": "Leão",
  "email": "jonas@gmail.com",
  "userType": "COMMON",
  "balance": 100.00
}
```

Observações:

- `userType` aceita `COMMON` ou `SHOPKEEPER`;
- `email` e `document` devem ser únicos no sistema — caso contrário, a API retorna `400 Bad Request`;
- a senha nunca retorna no corpo da resposta.

### 2) Transferências

#### Criar transferência

```http
POST /transfers
Content-Type: application/json
```

Payload:

```json
{
  "amount": 10.00,
  "userSendId": 1,
  "userReceiveId": 2
}
```

Resposta esperada (201 Created):

```json
{
  "id": 1,
  "amount": 10.00,
  "userSend": {
    "id": 1,
    "firstName": "Jonas",
    "lastName": "Leão",
    "email": "jonas@gmail.com",
    "userType": "COMMON",
    "balance": 90.00
  },
  "userReceive": {
    "id": 2,
    "firstName": "Monica",
    "lastName": "Hellen",
    "email": "monica@gmail.com",
    "userType": "COMMON",
    "balance": 110.00
  }
}
```

Regras importantes (ver seção de regras de negócio):

- remetente do tipo `SHOPKEEPER` não pode enviar transferência;
- remetente e destinatário não podem ser o mesmo usuário;
- valor deve ser positivo e não pode exceder o saldo disponível do remetente.

## Códigos de resposta e erros

A API usa respostas padronizadas por `@RestControllerAdvice` (`ExceptionsHandler`).

### Códigos mais comuns

- `200 OK` - operação de leitura bem-sucedida
- `201 Created` - recurso criado com sucesso (usuário ou transferência)
- `400 Bad Request` - dados inválidos, regra de negócio violada (saldo insuficiente, tipo de usuário não suportado, transferência para si mesmo, usuário/documento já existente)
- `404 Not Found` - usuário não encontrado

### Estruturas de erro

Exemplo de erro por saldo insuficiente:

```json
{
  "title": "No Balance Exception",
  "status": 400,
  "timestamp": "2026-08-24T23:11:40",
  "message": "No suficient balance for this transaction"
}
```

Exemplo de erro por tipo de usuário não suportado:

```json
{
  "title": "UserType Not Supported for this Transaction ",
  "status": 400,
  "timestamp": "2026-08-24T23:11:40",
  "message": "UserType Not Supported to make this Transaction"
}
```

Exemplo de erro de validação de campo (Bean Validation):

```json
{
  "email": "must be a well-formed email address"
}
```

## Testando a aplicação

O projeto tem cobertura de testes tanto na camada de serviço (regra de negócio) quanto na camada de controller (contrato HTTP).

### Testes de serviço (`Services`)

Usam JUnit 5 + Mockito (`BDDMockito`) + AssertJ, isolando o service de suas dependências:

- `TransferServicesTest` cobre: transferência bem-sucedida com atualização de saldo dos dois usuários, transferência para si mesmo, saldo zerado, saldo insuficiente e remetente do tipo lojista — garantindo em cada caso de falha que `transferRepository.save()` nunca é chamado.
- `UserServicesTest` cobre: listagem de usuários (com e sem resultados), criação bem-sucedida com senha codificada, e-mail duplicado e documento duplicado — também garantindo que `save()` não é chamado quando a regra é violada.

### Testes de controller (`Controllers`)

Usam `@WebMvcTest` + `MockMvc`, com filtros de segurança desabilitados (`@AutoConfigureMockMvc(addFilters = false)`) para isolar o contrato HTTP da camada de autenticação:

- `TransferControllerTest` cobre o retorno 201 no caminho feliz e o retorno 400 para cada exceção de negócio (`NoBalanceException`, `SelfTransactionException`, `TypeNotSupportedForTransaction`).
- `UserControllerTest` cobre a listagem, a criação bem-sucedida e o retorno 400 quando o usuário já existe.

Builders de teste (`Utils/*Creator.java`) centralizam a criação de objetos de teste (`UserRequest`, `UserResponse`, `TransferRequest`, `TransferResponse`), evitando duplicação entre as classes de teste.

### Rodando os testes

```bash
./mvnw test
```

## Swagger / OpenAPI

O projeto usa `springdoc-openapi-starter-webmvc-ui` para documentação interativa da API.

Após subir a aplicação, a documentação fica disponível em:

```text
http://localhost:8080/swagger-ui/index.html
```

A especificação em JSON fica em:

```text
http://localhost:8080/v3/api-docs
```

### Como usar

1. inicie a aplicação (`mvn spring-boot:run` ou via Docker);
2. abra `http://localhost:8080/swagger-ui/index.html` no navegador;
3. explore os endpoints disponíveis, agrupados por controller;
4. clique em **Try it out** em qualquer endpoint para montar e enviar uma requisição diretamente pela interface, sem precisar do Postman ou curl;
5. os schemas de request/response (DTOs) ficam documentados automaticamente na própria página.

### Teste rápido com curl

```bash
curl -X POST http://localhost:8080/users \
  -H 'Content-Type: application/json' \
  -d '{
    "firstName": "Jonas",
    "lastName": "Leão",
    "document": "12345678900",
    "email": "jonas@gmail.com",
    "userType": "COMMON",
    "balance": 100.00,
    "password": "senha123"
  }'
```

```bash
curl -X POST http://localhost:8080/transfers \
  -H 'Content-Type: application/json' \
  -d '{
    "amount": 10.00,
    "userSendId": 1,
    "userReceiveId": 2
  }'
```
