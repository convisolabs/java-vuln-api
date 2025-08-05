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

## Vulnerabilidades OAuth Implementadas

### 1. Client Secret Hardcoded
- **Localização**: `OAuthService.java` linha 15
- **Vulnerabilidade**: Client secret hardcoded no código
- **Impacto**: Exposição de credenciais sensíveis

### 2. Client Secret Exposto na URL
- **Localização**: `OAuthController.java` linha 25
- **Vulnerabilidade**: Client secret exposto como parâmetro de URL
- **Impacto**: Credenciais visíveis em logs e histórico do navegador

### 3. Reutilização de Códigos de Autorização
- **Localização**: `OAuthService.java` linha 65
- **Vulnerabilidade**: Códigos de autorização não são invalidados após uso
- **Impacto**: Permite reutilização de códigos de autorização

### 4. Validação Inadequada de Redirect URI
- **Localização**: `OAuthService.java` linha 75-78
- **Vulnerabilidade**: Não valida se o redirect_uri corresponde ao original
- **Impacto**: Permite redirecionamento para URLs maliciosas

### 5. Armazenamento Inseguro de Tokens
- **Localização**: `OAuthService.java` linha 20-21
- **Vulnerabilidade**: Tokens armazenados em memória sem expiração
- **Impacto**: Tokens podem ser acessados por outros processos

### 6. Validação Inadequada de Access Tokens
- **Localização**: `OAuthService.java` linha 85-88
- **Vulnerabilidade**: Não verifica expiração ou revogação de tokens
- **Impacto**: Tokens expirados ou revogados continuam válidos

### 7. Dados Fixos em UserInfo
- **Localização**: `OAuthService.java` linha 95-102
- **Vulnerabilidade**: Retorna dados fixos em vez de dados reais do usuário
- **Impacto**: Informações incorretas sobre o usuário

### 8. Usuário Fake na Geração de JWT
- **Localização**: `OAuthService.java` linha 110-115
- **Vulnerabilidade**: Cria usuário fake em vez de buscar usuário real
- **Impacto**: JWT gerado com dados incorretos

### Exemplos de Ataques

#### 1. Exposição de Client Secret
```bash
curl "http://localhost:8080/oauth/authorize?client_id=advocacia_client_123&client_secret=super_secret_key&redirect_uri=http://evil.com&response_type=code&scope=read"
```

#### 2. Reutilização de Código de Autorização
```bash
# Primeira requisição
curl -X POST http://localhost:8080/oauth/token \
  -H "Content-Type: application/json" \
  -d '{"grant_type":"authorization_code","client_id":"advocacia_client_123","client_secret":"super_secret_key","code":"CODE_HERE","redirect_uri":"http://localhost:5173/oauth/callback"}'

# Segunda requisição com o mesmo código (deveria falhar, mas funciona)
curl -X POST http://localhost:8080/oauth/token \
  -H "Content-Type: application/json" \
  -d '{"grant_type":"authorization_code","client_id":"advocacia_client_123","client_secret":"super_secret_key","code":"SAME_CODE","redirect_uri":"http://localhost:5173/oauth/callback"}'
```

#### 3. Acesso a UserInfo sem Validação Adequada
```bash
curl -H "Authorization: Bearer ANY_TOKEN" http://localhost:8080/oauth/userinfo
```
