# Copilot Instructions — Adapter (Pointer Only)

> Not the source of truth. Binding rules live in `AGENTS.md` at the repository root.
> Skill routing lives in `SKILLS.md` at the repository root.

## Required reading (in order)

1. `AGENTS.md` — global behavior, layering, task lifecycle, validation (§§1–14)
2. `SKILLS.md` — skill router
3. `.agents/skills/backend-core/SKILL.md` + `skills/backend/CORE.md` — core playbook + reference
4. The task file itself (when one exists; see `tasks/README.md` for locations)

Then load only the relevant skill(s) — playbook `.agents/skills/<skill>/SKILL.md` (HOW) + reference `skills/backend/<GUIDE>.md` (WHAT/WHY) — per `SKILLS.md` / `AGENTS.md §6`. Ordinary CRUD/auth/file/messaging work must not load the AI/RAG skill.

## Scope

Backend only: `src/main/java/com/bekaku/api/spring/`, `src/main/resources/`, `src/test/`, `build.gradle`.

## Rules

- Follow the existing layered architecture; keep changes minimal.
- Preserve intentional legacy spellings (`serviceImpl`, `DevelopmentContoller`, `/api/faceRegconition`, `AiFaceRegconitionServiceClient`).
- Never weaken auth, ownership, validation, or scoping; never expose secrets/tokens.
- Validate narrowly first; report exact commands and actual results.
- Task tracking, verification, and safe stop/resume follow `AGENTS.md §§7, 10, 11`. Canonical task template: `tasks/TASK_TEMPLATE.md`.
