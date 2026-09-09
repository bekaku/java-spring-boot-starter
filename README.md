# Spring Boot Backend Agent Guides

Drop these files at the backend repository root.

Recommended behavior:

```text
Every backend task
  -> AGENTS.md
  -> skills/backend/SKILL.md
  -> only the relevant domain guide(s)
```

Examples:

```text
Add CRUD endpoint
  -> API.md + DATA.md + TESTING.md

Fix refresh token security
  -> SECURITY.md + TESTING.md

Add RabbitMQ consumer
  -> ASYNC_MESSAGING.md + DATA.md (if persistence involved) + TESTING.md

Fix file streaming
  -> FILES.md + SECURITY.md + TESTING.md

Ordinary user CRUD
  -> DO NOT read AI_RAG.md

RAG ingestion change
  -> AI_RAG.md + DATA.md + FILES.md + TESTING.md as applicable
```
