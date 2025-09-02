<<<<<<< HEAD
# ProjetoVidaplus
Sistema gerenciador de clinicas e serviços de saude
=======
# VidaPlus - API

## Autenticação JWT

1. Registre um usuário:
```
POST /auth/register
{
  "username": "admin@vidaplus.com",
  "password": "123456",
  "roles": ["ROLE_ADMIN"]
}
```
2. Faça login:
```
POST /auth/login
{
  "username": "admin@vidaplus.com",
  "password": "123456"
}
```
3. Use o token Bearer nas chamadas protegidas.
- Rotas **abertas**: `/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`
- Rotas **ADMIN**: `/api/clinicas/**`, `/api/medicos/**`
- Rotas **autenticadas** (qualquer usuário): `/api/pacientes/**`, `/api/consultas/**`


## Perfis e vínculos
- Roles: ROLE_ADMIN, ROLE_PACIENTE, ROLE_PROFISSIONAL
- UserAccount possui vínculo opcional 1–1 com Paciente e/ou Medico.
- Regras de acesso aplicadas nos controllers de Consulta.

## Gestão de Leitos
- `PUT /api/clinicas/{id}/leitos/total/{total}`
- `PUT /api/clinicas/{id}/leitos/ocupar/{qtd}`
- `PUT /api/clinicas/{id}/leitos/liberar/{qtd}`
- `GET /api/clinicas/{id}/leitos/status`
>>>>>>> 260aaa1 (Comit da publicação do projeto no Github)
