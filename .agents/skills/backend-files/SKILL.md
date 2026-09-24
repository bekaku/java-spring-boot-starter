---
name: backend-files
description: Use when a change reads, writes, deletes, lists, uploads, downloads, streams, or serves files — FileManager/FilesDirectory endpoints, CDN/public paths, chunked upload and merge, HTTP Range streaming, image processing, or any filesystem path handling — in this Spring Boot backend.
---

# Backend Files — Playbook

> **Role:** HOW to change file handling safely. Binding facts + evidence: `skills/backend/FILES.md` (read it too).
> **Requires:** `backend-core`. **Usually paired with:** `backend-security` (ownership), `backend-data` (metadata), `backend-testing`.

## Know this first

- The upload root **is** the public `/cdn/**` root. Anything saved there is downloadable without login.
- DB rollback does not undo file writes or deletes.
- Path containment only proves "inside the root", not "belongs to this user".

## Recipe A — store a new kind of file

1. Decide: public (served via `/cdn/**`) or private. Private files must not be reachable through `/cdn/**`; serve them through an authenticated, owner-checked endpoint.
2. Generate the filename on the server; build the path from the configured root; verify containment after resolving.
3. Order the steps and define cleanup:

   ```text
   write file to final path
     → save FileManager row (@Transactional service method)
     → if the save fails: delete the written file (log, do not swallow)
   ```

4. Record the owner (`created_user` via audit, or an explicit owner id from `auth.getId()`).
5. Seed permission codes if a new admin route is added (`backend-data` Recipe D).

## Recipe B — serve / download / stream a file

1. Route under `/api/fileManager/...` (authenticated) unless it is intentionally public.
2. Load the metadata row **by id and owner** (or check an explicit sharing rule). Not owned → `404`.
3. Resolve the path, `toRealPath()`, check it starts with the upload root.
4. Stream the body; support `Range` for media (`206` + `Content-Range`). Do not throw a JSON error after bytes are sent.

## Recipe C — delete files

1. Check ownership/permission first.
2. Soft-delete the row (the `FileManager` `@SQLDelete` also clears `files_directory_id`) and decide when the physical file is removed.
3. Physical delete after the DB change is durable; log failures for later cleanup rather than failing the request silently.

## Done checklist

- [ ] No private data, secrets, logs, or backups under the public root.
- [ ] Server-generated filenames; `..`/NUL rejected; resolved path contained in the root.
- [ ] Owner check on every private read/delete; permission check on admin routes.
- [ ] Durable-success point and cleanup defined for DB + file steps.
- [ ] `Range` behavior preserved on stream routes.
- [ ] Tests with `@TempDir`: traversal rejected, other user's file rejected, partial failure cleaned up.

## Common mistakes

| Mistake | Fix |
|---|---|
| Saving a private export under the upload root | Store outside the public root; serve through an owner-checked route |
| Using `request.getOriginalFilename()` as the path | Generate the name; keep the original only as metadata |
| `if (file.getPath().startsWith(root))` on an unresolved path | Resolve with `toRealPath()` / `getCanonicalPath()` first |
| Assuming `@Transactional` rolls back a written file | Delete it explicitly in the failure path |
| Treating `mergeChunkApi` as atomic | Handle partial failure; chunks are deleted while copying |
