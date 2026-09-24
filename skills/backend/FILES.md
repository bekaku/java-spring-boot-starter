# Files / Storage / Streaming — Reference

> **Role:** binding file-handling rules + verified facts (WHAT / WHY).
> **Procedure (HOW):** `.agents/skills/backend-files/SKILL.md`.
> **Evidence style:** `path` + `Class#member` (no line numbers; search the symbol).

## Where files live

- `app.cdn-directory` (default `/usr/spring-data/`) → `app.cdn-path` = `file:///${app.cdn-directory}`.
- `AppProperties#getUploadPath()` returns that same directory: **the upload root is the public root.**
- `configuration/WebConfigurerAdapter#addResourceHandlers` serves `app.cdn-path` at `/{app.cdn-path-alias}/**` (default `/cdn/**`), and both security configs make that path public. Anything written under the upload root is downloadable without login unless it is stored somewhere else.
- Production log files default to `/usr/spring-data/logs` (`log4j2-prod.xml` `APP_LOG_ROOT`, `logging.file.path: ${app.cdn-directory}logs`) — inside the public root. See `docs/agent/KNOWN_ISSUES.md`.

## Main code paths

- `controller/api/FileManagerController` (`/api/fileManager`): upload (`uploadApi`, `uploadBase64Api`, `uploadChunkApi` + `mergeChunkApi`), list (`findAll`, `findAllFolder`, `findAllFile`, `findAllByAdmin`), delete (`/{id}`, `deleteFileApi/{id}`, `internalDeleteFileApi/{id}`), read (`public/{id}`, `images`, `files/download`, `files/stream/{id}`, `video/stream`).
- `controller/api/FilesDirectoryController` → `FilesDirectoryServiceImpl` (folders; throws `responseErrorForbidden` for not-owned folders).
- Metadata: `model/FileManager` (soft delete that also nulls `files_directory_id`), `FilesDirectory`, `FilesDirectoryPath` (composite key). Listing uses MyBatis `FileManagerMybatis` with an owner id and a sort allow-list (`FileManagerController#sortProperties`).
- Permission codes: `file_manager_{list,view,create,edit,delete,manage}`, `files_directory_{list,view,add,edit,delete}` (note `create`, not `add`, for file manager).

## Binding rules

- **Transactions do not cover the filesystem.** Define the durable-success point and the cleanup for each multi-step DB + file operation (write file → save row → on failure delete file, or the reverse).
- **Containment:** resolve with `toRealPath()` / `getCanonicalPath()` and require the result to start with the upload root (`FileManagerController#getImage`, `#isFileAccessAllowed`). Reject `..` and NUL (`#isValidFilePath`), restrict names (`#isValidFileName`).
- **Server-generated filenames** for every write; never use the client filename as a path.
- **Containment is not authorization.** A path inside the root can belong to another user. Private files need an owner check (`created_user` / owner id) behind an authenticated route.
- **Nothing private under the public root:** no secrets, logs, backups, exports, or private user files in a location served by `/cdn/**`.
- **Streaming:** stream large content; keep HTTP `Range` support (`206`, `Content-Range`) on `files/stream` and `video/stream`. These are listed in `JwtTokenFilter.STREAMING_ENDPOINTS` so a late auth error does not write into a committed response.
- **Remote URLs:** validate with `UrlUtil#validatePublicUrl` and re-validate every redirect (`UrlUtil#resolveRedirect`).
- **Chunk merge is non-atomic:** `mergeChunkApi` deletes chunks while copying. A failure midway loses chunks; changes must handle partial failure and recovery. `app.cron.clean-old-temp-chunks` only gates temp-chunk cleanup.
- Upload limits/MIME rules: `FileManagerController#isBlockedMimeType` / `#isAllowedMimeType`, image limits under `app.upload-image`.

## Verification notes

- `FileManagerControllerTest` uses standalone MockMvc — it does not prove the security chain.
- Use `@TempDir` for containment, ownership, and partial-failure tests.
