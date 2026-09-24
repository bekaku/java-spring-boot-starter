# Known Issues / Legacy Exceptions

Observed problems and exceptions in the current code. **Do not copy them as patterns.** Read only when debugging or touching the affected area. Fixing one is a separate task unless the current task requires it.

Grouped by skill so you can jump to the area you are working on.

## Core / code generator

1. **Generator controller template is off-contract** (`templates/spring-controller.ftl`): `create`, `update`, `findOne` return the raw DTO (implicit `200`, so `POST` is not `201`); `update` saves the entity built from the request body, so the path `{id}` is only checked for existence — a body without `id` inserts a new row and a body with another `id` updates that row. Fix generated controllers per `docs/agent/STANDARD_CRUD_SERVICE_REPOSITORY.md`.
2. The generator (`DevelopmentContoller#generateSrc`) inserts permission rows straight into the connected DB; they are not in a migration unless someone adds them.
3. `AppRoleServiceImpl` returns `null` from `findAllBy`, `findAllPageSpecificationBy`, `findAllPageSearchSpecificationBy`. `AppRoleController` keeps business logic (permission assignment, two `update` calls) in the controller.
4. Duplicate mappers for `AppRole`: `AppRoleMapper` and `RoleMapper` (the service uses `RoleMapper`).
5. `RoleValidator#validateUpdate` and `UserValidator#validateUpdate` detect a changed name/email/username by re-reading the row with `findById` and comparing it with the entity the controller has already modified (`AppRoleController#update`, `AppUserController#updateUser`). `spring.jpa.open-in-view` is not set, so it defaults to `true` and `findById` returns that same managed instance: the comparison never sees a change and the duplicate checks are skipped on update. (Inferred from the code and the framework default; not reproduced against a database.) If the stored row is ever a separate instance, `UserValidator#validateUpdate` throws `NullPointerException` for a stored user without a username whose request sets one.

## API

6. MyBatis `vo/Paging` treats `page` as 1-based (`offset = (page-1) * limit` only when `page > 0`), while Spring `Pageable` is 0-based — on MyBatis routes `page=0` and `page=1` return the same first page.
7. `ChatStreamEvent#type` comment lists four event types; the code emits seven (`chat_id`, `title`, `thinking`, `token`, `sources`, `error`, `done`).
8. `BaseApiController#getPagableWithValidateSort` calls `getPageableCustomSort(...)` and discards the result, so its fallback never applies. JPA list routes accept any client `sort` property; an unknown one → `500` via `GlobalExceptionHandler#handleAll`.

## Data

9. Flyway is disabled (`spring.flyway.enabled: false`) and dev uses `ddl-auto: update`. Migrations are never exercised locally; `V1` is a PostgreSQL dump (`COPY ... FROM stdin`) that H2 cannot run.
10. `AppUserMybatis.findAll` and `PermissionMybatis.findAll` use MySQL paging (`limit a, b`); `AppUserMybatis.xml` `selectUserData` joins `user_role` instead of `app_user_role`. Fix and DB-test before reuse.
11. `AccessTokenMybatis#updateLastestActive` is a MyBatis write (legacy; no new ones).
12. `AiDocumentMeta` and `IdentityLink` extend soft-delete bases but have `@SQLDelete` commented out — `delete()` is a hard delete.

## Security

13. `CustomPermissionEvaluator` always returns `false`; `AuthorizationInterceptor` always returns `true`. Neither enforces anything.
14. `JwtTokenFilter.SKIP_PATHS` hard-codes `/cdn/**` while `WebSecurityConfig` uses `app.cdn-path-alias`. Changing the alias breaks the public CDN path. Keep the two lists aligned by hand.
15. `PermissionRequireValidator` uses `LoggerFactory` instead of `@Slf4j` (legacy style).
16. Secrets live in plain YAML (`app.encrypt-key`, `app.jwt.secret`, datasource/RabbitMQ/mail/Qdrant keys, MCP URL password); `.env` is not in `.gitignore`. Check `git status` before staging config. `environments.production` (tracked value `true`) gates routes independently of `spring.profiles.active`.
17. OTP/reset throttling is process-local, keyed by email — not distributed rate limiting.
18. `JwtServiceImpl#jwtVerify` requires `Accept-Apiclient` to be non-empty but does not validate its value (`//TODO verify apiClient later`), and does not re-check `AppUser.active` per request (deactivated users keep access until the access token expires).

## Files

19. The upload root **is** the public `/cdn/**` root (`AppProperties#getUploadPath` = `app.cdn-path`). Production logs default to `/usr/spring-data/logs` (`log4j2-prod.xml` `APP_LOG_ROOT`, `logging.file.path: ${app.cdn-directory}logs`) — inside the publicly served directory.
20. `FileManagerController#getImage` path containment is not per-user ownership.
21. `mergeChunkApi` deletes chunks while copying (non-atomic). `app.cron.clean-old-temp-chunks` only gates temp-chunk cleanup (`FileManagerServiceImpl#cleanupOldTempChunks`).

## Async / messaging

22. Queues are declared non-durable and all bound with one shared routing key (topic publish fans out to every queue); no `@RabbitListener` exists and `@EnableRabbit` is commented out. `AppUserServiceImpl#processAsyncTask` is demo-only. The async executor is a platform-thread pool although `spring.threads.virtual.enabled: true`.

## AI / RAG

23. `DatabaseChatMemory#add` / `#clear` are no-ops; `chunk-overlap` / `max-num-chunks` are unused; image/video extraction is placeholder text; commit failure after the vector write can leave orphan vectors; `app.rag.qdrant-enabled` is not bound to anything.

## Unused or disabled infrastructure

24. Caching: `@EnableCaching` + `CacheConfig` (JCache/Ehcache) exist but there are no `@Cacheable` / `@CacheEvict` / `@CachePut` usages and no `ehcache.xml`. Verify named-cache creation before adding annotations. The Redis Compose service is not wired as an app cache.
25. Present but unused/disabled: POI (only `FileUtil.hasExcelFormat`), Firebase Admin (only `fcm_token` + `FcmVo` + `refreshFcmToken`), WebSocket broker, Kafka, Undertow, native image, `ChatClientConfig` advisor, MCP client (example config only, no integration code).

## Tests

26. All current tests are isolated unit tests. None proves the filter chain, database, or external services; `AuthControllerTest` constructs the controller directly.
