---
name: backend-ai-rag
description: Optional module. Use only when the task changes Spring AI, Ollama, Qdrant vector stores, document ingestion, SSE chat streaming, AI tools, chat memory, prompts, or face recognition in this Spring Boot backend. Never load for ordinary CRUD, auth, file, or messaging work.
---

# Backend AI / RAG — Playbook (optional module)

> **Role:** HOW to change the AI path safely. Binding facts + evidence: `skills/backend/AI_RAG.md` (read it too).
> **Requires:** `backend-core`. Add `backend-api` / `backend-data` / `backend-files` / `backend-security` only for the boundaries the change crosses. Finish with `backend-testing`.
> **Do not load for ordinary CRUD/auth/file/messaging work.**

## Know this first

- SSE runs on Spring MVC with Reactor `Flux` — not WebFlux. Reactor threads have no logged-in user: pass `userId`.
- Qdrant beans exist only when `spring.ai.vectorstore.qdrant.enabled=true`. `app.rag.qdrant-enabled` does nothing.
- Collection names (`rag_documents`, `table_schemas`) are hard-coded in `QdrantVectorStoreConfig`.
- `chunk-overlap` / `max-num-chunks` are not used by the splitter.

## Pick your path

| Change | Start at |
|---|---|
| Chat/SSE behavior, events | `AiChatController#streamChat` → `AiRagChatServiceImpl#streamAnswer` |
| Ingestion / deletion | `AiDocumentMetaController` → `AiDocumentIngestionServiceImpl` |
| Extraction by file type | `extraction/DocumentExtractorFactory` + extractors |
| Tools | `ai/*Tool.java`, `DatabaseQueryValidator`, tool registration in `streamAnswer` |
| Memory | `ai/DatabaseChatMemory` + persistence in `streamAnswer` |
| Prompts | `src/main/resources/prompts/system-rag*.txt` |
| Face recognition | `FaceRegconitionController` → `FaceRecognitionServiceImpl` → `AiFaceRegconitionServiceClient` |

## Recipe A — change the SSE chat stream

1. List the current event sequence (`chat_id` → `title`? → `thinking`/`token`… → `sources` → `done`, or `error`).
2. Keep existing event names and payload types; add new types instead of changing old ones. Record any change under `External Consumer Impact`.
3. Blocking work goes in `Mono.fromCallable(...).subscribeOn(Schedulers.boundedElastic())`.
4. Check chat ownership with `findByIdAndCreator(chatId, userId)` before touching history.
5. Test with mocked `ChatClient`/vector store: event order, error event, disabled Qdrant, not-owned chat.

## Recipe B — change ingestion or deletion

1. Write down the success point across three systems: source file, Qdrant vectors, `AiDocumentMeta` row.
2. Order: extract → split → add vectors → save meta (rollback vectors on failure) → delete source only after the meta is durable.
3. Keep re-ingest replacement (`findByFileName` → `deleteDocument`).
4. Gate every `VectorStore` use for the disabled mode.
5. Test: meta save failure removes vectors; disabled Qdrant returns a clear error, not an NPE.

## Recipe C — add or change an AI tool

1. Treat model arguments as untrusted input; validate like a public API.
2. Scope data access (tables, columns, rows, owner) and add timeouts + result limits. The app `JdbcTemplate` has full app privileges.
3. Register the tool conditionally (config flag) like the DB tools.
4. Test validator rejections (DDL/DML, multiple statements, comments) and scoping.

## Done checklist

- [ ] `userId` passed explicitly across Reactor/worker threads; ownership checked.
- [ ] Event names/payload types unchanged (or change recorded for the frontend).
- [ ] Disabled-Qdrant mode handled wherever a vector store is used.
- [ ] Multi-system writes have a defined success point and compensation.
- [ ] No prompt, retrieved text, or model SQL trusted as authorization.
- [ ] No credentials or raw prompts containing secrets logged.

## Common mistakes

| Mistake | Fix |
|---|---|
| Using `app.rag.qdrant-enabled` as a gate | Gate on `spring.ai.vectorstore.qdrant.enabled` / bean presence |
| `SecurityContextHolder` inside a `Flux` operator | Pass `userId` in |
| Saving messages in a new advisor too | Persistence already happens in `streamAnswer` |
| Promising overlap/max-chunks tuning | Only `chunk-size` affects the splitter |
| Renaming `/api/faceRegconition` | Keep the legacy spelling |
