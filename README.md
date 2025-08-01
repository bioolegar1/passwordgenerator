# Manual de Uso da API - Gerador de Senhas

**Versão:** 1.0  
**Data:** 29 de Julho de 2025  
**URL Base da API:** `SUA_URL`

## 1. Introdução

Este documento serve como um guia completo para desenvolvedores e sistemas que precisam interagir com a API Gerador de Senhas.

O objetivo principal desta API é fornecer um token (senha) de acesso compartilhado que se renova automaticamente a cada hora, permitindo uma forma de autenticação temporária e sincronizada entre diferentes sistemas e usuários.

**URL Base de Produção:** Todas as requisições descritas neste manual devem ser feitas para a seguinte URL base: `SUA_URL`

## 2. Conceitos Chave

- **Senha Ativa:** Em qualquer momento, existe apenas **uma única senha válida** para todos. Qualquer requisição para obter a senha dentro da mesma hora retornará o mesmo valor.
- **Rotação Horária:** A senha é invalidada e uma nova é gerada automaticamente no início de cada hora (ex: às 14:00:00, 15:00:00, etc.). O cálculo é baseado no fuso horário do Brasil (`America/Sao_Paulo`).
- **Formato de Data (UTC):** Todas as datas e horas retornadas pela API, como o campo `expiresAt`, estão no formato **UTC (Tempo Universal Coordenado)** e seguem o padrão ISO-8601. Isso garante consistência e evita problemas com fuso horário nos sistemas que consomem a API.

## 3. Endpoints da API

A API possui dois endpoints principais.

### 3.1 Obter a Senha Ativa

Este endpoint é utilizado para consultar qual é a senha válida no momento atual.

| Campo | Valor                                               |
|-------|-----------------------------------------------------|
| **Descrição** | Retorna o token/senha ativo e sua data de expiração |
| **Método HTTP** | `GET`                                               |
| **URL** | `/api/v1/passwords/current`                         |
| **URL Completa** | `SUA_URL/api/v1/passwords/current`                  |

**Exemplo de Requisição** (`curl`):
```bash
curl "SUA_URL/api/v1/passwords/current"
```

**Exemplo de Resposta (Sucesso** `200 OK`):
```json
{
  "token": "N9hWkRzJmFpXcVdGvA4t",
  "expiresAt": "2025-07-30T22:00:00Z"
}
```

- `token`: O valor da senha ativa.
- `expiresAt`: O momento exato (em UTC) em que esta senha se tornará inválida.

### 3.2 Validar uma Senha

Este endpoint é utilizado por um sistema para verificar se a senha informada por um usuário é a senha ativa no momento.

| Campo | Valor                                                                        |
|-------|------------------------------------------------------------------------------|
| **Descrição** | Verifica se um determinado token/senha é o correto e está dentro da validade |
| **Método HTTP** | `POST`                                                                       |
| **URL** | `/api/v1/passwords/validate`                                                 |
| **URL Completa** | `SUA_URL/api/v1/passwords/validate`                                          |
| **Cabeçalhos** | `Content-Type: application/json`                                             |

**Corpo da Requisição (Body):** É necessário enviar um objeto JSON com a senha que se deseja validar.
```json
{
    "token": "VALOR_DA_SENHA_A_SER_VALIDADA"
}
```

**Exemplo de Requisição** (`curl`):
```bash
curl --request POST "SUA_URL/api/v1/passwords/validate" \
--header "Content-Type: application/json" \
--data-raw '{
    "token": "N9hWkRzJmFpXcVdGvA4t"
}'
```

**Exemplo de Resposta (Sucesso** `200 OK`):

- Se a senha for válida:
```json
{
  "valid": true
}
```

- Se a senha for inválida ou expirada:
```json
{
  "valid": false
}
```

## 4. Fluxo de Uso Típico

O diagrama abaixo ilustra o fluxo de trabalho para autenticação entre dois sistemas (ex: Protheus e um Usuário).

1. **Consulta:** Um sistema (Sistema A) e/ou um usuário consultam a senha ativa fazendo uma requisição `GET` para `/api/v1/passwords/current`. Ambos recebem a mesma senha, por exemplo, `"N9hWkRzJmFpXcVdGvA4t"`.

2. **Ação do Usuário:** O usuário informa a senha `"N9hWkRzJmFpXcVdGvA4t"` ao Sistema A para tentar realizar uma ação.

3. **Validação:** O Sistema A, ao receber a senha do usuário, faz uma requisição `POST` para `/api/v1/passwords/validate`, enviando a senha recebida no corpo da requisição.

4. **Decisão:**
   - Se a API responder `{"valid": true}`, o Sistema A autoriza a ação do usuário.
   - Se a API responder `{"valid": false}`, o Sistema A nega a ação.