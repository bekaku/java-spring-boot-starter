---
name: backend-ai-rag
description: Optional Spring AI, Ollama, Qdrant, ingestion, SSE chat, tools, memory, and face-recognition integration. Load only when AI or RAG paths change. Never load for ordinary CRUD.
---

# Backend AI / RAG — Canonical (Optional Module)

> Canonical AI/RAG skill. Detailed rules: `skills/backend/AI_RAG.md`.
> Requires: `AGENTS.md`, `.agents/skills/backend-core/SKILL.md`.
> **Do not load for ordinary CRUD/auth/file/messaging work.**

## When to load

Only when the task touches Spring AI, Ollama, Qdrant, ingestion, SSE chat, AI tools, chat memory, or face-recognition integration.

## Implementation path

1. Trace only the affected path: `AiChatController` → `AiRagChatServiceImpl` for SSE/chat; `AiDocumentMetaController` → `AiDocumentIngestionServiceImpl` for ingestion; `ai/*Tool.java` for tools; `FaceRegconitionController` for the face service.
2. Add API, data, files, and security guides only when that path touches their boundaries. Preserve authenticated actor and owner scoping across Reactor/worker threads.
3. For Qdrant consumers, test both enabled and disabled store behavior. The active bean gate is `spring.ai.vectorstore.qdrant.enabled`, and two collection names are set in Java.
4. For ingestion, identify the PostgreSQL/Qdrant/source-file success point and compensation. For SSE, preserve event ordering and payload types. Use `backend-testing` for the relevant evidence.

## Rules (summary — binding details in `skills/backend/AI_RAG.md`)

- MVC SSE with Reactor publishers, not a WebFlux server; offload blocking work and pass user identity explicitly across threads.
- Qdrant stores are gated by `spring.ai.vectorstore.qdrant.enabled`; hardcoded collections `rag_documents` / `table_schemas`; null/disabled stores fail at runtime unless gated.
- Ingestion: resolve type → extractor (image/video is placeholder, no OCR) → splitter (`chunk-size` only) → Qdrant → `AiDocumentMeta`; compensation is limited.
- Streaming events `token|sources|done|error` (+ `chat_id|title|thinking`); `sources.content` is JSON-encoded inside a string.
- `DatabaseChatMemory` is read-only (`add`/`clear` no-ops); persistence lives in `streamAnswer`.
- Tools share app `JdbcTemplate`; MCP read-only credentials do not constrain them. Treat prompts/retrieved text/model SQL as untrusted.
- Preserve `findByIdAndCreator` ownership and `/api/faceRegconition` spelling.
