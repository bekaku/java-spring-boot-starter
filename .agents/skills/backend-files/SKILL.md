---
name: backend-files
description: File upload and download, CDN paths, chunk merge, media streaming, file ownership, and filesystem safety. Load when files or storage change.
---

# Backend Files — Canonical

> Canonical files skill. Detailed rules: `skills/backend/FILES.md`.
> Requires: `AGENTS.md`, `.agents/skills/backend-core/SKILL.md`. Related: backend-security, backend-data.

## When to load

File upload/download, CDN paths, chunk merge, media streaming, file ownership, or filesystem operations.

## Implementation path

1. Trace the route in `FileManagerController` or `FilesDirectoryController` through its service, metadata repository, and filesystem path. Check `WebConfigurerAdapter` public mappings separately from authenticated routes.
2. Check both real-path containment and owner scoping. A path inside the storage root can still belong to another user. Keep private material outside the publicly mapped root.
3. For DB plus filesystem changes, identify the durable-success point and cleanup after a partial failure. Chunk merge deletes chunks while copying, so treat it as non-atomic.
4. Test containment, ownership, Range behavior, and partial failure where affected. Use `backend-security` for access checks and `backend-data` when metadata persistence changes.

## Rules (summary — binding details in `skills/backend/FILES.md`)

- DB transactions do not roll back filesystem effects; define durable-success and compensation order.
- Preserve owner/creator scoping; permission checks alone do not replace ownership checks.
- Stream large content; preserve HTTP `Range` behavior.
- Preserve real-path containment + server-generated filenames; validate public URLs per redirect.
- Never place secrets, logs, backups, or private material under publicly mapped storage; private files stay behind authenticated owner-aware paths.
- Chunk merge is non-atomic — handle partial failure/recovery; include containment/ownership/partial-failure tests (`@TempDir`).
