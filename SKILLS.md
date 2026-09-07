# Agent Guide: Java Spring Boot API Starter

Read this guide and [README.md](README.md) before implementing changes. This is repository guidance for a coding agent, not an installable skill package. Current behavior and known defects are distinguished from the conventions agents should follow. Do not claim the conventions are uniformly implemented in existing code.

## Scope and starting workflow

1. Read any applicable `AGENTS.md`, the user's request and `git status --short`. Preserve staged changes, local configuration and unrelated edits.
2. Use the source map below to trace the requested operation from route to DTO, service, mapper/repository, SQL and configuration. Read active code, including annotations and callers; comments and file names can describe dormant examples.
3. Reproduce an issue with a focused test when practical. A request for review authorizes inspection and reporting; implement only the requested changes.
4. Implement the smallest coherent change, preserving existing routes and API contracts unless the user requests a migration.
5. Run relevant checks and report exact results, including known failures and untested integrations. Read [README review findings](README.md#review-findings) before calling a feature complete.

Do not launch the application against an existing database merely to inspect it: the development example uses `ddl-auto: update`; MCP can spawn a process; enabled Qdrant builders initialize collections; scheduled cleanup can delete files. Use unit tests or an explicitly disposable integration environment for those checks.

## Baseline and command reference

| Item | Repository choice |
| --- | --- |
| Application | Single Gradle project `api-service`, base package `com.bekaku.api.spring` |
| Runtime/build | Java 25, Gradle Wrapper 9.6.0, Spring Boot 4.1.0 |
| Data | PostgreSQL + pgvector; Spring Data JPA and MyBatis 4.0.1 |
| Code generation | Lombok 1.18.46 and MapStruct 1.6.3 annotation processors; separate FreeMarker development generator |
| HTTP/security | Servlet MVC/Tomcat, Spring Security, JJWT 0.13.0 |
| AI | Spring AI 2.0.0 BOM; explicit 2.0.0-M8 advisor; Ollama/Qdrant/MCP |
| Tests | Jupiter/Mockito/AssertJ; direct controller/service tests, standalone MockMvc image-endpoint tests and focused ownership tests |

Use the wrapper, keep dependency declarations in `build.gradle`, and do not upgrade libraries as an incidental cleanup. The build explicitly overrides some managed versions, including `spring-jcl`, the AI advisor and Jupiter API; inspect resolved dependencies before changing one component in isolation.

```bash
./gradlew test --tests '*AuthControllerTest*'
./gradlew test
./gradlew compileJava
./gradlew bootJar
./gradlew bootRun --args='--spring.profiles.active=dev'
git diff --check
```

`bootRun` requires configured infrastructure. `bootJar` packages without starting the app and does not run tests automatically. Use `gradlew.bat` equivalents on Windows. Profile helper tasks share `bootRun` configuration; explicit arguments are clearer. Native tasks are unavailable until the GraalVM plugin is enabled and verified.

As reviewed on 2026-09-07, `./gradlew test --rerun-tasks` compiled successfully and ran 32 tests, with 31 passing and one failing. `AuthControllerTest.SwitchAccount.fastPathSwitchesOnValidTargetRefreshCookie` supplies a session belonging to a different user than the target; line 393 expects 200 while the ownership guard returns 400. Do not remove that guard to make the test pass. Recheck the baseline after relevant code changes.

After the R2 fix, all 16 `FileManagerControllerTest` cases pass with a temporary Gradle init script restricting test sources to that class. The current standard test compilation is blocked because `AuthController.signup` was commented out while `AuthControllerTest` still calls it. `--tests` filters execution, not test-source compilation; do not mistake the isolated R2 result for a passing full suite.

After the R3 fix, all 5 `*OwnershipTest` cases pass with the same isolated-source technique. They cover unauthorized chat history, creator binding on chat creation, unauthorized chat resume, cross-user face registration and selection of another user's file. They do not replace filter-chain, database or external face-service integration tests.

## Source entry points by task

Paths in this table are relative to `src/main/java/com/bekaku/api/spring/` unless marked as resources.

| Task | Read these together |
| --- | --- |
| Login, refresh, logout, account linking | `controller/api/AuthController`, `serviceImpl/AuthServiceImpl`, `AccessTokenServiceImpl`, `IdentityLinkServiceImpl`, `model/AccessToken`, `util/CookieUtil` |
| Request authentication/permissions | `configuration/WebSecurityConfig`, `SecurityEnablerConfig`, `JwtTokenFilter`, `serviceImpl/JwtServiceImpl`, `util/PermissionChecker`, `AuthUtil`, `repository/PermissionRepository` |
| Add an admin resource | `controller/api/AppRoleController` or `PermissionController`, matching service/implementation/repository/mapper/DTO/entity |
| Paging/search | `controller/api/BaseApiController`, `util/ControllerUtil`, `specification/SearchSpecification`, `SearchCriteria`, `SearchOperation`, `vo/Paging` |
| Files and folders | `controller/api/FileManagerController`, `FilesDirectoryController`, corresponding services, `util/FileUtil`, resource MyBatis XML |
| Chat and ingestion | `controller/api/AiChatController`, `AiDocumentMetaController`, `serviceImpl/AiRagChatServiceImpl`, `AiDocumentIngestionServiceImpl`, `ai/DatabaseChatMemory`, `QdrantVectorStoreConfig`, `extraction/` |
| Database tools | `ai/PostgreSQLQueryTool`, `DatabaseQueryValidator`, `DatabaseSchemaTool`, `UserActivityTool`, `AiChatToolContext` |
| Face recognition | `controller/api/FaceRegconitionController`, `serviceImpl/FaceRecognitionServiceImpl`, `ai/AiFaceRegconitionServiceClient`, `model/AppUserFace`, `docker-compose/face-verification-service/` |
| Configuration/auditing | `SpringApiApplication`, `properties/`, `configuration/AuditAwareImpl`, `AuditListener`, `WebConfigurerAdapter`, `AsyncConfig`, `SnowflakeConfig` |
| Generated modules | `annotation/GenSourceableTable`, `controller/dev/DevelopmentContoller`, `service/CodeGeneratorService`, `util/ConstantData`, resource `templates/spring-*.ftl` and frontend templates |

Class/package names above preserve repository spelling. Do not rename `serviceImpl`, `DevelopmentContoller`, or `/api/faceRegconition` as part of an unrelated task.

## Skill: implement a resource through the existing layers

Use `Controller → Service interface → ServiceImpl → Repository` with DTO mapping at the transport boundary. Entities can be used internally by controllers in this codebase; return DTOs to clients and keep multi-step business writes in services.

For a new resource:

1. Define an entity and schema migration, choosing the appropriate existing ID/audit/soft-delete base class.
2. Define request/response DTOs and constraints; separate create/update requests when their allowed fields differ.
3. Add a MapStruct interface using `@Mapper(componentModel = "spring")`. Existing mappers often ignore unmapped targets, so review every field intentionally.
4. Add a Spring Data repository, usually `BaseRepository<Entity, Long>` plus `JpaSpecificationExecutor<Entity>` where filtering is required.
5. Add the service contract and transactional implementation. Implement each exposed method; do not copy `return null` stubs from existing service implementations.
6. Add endpoints using constructor injection (`@RequiredArgsConstructor`), `@Valid`, service calls and the appropriate permission/ownership checks.
7. Add permission records and intentional role assignments; a new permission does not automatically grant it to an existing role. Update UI ACL and localized labels only where the feature needs them.
8. Cover the operation and its access boundary with tests, and update docs/configuration when the external contract changes.

Prefer concrete references over copying large generated examples. `BaseService<T, DTO>` exposes entities for internal operations and conversion methods; implementations vary in completeness.

## Skill: preserve HTTP contracts

- `BaseApiController.responseEntity(body, status)` returns that body directly. Do not introduce an `ApiResponse` envelope on the assumption one already exists. `BaseResponseEntity` is a separate record, not an automatically applied wrapper.
- Paged resource endpoints commonly return `ResponseListDto<T>`; some file-list endpoints return arrays. Verify each existing response before changing serialization.
- Jakarta validation belongs on request DTOs with `@Valid` at controller boundaries. Use existing i18n keys where applicable.
- Throw `ApiException` using `BaseResponseException` helpers for expected API failures. `GlobalExceptionHandler` is the active main advice; `CustomRestExceptionHandler` is deprecated/inactive. Preserve endpoint-specific streaming/filter error behavior when necessary.
- Validate actual HTTP status codes in tests. A status field in the JSON body does not set the response status.
- Match current route names, field names, enum serialization and cookie names. Login bodies are unwrapped; `loginFrom` uses `WEB`, `IOS`, `ANDROID`.
- Preserve numeric-ID representation unless coordinating a client migration. Snowflake `Long` values can exceed JavaScript's safe integer range; inspect DTO serialization and consumers before introducing new IDs to frontend code.

## Skill: design persistence and transactions

- `model/superclass/Id` assigns a Snowflake ID only when null in `@PrePersist`. Do not combine it with an unrelated identity/sequence strategy. `SoftDeletedAuditable<Long>`'s type parameter represents the auditor, not a generic primary-key type.
- Use the correct audit superclass and entity listeners. `AuditAwareImpl` reads an `AppUserDto` from the security context; async/Reactor work may need the actor ID passed explicitly.
- Preserve entity-specific `@SQLDelete` and `@SQLRestriction` behavior. A base `deleted` field alone does not implement soft deletion. MyBatis/native SQL also needs explicit visibility/ownership predicates.
- Prefer lazy relationships, DTO projections and bounded fetching. Check N+1 behavior when adding a relation to a list response.
- Follow proxy-safe identity equality used by nearby entities. Avoid Lombok `@Data` on entities with relationships; it can traverse them in equality, logging or serialization.
- Many service classes use `@Transactional(readOnly = true)`; write entry points need `@Transactional`. Private methods and same-bean calls do not gain a new Spring proxy transaction merely from the annotation.
- Keep multi-step database operations in one service transaction. Database transactions do not roll back filesystem, Qdrant, email, or remote-service effects. Define compensation and the point at which source files/chunks may be deleted.
- Add the next Flyway migration instead of rewriting deployed migrations. Check the actual migration history before choosing a number; V1–V4 exist in this checkout.
- Use a disposable PostgreSQL + pgvector database for SQL/schema tests. H2 cannot validate PostgreSQL casts, vector operators or the dump-based migrations.

## Skill: implement search and MyBatis safely

Read `ControllerUtil` and `ConstantData`: `_q` conditions use `;`, comma-separated values become `IN`, and `_keyword` uses controller-specified columns. The keyword column list does not constrain arbitrary `_q` fields. Add server-derived owner predicates regardless of client filters.

Use `getPageable(pageable, Entity.getSort())` for the existing JPA default-sort behavior. For dynamic SQL, use the explicit sort allow-list path (`getPaging(pageable, acceptedFields)`) and inspect `Paging` as well as the XML. The base helper's direction check is not a general field authorization mechanism.

Keep MyBatis Java interfaces, `@Param` names, XML statement IDs and result maps synchronized. Bind values using `#{...}`. Existing `${page.sortfield}`/`${page.sortmode}` substitutions need trusted, validated values; do not interpolate arbitrary request text.

Use PostgreSQL pagination (`LIMIT ... OFFSET ...`) and current schema names. Legacy `AppUserMybatis.findAll` and `PermissionMybatis.findAll` still contain MySQL pagination, and `selectUserData` refers to `user_role` instead of `app_user_role`. Do not use them as SQL templates without correction and a database test.

## Skill: work on authentication and authorization

Trace three distinct boundaries: the security chain's route rules, the JWT filter's skip list/verification, and method/service ownership checks. `AuthorizationInterceptor` currently returns true; it does not supply missing authorization.

- Add public routes only to the explicitly intended method/path allow-list, and make the filter behavior agree. Current blanket skipping of `/api/auth/**` breaks protected account routes; direct controller tests conceal this.
- The principal is an `AppUserDto` containing identity/session fields, not necessarily a populated user profile. Load and validate the current user when account state matters.
- Permission annotations normally call `@permissionChecker.hasPermission('module_action')`; also scope each requested row to its owner, or verify a deliberate administrative permission.
- Cookie access is selected by `_sid` plus user-suffixed token cookies. `X-User-Id` is request input, not proof of identity.
- Keep raw refresh tokens out of persistent fields and logs. Existing service lookup methods hash raw inputs internally; do not double-hash them. Review the JWT-subject issue R5 before extending the session format.
- Maintain rotation/reuse detection and ownership checks. Test inactive/deleted users, revoked/expired sessions, wrong client and wrong owner, for both cookie and API refresh paths.
- Use `EncryptService.encrypt/check` for BCrypt passwords. Data encryption is a separate AES-GCM operation with a different key format. Legacy MD5 helpers exist, but automatic login migration is currently commented out.
- Signup should assign configured default roles, never caller-selected privileges. Making signup public requires an intentional route/filter change and corresponding tests.
- The OTP throttle is process-local and keyed by email; do not describe it as distributed rate limiting. Changes to reset/OTP flows need expiry, replay and abuse-path tests.
- CSRF is disabled while cookie authentication is supported. Revisit the browser threat model when changing cookie SameSite/CORS or adding cookie-authenticated writes.

## Skill: handle files and external requests

The storage root is currently mapped publicly by `WebConfigurerAdapter`. Do not place new secrets, logs, backups or confidential assets under that mapping. Private-file access must remain behind an authenticated, owner-aware service path.

Use server-generated filenames and canonical/real-path containment checks before reading/writing. Check absolute paths, `..`, encoded separators and symlinks as appropriate. `FileManagerController.getImage` now checks relative paths against the real upload root, validates symlink targets, and serves only readable regular files from the checked path. Preserve its regression tests. This containment check is not a per-user ownership check and does not fix the public CDN mapping.

Multipart uploads use `_filesUploadName`; chunks are one-based. A caller-supplied `chunkFilename` is not evidence of upload ownership. Bind upload sessions to the authenticated user and validate limits/order before combining them. Merge currently deletes chunks while copying; design recovery and validation before treating that operation as atomic.

Inspect Tika MIME detection, `app.allow-mimes`, `upload-image` settings, `FileUtil` directory rules and thumbnail naming before changing storage. Stream large content where feasible and preserve Range handling for video/file endpoints.

For URL fetching, use `UrlUtil.validatePublicUrl` and validate each redirect. DNS checks and a later HTTP connection are distinct steps; do not claim complete SSRF protection solely because the helper exists.

The actual scheduled cleanup gate is `app.cron.clean-old-temp-chunks`; `cleanupOldTempChunks` deletes regular files older than one day under the temp-chunk directory. Do not infer that generic `app.cron.enable` gates every scheduled task.

## Skill: extend AI/RAG without hidden dependencies

- This is MVC SSE with Reactor publishers, not a WebFlux server. Offload blocking JDBC/filesystem/model setup work appropriately and pass user identity explicitly across threads.
- Preserve event names and payload types in `ChatStreamEvent`. `sources.content` is serialized JSON inside a string. Handle errors before and after response commitment; do not append another HTTP body after streaming starts.
- Check conversation ownership before reading history, resuming a conversation, updating timestamps or adding messages. The current message-list and resume paths use `findByIdAndCreator`; preserve that creator-scoped lookup and bind new chats to the authenticated actor.
- Store chat messages in the service, matching `DatabaseChatMemory`'s read-only role; its `add/clear` methods do nothing. Avoid double-saving messages when adding advisors.
- Custom Qdrant registration uses `spring.ai.vectorstore.qdrant.enabled`; Spring AI's starter also has auto-configuration selected by `spring.ai.vectorstore.type`. Verify both. `app.rag.qdrant-enabled` is not bound or consumed.
- Preserve the named stores/qualifiers `documentVectorStore` and `schemaVectorStore`. They hardcode collection names, content field and schema initialization. Changing YAML alone will not change those custom settings.
- Null optional stores currently cause runtime failures on chat/ingestion. When implementing a disabled mode, gate every consumer and verify startup as well as endpoint behavior.
- Keep document metadata and vector IDs consistent. Current compensation is limited; a later transaction-commit failure may happen outside a local save catch. Source deletion must follow a defined durable-success boundary.
- `chunk-overlap` and `max-num-chunks` are unused by the current splitter; image/video extraction is placeholder text. Do not promise OCR, transcription, overlap or chunk caps without implementing and testing them.
- Built-in Java tools share the application's `JdbcTemplate`. The MCP stdio server's separate read-only credentials do not constrain those tools. Isolate credentials, tables/columns and row scope before broadening AI data access; add timeouts and result bounds.
- Treat prompts, retrieved text and model-generated SQL as untrusted input. Prompt instructions are not an authorization boundary.
- Face extraction is a separate Python service and PostgreSQL pgvector path. Registration passes the authenticated actor into the service and validates both the requested user and file owner before replacing stored data; preserve those checks and the existing route spelling unless migration is requested.

## Skill: configuration, messaging and delivery

Use existing typed `@ConfigurationProperties` records/classes under `properties/`. Nested records can be null when configuration is absent; validate required configuration instead of assuming every nested value has defaults. Bind new settings in code and demonstrate them in the development example.

`spring.profiles.active` selects configuration, while `environments.production` controls route exposure. Set both correctly for production. The ignored local dev YAML/logging files must be prepared from examples on a fresh checkout. `.env` is not globally ignored; inspect `git status` before staging any configuration.

Spring Cache currently delegates to JCache/Ehcache. Verify named cache creation/configuration before adding caching annotations. Do not assume the Redis Compose service is an application cache integration.

RabbitMQ declarations are active, queues are non-durable, and the same routing key binds multiple queues to the exchange. Inspect that fan-out before publishing new events. No active application listeners were found; retry/concurrency YAML alone does not implement consumers. If adding consumers, test duplicate delivery and define idempotency.

WebSocket broker registration, Kafka, Undertow and native-image build configuration are disabled. Restore and verify their wiring deliberately if requested. The configured custom async executor is a platform-thread pool despite the application's virtual-thread setting.

Inspect Dockerfile, Compose, Kubernetes and workflows before deployment changes. The standard image uses UID 1001, activates `prod`, skips tests at build time and reads `/usr/spring-data/env/`. Its public-storage overlap is unresolved. Keep each replica's Snowflake `WORKER_ID` unique within 0–1023. Native CI is currently inconsistent with the disabled plugin.

## Skill: use the source generator deliberately

The development HTTP generator is distinct from Lombok/MapStruct compilation. It walks all annotated entities, chooses templates/options and may insert permissions. Some outputs skip existing files, while the shared writer supports overwriting: inspect each output path and invocation before running it.

Never use generation as a read-only diagnostic. When generation is requested, inspect `@GenSourceableTable` options and `ConstantData` destinations, review all resulting diffs, implement validation/ownership/transactions, and compile the outputs. Do not edit `build/generated` as a lasting source change.

## Validation and handoff

Choose tests for the changed boundary:

| Change | Useful evidence |
| --- | --- |
| Controller/service logic | Focused unit test, including failure path and ownership |
| Authentication/routes | HTTP test using the real security filter chain, plus cookie and Bearer flows |
| SQL/schema/vector columns | PostgreSQL + pgvector integration test on a disposable database |
| File operations | Temporary-directory tests for containment, ownership and partial failure |
| RAG/SSE | Mock model/vector dependencies; verify event order, disabled mode, cancellation/error and persistence |
| Configuration/dependency wiring | Compile/package and a controlled application-context/startup test |
| Documentation only | Verify statements, commands, links and whitespace; do not claim runtime validation from a prose edit |

The authentication Mockito test constructs `AuthController` directly with lenient mocks. The file and ownership regression tests are also isolated controller/service tests. Passing results are not evidence of filter-chain enforcement, migrations, SQL correctness, external-service behavior or startup. Report baseline failures separately from regressions.

At handoff, state which files changed, what behavior or documentation changed, the commands actually run and their results, and any remaining integration limits. Do not stage/commit unrelated user work or claim deployment, security or test success that was not verified.
