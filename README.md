# Spring Boot Backend Agent Guides

> Entry point for agents. Binding behavior: `AGENTS.md`. Skill index: `SKILLS.md`.

Recommended behavior:

```text
Every backend task
  -> AGENTS.md
  -> SKILLS.md
  -> .agents/skills/backend-core/SKILL.md
  -> only the relevant domain skill(s)
```

Structure:

```text
AGENTS.md                        # Global agent instructions (§§1–14)
SKILLS.md                        # Canonical skill pointer list
CLAUDE.md / GEMINI.md            # Adapters (pointers, not source of truth)
.github/copilot-instructions.md  # Copilot adapter (pointer)
.agents/skills/                  # Canonical skills (Agent Skills spec)
skills/backend/                  # Detailed domain references
tasks/TASK_TEMPLATE.md           # Canonical task template
tasks/README.md                  # Task workflows (instances in docs/tasks/)
docs/agent/                      # Agent reference docs
docs/tasks/                      # Task instances
```

Examples:

```text
Add CRUD endpoint
  -> backend-api + backend-data + backend-testing

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

See `AGENTS.md §6` for full routing and `tasks/README.md` for task creation/execution.
