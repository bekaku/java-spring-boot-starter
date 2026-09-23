---
name: backend-data
description: JPA entities, repositories, MyBatis projections, PostgreSQL, Flyway, MapStruct, IDs, audit, and soft-delete rules. Load when persistence or migrations change.
---

# Backend Data — Canonical

> Canonical data skill. Detailed rules: `skills/backend/DATA.md`.
> Requires: `AGENTS.md`, `.agents/skills/backend-core/SKILL.md`.

## When to load

JPA entities/repositories, MyBatis mappers/XML, PostgreSQL, Flyway migrations, MapStruct persistence mapping, IDs, audit, or soft-delete work.

## Implementation path

1. Find the existing entity and repository or MyBatis interface/XML pair. Use JPA for CRUD, specifications, and simple lookups; use MyBatis with `vo/Paging` for joined or paged DTO read models.
2. Put write transactions in `serviceImpl`. Review every mapped field because MapStruct's ignored-target policy can hide omissions. For a new standard CRUD service/repository, use `docs/agent/STANDARD_CRUD_SERVICE_REPOSITORY.md`.
3. For owner-scoped rows, inspect `backend-security`; permission checks alone do not replace owner predicates. MyBatis and native SQL must state soft-delete and ownership conditions explicitly.
4. List `src/main/resources/db/migration/` before choosing a Flyway version. Add a migration instead of editing an existing one, then select PostgreSQL/pgvector evidence per `backend-testing`.

## Rules (summary — binding details in `skills/backend/DATA.md`)

- IDs: Snowflake `Long` via `@PrePersist`; never `IDENTITY`/`SEQUENCE`.
- Audit via `Auditable`/`SoftDeletedAuditable` + `AuditAwareImpl`; pass actor explicitly in async/Reactor paths.
- Soft delete requires field + `@SQLDelete` + `@SQLRestriction`; MyBatis/native SQL must add `deleted=false` + ownership predicates.
- Repositories extend `BaseRepository` + `JpaSpecificationExecutor` where filtering is needed.
- Flyway: `src/main/resources/db/migration/`, `V{version}__{snake_case}.sql`; inspect current versions before adding one; never rewrite deployed migrations; validate PG/pgvector SQL on disposable PostgreSQL, not H2.
- MapStruct: `*Mapper.java`, `componentModel="spring"`, `IGNORE` unmapped targets — review every field intentionally.
