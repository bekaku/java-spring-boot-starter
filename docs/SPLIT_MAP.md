# Split Map

Mapping from the original `SKILLS.md` to the split guide set.

| Original section | New location | Load when |
|---|---|---|
| Enforcement Instructions | `AGENTS.md`, `skills/backend/SKILL.md` | Every backend task |
| 1 Backend Overview | `docs/PROJECT_REFERENCE.md` | Architecture/dependency lookup |
| 2 Directory Structure | `docs/PROJECT_REFERENCE.md` | Navigation/structure changes |
| 3 Coding Conventions | `skills/backend/SKILL.md` + `docs/PROJECT_REFERENCE.md` | Core; detailed lookup as needed |
| 4 Architecture Rules | `skills/backend/SKILL.md` + `docs/PROJECT_REFERENCE.md` | Core; detailed lookup as needed |
| 5 API Contract | `skills/backend/API.md` | REST/API/DTO/controller changes |
| 6 Data Layer | `skills/backend/DATA.md` | JPA/MyBatis/Flyway/DB changes |
| 7 Security | `skills/backend/SECURITY.md` | Auth/RBAC/cookie/ownership changes |
| 8 AI/RAG | `skills/backend/AI_RAG.md` | AI/RAG tasks only |
| 9 Do/Don't | distilled into core + domain guides | Relevant domain only |
| 10 Testing | `skills/backend/TESTING.md` | Validation/testing |
| 11 Known Issues | `docs/KNOWN_ISSUES.md` | Debugging/touching legacy areas |

Additional focused guides were created for:
- `FILES.md` — storage/upload/download/streaming boundary
- `ASYNC_MESSAGING.md` — RabbitMQ/async/scheduling boundary

The original source is preserved at `docs/ORIGINAL_SKILLS.md` for audit/reference, but agents should not load it by default.
