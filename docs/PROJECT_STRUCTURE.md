# โครงสร้าง SKILL (Skill Structure)

> เฉพาะส่วน skill/reference ของโปรเจ็คนี้ — behavior ดู `AGENTS.md`, routing ดู `SKILLS.md`

## หลักการแบ่งไฟล์

| ชั้น | ที่อยู่ | ตอบคำถาม | อ่านเมื่อ |
|---|---|---|---|
| กฎกลาง | `AGENTS.md` | อะไรที่บังคับใช้เสมอ | ทุกงาน |
| Router | `SKILLS.md` | งานนี้ต้องโหลด skill ไหน | ทุกงาน |
| Playbook | `.agents/skills/<skill>/SKILL.md` | **ทำอย่างไร (HOW)** — ขั้นตอน, โค้ดตัวอย่าง, checklist, ข้อผิดพลาดที่พบบ่อย | ตาม skill ที่เลือก |
| Reference | `skills/backend/<GUIDE>.md` | **อะไร/ทำไม (WHAT/WHY)** — ข้อเท็จจริงที่ตรวจกับโค้ดแล้ว + กฎพร้อมหลักฐาน `Class#member` | อ่านคู่กับ playbook |
| Recipe/lookup | `docs/agent/*.md` | สูตร CRUD ครบวงจร, known issues, แผนผังโปรเจ็ค | เมื่อ playbook ชี้ไป |
| Task | `docs/tasks/<n>-<name>.md` | สัญญา (contract) และความคืบหน้าของงานนี้ | เมื่อมีไฟล์ task |

Playbook กับ reference ของ skill เดียวกันไม่ซ้ำเนื้อหากัน: ทำตาม playbook แล้วเปิด reference เพื่อดูข้อเท็จจริง

## ลำดับการอ่าน

```text
AGENTS.md → SKILLS.md → backend-core (SKILL.md + skills/backend/CORE.md)
  → docs/tasks/<task>.md (ถ้ามี)
  → skill ที่เกี่ยวข้อง (playbook + reference)
  → backend-testing (ก่อนปิดงาน)
```

## โครงสร้างไฟล์

```text
├── AGENTS.md                        # กฎกลางของ agent (§§1–14)
├── SKILLS.md                        # Router: เลือก skill, ลำดับการอ่าน, quick picks
├── CLAUDE.md                        # Claude adapter (pointer เท่านั้น)
├── GEMINI.md                        # Gemini adapter (pointer เท่านั้น)
├── .github/
│   └── copilot-instructions.md      # GitHub Copilot adapter (pointer)
├── .agents/
│   └── skills/                      # PLAYBOOKS (HOW) — Agent Skills spec
│       ├── backend-core/SKILL.md            # ทุกงาน: workflow, ตำแหน่งไฟล์, เลือก skill ต่อ
│       ├── backend-api/SKILL.md             # เพิ่ม/แก้ endpoint, DTO, validator, list/search
│       ├── backend-data/SKILL.md            # เพิ่ม column/entity, MyBatis, migration, seed permission
│       ├── backend-security/SKILL.md        # ป้องกัน endpoint, owner scoping, public route, token
│       ├── backend-files/SKILL.md           # เก็บ/เสิร์ฟ/ลบไฟล์, streaming
│       ├── backend-async-messaging/SKILL.md # @Async, RabbitMQ, @Scheduled
│       ├── backend-ai-rag/SKILL.md          # Spring AI/Qdrant/SSE (โมดูลเสริม เฉพาะงาน AI)
│       └── backend-testing/SKILL.md         # เลือกหลักฐาน, เขียนเทสต์, รันคำสั่ง, รายงานผล
├── skills/
│   └── backend/                     # REFERENCES (WHAT/WHY) — ข้อเท็จจริง + กฎ binding
│       ├── CORE.md                  # สถาปัตยกรรม, ตารางตำแหน่งโค้ด, building blocks, code generator
│       ├── API.md                   # Response/status, validation, error shape, _q/_keyword/paging
│       ├── DATA.md                  # Flyway ปิดอยู่, Snowflake ID, base class, soft delete, permission data
│       ├── SECURITY.md              # 3 boundaries, filter, principal ไม่มี authority, refresh, cookie
│       ├── FILES.md                 # upload root = public /cdn, containment, Range, chunk merge
│       ├── ASYNC_MESSAGING.md       # executor "asyncExecutor", queue topology, ไม่มี @RabbitListener
│       ├── AI_RAG.md                # Qdrant gate, ingestion, SSE events, memory, tools
│       └── TESTING.md               # setup เทสต์, inject I18n, evidence matrix, คำสั่ง gradle
├── tasks/
│   ├── TASK_TEMPLATE.md             # Task template (canonical)
│   └── README.md                    # ขั้นตอนสร้าง/ทำ task
└── docs/
    ├── agent/
    │   ├── README.md                            # Index ของ docs/agent
    │   ├── STANDARD_CRUD_SERVICE_REPOSITORY.md  # สูตรสร้าง CRUD resource ครบวงจร
    │   ├── KNOWN_ISSUES.md                      # Legacy/บั๊กที่รู้แล้ว (ห้าม copy เป็น pattern)
    │   ├── PROJECT_REFERENCE.md                 # แผนผังโปรเจ็ค, dependencies, naming evidence
    │   ├── SPLIT_MAP.md                         # ที่มาของการแยกไฟล์ (audit trail)
    │   └── ORIGINAL_SKILLS.md                   # ต้นฉบับเก่า (audit เท่านั้น, บางส่วนล้าสมัย)
    └── tasks/
        └── .gitkeep                 # Task instances (<number>-<short-name>.md)
```
