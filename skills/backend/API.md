# API / Controller Contract

Read this for controller, DTO, pagination, validation, response, or endpoint changes.

## API contract rules

- **No envelope.** `BaseApiController.responseEntity(body, status)` returns the body directly (`src/main/java/com/bekaku/api/spring/controller/api/BaseApiController.java:34-44`). `BaseResponseEntity` is a separate record, not an automatic wrapper. `responseServerMessage` builds `{message, status, timestamp}` with `ConstantData.SERVER_*` keys (`BaseApiController.java:66-72`); the 4-arg overload adds `success`.

- **Routes:** `@RequestMapping("/api/<resource>")`, e.g. `/api/appRole` (`src/main/java/com/bekaku/api/spring/controller/api/AppRoleController.java:29`), `/api/fileManager` (`FileManagerController.java`), `/api/aiChat` (`AiChatController.java:39`). Preserve existing spellings.

- **Standard CRUD verbs/status:** `GET /` → `200 ResponseListDto<T>`; `GET /{id}` → `200 DTO`; `POST /` → `201 DTO`; `PUT /{id}` → `200 DTO`; `DELETE /{id}` → `200` message via `responseDeleteMessage()` (not 204). Inspect the existing route before changing its contract; `AiChatController` returns raw DTOs (implicit 200).

- **Paged shape:** `ResponseListDto.java:7-18` declares Java field `isLast`; Lombok generates `isLast()`/`setLast()`, so the JSON property is `last`: `{dataList, totalPages, totalElements, last}`. Some file-list endpoints return bare lists — verify the endpoint and serialization before changing its contract.

- **HATEOAS:** dependency present (`build.gradle:51`) but no `EntityModel/Link` usage — do not introduce HATEOAS links.

- **Validation:** `@Valid @RequestBody` on all mutating endpoints (no class-level `@Validated`), then manual `*Validator` duplicate/existence checks. Example `AppRoleController.java:68-75`: `@Valid @RequestBody AppRoleDto` + `roleValidator`.

- **Errors:** throw `ApiException` via `BaseResponseException` helpers (`src/main/java/com/bekaku/api/spring/exception/BaseResponseException.java`). `GlobalExceptionHandler` maps these to `ApiError{status,message,errors,timestamp}`; `CustomRestExceptionHandler` is inactive. `JwtTokenFilter.sendUnauthorizedResponse` writes a separate `{"error":"..."}` 401 for missing/invalid JWT or API key. Streaming/file errors must respect committed responses (see `GlobalExceptionHandler` `ClientAbort/IOException` handlers).

- **Pagination params:** standard `page,size,sort` → `Pageable`; MyBatis path uses `vo/Paging.java:10-52` (`page-1` offset, `limit≤100`, `sort=field,asc|desc`). Validate dynamic sort field against allow-list; the base direction check is not field authorization.
