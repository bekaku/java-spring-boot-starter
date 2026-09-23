# โครงสร้าง SKILL (Skill Structure)

> เฉพาะส่วน skill/reference ของโปรเจ็คนี้ — behavior ดู `AGENTS.md`, routing ดู `SKILLS.md`

```text
├── AGENTS.md                        # Global agent instructions (§§1–14)
├── SKILLS.md                        # Canonical skill pointer list
├── CLAUDE.md                        # Claude adapter (pointer, not source of truth)
├── GEMINI.md                        # Gemini adapter (pointer, not source of truth)
├── .github/
│   └── copilot-instructions.md      # GitHub Copilot adapter (pointer)
├── .agents/
│   └── skills/                      # CANONICAL skills (Agent Skills spec)
│       ├── backend-core/
│       │   └── SKILL.md             # Core ทุกงาน backend: layering, JPA-vs-MyBatis, convention
│       ├── backend-api/
│       │   └── SKILL.md             # Controller/DTO/paging/validation/response
│       ├── backend-data/
│       │   └── SKILL.md             # JPA/MyBatis/Flyway/MapStruct/ID/audit/soft-delete
│       ├── backend-security/
│       │   └── SKILL.md             # JWT/cookie/RBAC/ownership/signup/OTP
│       ├── backend-files/
│       │   └── SKILL.md             # Upload/download/CDN/chunk/streaming
│       ├── backend-async-messaging/
│       │   └── SKILL.md             # @Async/RabbitMQ/scheduler
│       ├── backend-ai-rag/
│       │   └── SKILL.md             # Spring AI/Ollama/Qdrant/SSE (เสริมเท่านั้น)
│       └── backend-testing/
│           └── SKILL.md             # Verify ก่อนปิดงานทุกครั้ง
├── skills/
│   └── backend/                     # DETAILED domain references (binding)
│       ├── SKILL.md                 # Core ฉบับเต็ม: enforcement, architecture, do/don't, routing
│       ├── API.md                   # Contract: BaseApiController, ResponseListDto, ApiError
│       ├── DATA.md                  # Snowflake ID, audit, @SQLDelete/@SQLRestriction, Flyway
│       ├── SECURITY.md              # 3 boundaries, refresh rotation, cookie, permission
│       ├── FILES.md                 # Filesystem safety, Range, chunk non-atomic
│       ├── ASYNC_MESSAGING.md       # Idempotency, retry/DLQ (ไม่มี @RabbitListener ที่ active)
│       ├── AI_RAG.md                # MVC SSE, Qdrant gate, ingestion, memory, tools
│       └── TESTING.md               # Evidence matrix + คำสั่ง gradle
├── tasks/
│   ├── TASK_TEMPLATE.md             # Canonical task template
│   └── README.md                    # Task creation/execution workflows
└── docs/
    ├── agent/
    │   ├── README.md                            # Index ของ docs/agent
    │   ├── PROJECT_REFERENCE.md                 # Layout/deps/architecture lookup
    │   ├── KNOWN_ISSUES.md                      # Legacy exception (ห้าม copy)
    │   ├── STANDARD_CRUD_SERVICE_REPOSITORY.md  # Template ก่อนสร้าง CRUD
    │   ├── SPLIT_MAP.md                         # Original-to-split audit trail
    │   └── ORIGINAL_SKILLS.md                   # Archive ต้นฉบับ (audit อย่างเดียว)
    └── tasks/
        └── .gitkeep                 # Task instances (<number>-<short-name>.md)
```
