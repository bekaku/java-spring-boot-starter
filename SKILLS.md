# SKILLS.md — Canonical Skill Pointer List

> Pointer index only. Source of truth for behavior is `AGENTS.md`.
> Canonical skills: `.agents/skills/*/SKILL.md` (Agent Skills spec).
> Detailed rules: `skills/backend/*.md`. References: `docs/agent/*.md`.
> Do not duplicate rule text here — point to the canonical file.

Backend skills are split by concern so agents load only what the task needs.

## Core (always read for backend implementation tasks)

- Canonical: `.agents/skills/backend-core/SKILL.md`
- Detailed: `skills/backend/SKILL.md`
- Covers: enforcement, layering, JPA-vs-MyBatis decision, coding conventions, do/don't, task routing

## Domain guides (read only when relevant)

| Concern | Canonical skill | Detailed guide | Load when |
|---|---|---|---|
| API / controllers / DTO contract | `.agents/skills/backend-api/SKILL.md` | `skills/backend/API.md` | REST/SSE, DTO, pagination, validation, response changes |
| Persistence / JPA / MyBatis / Flyway | `.agents/skills/backend-data/SKILL.md` | `skills/backend/DATA.md` | entities, repos, MyBatis XML, migrations, IDs, audit, soft-delete |
| Security / JWT / cookies / RBAC / ownership | `.agents/skills/backend-security/SKILL.md` | `skills/backend/SECURITY.md` | auth, JWT, refresh/session, cookies, permissions, ownership, signup/OTP |
| Files / storage / uploads / streaming | `.agents/skills/backend-files/SKILL.md` | `skills/backend/FILES.md` | uploads, downloads, CDN, chunk merge, streaming, filesystem ops |
| RabbitMQ / async / scheduling | `.agents/skills/backend-async-messaging/SKILL.md` | `skills/backend/ASYNC_MESSAGING.md` | `@Async`, queues, consumers/producers, schedulers |
| Optional AI / RAG module | `.agents/skills/backend-ai-rag/SKILL.md` | `skills/backend/AI_RAG.md` | Spring AI, Ollama, Qdrant, ingestion, SSE chat, tools, memory — AI tasks only |
| Testing / validation | `.agents/skills/backend-testing/SKILL.md` | `skills/backend/TESTING.md` | before final validation of implementation work |

## Reference docs (read only when indicated)

- Repository/dependency overview: `docs/agent/PROJECT_REFERENCE.md` — layout, deps, naming, architecture lookup only
- Known issues and legacy exceptions: `docs/agent/KNOWN_ISSUES.md` — debugging/touching a listed legacy area only (do not copy as patterns)
- Standard CRUD templates: `docs/agent/STANDARD_CRUD_SERVICE_REPOSITORY.md` — before creating a standard CRUD service/repository
- Split map: `docs/agent/SPLIT_MAP.md` — original-to-split audit trail
- Original unsplit source: `docs/agent/ORIGINAL_SKILLS.md` — archive, do not load by default

## Loading policy

```text
Every backend task
  -> AGENTS.md
  -> SKILLS.md (this file)
  -> .agents/skills/backend-core/SKILL.md
  -> only the relevant domain skill(s) above
```

Examples:

```text
Add CRUD endpoint
  -> backend-api + backend-data + backend-testing

Fix refresh token security
  -> backend-security + backend-testing

Add RabbitMQ consumer
  -> backend-async-messaging + backend-data (if persistence involved) + backend-testing

Fix file streaming
  -> backend-files + backend-security + backend-testing

Ordinary user CRUD
  -> DO NOT read backend-ai-rag

RAG ingestion change
  -> backend-ai-rag + backend-data + backend-files + backend-testing as applicable
```
