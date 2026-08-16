# RPGSpace

Infraestrutura inicial da plataforma RPGSpace. Nenhuma regra de negócio ou endpoint está implementado nesta fase.

## Requisitos

- Java 21
- Docker Engine com Docker Compose v2
- PostgreSQL 18 (o Compose fornece a instância local)

## Executar localmente

1. Crie o arquivo de ambiente: `cp .env.example .env`.
2. Ajuste `DATABASE_PASSWORD` e `JWT_SECRET` no `.env`.
3. Inicie o banco: `docker compose up -d postgres`.
4. Aguarde o healthcheck: `docker compose ps`.
5. Exporte as variáveis e execute a API:

```bash
set -a
source .env
set +a
cd backend
./mvnw spring-boot:run
```

O Flyway aplica as migrações automaticamente na inicialização. A documentação OpenAPI estará em `http://localhost:8080/swagger-ui`.

## Variáveis de ambiente

| Variável | Finalidade |
| --- | --- |
| `DATABASE_URL` | URL JDBC do PostgreSQL. |
| `DATABASE_USERNAME` | Usuário da aplicação e do banco local. |
| `DATABASE_PASSWORD` | Senha do banco. |
| `JWT_SECRET` | Reservada para a futura assinatura de tokens; não é usada nesta fase. |
| `JWT_EXPIRATION` | Reservada para a futura expiração de tokens; não é usada nesta fase. |
| `SPRING_PROFILES_ACTIVE` | Perfil Spring ativo: `dev` ou `prod`. |

## Estrutura

```text
backend/src/main/java/com/rpgspace
├── config/                 # Configurações transversais do framework
├── shared/
│   ├── exception/          # Exceções e resposta global de erros
│   └── response/           # Contratos HTTP reutilizáveis
└── modules/
    └── user/
        └── domain/         # Modelo de usuário, sem casos de uso nesta fase
```

Os diretórios `shared/utils`, `shared/constants` e `shared/validation` serão adicionados quando possuírem responsabilidade concreta; não há classes-marcador ou pacotes vazios no projeto.

## Banco de dados

`docker-compose.yml` inicia apenas o PostgreSQL, com volume nomeado, healthcheck e rede isolada. A aplicação não é conteinerizada nesta fase.
