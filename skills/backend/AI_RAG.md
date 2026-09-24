# Optional AI / RAG Module — Reference

> **Optional module. Do not read for ordinary CRUD/auth/file/messaging tasks.**
> **Role:** binding AI/RAG rules + verified facts (WHAT / WHY).
> **Procedure (HOW):** `.agents/skills/backend-ai-rag/SKILL.md`.
> **Evidence style:** `path` + `Class#member` (no line numbers; search the symbol).

## Stack guard

- MVC SSE with Reactor publishers — not a WebFlux server. `AiChatController#streamChat` (`POST /api/aiChat/stream`, `produces = TEXT_EVENT_STREAM_VALUE`) returns `Flux<ChatStreamEvent>` from `AiRagChatServiceImpl#streamAnswer(userId, request)`.
- Blocking JDBC/filesystem/model work is offloaded (`Mono.fromCallable(...).subscribeOn(Schedulers.boundedElastic())` inside `streamAnswer`). Reactor threads have no security context: `userId` is passed in explicitly.

## Configuration

| Setting | Bound to | Effect |
|---|---|---|
| `spring.ai.vectorstore.qdrant.enabled` | `@ConditionalOnProperty` on `ai/QdrantVectorStoreConfig` | creates the Qdrant beans; `false` in tracked `application-dev-example.yml` |
| `app.rag.*` | `properties/RagProperties` (`topK`, `similarityThreshold`, `chunkSize`, `chunkOverlap`, `minChunkSizeChars`, `minChunkLengthToEmbed`, `maxNumChunks`, `memorySize`, `deleteSourceAfterIngest`, `databaseToolsSchema`, `databaseTools.enabled`) | RAG behavior |
| `app.rag.qdrant-enabled` | **nothing** (not a field of `RagProperties`) | no effect — do not use it as a gate |
| `app.rag.database-tools.enabled` | `RagDatabaseToolsProperties#enabled` | turns on the DB schema/SQL tools and switches the system prompt |

- Ollama/Qdrant connection examples: `application-dev-example.yml`. `application-dev.yml` is local and git-ignored.
- Prompts: `src/main/resources/prompts/system-rag.txt`, `system-rag-db-tools.txt`, `system-rag-generate-title.txt`.

## Vector stores

- `QdrantVectorStoreConfig` beans: `documentVectorStore` (`@Primary`, collection `rag_documents`) and `schemaVectorStore` (collection `table_schemas`); both `contentFieldName = "doc_content"`, `initializeSchema(true)`. Collection names are hard-coded in Java — YAML does not change them.
- When Qdrant is disabled the beans do not exist; consumers that inject them fail at runtime unless gated. Every consumer must handle the disabled mode.

## Ingestion pipeline — `serviceImpl/AiDocumentIngestionServiceImpl`

1. `ingest(FileManager)` / `ingest(path, originalName, mime)` → re-ingest replaces: `findByFileName` → `deleteDocument`.
2. Type: `FileUtil.resolveAiDocumentTypeByMime` → `extraction/DocumentExtractorFactory#getExtractor`: IMAGE/VIDEO → `MediaPlaceholderDocumentExtractor` (placeholder text, no OCR/transcription); others → `TikaDocumentExtractor` (`TikaDocumentReader`).
3. Split: `TokenTextSplitter` built from `app.rag.chunk-size` (`keepSeparator = true`). `chunk-overlap` and `max-num-chunks` are **not** used by the splitter — do not promise them.
4. `documentVectorStore.add(chunks)` → save `AiDocumentMeta` with vector ids (`@ElementCollection ai_document_vector_ids.vector_id`) + metadata map (`ai_document_metadata`). Meta save failure → `safeRollbackVectors`.
5. Optional source deletion when `delete-source-after-ingest: true` (`AiDocumentMetaController#ingest`, `#delete`).
- Delete: `deleteDocument(meta)` = `documentVectorStore.delete(vectorIds)` + delete meta row (hard delete — `@SQLDelete` is commented out on `AiDocumentMeta`).
- Compensation is limited: a commit failure after the vector write leaves orphan vectors. Source deletion must follow a defined durable-success point.
- Schema ingestion: `AiDocumentMetaController` `POST /api/aiDocumentMeta/ingestDatabaseSchemas` → `ingestDatabaseSchemas()` (synchronized) → `schemaVectorStore`.

## Streaming chat events — `dto/ChatStreamEvent`

- `type` values used in code: `chat_id`, `title`, `thinking`, `token`, `sources`, `error`, `done` (the field comment lists only four).
- `sources.content` is a JSON array serialized **inside** a string.
- Preserve event names, order, and payload types — the external frontend parses them.
- After streaming starts, never write another HTTP body; errors go out as an `error` event. `GlobalExceptionHandler` silently handles `ClientAbortException` / `IOException`.

## Memory

- `ai/DatabaseChatMemory` is read-only: `add` / `clear` are no-ops; `get` loads the last `app.rag.memory-size` messages (`findLastNMessagesByChatId`) and drops the trailing user message.
- Messages are persisted in `streamAnswer`; do not double-save when adding advisors.
- Only active advisor: `MessageChatMemoryAdvisor`. `configuration/ChatClientConfig` (`QuestionAnswerAdvisor`) is disabled (`//@Configuration`).

## Tools

- Always: `ToolCallbacks.from(userActivityTool)`. When `databaseTools.enabled`: `DatabaseSchemaTool` (searches `schemaVectorStore`) and `PostgreSQLQueryTool`. Context object `AiChatToolContext` under key `chatToolContext`.
- `PostgreSQLQueryTool#executeSelect` → `DatabaseQueryValidator` (must start with `SELECT`/`WITH`, single statement, rejects comments/DDL/DML/COPY/DO) → `jdbcTemplate.queryForList`.
- Tools use the application's `JdbcTemplate` and DB user. The separately configured MCP read-only credentials do not constrain them. Add credential/table/column/row scoping, timeouts, and result limits before widening tool access.
- Prompts, retrieved text, and model-generated SQL are untrusted input. Prompt text is not an authorization boundary.

## Ownership

- Check chat ownership (`AiChatService#findByIdAndCreator(chatId, userId)`) before reading history, resuming, renaming, updating timestamps, or adding messages. Bind new chats to the authenticated user.
- Face recognition: `FaceRegconitionController` (`/api/faceRegconition/register`, `/detection`) → `FaceRecognitionServiceImpl` → external Python service via `ai/AiFaceRegconitionServiceClient` + pgvector `app_user_face.embedding vector(512)`. Keep actor and file-owner checks and the legacy spellings.
