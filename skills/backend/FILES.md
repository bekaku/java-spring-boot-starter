# Files / Storage / Streaming

Read this for file upload/download, CDN paths, chunk merge, media streaming, file ownership, or filesystem operations.

## Core rules

- File operations are not rolled back by DB transactions. Define durable-success and compensation order for multi-step DB/filesystem work.
- Preserve owner/creator scoping for user-owned files. Permission checks alone do not replace ownership checks.
- Stream large content and preserve HTTP Range behavior on video/file endpoints.
- Preserve real-path containment checks and use server-generated filenames for filesystem writes.
- Validate public URLs with the existing URL utility and validate every redirect when fetching remote content.
- Never place secrets, logs, backups, or private material under publicly mapped storage.
- Private files must remain behind authenticated owner-aware endpoints.
- Do not rely on path containment alone as per-user authorization.
- Chunk merge is non-atomic because chunks are deleted while copying; changes must consider partial failure/recovery.

## Verification

- File changes should include containment, ownership, and partial-failure tests using `@TempDir` where practical.
- Existing `FileManagerControllerTest` is a standalone MockMvc pattern, not proof of the full security chain.

## Related guides

- Read `SECURITY.md` for owner/permission boundaries.
- Read `DATA.md` when file metadata persistence changes.
- Read `docs/KNOWN_ISSUES.md` when touching public storage mappings or chunk merge.
