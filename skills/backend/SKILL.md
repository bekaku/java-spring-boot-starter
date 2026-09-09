# Backend Core Skill

Use this skill for every backend implementation task. Read additional domain files only when relevant.

## Enforcement

- These backend rules are authoritative unless the user explicitly overrides them in-session.
- If a task conflicts with a documented rule, surface the conflict instead of silently choosing.
- Do not invent conventions. Prefer evidence from existing code.
- Known legacy violations are not patterns to copy.

## Architecture

- Layering: `Controller -> Service interface -> ServiceImpl -> Repository/MyBatis`.
- MapStruct is the transport mapping boundary.
- Controllers must not call `EntityManager`, `JdbcTemplate`, or vector stores directly. AI tool classes are the documented exception.
- Services own transactions. Use read-only defaults where appropriate and explicit write transactions for mutations.
- A DB transaction does not roll back filesystem, vector-store, email, or remote-service effects; multi-system writes need explicit compensation.
- Never call controllers from services or place business writes in controllers.

## JPA vs MyBatis

- Use JPA for CRUD, specifications, and simple lookups.
- Use MyBatis + `vo/Paging` for join-heavy/paged read-model DTO projections.
- Do not add new MyBatis writes.
- Do not use MyBatis for simple single-table CRUD already covered by JPA.

## Coding conventions

- Preserve intentional legacy spellings such as `serviceImpl`, `DevelopmentContoller`, `/api/faceRegconition`, and `AiFaceRegconitionServiceClient` unless explicitly changing the contract.
- Prefer constructor injection with Lombok `@RequiredArgsConstructor`; do not spread legacy field injection.
- Logging is Log4j2 / `@Slf4j`; never add Logback imports.
- New grouped configuration belongs in typed `@ConfigurationProperties` records, not scattered `@Value`.
- DTO validation uses Jakarta constraints with i18n message keys.
- MapStruct mappers use `@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.IGNORE)` and require intentional field review.
- MyBatis interface method, namespace/statement id, `@Param`, and XML bindings must stay aligned.

## General do / don't

Do:
- Extend existing base controllers/repositories/helpers when they fit.
- Use i18n validation/error messages.
- Scope user-owned resources by authenticated owner in addition to permission checks.
- Use typed properties and existing utilities for cookies, passwords, URLs, files, and IDs.

Don't:
- Add a new global API envelope or HATEOAS convention.
- Introduce `IDENTITY`/`SEQUENCE` IDs into Snowflake entities.
- Put Lombok `@Data` on relational entities.
- Interpolate untrusted request text into MyBatis SQL.
- Treat legacy stubs, prompt text, headers, or config-only toggles as security boundaries.

## Task routing

Read additionally when relevant:
- API -> `API.md`
- Data -> `DATA.md`
- Security -> `SECURITY.md`
- Files -> `FILES.md`
- Async/messaging -> `ASYNC_MESSAGING.md`
- AI/RAG -> `AI_RAG.md`
- Tests -> `TESTING.md`
