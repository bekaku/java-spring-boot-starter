**# SKILLS.md — Backend (Authoritative)**

**## Enforcement Instructions (binding)**

1. This file is authoritative for all backend work. Any new code, refactor, or suggestion MUST comply with every rule here unless the user explicitly overrides it in-session.

2. Before writing backend code, re-check the relevant rules in this file (Security, Data Layer, API Contract, AI/RAG as applicable).

3. If a request conflicts with a documented rule, flag the conflict explicitly before proceeding — never silently pick a side.

4. If uncertain whether existing code follows a rule, ask rather than assume. Many areas below have Known Issues where legacy code violates the rule.

5. Rules apply only to `./` backend (`src/main/java/com/bekaku/api/spring/`, `src/main/resources/`, `src/test/`, `build.gradle`). Root/frontend is out of scope for this file.

6. Cite file-path evidence when claiming a convention. Do not invent conventions not present in code.

---

**## 1. Backend Overview**

- Single Gradle project `api-service`, base package `com.bekaku.api.spring` (`build.gradle:8`, `src/main/java/com/bekaku/api/spring/SpringApiApplication.java`).

- Stack (verified in `build.gradle:47-149`): Java 25 toolchain, Spring Boot 4.1.0, Servlet Spring Web MVC (Tomcat), Spring Security + JJWT 0.13.0, Validation + commons-validator, Spring Data JPA + PostgreSQL + Flyway + HikariCP, MyBatis Spring Boot Starter 4.0.1, MapStruct 1.6.3 + Lombok (+ `lombok-mapstruct-binding`), Spring AI 2.0.0 BOM (Ollama chat+embeddings, Qdrant vector store, PDF reader, Tika reader, MCP client, `spring-ai-advisors-vector-store:2.0.0-M8`), AMQP + Mail, Cache + Ehcache 3.12.0, POI, Thumbnailator, metadata-extractor, Tika core/parsers-standard 3.3.1, TwelveMonkeys WebP, Guava, Firebase Admin, Jsoup, uuid-creator, Gson, Actuator + Micrometer Prometheus, Log4j2, springdoc-openapi webmvc-ui, JUnit Jupiter + REST Docs MockMvc + spring-security-test.

- Architecture style: layered servlet MVC — `controller/api/*` → `service/*` interface → `serviceImpl/*` → `repository/*` (JPA) and/or `mybatis/*` + `src/main/resources/mybatis/*.xml`. DTO mapping at transport boundary via `mapper/*` (MapStruct). No WebFlux server; streaming chat is MVC SSE returning Reactor `Flux` (`src/main/java/com/bekaku/api/spring/controller/api/AiChatController.java:53-56`).

- App entry: `src/main/java/com/bekaku/api/spring/SpringApiApplication.java` carries `@EnableCaching @EnableAsync @EnableScheduling` (Rabbit `@EnableRabbit` commented out).

**## 2. Directory Structure**

Annotated tree (relative to `./`):

```

build.gradle — all backend deps; Java 25, Boot 4.1.0, AI BOM 2.0.0 (§1)

src/main/java/com/bekaku/api/spring/

  SpringApiApplication.java — @EnableCaching/@EnableAsync/@EnableScheduling entry point

  controller/api/ — REST resources, all @RequestMapping("/api/..."); BaseApiController.java response/paging helpers

  controller/dev/DevelopmentContoller.java — code-generator HTTP endpoint (spelling is intentional, do not rename)

  controller/socket|test|web/ — websocket, test-only, thymeleaf/web endpoints

  service/ + serviceImpl/ — contracts + transactional impls (AuthServiceImpl, JwtServiceImpl, AccessTokenServiceImpl,

    AiRagChatServiceImpl, AiDocumentIngestionServiceImpl, FileManagerServiceImpl, EmailServiceImpl, ...)

  repository/ + repositoryImpl/ — Spring Data repos (all extend BaseRepository); PermissionRepositoryCustom(+Impl) is the only custom impl

  mybatis/ — read-model mappers: AppUserMybatis, AppRoleMybatis, PermissionMybatis, AccessTokenMybatis,

    FileManagerMybatis, FilesDirectoryMybatis

  model/ — ~30 JPA entities; model/superclass/ — 11 base classes (Id, SoftDeletedId, Auditable*, SoftDeletedAuditable*,

    Created*, CodeNameSoftDeletedAuditable)

  dto/ — request/response DTOs (LoginRequest, AppRoleDto, ChatRequest, ChatStreamEvent, ResponseListDto, AppUserDto, ...)

  mapper/ — 15 MapStruct interfaces (*Mapper.java)

  ai/ — QdrantVectorStoreConfig, DatabaseChatMemory, AiChatToolContext, DatabaseSchemaTool, PostgreSQLQueryTool,

    DatabaseQueryValidator, UserActivityTool, AiFaceRegconitionServiceClient (spelling intentional)

  extraction/ — DocumentExtractor, DocumentExtractorFactory, TikaDocumentExtractor, MediaPlaceholderDocumentExtractor

  configuration/ — WebSecurityConfig, SecurityEnablerConfig, JwtTokenFilter, CustomPermissionEvaluator (stub),

    AsyncConfig, CacheConfig, AuditAwareImpl, AuditListener, WebConfigurerAdapter, I18n

  exception/ — GlobalExceptionHandler (active), CustomRestExceptionHandler (inactive), ApiException, ApiError,

    BaseResponseException, ChatStreamException

  specification/ — BaseSpecification, SearchSpecification, DynamicFilterSpec, Filter, SearchCriteria, SearchOperation

  properties/ — typed @ConfigurationProperties records (AppProperties prefix `app`, RagProperties prefix `app.rag`, JwtProperties,

    CookieProperties, MailProperties, QueueConfig, UploadImageConfig, AppCorsProperties, AppCronProperties, ...)

  util/ — ControllerUtil, CookieUtil, FileUtil, AuthUtil, PermissionChecker (via util/), ConstantData, UrlUtil, HashUtil, DateUtil

  validator/ — BaseValidator, RoleValidator, UserValidator, PermissionRequireValidator

  middleware/AuthorizationInterceptor.java — returns true; NOT an authorization boundary (see §9)

  queue/ — QueueConfig (TopicExchange + 6 non-durable queues + RabbitTemplate), QueueSender (no active @RabbitListener)

  scheduler/, vo/Paging.java, enumtype/, annotation/GenSourceableTable.java, logger/

src/main/resources/

  application.yml — prod base (profiles.active: dev, hikari, mail, rabbitmq, app.*)

  application-dev.yml — local PG localhost:5432, ddl-auto:update, ollama/mcp/qdrant, cdn-directory absolute path

  application-localdocker.yml — host.docker.internal PG override only

  application-dev-example.yml — sanitized template; encrypted.yml — ENC(...) placeholder

  db/migration/V1__init_current_schema.sql … V4__create_unanswered_prompt_log_table.sql

  mybatis/*.xml — AppUserMybatis.xml, PermissionMybatis.xml, AccessTokenMybatis.xml, FileManagerMybatis.xml,

    FilesDirectoryMybatis.xml (no AppRoleMybatis.xml)

  log4j2-dev.xml, log4j2-prod.xml, log4j2-dev-example.xml

  prompts/system-rag.txt, system-rag-db-tools.txt, system-rag-generate-title.txt

  templates/spring-{controller,service,service-impl,repository,mapper,dto}.ftl + mail templates + error.html

src/test/java/com/bekaku/api/spring/

  controller/api/AuthControllerTest.java, FileManagerControllerTest.java, AiChatControllerOwnershipTest.java

  serviceImpl/AiRagChatServiceOwnershipTest.java, FaceRecognitionServiceOwnershipTest.java

```

**## 3. Coding Conventions**

- ****Packages/naming:**** `serviceImpl` (capital I), `DevelopmentContoller` (one `r`), route `/api/faceRegconition` (missing `o`), `AiFaceRegconitionServiceClient` — all intentional legacy spellings. Do not rename in unrelated work (evidence: `src/main/java/com/bekaku/api/spring/serviceImpl/`, `src/main/java/com/bekaku/api/spring/controller/dev/DevelopmentContoller.java`, `src/main/java/com/bekaku/api/spring/ai/AiFaceRegconitionServiceClient.java`).

- ****DI:**** constructor injection with Lombok `@RequiredArgsConstructor` on controllers/services; `JwtTokenFilter` uses field `@Autowired` (`src/main/java/com/bekaku/api/spring/configuration/JwtTokenFilter.java:36-40`). `BaseApiController` uses field `@Autowired HttpServletRequest + I18n` (`src/main/java/com/bekaku/api/spring/controller/api/BaseApiController.java:28-32`).

- ****Logging:**** Log4j2 only. `@Slf4j` everywhere; some classes use `LoggerFactory.getLogger(...)` (`src/main/java/com/bekaku/api/spring/validator/*`, `src/main/java/com/bekaku/api/spring/middleware/AuthorizationInterceptor.java`, `src/main/java/com/bekaku/api/spring/serviceImpl/EmailServiceImpl.java`, `src/main/java/com/bekaku/api/spring/controller/test/TestController.java:48` uses `login-log` logger). Config via `logging.config: classpath:log4j2-prod.xml` (`src/main/resources/application.yml:241-243`) and `log4j2-dev.xml` in dev. Logback is excluded in `build.gradle:28-30`; never import `ch.qos.logback`.

- ****Entities:**** `@Table(comment=..., indexes=@Index)`, lazy relations, `@JoinColumn(name=..., comment="FK -> Ref table: X (id)...")`, `public static Sort getSort()` default-sort helper. Example `src/main/java/com/bekaku/api/spring/model/AppUser.java`:

  ```java

  @SQLDelete(sql="UPDATE app_user SET deleted=true WHERE id=?")

  @SQLRestriction("deleted=false")

  public class AppUser extends *SoftDeletedAuditable*<Long> {

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="avatar_file_id") private FileManager avatarFile;

    @ManyToMany(fetch = FetchType.LAZY) @JoinTable(name="app_user_role", ...) private Set<AppRole> appRoles;

  }

  ```

- ****DTOs:**** Lombok `@Data/@Getter/@Setter` + Jakarta constraints with i18n keys. Example `src/main/java/com/bekaku/api/spring/dto/AppRoleDto.java:20`: `@Size(min=3,max=100,message="{error.Size3Limit100}")`; `src/main/java/com/bekaku/api/spring/dto/LoginRequest.java:23`: `@NotBlank(message="{error.validateRequire}")`.

- ****Mappers:**** `src/main/java/com/bekaku/api/spring/mapper/*Mapper.java`, always `@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.IGNORE)`. Simple (`src/main/java/com/bekaku/api/spring/mapper/AppUserMapper.java`): `AppUserDto toDto(AppUser e); AppUser toEntity(AppUserDto d);`. With ignores (`src/main/java/com/bekaku/api/spring/mapper/FileManagerMapper.java`): `@Mapping(target="fileMime", ignore=true)`.

- ****MyBatis:**** interface method ↔ XML `namespace + statement id` must match; `@Param` names must match `#{...}` bindings. Example `src/main/java/com/bekaku/api/spring/mybatis/AppUserMybatis.java`: `List<AppUserDto> findAll(@Param("page") Paging page);` ↔ `src/main/resources/mybatis/AppUserMybatis.xml: namespace=com.bekaku.api.spring.mybatis.AppUserMybatis`.

- ****Config:**** new settings go in typed records under `src/main/java/com/bekaku/api/spring/properties/` (e.g. `AppProperties.java`, `RagProperties.java`) with `@ConfigurationPropertiesScan`, never `@Value` sprawl for new groups (legacy `EncryptServiceImpl.java:33 @Value(${app.encrypt-key})` is exception).

**## 4. Architecture Rules**

1. Layering is `Controller → Service interface → ServiceImpl → Repository/MyBatis`; MapStruct at transport boundary. Controllers call services, never `EntityManager`/`JdbcTemplate`/`VectorStore` directly. AI tools (`src/main/java/com/bekaku/api/spring/ai/*Tool.java`) are the only non-service DB/vector callers and share app `JdbcTemplate`.

2. Transaction boundaries: services own transactions. Many service classes default `@Transactional(readOnly=true)`; write entry points must declare `@Transactional`. Private methods and same-bean self-calls do not create a new proxy transaction.

3. DB transactions do not roll back filesystem, Qdrant, email, or remote-service effects. Multi-step writes (ingestion, file merge, signup) must define compensation order (see §8 RAG lifecycle).

4. Dependency direction: `controller → service → repository/mapper`; `specification/*`, `util/ControllerUtil`, `vo/Paging` are shared helpers. Do not call controllers from services; do not put business writes in controllers.

5. ****JPA-vs-MyBatis decision rule (binding):**** JPA for CRUD + `JpaSpecificationExecutor` filtering + simple `@Query`/derived lookups (`src/main/java/com/bekaku/api/spring/repository/AppUserRepository.java`). MyBatis + `vo/Paging` for join/paging read-model projections returning DTOs (`src/main/java/com/bekaku/api/spring/mybatis/*.java`, `src/main/resources/mybatis/*.xml`). Do not add new MyBatis writes; do not use MyBatis for single-table CRUD that JPA already covers.

6. Search: JPA path uses `ControllerUtil.getSearchCriteriaList` + `SearchSpecification` + `getPageable(pageable, Entity.getSort())` (`src/main/java/com/bekaku/api/spring/util/ControllerUtil.java:23-61`, `src/main/java/com/bekaku/api/spring/controller/api/BaseApiController.java:116-123`). MyBatis path uses `getPaging(pageable, acceptSortField)` allow-list (`BaseApiController.java:140-155`) and `LIMIT/OFFSET` in XML. `_q` syntax uses `;` separators, comma = `IN`, `_keyword` searches controller-specified columns only.

7. Async/messaging: `@Async("asyncTaskExecutor")` pool is defined in `src/main/java/com/bekaku/api/spring/configuration/AsyncConfig.java:13-21` (platform-thread pool despite virtual-thread setting). Only demo usage exists (`AppUserServiceImpl.java:207 processAsyncTask`). RabbitMQ topology is declared (`src/main/java/com/bekaku/api/spring/queue/QueueConfig.java:20-28`, `QueueSender.java:34-66`, `application.yml:153-169`) but there is ****no active `@RabbitListener`**** — retry/concurrency YAML alone implements nothing. Kafka, WebSocket broker, Undertow configs are disabled/commented. If adding consumers, define idempotency + duplicate-delivery tests.

**## 5. API Contract Rules**

- ****No envelope.**** `BaseApiController.responseEntity(body, status)` returns the body directly (`src/main/java/com/bekaku/api/spring/controller/api/BaseApiController.java:34-44`). `BaseResponseEntity` is a separate record, not an automatic wrapper. `responseServerMessage` builds `{message, status, timestamp}` with `ConstantData.SERVER_*` keys (`BaseApiController.java:66-72`); the 4-arg overload adds `success`.

- ****Routes:**** `@RequestMapping("/api/<resource>")`, e.g. `/api/appRole` (`src/main/java/com/bekaku/api/spring/controller/api/AppRoleController.java:29`), `/api/fileManager` (`FileManagerController.java`), `/api/aiChat` (`AiChatController.java:39`). Preserve existing spellings.

- ****Verbs/status:**** `GET /` → `200 ResponseListDto<T>`; `GET /{id}` → `200 DTO`; `POST /` → `201 DTO`; `PUT /{id}` → `200 DTO`; `DELETE /{id}` → `200` message via `responseDeleteMessage()` (which returns HTTP 200, not 204). `AiChatController` returns raw DTOs (implicit 200, no `ResponseEntity`).

- ****Paged shape:**** `src/main/java/com/bekaku/api/spring/dto/ResponseListDto.java:7-13` = `{dataList, totalPages, totalElements, isLast}`. Some file-list endpoints return bare `List<FileManagerDto>` — verify per endpoint before changing serialization.

- ****HATEOAS:**** dependency present (`build.gradle:51`) but no `EntityModel/Link` usage — do not introduce HATEOAS links.

- ****Validation:**** `@Valid @RequestBody` on all mutating endpoints (no class-level `@Validated`), then manual `*Validator` duplicate/existence checks. Example `AppRoleController.java:68-75`: `@Valid @RequestBody AppRoleDto` + `roleValidator`.

- ****Errors:**** throw `ApiException` via `BaseResponseException` helpers (`src/main/java/com/bekaku/api/spring/exception/BaseResponseException.java:13-75`: `responseErrorUnauthorized/Forbidden/BadRequest/Notfound/Duplicate` with i18n keys `error.401/403/error/dataNotfound/validateDuplicate`). Handled by `GlobalExceptionHandler` (`@RestControllerAdvice @Order(HIGHEST_PRECEDENCE)`, `src/main/java/com/bekaku/api/spring/exception/GlobalExceptionHandler.java:42-45`) into `ApiError{status,message,errors,timestamp}` (`src/main/java/com/bekaku/api/spring/exception/ApiError.java:17-22`). `CustomRestExceptionHandler` is `//@ControllerAdvice` — inactive, do not use. `JwtTokenFilter` 401s use separate `{"error":"..."}` shape (`JwtTokenFilter.java:124-136`) — not `ApiError`. Streaming/file errors must respect committed responses (see `GlobalExceptionHandler.java:207-255` `ClientAbort/IOException` handlers).

- ****Pagination params:**** standard `page,size,sort` → `Pageable`; MyBatis path uses `vo/Paging.java:10-52` (`page-1` offset, `limit≤100`, `sort=field,asc|desc`). Validate dynamic sort field against allow-list; the base direction check is not field authorization.

**## 6. Data Layer Rules**

- ****IDs:**** all entities extend `model/superclass/Id.java` — Snowflake `Long` assigned in `@PrePersist` only when null (`SnowflakeIdHolder.generator().nextId()`). Never mix with `IDENTITY/SEQUENCE`. `SoftDeletedAuditable<Long>` type param is the auditor type, not PK type.

- ****Audit:**** `SoftDeletedAuditable<U>/Auditable<U>` use `AuditingEntityListener` + `@CreatedBy/@CreatedDate/@LastModifiedBy/@LastModifiedDate` (all `@JsonIgnore`); resolver is `configuration/AuditAwareImpl` reading `AppUserDto` from security context — pass actor ID explicitly in async/Reactor paths. Selective audit-trail logging via `configuration/AuditListener` (`@PrePersist/@PreUpdate/@PreRemove → auditLogService`) enabled per entity with `@EntityListeners(AuditListener.class)` (e.g. `AppRole`, `Permission`).

- ****Soft delete:**** requires all three: `deleted` boolean field (default false, e.g. `SoftDeletedId.java`), entity-level `@SQLDelete` + `@SQLRestriction("deleted=false")` (verify per entity — `FileManager` also nulls FK: `UPDATE file_manager SET deleted=true, files_directory_id=null WHERE id=?`). Base field alone deletes nothing. MyBatis/native SQL must add explicit `deleted=false` + ownership predicates.

- ****Relations:**** prefer `fetch=LAZY`, DTO projections, bounded fetching; check N+1 before adding relations to list responses. Never use Lombok `@Data` on entities with relationships (equality/logging/serialization traversal); nearby entities use proxy-safe identity equality.

- ****Repositories:**** extend `BaseRepository<Entity,Long>` (`@NoRepositoryBean`, `src/main/java/com/bekaku/api/spring/repository/BaseRepository.java`) plus `JpaSpecificationExecutor` where filtering is needed. Example custom: `repository/PermissionRepositoryCustom.java` + `repositoryImpl/PermissionRepositoryCustomImpl.java` (EntityManager + JdbcTemplate + `BeanPropertyRowMapper`).

- ****Flyway:**** location `src/main/resources/db/migration/`, naming `V{version}__{snake_case}.sql`. Only V1–V4 exist in this checkout (`V1__init_current_schema.sql` … `V4__create_unanswered_prompt_log_table.sql`). Add the next migration; never rewrite deployed migrations. Validate PG-specific SQL (casts, vector operators) on disposable PostgreSQL + pgvector — H2 cannot validate them.

- ****MapStruct:**** new mappers go in `mapper/`, name `*Mapper.java`, `@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.IGNORE)` — and because unmapped targets are ignored, review every field intentionally. Separate create/update request DTOs when allowed fields differ.

- ****Permissions data:**** new permissions require explicit permission records + intentional role assignments; a new permission grants nothing by itself. Update UI ACL/localized labels only where the feature needs them.

**## 7. Security Rules**

- ****Three boundaries (trace all three):**** (1) `WebSecurityConfig` route rules, (2) `JwtTokenFilter` skip-list + verification, (3) method/service ownership checks. `AuthorizationInterceptor` returns true — supplies no authorization (`src/main/java/com/bekaku/api/spring/middleware/AuthorizationInterceptor.java`).

- ****Filter chain**** (`src/main/java/com/bekaku/api/spring/configuration/WebSecurityConfig.java:77-138`, stateless, CSRF disabled, `JwtTokenFilter` before `UsernamePasswordAuthenticationFilter`): permits `OPTIONS`, `/favicon.ico`, `/_websocket/**`, `/actuator/**`, `/css/**`, `/<cdnPathAlias>/**`, `ASYNC` dispatches, `POST /api/auth/login|loginApi|logout|logoutApi|refreshToken|refreshTokenApi|requestVerifyCodeToResetPwd|sendVerifyCodeToResetPwd|resetPassword`, `GET /api/public/**`, `GET /schedule/**`; requires auth for `/api/**` (non-prod also opens `/test/**`, `/dev/development/**`, `/welcome`, `/swagger-ui/**`, `/api-docs/**`, `/theymeleaf`); `anyRequest().denyAll()`. `SecurityEnablerConfig` enables method security (`@EnableWebSecurity @EnableMethodSecurity`).

- ****Filter skip**** (`JwtTokenFilter.java:43-59,146-151`): `SKIP_PATHS=[/api/public/**, /api/auth/**, /schedule/**, /cdn/**, /favicon.ico, /_websocket/**, /dev/development/**, /actuator/**, /test/**, /welcome, /theymeleaf, /api-docs/**, /swagger-ui/**]` + `OPTIONS`. Token resolution is cookie-first then `Authorization` header; user-id resolution is `_sid` cookie then `X-User-ID` header (`JwtTokenFilter.java:75-83`). Missing token → `401 {"error":"Jwt token not found"}`; failed verify → `401 {"error":"Invalid or missing token"}`.

- ****JWT:**** HMAC with `app.jwt.secret`; access TTL minutes + refresh TTL days (`application.yml` `jwt:` block; `JwtServiceImpl.java:56-80`). `sub` = raw `AccessToken` token, claims `{uid, JwtType}`; verification (`JwtServiceImpl.java:149-206`) requires sub+JwtType+uid, `JwtType==Authen`, live non-revoked session looked up by SHA-256 hash (`AccessTokenServiceImpl.java:212-214`), then sets `UsernamePasswordAuthenticationToken(AppUserDto{id,token,accessTokenId}, null, emptyList())`.

- ****Cookies**** (`application.yml:344-354`, `util/CookieUtil.java:20-64`, `controller/api/AuthController.java:260-286`): names `_session_<uid>` (access, minutes), `_slid_<uid>` (refresh, days), `_sid` (current-user id, days); all `httpOnly, secure, SameSite=Lax, path=/`. `X-User-Id` header is request input, never proof of identity.

- ****Refresh flow (binding lifecycle):**** `AuthController.java:351-429` + `AuthServiceImpl.java:118-146` + `AccessTokenServiceImpl.java:190-203`: `handleRefreshTokenReuse(presentedKey)` — if presented token is revoked, `revokeTokenByUserId(userId)` (reuse detection); else require live non-revoked session + active non-deleted user + unexpired token (else delete cookies + `403 Session Expired`); then `refreshToken`: revoke old, insert new `AccessToken` + cloned `LoginLog`, return `{authenticationToken, refreshToken, expiresAt, userId}`. Preserve rotation + reuse detection + ownership checks; test inactive/deleted users, revoked/expired sessions, wrong client/owner on both cookie and API paths.

- ****Authorization:**** permission checks via `@PreAuthorize("@permissionChecker.hasPermission('module_action')")` (`src/main/java/com/bekaku/api/spring/util/PermissionChecker.java:9-66`, e.g. `AppRoleController.java:50,66,91,118,136`: `app_role_list|_add|_edit|_view|_delete`). `CustomPermissionEvaluator` always returns false — do not rely on it. Owner-scoped resources (chat, files, faces) must additionally scope by creator/owner (`AiChatController.java:61,90,104,114` uses `findByIdAndCreator(id, auth.getId())`).

- ****Never log/expose:**** raw refresh tokens, JWT secrets (`app.jwt.secret`), AES key (`app.encrypt-key`), password hashes, `X-API-KEY`, MCP DB URL credentials. Service lookups hash raw tokens internally — do not double-hash. Passwords use `EncryptService.encrypt/check` (BCrypt); AES-GCM data encryption is separate; legacy MD5 helpers exist but auto-migration is commented out.

- ****Signup/OTP:**** signup must assign configured default roles, never caller-selected privileges; making it public needs intentional route+filter change + tests. OTP throttle is process-local keyed by email — not distributed rate limiting; reset/OTP changes need expiry/replay/abuse tests. CSRF is disabled while cookie auth is supported — revisit browser threat model when touching SameSite/CORS or cookie-authenticated writes.

**## 8. AI/RAG Module Rules**

- ****Stack guard:**** MVC SSE with Reactor publishers, not a WebFlux server (`AiChatController.streamChat` returns `Flux<ChatStreamEvent>` with `produces=TEXT_EVENT_STREAM_VALUE`). Offload blocking JDBC/filesystem/model work (e.g. `Mono.fromCallable(...).subscribeOn(boundedElastic)` in `AiRagChatServiceImpl.java:101-303`) and pass user identity explicitly across threads.

- ****Vector stores**** (`src/main/java/com/bekaku/api/spring/ai/QdrantVectorStoreConfig.java:15-75`): gated by `@ConditionalOnProperty(spring.ai.vectorstore.qdrant.enabled=true, matchIfMissing=false)`; beans `documentVectorStore` (`@Primary`, collection `rag_documents`) + `schemaVectorStore` (collection `table_schemas`), both `contentFieldName="doc_content"`, `initializeSchema(true)`. Changing YAML alone does not change these hardcoded names. `app.rag.qdrant-enabled` is not bound — the real flag is `spring.ai.vectorstore.qdrant.enabled` (dev default `false` in `application-dev.yml:86-122`). Null/disabled stores cause runtime failures — gate every consumer and verify startup + endpoint behavior when implementing a disabled mode.

- ****Ingestion pipeline**** (`src/main/java/com/bekaku/api/spring/serviceImpl/AiDocumentIngestionServiceImpl.java:76-132`): resolve type via `FileUtil.resolveAiDocumentTypeByMime` → `DocumentExtractorFactory.getExtractor` (`extraction/DocumentExtractorFactory.java:17-20`: IMAGE/VIDEO → `MediaPlaceholderDocumentExtractor` placeholder, no OCR/transcription; others → `TikaDocumentExtractor` using `TikaDocumentReader`) → `extract` → `TokenTextSplitter` (`AiDocumentIngestionServiceImpl.java:148-153`: `chunkSize` from `app.rag`, `keepSeparator=true`) → `documentVectorStore.add(chunks)` → save `AiDocumentMeta` with vector IDs → optionally `deleteSourceAfterIngest`. `chunk-overlap` and `max-num-chunks` config keys are unused by the splitter — do not promise them.

- ****Postgres↔Qdrant tie**** (`src/main/java/com/bekaku/api/spring/model/AiDocumentMeta.java:49-61`): `@ElementCollection ai_document_vector_ids.vector_id List<String>` holds Qdrant chunk IDs; `ai_document_metadata` map holds extra metadata. Keep them consistent.

- ****Deletion lifecycle (binding):**** re-ingest replaces via `findByFileName → deleteDocument`; `deleteDocument` = `documentVectorStore.delete(vectorIds)` + `documentMetaRepository.delete` (`AiDocumentIngestionServiceImpl.java:82-85,211-229` + `safeRollbackVectors` on meta-save failure); controller `AiDocumentMetaController.java:52-63,121-130` (`ingest/{fileManagerId}`, `DELETE /{id}`) additionally deletes the source `FileManager` file when `delete-source-after-ingest:true`. Source deletion must follow a defined durable-success boundary — a later commit failure after vector write leaves orphans (compensation is limited).

- ****Streaming chat**** (`AiChatController.java:53-56`, `AiRagChatServiceImpl.java:101-303`, `dto/ChatStreamEvent.java:17`): event types `token|sources|done|error` (+ `chat_id|title|thinking` used in code); `sources.content` is JSON-serialized inside a string. Preserve event names/payload types. Handle errors before/after commit; never append another HTTP body after streaming starts (`GlobalExceptionHandler` silent `ClientAbort/IOException` path).

- ****Memory:**** `DatabaseChatMemory` is read-only — `add/clear` are no-ops, `get` loads last-N via `findLastNMessagesByChatId(..., app.rag.memorySize)` and drops the trailing user message (`src/main/java/com/bekaku/api/spring/ai/DatabaseChatMemory.java:31-59`). Chat persistence lives in `streamAnswer`; do not double-save when adding advisors. Only active advisor is `MessageChatMemoryAdvisor`; `ChatClientConfig` (`QuestionAnswerAdvisor`) is `//@Configuration` disabled.

- ****Tools:**** active `ToolCallbacks.from(userActivityTool)` + conditional `databaseSchemaTool, postgreSQLQueryTool` (`AiRagChatServiceImpl.java:181-193`); context collector `AiChatToolContext` via `chatToolContext` key. `DatabaseSchemaTool` fans out to `schemaVectorStore.similaritySearch`; `PostgreSQLQueryTool.executeSelect` validates via `DatabaseQueryValidator` (must start `SELECT/WITH`, rejects comments/DDL/DML/COPY/DO, single statement) then `jdbcTemplate.queryForList`. Built-in tools share app `JdbcTemplate` — MCP stdio read-only credentials (`application-dev.yml:100-113`) do not constrain them. Isolate credentials/tables/columns/row scope, add timeouts + result bounds. Treat prompts, retrieved text, model SQL as untrusted; prompt text is not an authorization boundary.

- ****Ownership:**** check conversation ownership before reading history, resuming, timestamp updates, or adding messages — preserve `findByIdAndCreator` scoping and bind new chats to the authenticated actor. Face recognition is a separate Python service + pgvector path; preserve actor + file-owner checks and `/api/faceRegconition` spelling.

- ****Config:**** `app.rag{top-k:5, similarity-threshold:0.5, chunk-size:800, memory-size:15, delete-source-after-ingest:true}` + `spring.ai.ollama{base-url, chat.model:gemma4:e2b, embedding.model:bge-m3}` (`application-dev.yml:86-122`); typed in `properties/RagProperties.java`. Prompts in `src/main/resources/prompts/system-rag*.txt`.

**## 9. Do / Don't**

Do:

- Extend `BaseApiController` for REST resources; use `responseEntity(dto, CREATED/OK)`, `ResponseListDto` for paged lists, `getPageable(pageable, Entity.getSort())` for JPA sorts.

- Put `@Valid` on every mutating `@RequestBody`, use i18n constraint messages, then call the matching `validator/*` for duplicates/existence.

- Use `@PreAuthorize("@permissionChecker.hasPermission('...')")` for admin resources AND owner-scoping (`findByIdAndCreator` / `requireTheSameUser`) for user-owned rows.

- Use `#{...}` bindings in MyBatis XML; `LIMIT/OFFSET` + current schema names; keep interface/`@Param`/XML ids/resultMaps in sync.

- Use `@Slf4j`, typed `properties/*` records, `CookieUtil` for auth cookies, `EncryptService` for passwords, `UrlUtil.validatePublicUrl` + per-redirect validation for URL fetching, server-generated filenames + real-path containment for file I/O.

- Stream large content; preserve `Range` handling on video/file endpoints; preserve `FileManagerController.getImage` containment checks + regression tests.

Don't:

- Don't add an `ApiResponse` envelope, HATEOAS links, class-level `@Validated`, `IDENTITY/SEQUENCE` IDs, Lombok `@Data` on relational entities, `${...}` interpolation of request text in MyBatis XML, or new MyBatis writes for JPA-covered CRUD.

- Don't log/expose raw refresh tokens, JWT/AES secrets, password hashes, API keys, or MCP credentials. Don't double-hash tokens. Don't trust `X-User-Id` as identity.

- Don't place secrets/logs/backups under the public storage mapping (`WebConfigurerAdapter` maps storage root publicly); private files stay behind authenticated owner-aware paths.

- Don't treat `AuthorizationInterceptor`, prompt instructions, `UrlUtil` alone, OTP throttle, or YAML-only Qdrant/consumer settings as complete security/correctness boundaries (see §§7–8).

- Don't use `AppUserMybatis.findAll` / `PermissionMybatis.findAll` MySQL pagination or `selectUserData`'s `user_role` table as templates — both are stale (see §10).

- Don't run the code generator (`DevelopmentContoller`) as a read-only diagnostic; when requested, inspect `@GenSourceableTable` + `ConstantData` destinations, review diffs, add validation/ownership/transactions, compile outputs; never hand-edit `build/generated` as a lasting change.

**## 10. Testing Requirements**

- Location: `src/test/java/com/bekaku/api/spring/` (`controller/api/`, `serviceImpl/`). Framework: JUnit Jupiter + Mockito (`@Mock/@InjectMocks`, `MockitoSettings(LENIENT)`) + AssertJ; no `@SpringBootTest/@WebMvcTest/@WithMockUser` in current suite despite `spring-security-test` + `spring-restdocs-mockmvc` deps.

- What/how/where:

  | Change | Required evidence |

  |---|---|

  | Controller/service logic | Focused unit test incl. failure path + ownership (`AiChatControllerOwnershipTest`, `AiRagChatServiceOwnershipTest`, `FaceRecognitionServiceOwnershipTest` pattern: assert `findByIdAndCreator` scoping, `verifyNoInteractions` on bypass) |

  | Auth/routes | HTTP test through real filter chain + cookie AND Bearer flows (current `AuthControllerTest` constructs controller directly — insufficient for chain enforcement) |

  | SQL/schema/vector columns | Disposable PostgreSQL + pgvector integration test (H2 invalid for casts/vector ops/dump migrations) |

  | File ops | `@TempDir` containment/ownership/partial-failure tests (`FileManagerControllerTest` standalone MockMvc pattern) |

  | RAG/SSE | Mock model/vector deps; verify event order, disabled mode, cancellation/error, persistence |

  | Config/wiring | `compileJava`/context-startup test |

  | Docs only | Verify statements/commands/links/whitespace; claim no runtime validation |

- Commands: `./gradlew test --tests '*AuthControllerTest*'`, `./gradlew test`, `./gradlew compileJava`, `./gradlew bootJar`, `./gradlew bootRun --args='--spring.profiles.active=dev'` (needs PG/Qdrant/Ollama infra). Note: `--tests` filters execution, not test-source compilation.

- Report changed files, behavior deltas, exact commands + results, remaining integration limits. Never claim deployment/security/success without verification.

**## 11. Known Issues / Legacy Exceptions (do NOT copy as patterns)**

1. `JwtTokenFilter.SKIP_PATHS` blanket-skips `/api/auth/**` while protected account routes live under it — direct controller tests conceal the hole; fix needs route/filter agreement + chain tests.

2. `AppUserMybatis.findAll` and `PermissionMybatis.findAll` contain MySQL pagination; `selectUserData` references `user_role` instead of `app_user_role` — correct + DB-test before reuse.

3. `CustomPermissionEvaluator` stub returns false; `AuthorizationInterceptor` returns true — neither enforces authorization.

4. `DatabaseChatMemory.add/clear` no-ops; `chunk-overlap`/`max-num-chunks` unused; image/video extraction is placeholder text (no OCR/transcription); RAG compensation on commit failure is limited (orphan vectors possible).

5. Caching: `@EnableCaching` + `CacheConfig` (JCache/Ehcache) exist but zero `@Cacheable/@CacheEvict/@CachePut` usages and no `ehcache.xml` — verify named-cache creation before adding annotations. Redis Compose service is not an app-cache integration.

6. Queues: declarations active + non-durable + shared routing-key fan-out, but no active `@RabbitListener` (factory bean only); `AppUserServiceImpl.processAsyncTask` is demo-only; async executor is platform-thread pool despite virtual-thread setting.

7. Deps present but unused: POI (only `FileUtil.hasExcelFormat`), Firebase Admin (only `fcm_token` column + `FcmVo` + `refreshFcmToken`), WebSocket broker/Kafka/Undertow/native-image (disabled), `ChatClientConfig` advisor (disabled), MCP client (config-only in `application-dev.yml`, no `McpClient/ToolCallbackProvider` code).

8. Secrets in plain YAML (`app.encrypt-key`, `jwt.secret`, datasource/rabbitmq/mail/qdrant keys, MCP URL password); `.env` not globally ignored — inspect `git status` before staging config. `environments.production` gates routes separately from `spring.profiles.active`.

9. Storage root publicly mapped (`WebConfigurerAdapter`); `FileManagerController.getImage` path containment is not per-user ownership; chunk merge deletes chunks while copying (non-atomic); `app.cron.clean-old-temp-chunks` gates only temp-chunk cleanup, not all scheduled tasks.

10. Baseline fragility: full test compilation has been blocked when `AuthController.signup` is commented out while `AuthControllerTest` still calls it; ownership/file tests are isolated unit tests, not chain/DB/external-service proof.