# CLAUDE.md — Claude Adapter (Pointer Only)

> Not the source of truth. Binding rules live in `AGENTS.md`.
> Skill routing lives in `SKILLS.md`.

For every backend task in this repo, read in order:

```text
AGENTS.md
SKILLS.md
.agents/skills/backend-core/SKILL.md + skills/backend/CORE.md
<this-task> (when a task file exists, see tasks/README.md)
```

Then load only the relevant skill(s) — playbook `.agents/skills/<skill>/SKILL.md` (HOW) + reference `skills/backend/<GUIDE>.md` (WHAT/WHY) — per `SKILLS.md` / `AGENTS.md §6`.

Backend scope: `src/main/java/com/bekaku/api/spring/`, `src/main/resources/`, `src/test/`, `build.gradle`.

Task lifecycle, validation, and safe stop/resume: follow `AGENTS.md §§7, 10, 11`. Task template: `tasks/TASK_TEMPLATE.md`.
