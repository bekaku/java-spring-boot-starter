# SKILLS.md — Skill Router

> Router only. Binding behavior: `AGENTS.md`. Do not copy rule text here — point to the file that owns it.

## How the agent docs fit together

| Layer | Path | Answers | Read |
|---|---|---|---|
| Global rules | `AGENTS.md` | What is always binding in this repo | always |
| Router | `SKILLS.md` (this file) | Which skills this task needs | always |
| Playbook | `.agents/skills/<skill>/SKILL.md` | **HOW** — steps, code shapes, done-checklist, common mistakes | per chosen skill |
| Reference | `skills/backend/<GUIDE>.md` | **WHAT / WHY** — verified facts + binding rules with `Class#member` evidence | together with its playbook |
| Recipes & lookups | `docs/agent/*.md` | End-to-end CRUD recipe, known issues, repo map | only when a playbook points there |
| Task | `docs/tasks/<n>-<name>.md` | This task's contract and progress | when it exists |

Playbook and reference for the same skill never repeat each other: follow the playbook, look up facts in the reference.

## Loading order

```text
1. AGENTS.md
2. SKILLS.md                                   (pick skills from the tables below)
3. .agents/skills/backend-core/SKILL.md   +  skills/backend/CORE.md
4. docs/tasks/<n>-<name>.md                    (when one exists)
5. each chosen domain skill:  playbook    +  reference
6. .agents/skills/backend-testing/SKILL.md +  skills/backend/TESTING.md   (before final validation)
```

## Skills

| Skill | Playbook | Reference | Load when the task touches… | Do not load for… |
|---|---|---|---|---|
| `backend-core` | `.agents/skills/backend-core/SKILL.md` | `skills/backend/CORE.md` | every backend task | — |
| `backend-api` | `.agents/skills/backend-api/SKILL.md` | `skills/backend/API.md` | controllers, routes, DTOs, validation, paging/search, status codes, error bodies | persistence-only changes |
| `backend-data` | `.agents/skills/backend-data/SKILL.md` | `skills/backend/DATA.md` | entities, columns, repositories, MyBatis, Flyway, mappers, IDs, audit, soft delete, permission seed rows | HTTP-only changes |
| `backend-security` | `.agents/skills/backend-security/SKILL.md` | `skills/backend/SECURITY.md` | public/protected routes, JWT, cookies, refresh, API keys, `@PreAuthorize`, owner scoping, signup/OTP/reset | endpoints that only reuse an existing permission pattern unchanged |
| `backend-files` | `.agents/skills/backend-files/SKILL.md` | `skills/backend/FILES.md` | uploads, downloads, `/cdn/**`, chunk merge, streaming, filesystem paths | — |
| `backend-async-messaging` | `.agents/skills/backend-async-messaging/SKILL.md` | `skills/backend/ASYNC_MESSAGING.md` | `@Async`, RabbitMQ producers/consumers, `@Scheduled` | — |
| `backend-ai-rag` (optional) | `.agents/skills/backend-ai-rag/SKILL.md` | `skills/backend/AI_RAG.md` | Spring AI, Ollama, Qdrant, ingestion, SSE chat, AI tools, prompts, face recognition | **any non-AI task** |
| `backend-testing` | `.agents/skills/backend-testing/SKILL.md` | `skills/backend/TESTING.md` | before final validation of any implementation | — |

## Quick picks

```text
New CRUD resource (entity → API)      core + data + api + security + testing
                                      + docs/agent/STANDARD_CRUD_SERVICE_REPOSITORY.md
Add a field to an existing resource   core + data + api + testing
New endpoint on an existing controller core + api + testing (+ security if new permission/ownership)
Add/change a MyBatis list query       core + data + api + testing
Make a route public / change auth     core + security + api + testing
Fix refresh token / session logic     core + security + testing
Owner-only resource (my items)        core + security + data + api + testing
File upload/download/streaming        core + files + security + testing (+ data if metadata changes)
Add @Async work or a scheduled job    core + async-messaging + testing (+ data if it writes)
Add a RabbitMQ consumer               core + async-messaging + data + testing
RAG ingestion / SSE chat change       core + ai-rag + testing (+ data / files / api as crossed)
Ordinary CRUD / auth / file work      NEVER load backend-ai-rag
Documentation-only change             core + testing (docs-only evidence rules)
```

## Reference docs (only when indicated)

| File | Read when |
|---|---|
| `docs/agent/STANDARD_CRUD_SERVICE_REPOSITORY.md` | creating a standard CRUD resource (end-to-end recipe: entity → migration → i18n → DTO → mapper → repository → service → controller → tests) |
| `docs/agent/KNOWN_ISSUES.md` | debugging or touching a listed legacy area — never copy these as patterns |
| `docs/agent/PROJECT_REFERENCE.md` | you need the repo map, dependency list, or naming evidence |
| `docs/agent/SPLIT_MAP.md` | tracing where a rule from the original single `SKILLS.md` moved |
| `docs/agent/ORIGINAL_SKILLS.md` | audit only — do not load by default (outdated) |
