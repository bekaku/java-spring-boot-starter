---
name: backend-files
description: File upload and download, CDN paths, chunk merge, media streaming, file ownership, and filesystem safety. Load when files or storage change.
---

# Backend Files — Canonical

> Canonical files skill. Detailed rules: `skills/backend/FILES.md`.
> Requires: `AGENTS.md`, `.agents/skills/backend-core/SKILL.md`. Related: backend-security, backend-data.

## When to load

File upload/download, CDN paths, chunk merge, media streaming, file ownership, or filesystem operations.

## Rules (summary — binding details in `skills/backend/FILES.md`)

- DB transactions do not roll back filesystem effects; define durable-success and compensation order.
- Preserve owner/creator scoping; permission checks alone do not replace ownership checks.
- Stream large content; preserve HTTP `Range` behavior.
- Preserve real-path containment + server-generated filenames; validate public URLs per redirect.
- Never place secrets, logs, backups, or private material under publicly mapped storage; private files stay behind authenticated owner-aware paths.
- Chunk merge is non-atomic — handle partial failure/recovery; include containment/ownership/partial-failure tests (`@TempDir`).
