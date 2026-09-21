---
name: backend-api
description: REST controller, DTO, pagination, validation, and response contract rules. Load when endpoints, DTOs, paging, or error shapes change.
---

# Backend API — Canonical

> Canonical API skill. Detailed rules: `skills/backend/API.md`.
> Requires: `AGENTS.md`, `.agents/skills/backend-core/SKILL.md`.

## When to load

Controller, DTO, pagination, validation, response, or endpoint changes.

## Rules (summary — binding details in `skills/backend/API.md`)

- No universal envelope: `BaseApiController.responseEntity` passes body + status through.
- `GET /` → `200 ResponseListDto`; `GET /{id}` → `200 DTO`; `POST /` → `201 DTO`; `PUT /{id}` → `200 DTO`; `DELETE /{id}` → `200` delete message (not `204`).
- Paged shape `ResponseListDto` = `{dataList, totalPages, totalElements, last}`; verify per endpoint (some file endpoints return bare lists).
- `@Valid @RequestBody` on all mutating endpoints, then manual `*Validator` checks. No class-level `@Validated`. No HATEOAS.
- Errors: `ApiException` / `BaseResponseException` → `GlobalExceptionHandler` → `ApiError`; JWT filter 401s use separate `{"error":"..."}` shape.
- See `skills/backend/API.md` for file-path evidence (`BaseApiController`, `ResponseListDto`, `Paging`, exception handlers).
