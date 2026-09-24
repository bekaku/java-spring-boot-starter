# Task Template

Use this template for **backend tasks** in this repository.

This repo is a **backend-only Spring Boot starter** (`src/main/java/com/bekaku/api/spring/`,
`src/main/resources/`, `src/test/`, `build.gradle`). There is no `backend/` or `frontend/`
directory here. The frontend lives in an **external repository** — do not add frontend
implementation blocks to tasks in this repo. When a change affects external consumers,
record the contract impact under `External Consumer Impact` instead.

AI/RAG (Spring AI / Qdrant) is an **optional module**, not the focus of this starter.
Ordinary CRUD/auth/file/messaging work must **not** reference RAG. Load the
`backend-ai-rag` skill (`.agents/skills/backend-ai-rag/SKILL.md` + `skills/backend/AI_RAG.md`)
only when the task touches `ai/`, `extraction/`, vector-store config, ingestion, or chat/SSE endpoints.

Save task specs in the repository location appropriate to the work, e.g.:

```text
docs/tasks/<number>-<short-name>.md
```

Before assigning a number, list existing files under `docs/tasks/` (if it exists) and
confirm the number is free. Do not reuse a number already taken by another task.

# Task: <name>

## Objective

Describe one concrete outcome — the implementation, refactor, migration or fix required.

```text
...
```

Keep the objective bounded.

Do not include unrelated cleanup or future roadmap work.

---

## Required Reading

Always read:

```text
AGENTS.md
SKILLS.md
.agents/skills/backend-core/SKILL.md + skills/backend/CORE.md
<this-task>
```

Then read only the relevant skills — playbook (HOW) + reference (WHAT / WHY).
Tick the ones this task needs and delete the rest:

```text
[ ] API       .agents/skills/backend-api/SKILL.md             + skills/backend/API.md
[ ] Data      .agents/skills/backend-data/SKILL.md            + skills/backend/DATA.md
[ ] Security  .agents/skills/backend-security/SKILL.md        + skills/backend/SECURITY.md
[ ] Files     .agents/skills/backend-files/SKILL.md           + skills/backend/FILES.md
[ ] Async     .agents/skills/backend-async-messaging/SKILL.md + skills/backend/ASYNC_MESSAGING.md
[ ] AI/RAG    .agents/skills/backend-ai-rag/SKILL.md          + skills/backend/AI_RAG.md (AI tasks only)
[x] Testing   .agents/skills/backend-testing/SKILL.md         + skills/backend/TESTING.md
```

Read `docs/agent/KNOWN_ISSUES.md` only when debugging or touching a listed legacy area.
Read `docs/agent/PROJECT_REFERENCE.md` only when repository layout or dependency context is needed.
When creating a standard CRUD resource, follow the end-to-end recipe in
`docs/agent/STANDARD_CRUD_SERVICE_REPOSITORY.md` first.

Do not load unrelated skill/reference files by default.

---

## Existing Implementation to Inspect

Inspect the existing implementation before editing.

```text
Controllers/endpoints:
- ...

Services:
- ...

DTOs/contracts:
- ...

Persistence (JPA / MyBatis / Flyway migration):
- ...

Security (permissions / ownership / tenant scope):
- ...

Tests:
- ...
```

Prefer modifying the existing path over creating a parallel implementation.

---

## Scope

Mark only what applies:

- [ ] REST / SSE API
- [ ] DTO / validation
- [ ] Security / tenant scope
- [ ] Database / migration (JPA / MyBatis / Flyway)
- [ ] Files / storage
- [ ] Messaging / async / scheduling
- [ ] AI / RAG (optional module only)
- [ ] Tests
- [ ] Documentation

### Out of Scope

```text
- ...
- ...
```

Do not modify unrelated areas unless required for a narrowly justified compatibility fix.
Preserve intentional legacy spellings (`serviceImpl`, `DevelopmentContoller`,
`/api/faceRegconition`, `AiFaceRegconitionServiceClient`) unless this task explicitly
changes the contract.

---

## Authoritative Contract

Define the API contract before implementing. This contract is authoritative for all
consumers, including the external frontend repository.

### Endpoint

```http
<METHOD> /api/...
```

### Request

```json
{}
```

### Success Response

```json
{}
```

### Error Response

```json
{}
```

### Streaming / Events

Include only when applicable (e.g. SSE chat endpoints).

```text
...
```

### Authentication / Authorization

```text
...
```

### Precision / Serialization

Document only relevant cross-boundary rules.

For example:

```text
Snowflake IDs exposed to JavaScript are serialized using the project's
existing precision-safe strategy.
```

### Compatibility

Existing callers/contracts that must continue to work:

```text
- ...
```

---

## Required Behavior

Describe the complete backend flow.

```text
API request
    ↓
Backend validation / authorization
    ↓
Domain operation
    ↓
Backend response / event
```

Required behavior:

```text
1. ...
2. ...
3. ...
```

---

## Backend Requirements

Include only task-specific backend behavior.

```text
- ...
```

Do not duplicate generic rules already defined in `AGENTS.md` or backend skills.

---

## External Consumer Impact

Fill in only when the task adds or changes an HTTP/SSE contract consumed outside this
repo (e.g. by the external frontend). Otherwise state `None`.

```text
Request field names/types changed:
Response field names/types changed:
Status/error handling changed:
Auth transport changed:
Optional/null behavior changed:
Legacy callers affected:
```

Do not invent frontend behavior here — record only what the backend guarantees.

---

## Progress Checklist

This is the **single source of truth** for task completion. Update this checklist and Resume State incrementally as work progresses.

### Initial Setup & Inspection

- [ ] Transition Resume State to `IN_PROGRESS` and record initial `Current Step` before editing code.
- [ ] Inspect current backend implementation.
- [ ] Confirm the existing contract.
- [ ] Finalize the authoritative target contract.
- [ ] Confirm compatibility requirements.

### Implementation

- [ ] Complete required backend changes.
- [ ] Complete backend validation/security changes.
- [ ] Complete persistence/integration changes if applicable.
- [ ] Add/update focused backend tests.

### Verification

- [ ] Transition Resume State to `IMPLEMENTATION_COMPLETE` before final verification.
- [ ] Verify success/error responses are handled correctly.
- [ ] Verify precision-sensitive IDs where applicable.
- [ ] Verify authentication/authorization boundaries.
- [ ] Run focused backend verification (only tests related to this task).
- [ ] Run broader builds/tests only when the impact justifies it (otherwise record `Skipped — <reason>`).
- [ ] Review `git status`, `git diff`, `git diff --stat`.
- [ ] Confirm no unrelated modifications remain.
- [ ] Transition Resume State to `COMPLETED` and finalize Completion Report.

Mark `[x]` incrementally only after implementation and repository evidence verify the item.

---

## Verification

Test only what this task changed. Do NOT run the full suite by default.
Use this project's actual verification commands (see `skills/backend/TESTING.md`).
Use the narrowest useful validation first.

```bash
./gradlew test --tests '<FocusedTest>'   # required: only the related test(s)
./gradlew compileJava                    # when Java sources changed
```

Run these only when the task impact justifies them — never as a default:

```bash
./gradlew test      # only for cross-cutting / shared-base / security-chain / migration / build-config changes
./gradlew bootJar   # only when packaging is affected
```

Report the exact commands run and their actual results. If the full suite was
skipped, state `Skipped — <reason>` (e.g. `Skipped — single-endpoint change, focused test covers it`).
Do not claim deployment, security, integration, or production success without
evidence. Do not report a command that was not executed as passing.

---

## HTTP / Curl Deliverables

Include this section only when the task adds or changes an HTTP/SSE contract.

Create/update the companion curl guide and flow documentation, e.g.:

```text
docs/tasks/<task-number>-curl-test.md
docs/<FLOW>.md
```

The guide should include:

- prerequisites
- authentication / permission requirements
- request example
- success response
- error response
- SSE examples when applicable
- assertions
- safe cleanup

State `No request body` for endpoints without a body.

Use synthetic data only.

Clearly distinguish documented examples from commands actually executed.

---

## Resume State

### Status Lifecycle Protocol (Mandatory)
Follow this 3-phase status lifecycle:
1. **Start (Before editing code):** Transition `Overall Status` from `NOT_STARTED` to `IN_PROGRESS`. Record initial `Current Step` and anticipated `Files Currently Being Modified`.
2. **During Implementation:** Incrementally mark `[x]` on `Progress Checklist` items as they are finished. Keep `Current Step`, `Completed Work`, and `Partially Completed Work` up to date. Once code edits are finished and verification begins, transition `Overall Status` to `IMPLEMENTATION_COMPLETE`. If an unexpected issue or blocker arises, set `Overall Status` to `BLOCKED` and describe it in `Known Issues / Blockers`.
3. **Completion (After verification):** Transition `Overall Status` to `COMPLETED` only after all verification commands succeed, git diff is reviewed clean, and the `Completion Report` is filled.

**Overall Status:** `NOT_STARTED | IN_PROGRESS | IMPLEMENTATION_COMPLETE | COMPLETED | BLOCKED`

**Current Step:**

```text
...
```

**Last Successful Backend Verification:**

```text
None
```

**Completed Work:**

```text
- None
```

**Partially Completed Work:**

```text
- None
```

**Files Currently Being Modified:**

```text
- None
```

**Known Issues / Blockers:**

```text
- None
```

**Next Recommended Steps:**

```text
1. ...
```

Do not mark the task `BLOCKED` only because of an unrelated pre-existing repository issue.

---

## Safe Stop / Resume

Before stopping incomplete work:

1. finish the current safe atomic change where practical;
2. update Progress Checklist;
3. update Resume State;
4. inspect `git status`, `git diff`, `git diff --stat`;
5. record partial backend work;
6. record the exact next action.

When resuming:

1. read `AGENTS.md`;
2. read `SKILLS.md`;
3. read `.agents/skills/backend-core/SKILL.md` + `skills/backend/CORE.md`;
4. read this task;
5. load only relevant domain skills;
6. inspect Progress Checklist and Resume State;
7. inspect repository state/diff;
8. continue from Next Recommended Steps.

---

## Completion Report

Report only applicable categories:

```text
Implemented / reused:
Backend changes:
API / contract changes:
Database / migration:
Security / tenant scope:
Files / storage:
Messaging / async:
AI / RAG (when applicable):
External consumer impact:
Backend verification:
Integrations not executed:
Remaining risks:
Final status:
```

Do not stop at conceptual recommendations.

Make the repository changes required by the task.
