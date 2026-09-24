---
name: backend-core
description: Use first for every backend implementation task in this Spring Boot repo (Java 25, Spring Boot 4.1, JPA/MyBatis, JWT). Gives the start-to-finish workflow, where each kind of code goes, layering and transaction rules, and which domain skill to load next.
---

# Backend Core — Playbook

> **Role:** HOW to run any backend task. Binding facts + evidence: `skills/backend/CORE.md` (read it after this file).
> **Global rules:** `AGENTS.md`. **Skill index:** `SKILLS.md`.

## 1. Before you edit

1. If a task file exists in `docs/tasks/`, read it and set Resume State to `IN_PROGRESS` before touching code (`AGENTS.md §7`).
2. Pick the domain skills from the table in §4. Load only those.
3. Find the closest existing feature and trace it end to end:

   ```text
   controller/api/{Model}Controller → service/{Model}Service → serviceImpl/{Model}ServiceImpl
     → repository/{Model}Repository  (JPA)   and/or   mybatis/{Model}Mybatis + resources/mybatis/{Model}Mybatis.xml
     + model/{Model}  dto/{Model}Dto  mapper/{Model}Mapper  validator/{Model}Validator
   ```

   Good references: `AppRole*` (standard CRUD), `AiChat*` (owner-scoped rows), `FileManager*` (MyBatis paging + files).
4. Write down, before coding: the HTTP contract, who owns each write, the transaction boundary, and any non-DB side effect (file, vector, email, queue, remote call).

## 2. While you implement

- Put code where the table in `skills/backend/CORE.md#where-code-goes` says. Follow the naming exactly.
- Controller = HTTP only. Service = business rules + `@Transactional` writes. Repository/MyBatis = queries only.
- Current user: `@AuthenticationPrincipal AppUserDto auth` in the controller → pass `auth.getId()` to the service.
- Errors: `throw responseErrorNotfound();` (and siblings) from controllers; services use `BaseResponseException` or `new ApiException(new ApiError(...))`.
- Text shown to users: i18n key in both `messages.properties` and `messages_th.properties`.
- New standard CRUD resource: follow `docs/agent/STANDARD_CRUD_SERVICE_REPOSITORY.md` step by step.
- Keep the change minimal. Do not refactor unrelated legacy code, even if it looks wrong — note it instead.

## 3. Before you finish

1. Load `backend-testing` and run the narrowest check that proves the change, then `./gradlew compileJava`.
2. Review `git status` and `git diff`; remove unrelated edits.
3. Update the task file (checklist, Resume State, Completion Report) if one exists.
4. Report exact commands and actual results.

## 4. Which domain skill to load

| The task touches… | Load |
|---|---|
| Controller, route, DTO, validation, paging, response/error shape | `backend-api` |
| Entity, repository, MyBatis, Flyway migration, mapper, IDs, audit, soft delete, permission seed data | `backend-data` |
| Login, JWT, refresh, cookies, API keys, `@PreAuthorize`, owner scoping, public routes | `backend-security` |
| Upload, download, CDN path, chunk merge, streaming, filesystem | `backend-files` |
| `@Async`, RabbitMQ, `@Scheduled` | `backend-async-messaging` |
| Spring AI, Ollama, Qdrant, ingestion, SSE chat, AI tools, face recognition | `backend-ai-rag` (AI tasks only) |
| Any implementation, before final validation | `backend-testing` |

Each skill has a detailed reference in `skills/backend/` (`API.md`, `DATA.md`, `SECURITY.md`, `FILES.md`, `ASYNC_MESSAGING.md`, `AI_RAG.md`, `TESTING.md`). Read it together with the skill.

Optional references — only when needed:
- `docs/agent/PROJECT_REFERENCE.md` — repo layout and dependency lookup.
- `docs/agent/KNOWN_ISSUES.md` — when debugging or touching a listed legacy area.

## 5. Stop signs

Stop and tell the user before continuing if the task would:
- weaken authentication, authorization, ownership, or validation;
- change an existing HTTP contract used by the external frontend;
- rewrite an existing Flyway migration;
- need a new dependency or a new cross-cutting abstraction;
- conflict with any rule in `AGENTS.md` or a skill.
