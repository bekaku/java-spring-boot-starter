# docs/agent/ — Agent Reference Docs

> Index only. Binding behavior lives in `AGENTS.md`; skill routing in `SKILLS.md`.
> Load reference files only when indicated — never by default.

| File | Purpose | Load when |
|---|---|---|
| `PROJECT_REFERENCE.md` | Repo layout, dependencies, naming/architecture evidence | Layout, dependency, naming, or architecture context is required |
| `KNOWN_ISSUES.md` | Observed problems / legacy exceptions (do not copy as patterns) | Debugging or touching a listed legacy area |
| `STANDARD_CRUD_SERVICE_REPOSITORY.md` | End-to-end recipe for a new CRUD resource: entity, migration + permission seed, i18n, DTO, mapper, repository, service, controller, tests | Creating a standard CRUD resource |
| `SPLIT_MAP.md` | Original-to-split section mapping (audit trail) | Tracing where an original rule moved |
| `ORIGINAL_SKILLS.md` | Original unsplit source (archive, partly outdated) | Audit only — do not load by default |

Task instances live in `docs/tasks/` (see `tasks/README.md`), not here.
