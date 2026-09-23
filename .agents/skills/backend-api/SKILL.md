---
name: backend-api
description: REST controller, DTO, pagination, validation, and response contract rules. Load when endpoints, DTOs, paging, or error shapes change.
---

# Backend API — Canonical

> Canonical API skill. Detailed rules: `skills/backend/API.md`.
> Requires: `AGENTS.md`, `.agents/skills/backend-core/SKILL.md`.

## When to load

Controller, DTO, pagination, validation, response, or endpoint changes.

## Implementation path

1. Inspect the current route in `controller/api/`, its DTO, `BaseApiController`, and `GlobalExceptionHandler`. Record method, path, status, body, validation, and error behavior; existing endpoints vary from the standard CRUD shape.
2. Keep HTTP handling in the controller and business writes in the service. Use the relevant `*Validator` after request validation. For route access or owner-scoped data, also load `backend-security`; for query changes, load `backend-data`.
3. Check serialization before changing a response. `ResponseListDto` declares Java field `isLast` and Lombok getter `isLast()`; the JSON property is `last`. Some file-list routes return bare lists.
4. Verify pagination, allowed sort fields, response status, and failure shape with tests appropriate to the changed contract (`backend-testing`).

## Rules (summary — binding details in `skills/backend/API.md`)

- No universal envelope: `BaseApiController.responseEntity` passes body + status through.
- `GET /` → `200 ResponseListDto`; `GET /{id}` → `200 DTO`; `POST /` → `201 DTO`; `PUT /{id}` → `200 DTO`; `DELETE /{id}` → `200` delete message (not `204`).
- Serialized paged shape `ResponseListDto` = `{dataList, totalPages, totalElements, last}`; its Java field is `isLast`. Verify per endpoint (some file endpoints return bare lists).
- `@Valid @RequestBody` on all mutating endpoints, then manual `*Validator` checks. No class-level `@Validated`. No HATEOAS.
- Errors: `ApiException` / `BaseResponseException` → `GlobalExceptionHandler` → `ApiError`; JWT filter 401s use separate `{"error":"..."}` shape.
- See `skills/backend/API.md` for file-path evidence (`BaseApiController`, `ResponseListDto`, `Paging`, exception handlers).
