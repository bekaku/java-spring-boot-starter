# Spring Boot Backend Agent Guides

> Entry point for agents. Binding behavior: `AGENTS.md`. Skill router: `SKILLS.md`.

Recommended behavior:

```text
Every backend task
  -> AGENTS.md
  -> SKILLS.md                      (pick skills)
  -> backend-core playbook + skills/backend/CORE.md
  -> only the relevant skill(s): playbook + reference
  -> backend-testing before final validation
```

Structure:

```text
AGENTS.md                        # Global agent instructions (§§1–14)
SKILLS.md                        # Skill router: which skills, loading order, quick picks
CLAUDE.md / GEMINI.md            # Adapters (pointers, not source of truth)
.github/copilot-instructions.md  # Copilot adapter (pointer)
.agents/skills/<skill>/SKILL.md  # Playbooks (HOW): steps, code shapes, checklists, common mistakes
skills/backend/*.md              # References (WHAT/WHY): verified facts + binding rules (CORE.md, API.md, ...)
docs/agent/                      # End-to-end CRUD recipe, known issues, project map
tasks/TASK_TEMPLATE.md           # Canonical task template
tasks/README.md                  # Task workflows (instances in docs/tasks/)
docs/tasks/                      # Task instances
```

Examples:

```text
New CRUD resource
  -> backend-api + backend-data + backend-security + backend-testing
     + docs/agent/STANDARD_CRUD_SERVICE_REPOSITORY.md

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

See `SKILLS.md` for the full routing table and `tasks/README.md` for task creation/execution.
