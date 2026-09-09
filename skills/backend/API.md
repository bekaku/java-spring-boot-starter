# API / Controller Contract

Read this for controller, DTO, pagination, validation, response, or endpoint changes.

**## 5. API Contract Rules**

- ****No envelope.**** `BaseApiController.responseEntity(body, status)` returns the body directly (`src/main/java/com/bekaku/api/spring/controller/api/BaseApiController.java:34-44`). `BaseResponseEntity` is a separate record, not an automatic wrapper. `responseServerMessage` builds `{message, status, timestamp}` with `ConstantData.SERVER_*` keys (`BaseApiController.java:66-72`); the 4-arg overload adds `success`.

- ****Routes:**** `@RequestMapping("/api/<resource>")`, e.g. `/api/appRole` (`src/main/java/com/bekaku/api/spring/controller/api/AppRoleController.java:29`), `/api/fileManager` (`FileManagerController.java`), `/api/aiChat` (`AiChatController.java:39`). Preserve existing spellings.

- ****Verbs/status:**** `GET /` → `200 ResponseListDto<T>`; `GET /{id}` → `200 DTO`; `POST /` → `201 DTO`; `PUT /{id}` → `200 DTO`; `DELETE /{id}` → `200` message via `responseDeleteMessage()` (which returns HTTP 200, not 204). `AiChatController` returns raw DTOs (implicit 200, no `ResponseEntity`).

- ****Paged shape:**** `src/main/java/com/bekaku/api/spring/dto/ResponseListDto.java:7-13` = `{dataList, totalPages, totalElements, isLast}`. Some file-list endpoints return bare `List<FileManagerDto>` — verify per endpoint before changing serialization.

- ****HATEOAS:**** dependency present (`build.gradle:51`) but no `EntityModel/Link` usage — do not introduce HATEOAS links.

- ****Validation:**** `@Valid @RequestBody` on all mutating endpoints (no class-level `@Validated`), then manual `*Validator` duplicate/existence checks. Example `AppRoleController.java:68-75`: `@Valid @RequestBody AppRoleDto` + `roleValidator`.

- ****Errors:**** throw `ApiException` via `BaseResponseException` helpers (`src/main/java/com/bekaku/api/spring/exception/BaseResponseException.java:13-75`: `responseErrorUnauthorized/Forbidden/BadRequest/Notfound/Duplicate` with i18n keys `error.401/403/error/dataNotfound/validateDuplicate`). Handled by `GlobalExceptionHandler` (`@RestControllerAdvice @Order(HIGHEST_PRECEDENCE)`, `src/main/java/com/bekaku/api/spring/exception/GlobalExceptionHandler.java:42-45`) into `ApiError{status,message,errors,timestamp}` (`src/main/java/com/bekaku/api/spring/exception/ApiError.java:17-22`). `CustomRestExceptionHandler` is `//@ControllerAdvice` — inactive, do not use. `JwtTokenFilter` 401s use separate `{"error":"..."}` shape (`JwtTokenFilter.java:124-136`) — not `ApiError`. Streaming/file errors must respect committed responses (see `GlobalExceptionHandler.java:207-255` `ClientAbort/IOException` handlers).

- ****Pagination params:**** standard `page,size,sort` → `Pageable`; MyBatis path uses `vo/Paging.java:10-52` (`page-1` offset, `limit≤100`, `sort=field,asc|desc`). Validate dynamic sort field against allow-list; the base direction check is not field authorization.
