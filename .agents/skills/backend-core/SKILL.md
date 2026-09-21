---
name: backend-core
description: Core Spring Boot backend rules for every implementation task. Layering, transactions, JPA-vs-MyBatis decision, coding conventions, do/dont, and task routing. Always load first.
---

# Backend Core — Canonical

> Canonical core skill. Detailed rules: `skills/backend/SKILL.md`.
> Global behavior: `AGENTS.md`. Index: `SKILLS.md`.

## When to load

Every backend implementation task. Load before any domain skill.

## Core rules (summary — details in `skills/backend/SKILL.md`)

- Layering: `controller/api/* → service/* → serviceImpl/* → repository/*` and/or `mybatis/*`; MapStruct at transport boundary.
- Controllers never call `EntityManager` / `JdbcTemplate` / `VectorStore` directly (AI `*Tool.java` is the documented exception).
- Services own transactions; DB transactions do not roll back filesystem/vector/email/remote effects.
- JPA for CRUD + specifications + simple lookups; MyBatis + `vo/Paging` for join/paging DTO projections; no new MyBatis writes.
- Preserve legacy spellings: `serviceImpl`, `DevelopmentContoller`, `/api/faceRegconition`, `AiFaceRegconitionServiceClient`.
- Constructor injection (`@RequiredArgsConstructor`); Log4j2/`@Slf4j` only; typed `@ConfigurationProperties` for new config groups.
- When creating a standard CRUD service/repository, read `docs/agent/STANDARD_CRUD_SERVICE_REPOSITORY.md` first.

## Routing

After this file, load only the relevant domain skill(s) per `AGENTS.md §6`:

```text
API       -> .agents/skills/backend-api/SKILL.md + skills/backend/API.md
Data      -> .agents/skills/backend-data/SKILL.md + skills/backend/DATA.md
Security  -> .agents/skills/backend-security/SKILL.md + skills/backend/SECURITY.md
Files     -> .agents/skills/backend-files/SKILL.md + skills/backend/FILES.md
Async     -> .agents/skills/backend-async-messaging/SKILL.md + skills/backend/ASYNC_MESSAGING.md
AI/RAG    -> .agents/skills/backend-ai-rag/SKILL.md + skills/backend/AI_RAG.md (AI tasks only)
Testing   -> .agents/skills/backend-testing/SKILL.md + skills/backend/TESTING.md
```
