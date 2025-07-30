# Endpoints de Processos Judiciais

Este documento descreve os endpoints disponíveis para gerenciar processos judiciais na API.

## Base URL
```
http://localhost:8080/api/processos
```

## Endpoints Disponíveis

### 1. Cadastrar Processo Judicial
**POST** `/api/processos/cadastrar`

Cadastra um novo processo judicial no sistema.

**Body (JSON):**
```json
{
    "numeroProcesso": "0001234-56.2023.8.26.0100",
    "tribunal": "Tribunal de Justiça de São Paulo",
    "vara": "1ª Vara Cível",
    "cliente": "João Silva",
    "parteContraria": "Maria Santos",
    "advogadoResponsavel": "Dr. Carlos Oliveira",
    "status": "EM_ANDAMENTO",
    "observacoes": "Processo sobre indenização por danos morais",
    "valorCausa": 50000.00,
    "tipoAcao": "Indenização por Danos Morais"
}
```

**Resposta de Sucesso (201):**
```json
{
    "id": "uuid-do-processo",
    "numeroProcesso": "0001234-56.2023.8.26.0100",
    "cliente": "João Silva",
    "tribunal": "Tribunal de Justiça de São Paulo",
    "vara": "1ª Vara Cível",
    "status": "EM_ANDAMENTO",
    "message": "Processo cadastrado com sucesso!"
}
```

### 2. Buscar Processo por ID
**GET** `/api/processos/{id}`

Retorna um processo específico pelo ID.

### 3. Buscar Processo por Número
**GET** `/api/processos/numero/{numeroProcesso}`

Retorna um processo específico pelo número do processo.

### 4. Listar Todos os Processos
**GET** `/api/processos/todos`

Retorna todos os processos cadastrados.

### 5. Buscar Processos por Cliente
**GET** `/api/processos/cliente/{cliente}`

Retorna todos os processos de um cliente específico.

### 6. Buscar Processos por Tribunal
**GET** `/api/processos/tribunal/{tribunal}`

Retorna todos os processos de um tribunal específico.

### 7. Buscar Processos por Status
**GET** `/api/processos/status/{status}`

Retorna todos os processos com um status específico.

**Status disponíveis:**
- EM_ANDAMENTO
- SUSPENSO
- ARQUIVADO
- FINALIZADO
- APELACAO
- RECURSO
- SENTENCA
- EXECUCAO

### 8. Buscar Processos por Termo
**GET** `/api/processos/buscar?termo={termo}`

Busca processos que contenham o termo no número do processo, cliente ou parte contrária.

### 9. Atualizar Processo
**PUT** `/api/processos/{id}`

Atualiza um processo existente.

**Body (JSON):** Mesmo formato do cadastro.

### 10. Deletar Processo
**DELETE** `/api/processos/{id}`

Remove um processo do sistema.

### 11. Listar Status Disponíveis
**GET** `/api/processos/status-disponiveis`

Retorna todos os status disponíveis para processos.

## Status de Resposta

- **200 OK**: Operação realizada com sucesso
- **201 Created**: Recurso criado com sucesso
- **400 Bad Request**: Dados inválidos ou erro de validação
- **404 Not Found**: Recurso não encontrado

## Exemplos de Uso

### Cadastrar um novo processo:
```bash
curl -X POST http://localhost:8080/api/processos/cadastrar \
  -H "Content-Type: application/json" \
  -d '{
    "numeroProcesso": "0001234-56.2023.8.26.0100",
    "tribunal": "Tribunal de Justiça de São Paulo",
    "vara": "1ª Vara Cível",
    "cliente": "João Silva",
    "parteContraria": "Maria Santos",
    "advogadoResponsavel": "Dr. Carlos Oliveira",
    "status": "EM_ANDAMENTO",
    "observacoes": "Processo sobre indenização por danos morais",
    "valorCausa": 50000.00,
    "tipoAcao": "Indenização por Danos Morais"
  }'
```

### Buscar todos os processos:
```bash
curl -X GET http://localhost:8080/api/processos/todos
```

### Buscar processos por cliente:
```bash
curl -X GET http://localhost:8080/api/processos/cliente/João%20Silva
```

## Banco de Dados

A tabela `processo_judicial` será criada automaticamente pelo Hibernate quando a aplicação for iniciada pela primeira vez. O script SQL também está disponível em `src/main/resources/database/create_processo_judicial_table.sql` para criação manual se necessário.

## Campos Obrigatórios

- `numeroProcesso`: Número único do processo
- `tribunal`: Nome do tribunal
- `vara`: Nome da vara
- `cliente`: Nome do cliente

## Campos Opcionais

- `parteContraria`: Nome da parte contrária
- `advogadoResponsavel`: Nome do advogado responsável
- `status`: Status do processo (padrão: EM_ANDAMENTO)
- `observacoes`: Observações sobre o processo
- `valorCausa`: Valor da causa
- `tipoAcao`: Tipo da ação judicial 