# SKILLS.md

Backend skills are split by concern so agents do not need to load the full backend rule set for every task.

## Core
- `skills/backend/SKILL.md`

## Domain guides
- API / controllers / DTO contract: `skills/backend/API.md`
- Persistence / JPA / MyBatis / Flyway: `skills/backend/DATA.md`
- Security / JWT / cookies / RBAC / ownership: `skills/backend/SECURITY.md`
- Files / storage / uploads / streaming: `skills/backend/FILES.md`
- RabbitMQ / async / scheduling: `skills/backend/ASYNC_MESSAGING.md`
- Optional AI/RAG module: `skills/backend/AI_RAG.md`
- Testing / validation: `skills/backend/TESTING.md`

## Reference docs
- Repository/dependency overview: `docs/PROJECT_REFERENCE.md`
- Known issues and legacy exceptions: `docs/KNOWN_ISSUES.md`
- Original unsplit source: `docs/ORIGINAL_SKILLS.md`
- Split map: `docs/SPLIT_MAP.md`

Read only the files relevant to the current task. AI/RAG is optional and must not be loaded for ordinary backend tasks.
