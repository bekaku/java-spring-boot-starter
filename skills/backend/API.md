# API / Controller Contract — Reference

> **Role:** binding API rules + verified facts (WHAT / WHY).
> **Procedure (HOW):** `.agents/skills/backend-api/SKILL.md`.
> **Evidence style:** `path` + `Class#member` (no line numbers; search the symbol).

## Response contract (binding)

- **No envelope.** `BaseApiController#responseEntity(body, status)` returns the body as-is. `dto/BaseResponseEntity` is a separate record, not an automatic wrapper. Do not add a global wrapper.
- **Server message body** — `BaseApiController#responseServerMessage(msg, status)` → `{ "message", "status", "timestamp" }` (keys from `ConstantData.SERVER_*`). The 3-arg overload adds `"success"`. `responseDeleteMessage()` uses i18n key `success.deleteSuccesfull`.
- **Standard CRUD verbs/status:**

  | Route | Status | Body |
  |---|---|---|
  | `GET /api/{resource}` | `200` | `ResponseListDto<Dto>` |
  | `GET /api/{resource}/{id}` | `200` | `Dto` |
  | `POST /api/{resource}` | `201` | `Dto` |
  | `PUT /api/{resource}/{id}` | `200` | `Dto` |
  | `DELETE /api/{resource}/{id}` | `200` | server message via `responseDeleteMessage()` (not `204`) |

  Existing endpoints vary (e.g. `AiChatController#create` returns a raw `AiChatDto` → implicit `200`). Inspect the current route before changing its contract.
- **Paged shape** — `dto/ResponseListDto` has Java field `isLast`; Lombok generates `isLast()` so the JSON property is `last`: `{ "dataList": [...], "totalPages": n, "totalElements": n, "last": bool }`. Some file-list routes (`FileManagerController#findAll`) return a bare `List<Dto>`.
- **IDs in JSON** — `Long` IDs are strings (`DtoId#id` has `@JsonFormat(shape = STRING)`); do the same for FK IDs in DTOs (`ApiClientDto#appUserId`).
- **HATEOAS** — dependency exists in `build.gradle` but is unused. Do not introduce links.

## Routes

- `@RequestMapping(path = "/api/{resourceCamelCase}")`, e.g. `/api/appRole`, `/api/fileManager`, `/api/aiChat`. Preserve existing spellings (`/api/faceRegconition`).
- Every `/api/**` route requires authentication unless it is explicitly listed as public in **both** `WebSecurityConfig` and `JwtTokenFilter.SKIP_PATHS` (see `SECURITY.md`). `GET /api/public/**` is public.

## Validation (binding)

- Mutating endpoints take `@Valid @RequestBody`. No class-level `@Validated`.
- Field rules: Jakarta constraints with i18n keys (`@NotEmpty(message = "{error.NotEmpty}")`, `@Size(min = 3, max = 100, message = "{error.Size3Limit100}")` in `dto/AppRoleDto.java`). Keys live in `i18n/error/messages*.properties`.
- Cross-row rules (duplicate name, existence): a `validator/{Model}Validator` `@Component` that `extends BaseValidator`, collects messages in a list **local to each call**, and ends with `BaseValidator#checkValidate(errors)`, which throws `new ApiException(new ApiError(HttpStatus.BAD_REQUEST, i18n.getMessage("error.error"), errors))`. Validators are singletons shared by concurrent requests: never keep errors or other request data in a field. The caller states create vs update (`RoleValidator#validateCreate` / `#validateUpdate`), or the validator compares ids (`docs/agent/STANDARD_CRUD_SERVICE_REPOSITORY.md` §9); do not infer it from the HTTP method.
- `GlobalExceptionHandler#handleMethodArgumentNotValid` turns bean-validation failures into `400 ApiError` with `errors = ["field: message", ...]`.

## Errors (binding)

- Throw `ApiException` built by `BaseResponseException` helpers (`throw responseErrorNotfound();`).
- `exception/GlobalExceptionHandler` (active, `@Order(HIGHEST_PRECEDENCE)`) maps exceptions to `ApiError { status, message, errors[], timestamp "yyyy-MM-dd HH:mm:ss" }`. `AccessDeniedException` (failed `@PreAuthorize`) → `403`. `CustomRestExceptionHandler` is inactive.
- `JwtTokenFilter#sendUnauthorizedResponse` writes a different shape: `401 {"error": "..."}` for a missing/invalid JWT or API key.
- Streaming/file responses: once the body is committed, do not write another error body (`GlobalExceptionHandler` handles `ClientAbortException` / `IOException` silently).

## Search, sort, paging

- **JPA list routes** — `ControllerUtil.buildSpecification(request, keywordColumns)` + `getPageable(pageable, {Model}.getSort())`.
  - `page` (0-based Spring `Pageable`), `size` (default 10, max 50 from `spring.data.web.pageable`), `sort=field,asc|desc`.
  - `getPageable` applies `{Model}.getSort()` only when the request has **no** sort. A client sort is passed through as-is: sort fields are **not** allow-listed on the JPA path, and an unknown property ends in `GlobalExceptionHandler#handleAll` → `500`. If a field must not be sortable, filter `pageable.getSort()` yourself. (The direction "fallback" in `getPagableWithValidateSort` discards its result — see `KNOWN_ISSUES.md`.)
  - `_q` filter: `;`-separated `field{op}value`. Ops: `:` (like), `=`, `!=`, `>`, `>=`, `<`, `<=`. A comma in the value becomes `IN` (`_q=status=A,B`). `true`/`false` and ISO dates are parsed. Field names match `[A-Za-z0-9_.]+`.
  - `_keyword=text` searches only the columns passed as `keywordColumns` (e.g. `List.of("name")`).
- **MyBatis list routes** — `getPaging(pageable, allowedSortFields)` → `vo/Paging` (`limit` ≤ 100, `offset = (page-1) * limit` when `page > 0`, sort value lower-cased). XML sorts with `${page.sortfield} ${page.sortmode}`, so the sort field **must** come from a non-empty allow-list (`FileManagerController#sortProperties`). An empty allow-list accepts any `[A-Za-z0-9_.]+` value.
- The base direction check is not field authorization; always allow-list dynamic sort fields.
