# Split Map

Mapping from the original `SKILLS.md` to the split guide set.
Canonical skills now live in `.agents/skills/`; detailed guides stay in `skills/backend/`; references in `docs/agent/`.

| Original section | Canonical skill | Detailed guide / reference | Load when |
|---|---|---|---|
| Enforcement Instructions | `.agents/skills/backend-core/SKILL.md` | `AGENTS.md`, `skills/backend/SKILL.md` | Every backend task |
| 1 Backend Overview | — | `docs/agent/PROJECT_REFERENCE.md` | Architecture/dependency lookup |
| 2 Directory Structure | — | `docs/agent/PROJECT_REFERENCE.md` | Navigation/structure changes |
| 3 Coding Conventions | `.agents/skills/backend-core/SKILL.md` | `skills/backend/SKILL.md` + `docs/agent/PROJECT_REFERENCE.md` | Core; detailed lookup as needed |
| 4 Architecture Rules | `.agents/skills/backend-core/SKILL.md` | `skills/backend/SKILL.md` + `docs/agent/PROJECT_REFERENCE.md` | Core; detailed lookup as needed |
| 5 API Contract | `.agents/skills/backend-api/SKILL.md` | `skills/backend/API.md` | REST/API/DTO/controller changes |
| 6 Data Layer | `.agents/skills/backend-data/SKILL.md` | `skills/backend/DATA.md` | JPA/MyBatis/Flyway/DB changes |
| 7 Security | `.agents/skills/backend-security/SKILL.md` | `skills/backend/SECURITY.md` | Auth/RBAC/cookie/ownership changes |
| 8 AI/RAG | `.agents/skills/backend-ai-rag/SKILL.md` | `skills/backend/AI_RAG.md` | AI/RAG tasks only |
| 9 Do/Don't | `.agents/skills/backend-core/SKILL.md` + domain skills | distilled into core + domain guides | Relevant domain only |
| 10 Testing | `.agents/skills/backend-testing/SKILL.md` | `skills/backend/TESTING.md` | Validation/testing |
| 11 Known Issues | — | `docs/agent/KNOWN_ISSUES.md` | Debugging/touching legacy areas |

Additional focused guides:
- Files: `.agents/skills/backend-files/SKILL.md` + `skills/backend/FILES.md` — storage/upload/download/streaming boundary
- Async: `.agents/skills/backend-async-messaging/SKILL.md` + `skills/backend/ASYNC_MESSAGING.md` — RabbitMQ/async/scheduling boundary

The original source is preserved at `docs/agent/ORIGINAL_SKILLS.md` for audit/reference, but agents should not load it by default.
