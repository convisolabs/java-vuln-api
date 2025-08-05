# API Vulnerável em Java

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)

Este projeto foi criado com vulnerabilidades para uso em laboratório como back-end e rodar localmente

## Instalação
* VSCode ou outra IDE que rode Java
* Java 17
* Dependências existentes no pom.xml
* Postman ou Insomnia (ou ferramenta similar) - Para interagir com a API

## Instalação

1. Clone o repositório:

```bash
git clone https://github.com/convisolabs/java-vuln-api.git
```

2. Start a API executando o arquivo JavaVulnAppForumApplication.java
> A API pode ser acessada em http://localhost:8080

3. Endpoints da API
A API prover os seguintes endpoints:

```markdown
POST /api/register - Criar novo usuário

POST /api/login - Logar na aplicação.

GET /api/allusers - Consultador todos os usuários

GET /api/userid/{id} - Consultar usuário por ID

GET /api/updatepass - Atualizar senha do usuário

GET /api/deluser/{id} - Deletar usuário por ID

POST /api/sendfile - Salvar arquivo

GET /api/name - Ler conteúdo do arquivo

## Endpoints OAuth Vulneráveis

GET /oauth/authorize - Autorização OAuth (vulnerável a client secret exposto)

POST /oauth/token - Troca de código por token (vulnerável a reutilização de código)

GET /oauth/userinfo - Informações do usuário (vulnerável a validação inadequada)

POST /oauth/jwt - Geração de JWT a partir do OAuth (vulnerável a usuário fake)

GET /oauth/callback - Callback OAuth (vulnerável a validação inadequada)
```
> Os endpoints disponibilizados pela API podem ser acessados utilizando-se a collection existente em /java-vuln-api-forum/tree/main/src/main/resources

## Autenticação
A API utiliza Spring Security para controlar a autenticação, conforme as permissões a seguir:

```
USER -> Permissão padrão para usuários.
ADMIN -> Permissão de admin para usuários administradores.
```

## Banco de Dados
O projeto utiliza Sqlite3 como solução de banco de dados