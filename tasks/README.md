# tasks/ — Canonical Task Template + Workflows

> Template: `tasks/TASK_TEMPLATE.md` (15 sections, canonical).
> Instances: `docs/tasks/<number>-<short-name>.md`.
> Behavior: `AGENTS.md §7` (tracking), `§11` (stop/resume).

## Task index

Task instances live in `docs/tasks/`, not in `tasks/`. `tasks/` holds only the template and this workflow guide.

Before assigning a number, list existing files under `docs/tasks/` and confirm the number is free. Do not reuse numbers.

```text
docs/tasks/<number>-<short-name>.md
docs/tasks/<task-number>-curl-test.md   # only when the task changes an HTTP/SSE contract
```

## Template sections (15)

`tasks/TASK_TEMPLATE.md` contains:

1. Task title (`# Task: <name>`)
2. Objective
3. Required Reading
4. Existing Implementation to Inspect
5. Scope
6. Authoritative Contract
7. Required Behavior
8. Backend Requirements
9. External Consumer Impact
10. Progress Checklist (single source of truth)
11. Verification
12. HTTP / Curl Deliverables
13. Resume State (Status lifecycle: `NOT_STARTED → IN_PROGRESS → IMPLEMENTATION_COMPLETE → COMPLETED`, plus `BLOCKED`)
14. Safe Stop / Resume
15. Completion Report

Do not add frontend implementation blocks. Record cross-repo contract impact under `External Consumer Impact`.

## Creation workflow

1. List `docs/tasks/`; pick the next free `<number>`.
2. Copy `tasks/TASK_TEMPLATE.md` to `docs/tasks/<number>-<short-name>.md`.
3. Fill Objective as one bounded outcome; define the Authoritative Contract before implementing.
4. Mark Scope checkboxes; list Out of Scope explicitly.
5. Leave Resume State at `NOT_STARTED` with Next Recommended Steps filled.

## Execution workflow

1. Read: `AGENTS.md` → `SKILLS.md` → `.agents/skills/backend-core/SKILL.md` → this task → only relevant domain skills.
2. Before editing code: set Resume State to `IN_PROGRESS`; record Current Step + Files Currently Being Modified.
3. Inspect existing implementation; prefer modifying the existing path.
4. Implement narrowly; update Progress Checklist incrementally with evidence.
5. Before final verification: set Resume State to `IMPLEMENTATION_COMPLETE`.
6. Verify narrowly first (focused test → `compileJava` → relevant group → full `test`/`bootJar` only when appropriate); report exact commands + results.
7. Inspect `git status`, `git diff`, `git diff --stat`; confirm no unrelated modifications.
8. Set Resume State to `COMPLETED`; fill Completion Report. If blocked, set `BLOCKED` with the blocker under Known Issues / Blockers.

AI/RAG is optional: ordinary tasks must not reference RAG. See `AGENTS.md §6`.
