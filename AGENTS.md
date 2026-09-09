# AGENTS.md

## Project

General-purpose/open-source Spring Boot backend.

Primary stack:
- Java 25
- Spring Boot 4.1
- Spring MVC / Tomcat
- Spring Security + JJWT
- Spring Data JPA + PostgreSQL + Flyway
- MyBatis for read-model projections
- MapStruct + Lombok
- RabbitMQ / Async / Scheduling
- Optional Spring AI / Qdrant modules

## Core Working Rules

- Make the smallest correct change for the current task.
- Follow the existing layered architecture.
- Do not refactor unrelated legacy code.
- Preserve intentional legacy spellings unless the task explicitly changes them.
- Services own business logic and transaction boundaries.
- Controllers must not perform direct database/business writes.
- Never weaken authentication, authorization, ownership, validation, or tenant/user scoping.
- Never expose secrets, raw tokens, credentials, password hashes, or internal driver errors.
- Prefer existing project patterns and utilities before adding abstractions or dependencies.
- Report verification commands and actual results.

## Required Reading

For every backend implementation task:
- `skills/backend/SKILL.md`

Then read only the relevant domain guide:
- REST/controller/API contract -> `skills/backend/API.md`
- JPA/MyBatis/Flyway/data changes -> `skills/backend/DATA.md`
- Authentication/authorization/ownership/cookies -> `skills/backend/SECURITY.md`
- Files/uploads/downloads/CDN/storage -> `skills/backend/FILES.md`
- RabbitMQ/async/scheduling -> `skills/backend/ASYNC_MESSAGING.md`
- AI/RAG/Qdrant/SSE/tools -> `skills/backend/AI_RAG.md`
- Tests/verification -> `skills/backend/TESTING.md`

Read `docs/KNOWN_ISSUES.md` only when debugging or touching a listed legacy area.
Read `docs/PROJECT_REFERENCE.md` only when repository layout or dependency context is needed.

## Scope Rule

This guide applies to the backend only:
- `src/main/java/com/bekaku/api/spring/`
- `src/main/resources/`
- `src/test/`
- `build.gradle`

Frontend/root application concerns are out of scope unless explicitly requested.

## Validation

Use the narrowest useful validation first:
1. focused test
2. `./gradlew compileJava`
3. relevant test group
4. full `./gradlew test` / `bootJar` only when appropriate

Do not claim deployment, security, integration, or production success without evidence.
