---
name: backend-testing
description: Testing and verification evidence rules for backend changes. Load before final validation of any implementation work.
---

# Backend Testing — Canonical

> Canonical testing skill. Detailed rules: `skills/backend/TESTING.md`.
> Requires: `AGENTS.md`, `.agents/skills/backend-core/SKILL.md`.

## When to load

Before final validation of implementation work.

## Select evidence

1. Match the check to the boundary changed: focused unit tests for service/controller logic; real filter-chain HTTP tests for auth/routes; disposable PostgreSQL plus pgvector for PG-specific SQL; `@TempDir` for file safety; mocked model/vector dependencies for SSE behavior.
2. Run the narrowest relevant check first, then `./gradlew compileJava`. Expand to a relevant group or full `test`/`bootJar` only for a concrete remaining risk. `--tests` filters execution but still compiles all test sources.
3. Report exact commands and actual results. Distinguish compile, unit, filter-chain, database, external-service, and deployment evidence. For documentation-only edits, verify paths, links, factual claims, and diff; do not imply runtime validation.

## Rules (summary — binding details in `skills/backend/TESTING.md`)

- Location: `src/test/java/com/bekaku/api/spring/` (`controller/api/`, `serviceImpl/`). JUnit Jupiter + Mockito (`LENIENT`) + AssertJ.
- Controller/service logic: focused unit test incl. failure path + ownership (`findByIdAndCreator` scoping, `verifyNoInteractions` on bypass).
- Auth/routes: HTTP test through the real filter chain (cookie AND Bearer); direct controller construction is insufficient.
- SQL/schema/vector: disposable PostgreSQL + pgvector integration test (H2 is invalid).
- File ops: `@TempDir` containment/ownership/partial-failure tests. RAG/SSE: mock model/vector deps, verify event order/disabled mode/errors.
- Narrowest validation first: focused test → `compileJava` → relevant group → full `test` / `bootJar` only when appropriate.
- Report exact commands + actual results; never claim unexecuted success.
