---
name: backend-data
description: JPA entities, repositories, MyBatis projections, PostgreSQL, Flyway, MapStruct, IDs, audit, and soft-delete rules. Load when persistence or migrations change.
---

# Backend Data — Canonical

> Canonical data skill. Detailed rules: `skills/backend/DATA.md`.
> Requires: `AGENTS.md`, `.agents/skills/backend-core/SKILL.md`.

## When to load

JPA entities/repositories, MyBatis mappers/XML, PostgreSQL, Flyway migrations, MapStruct persistence mapping, IDs, audit, or soft-delete work.

## Rules (summary — binding details in `skills/backend/DATA.md`)

- IDs: Snowflake `Long` via `@PrePersist`; never `IDENTITY`/`SEQUENCE`.
- Audit via `Auditable`/`SoftDeletedAuditable` + `AuditAwareImpl`; pass actor explicitly in async/Reactor paths.
- Soft delete requires field + `@SQLDelete` + `@SQLRestriction`; MyBatis/native SQL must add `deleted=false` + ownership predicates.
- Repositories extend `BaseRepository` + `JpaSpecificationExecutor` where filtering is needed.
- Flyway: `src/main/resources/db/migration/`, `V{version}__{snake_case}.sql`; never rewrite deployed migrations; validate PG/pgvector SQL on disposable PostgreSQL, not H2.
- MapStruct: `*Mapper.java`, `componentModel="spring"`, `IGNORE` unmapped targets — review every field intentionally.
