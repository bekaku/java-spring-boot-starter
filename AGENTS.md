# AGENTS.md — Global Agent Instructions

> Source of truth for agent behavior in this repo.
> Adapters (`CLAUDE.md`, `GEMINI.md`, `.github/copilot-instructions.md`) point here and must not diverge.
> Canonical skills live in `.agents/skills/`; detailed domain rules live in `skills/backend/`; reference docs live in `docs/agent/`.

## 1. Project

General-purpose / open-source Spring Boot backend.

Backend scope only:

- `src/main/java/com/bekaku/api/spring/`
- `src/main/resources/`
- `src/test/`
- `build.gradle`

There is no `backend/` or `frontend/` directory here. The frontend lives in an external repository. Frontend/root application concerns are out of scope unless explicitly requested.

## 2. Stack

Primary stack (declarations in `build.gradle`, not upgrade recommendations):

- Java 25, Spring Boot 4.1, Spring MVC / Tomcat
- Spring Security + JJWT
- Spring Data JPA + PostgreSQL + Flyway, MyBatis for read-model projections
- MapStruct + Lombok
- RabbitMQ / Async / Scheduling
- Optional Spring AI / Qdrant modules (Ollama, SSE chat)

## 3. Repository Layout

- `AGENTS.md` — this file, global instructions (§§1–14)
- `SKILLS.md` — skill router: which skills a task needs, loading order, quick picks
- `.agents/skills/<skill>/SKILL.md` — **playbooks** (HOW): steps, code shapes, done-checklist, common mistakes (Agent Skills spec, frontmatter `name` + `description`)
- `skills/backend/*.md` — **references** (WHAT / WHY): verified facts + binding rules with `Class#member` evidence; `CORE.md` pairs with `backend-core`
- `tasks/TASK_TEMPLATE.md` — canonical task template; `tasks/README.md` — task workflows
- `docs/tasks/` — task instances (`<number>-<short-name>.md`)
- `docs/agent/` — end-to-end CRUD recipe, known issues, project map, split map, original source (archive)

Read `docs/agent/PROJECT_REFERENCE.md` only when repository layout, dependency, naming, or architecture context is required.

## 4. Layered Architecture

Binding layering:

```text
controller/api/* → service/* interface → serviceImpl/* → repository/* (JPA)
  and/or mybatis/* + src/main/resources/mybatis/*.xml
DTO mapping at transport boundary via mapper/* (MapStruct)
```

- Controllers call services, never `EntityManager` / `JdbcTemplate` / `VectorStore` directly. AI tool classes under `ai/*Tool.java` are the documented exception.
- Services own business logic and transaction boundaries.
- Controllers must not perform direct database / business writes.
- Do not call controllers from services.

JPA vs MyBatis (binding):

- JPA for CRUD + `JpaSpecificationExecutor` filtering + simple lookups.
- MyBatis + `vo/Paging` for join/paging read-model DTO projections.
- Do not add new MyBatis writes; do not use MyBatis for single-table CRUD that JPA already covers.

## 5. Core Working Rules

- Make the smallest correct change for the current task.
- Follow the existing layered architecture.
- Do not refactor unrelated legacy code.
- Preserve intentional legacy spellings unless the task explicitly changes them: `serviceImpl`, `DevelopmentContoller`, `/api/faceRegconition`, `AiFaceRegconitionServiceClient`.
- Never weaken authentication, authorization, ownership, validation, or tenant/user scoping.
- Never expose secrets, raw tokens, credentials, password hashes, or internal driver errors.
- Prefer existing project patterns and utilities before adding abstractions or dependencies.
- Cite file-path evidence when claiming a convention (`path:line` in reports; `path` + `Class#member` in docs, because line numbers drift). Do not invent conventions.
- Report verification commands and actual results. Do not claim deployment, security, integration, or production success without evidence.

## 6. Required Reading / Skill Routing

For every backend implementation task, always read:

```text
AGENTS.md
SKILLS.md
.agents/skills/backend-core/SKILL.md + skills/backend/CORE.md
<this-task> (when a task file exists)
```

Then read only the relevant skill playbook (HOW) + its reference (WHAT / WHY):

```text
REST/controller/API contract          -> .agents/skills/backend-api/SKILL.md + skills/backend/API.md
JPA/MyBatis/Flyway/data changes       -> .agents/skills/backend-data/SKILL.md + skills/backend/DATA.md
Auth/authorization/ownership/cookies  -> .agents/skills/backend-security/SKILL.md + skills/backend/SECURITY.md
Files/uploads/downloads/CDN/storage   -> .agents/skills/backend-files/SKILL.md + skills/backend/FILES.md
RabbitMQ/async/scheduling             -> .agents/skills/backend-async-messaging/SKILL.md + skills/backend/ASYNC_MESSAGING.md
AI/RAG/Qdrant/SSE/tools               -> .agents/skills/backend-ai-rag/SKILL.md + skills/backend/AI_RAG.md (AI tasks only)
Tests/verification                    -> .agents/skills/backend-testing/SKILL.md + skills/backend/TESTING.md
```

- Ordinary CRUD/auth/file/messaging work must not load AI/RAG.
- Read `docs/agent/KNOWN_ISSUES.md` only when debugging or touching a listed legacy area.
- When creating a standard CRUD resource, follow the end-to-end recipe in `docs/agent/STANDARD_CRUD_SERVICE_REPOSITORY.md` (entity → migration → i18n → DTO → mapper → repository → service → controller → tests).
- Do not load unrelated skill/reference files by default.

## 7. Task Tracking (Mandatory)

For every task that has a task file (see `tasks/TASK_TEMPLATE.md`, instances in `docs/tasks/`):

- **Before editing code:** set Resume State `Overall Status` to `IN_PROGRESS`, record the initial `Current Step` and `Files Currently Being Modified`.
- **During implementation:** update the task file incrementally — tick off `Progress Checklist` items only after each item is actually done and evidenced, and keep `Current Step`, `Completed Work`, and `Partially Completed Work` current. Never leave all updates until the work is finished.
- **After implementation, before final verification:** set `Overall Status` to `IMPLEMENTATION_COMPLETE`.
- **After verification:** set `Overall Status` to `COMPLETED` and fill the Completion Report. If blocked, set `BLOCKED` and describe the blocker under `Known Issues / Blockers`.

## 8. API / Data Contracts

- No universal `ApiResponse` envelope: `BaseApiController.responseEntity` passes body + status through.
- Standard verbs/status: `GET /` → `200 ResponseListDto`, `GET /{id}` → `200 DTO`, `POST /` → `201 DTO`, `PUT /{id}` → `200 DTO`, `DELETE /{id}` → `200` delete message (not `204`).
- Paged shape `ResponseListDto` = `{dataList, totalPages, totalElements, last}`; some file endpoints return bare lists — verify per endpoint.
- `@Valid @RequestBody` on all mutating endpoints, then manual `*Validator` checks.
- Errors via `ApiException` / `BaseResponseException` → `GlobalExceptionHandler` → `ApiError`; JWT filter 401s use separate `{"error":"..."}` shape.
- IDs are Snowflake `Long` via `@PrePersist`; never mix `IDENTITY`/`SEQUENCE`. Soft delete requires field + `@SQLDelete` + `@SQLRestriction`; MyBatis/native SQL must add `deleted=false` + ownership predicates.
- Flyway: `src/main/resources/db/migration/`, `V{version}__{snake_case}.sql`; never rewrite deployed migrations. Validate PG/pgvector SQL on disposable PostgreSQL, not H2.
- Flyway is disabled in `application.yml` and dev runs `ddl-auto: update`, so a local app start is not migration evidence. Every schema change still needs a new migration.
- Full rules: see §6 guides, do not duplicate them here.

## 9. Security / Files / Async / AI Boundaries

- Trace all three boundaries: (1) `WebSecurityConfig` routes, (2) `JwtTokenFilter` skip-list + verification, (3) method/service ownership checks. `AuthorizationInterceptor` (returns `true`) and `CustomPermissionEvaluator` (stub) enforce nothing.
- Admin resources: `@PreAuthorize("@permissionChecker.hasPermission('...')")`. The authenticated principal has no granted authorities, so `hasRole` / `hasAuthority` never pass. Owner-scoped rows (chat, files, faces): additionally scope by creator/owner (`findByIdAndCreator`).
- Never log/expose raw refresh tokens, JWT/AES secrets, password hashes, API keys, or MCP credentials. `X-User-ID` is input, never identity.
- Files: DB transactions do not roll back filesystem effects; preserve real-path containment + server-generated filenames; keep secrets/logs/private data out of the public storage mapping; chunk merge is non-atomic.
- Async/messaging: no active `@RabbitListener` in this checkout; adding a consumer requires idempotency + retry/DLQ + duplicate-delivery tests; pass actor identity explicitly into async work.
- AI/RAG is optional: MVC SSE (not WebFlux server); disabled Qdrant stores fail at runtime unless explicitly gated; treat prompts/retrieved text/model SQL as untrusted.

## 10. Validation

Use the narrowest useful validation first:

1. focused test
2. `./gradlew compileJava`
3. relevant test group
4. full `./gradlew test` / `bootJar` only when appropriate

See `.agents/skills/backend-testing/SKILL.md` + `skills/backend/TESTING.md` for the evidence matrix. Report exact commands + actual results.

## 11. Safe Stop / Resume

Before stopping incomplete work: finish the current safe atomic change where practical; update checklist + Resume State; inspect `git status`, `git diff`, `git diff --stat`; record partial work and the exact next action.

When resuming: read `AGENTS.md` → `SKILLS.md` → core playbook + `skills/backend/CORE.md` → this task → only relevant domain skills; inspect checklist + Resume State + repo diff; continue from Next Recommended Steps.

## 12. Prohibited Behaviors

- No new global API envelope, HATEOAS links, class-level `@Validated`, `IDENTITY`/`SEQUENCE` IDs on Snowflake entities, Lombok `@Data` on relational entities, or `${...}` interpolation of request text in MyBatis XML.
- No new MyBatis writes for JPA-covered CRUD.
- No secrets/logs/backups under the public storage mapping.
- Do not treat legacy stubs, prompt text, `UrlUtil` alone, OTP throttle, or YAML-only Qdrant/consumer settings as complete security boundaries.
- Do not run the code generator (`DevelopmentContoller`) as a read-only diagnostic; never hand-edit `build/generated` as a lasting change.

## 13. Reference Index

```text
Skill router:        SKILLS.md
Playbooks (HOW):     .agents/skills/{backend-core, backend-api, backend-data, backend-security,
                     backend-files, backend-async-messaging, backend-ai-rag, backend-testing}/SKILL.md
References (WHAT):   skills/backend/CORE.md, API.md, DATA.md, SECURITY.md,
                     FILES.md, ASYNC_MESSAGING.md, AI_RAG.md, TESTING.md
Tasks:               tasks/TASK_TEMPLATE.md, tasks/README.md, docs/tasks/
Recipes / lookups:   docs/agent/STANDARD_CRUD_SERVICE_REPOSITORY.md (end-to-end CRUD recipe)
                     docs/agent/KNOWN_ISSUES.md
                     docs/agent/PROJECT_REFERENCE.md
                     docs/agent/SPLIT_MAP.md
                     docs/agent/ORIGINAL_SKILLS.md (archive, do not load by default)
Adapters:            CLAUDE.md, GEMINI.md, .github/copilot-instructions.md (pointers only)
```

## 14. Authority Hierarchy

1. `AGENTS.md` (this file) — global behavior, binding unless the user explicitly overrides in-session.
2. `SKILLS.md` + `.agents/skills/*/SKILL.md` — skill routing and playbooks, binding.
3. `skills/backend/*.md` + `docs/agent/*.md` — references and recipes, binding within their domain. If a playbook and its reference disagree, the reference (evidence-backed) wins; flag the mismatch.
4. `CLAUDE.md` / `GEMINI.md` / `.github/copilot-instructions.md` — adapters only, never the source of truth. On conflict, §§1–13 above win.
5. Legacy code under `Known Issues` — explicitly not a pattern to copy.

If a request conflicts with a documented rule, flag the conflict explicitly before proceeding — never silently pick a side.
