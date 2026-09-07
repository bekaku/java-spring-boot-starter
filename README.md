# Java Spring Boot API Starter

A Java 25 / Spring Boot 4.1 backend for administration APIs, database-backed login sessions, permission management, file storage, and AI document chat. It is a single Gradle application (`api-service`) with a layered architecture and optional infrastructure examples.

This README reflects a source review on **2026-09-07**. The application has useful reusable components, but also incomplete integrations and access-control gaps described under [Review findings](#review-findings). Compilation and unit tests were executed; database initialization, application startup, containers, and external services were not exercised during this review.

For coding agents, start with [SKILLS.md](SKILLS.md): it identifies source entry points, implementation conventions, and checks to perform before changing behavior.

## Technology stack

Versions below are declarations in [build.gradle](build.gradle) and the [Gradle wrapper configuration](gradle/wrapper/gradle-wrapper.properties), not recommendations for upgrades. Dependencies without explicit versions use the imported dependency management.

| Area | Implementation |
| --- | --- |
| Language/build | Java 25 toolchain; Gradle 9.6.0; Spring dependency-management plugin 1.1.7 |
| Application | Spring Boot 4.1.0; Spring MVC; embedded Tomcat |
| Persistence | Spring Data JPA/Hibernate; PostgreSQL JDBC; HikariCP 7.1.0; MyBatis starter 4.0.1 |
| Database extension | pgvector `vector(512)` for face embeddings; separate Qdrant collections for document/schema embeddings |
| Schema | Flyway starter and PostgreSQL support; migrations V1–V4; disabled in shared configuration |
| Security | Spring Security; JJWT 0.13.0; BCrypt password hashing; SHA-256 refresh-token storage; AES-GCM data encryption |
| Mapping | MapStruct 1.6.3; Lombok 1.18.46; Lombok/MapStruct binding processor |
| AI | Spring AI 2.0.0 BOM; Ollama chat/embedding models; Qdrant; MCP client; advisor dependency explicitly pinned to 2.0.0-M8 |
| Documents/media | Spring AI PDF/Tika readers; Tika 3.3.1; POI 5.5.1; Thumbnailator 0.4.21; metadata-extractor; TwelveMonkeys WebP |
| Templates/localization | Thymeleaf for web/mail; FreeMarker for source generation; English/Thai message bundles |
| Messaging | Spring AMQP/RabbitMQ topology and producer helpers |
| Cache | Spring Cache → JCache → Ehcache 3.12.0 |
| Monitoring/logging | Actuator; Micrometer Prometheus; Log4j2 through SLF4J; Logback excluded |
| API tooling | springdoc OpenAPI/Swagger UI 3.0.3 |
| Tests | JUnit Jupiter/Platform; Mockito; AssertJ; Boot Test, Spring Security Test and REST Docs dependencies; explicit Jupiter API 6.1.0-M1 |
| Supporting service | Python FastAPI face extraction with InsightFace, ONNX Runtime, OpenCV and NumPy |
| Delivery | Java 25 Dockerfiles; Compose service definitions; Kubernetes examples; Docker Hub publishing workflows |

This is a servlet MVC application even though chat returns Reactor `Flux` for Server-Sent Events. The WebFlux starter is commented out. Virtual threads are enabled in YAML, while the explicitly configured async executor uses a conventional 10–20 thread pool.

### Integration status

| Component | Actual state |
| --- | --- |
| PostgreSQL | Active JDBC driver and PostgreSQL-specific entities/queries. Bundled Compose defaults to `pgvector/pgvector:pg18`. |
| MySQL/H2 | MySQL examples remain, but its connector is commented out. H2 settings remain without an H2 dependency. Neither is a ready alternative. |
| Undertow | Dependency/configuration disabled; leftover YAML does not select it as the server. |
| RabbitMQ | Exchange, non-durable queues, JSON converter and sender exist. No active application `@RabbitListener` consumers were found. |
| WebSocket/STOMP | Starter and sample code exist; `WebSocketConfig` registration annotations and most chat handlers are commented out. |
| Kafka/Redis | Infrastructure examples; Kafka application code/dependency disabled; no active Spring Data Redis integration. |
| OpenAI | Example properties exist, but the model starter is commented out; Ollama is the compiled provider. |
| MCP | Starter installed; development example enables a Node/npx PostgreSQL server. It is separate from the built-in Java SQL tools. |
| Qdrant | Custom named stores are conditional; see the configuration details below. Disabled stores do not produce a working non-RAG chat fallback. |
| Native image | Dockerfile and CI example exist, but the GraalVM Gradle plugin is commented out; `nativeCompile` is unavailable in the current build. |

## Architecture and source map

```text
HTTP / servlet security filter
  → API controller: request validation, permission/ownership checks, response
  → service interface → serviceImpl: business operations, transaction boundaries
  → Spring Data repository / Specification or MyBatis interface + XML
  → PostgreSQL

AI chat: MVC SSE → AiRagChatServiceImpl → Ollama
                        ├─ JPA chat history and message persistence
                        ├─ Qdrant document retrieval
                        └─ optional Java tools / PostgreSQL queries
```

All Java paths below are relative to `src/main/java/com/bekaku/api/spring/`.

| Location | Responsibility |
| --- | --- |
| `SpringApiApplication.java` | Boot application; configuration-property scan, caching, JPA auditing, scheduling and async enablement |
| `controller/api/` | Authentication, users, roles, permissions, API clients, files/directories, chat, document metadata, face endpoints |
| `controller/dev/`, `controller/test/`, `controller/web/` | Development generator, diagnostic examples, Thymeleaf pages |
| `service/`, `serviceImpl/` | Contracts and implementations; some generic contract methods are still stubs |
| `model/`, `model/superclass/` | Domain entities, soft deletion, audit metadata, Snowflake IDs |
| `repository/`, `repositoryImpl/` | JPA repositories, Specifications and custom repository extensions |
| `mapper/` | MapStruct entity/DTO conversion; separate from MyBatis |
| `mybatis/` + `src/main/resources/mybatis/` | Mapper interfaces and XML SQL/projection definitions |
| `dto/`, `vo/`, `specification/` | Transport types, paging, dynamic filters |
| `configuration/`, `properties/` | Security, HTTP, cache, persistence, async and typed configuration |
| `ai/`, `extraction/` | Chat memory, model tools, Qdrant stores, Tika/media extraction |
| `queue/`, `scheduler/`, `exception/` | Queue declarations, scheduled jobs, error handling |

Other useful directories are `src/main/resources/templates/` (mail/web/code templates), `i18n/` (English/Thai bundles), `prompts/` (AI instructions), `db/migration/`, `docker-compose/`, `kubernetes/`, and `spring-data/` (runtime data and reference SQL).

## Local configuration and startup

### 1. Prepare Java and local profiles

Install JDK 25 and use `./gradlew` on Unix/macOS or `gradlew.bat` on Windows. First-time builds require access to the configured Gradle/Maven repositories.

The ignored local files are not supplied by a fresh clone. Copy both templates only if their destinations do not already exist:

```bash
test -e src/main/resources/application-dev.yml || cp src/main/resources/application-dev-example.yml src/main/resources/application-dev.yml
test -e src/main/resources/log4j2-dev.xml || cp src/main/resources/log4j2-dev-example.xml src/main/resources/log4j2-dev.xml
```

Edit the local profile for your machine. Merge these settings into the existing sections rather than creating duplicate YAML keys:

```yaml
environments:
  production: false
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/spring_starter_postgres
    username: postgres_user
    password: ${LOCAL_DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
  ai:
    mcp:
      client:
        enabled: false
app:
  cdn-directory: ${LOCAL_STORAGE_DIR}
  cdn-path: file:///${app.cdn-directory}
  jwt:
    secret: ${APP_JWT_SECRET}
  encrypt-key: ${APP_ENCRYPT_KEY}
```

Set `LOCAL_STORAGE_DIR` to an absolute directory with a trailing slash. `AppProperties.getUploadPath()` strips `file:///` literally, so preserve the template's URI convention. Keep configuration, logs, backups and private data outside publicly served storage; see finding R1 before exposing the application.

The JWT implementation Base64-decodes `APP_JWT_SECRET`; provide at least 32 random decoded bytes. Data encryption currently uses the literal bytes of `APP_ENCRYPT_KEY` as its AES key, requiring 16, 24 or 32 bytes; it does **not** Base64-decode that setting. Use separate values.

Also configure `app.cors.allowed-origins`, application/CDN URL and ports, RabbitMQ credentials, and mail settings when needed. The Log4j2 example contains a Windows path and `${sys:APP_LOG_ROOT}` lookups: configure its file appenders for a valid local path/system property instead of assuming `logging.file.path` controls them.

MCP is enabled in the supplied development example and launches `npx`. Leave it disabled for an initial run unless Node.js, the PostgreSQL MCP server, and its connection are intentionally configured. No OpenAI key is needed for the compiled Ollama provider; remove unused OpenAI example configuration if retaining it causes unresolved placeholders.

### 2. Prepare PostgreSQL and pgvector

Use PostgreSQL with pgvector installed. The default development profile uses Hibernate `ddl-auto: update`, and the mapped `AppUserFace` entity requires the `vector` type even if the face API is not called.

For the bundled Compose service, create/update `docker-compose/postgres/.env` locally:

```dotenv
POSTGRES_DB=spring_starter_postgres
POSTGRES_USER=postgres_user
POSTGRES_PASSWORD=replace-with-your-local-password
POSTGRES_PORT=5432
```

```bash
docker compose -f docker-compose/postgres/docker-compose.yml up -d
docker compose -f docker-compose/postgres/docker-compose.yml exec postgres psql -U postgres_user -d spring_starter_postgres -c 'CREATE EXTENSION IF NOT EXISTS vector;'
```

Use matching credentials in the application. The Compose initialization mount is commented out: the included `init/01-enable-pgvector.sql` does not run automatically under the current definition. `.env` is not globally ignored in this repository; keep credentials out of commits.

Choose a schema strategy explicitly:

- **Local schema generation:** keep Flyway disabled and allow Hibernate `update` against a disposable development database. This creates mapped tables, not all required reference users, API clients, role assignments or permission records.
- **Migration-managed schema:** review V1–V4, enable `spring.flyway.enabled`, and set Hibernate to `validate` or `none`. V1 is a PostgreSQL 18.4 dump containing seed data and `OWNER TO postgres_user`; V3 requires pgvector. V2 drops and recreates `login_from`, losing existing values. Verify these prerequisites and migrations on a disposable database before deployment.
- **Existing dump:** `spring-data/files/spring_starter_postgres.sql` is a reference snapshot, not an automatic startup initializer. Do not import it and then run V1 on the same schema without a deliberate baseline plan.

Authentication requires an active user with a BCrypt password and a registered API-client name. Permission-protected routes additionally require role/permission associations. Inspect seed data; do not assume a supported default admin password. The generator's `/migrateData` endpoint has its seed calls commented out, and signup is currently blocked by the filter/routing mismatch in R4.

### 3. Prepare enabled services and run

RabbitMQ connection settings must match your broker. The bundled service mounts its own configuration and definitions:

```bash
docker compose -f docker-compose/rabbitmq/docker-compose.yml up -d
./gradlew bootRun --args='--spring.profiles.active=dev'
```

Configure AI dependencies before calling the chat/ingestion endpoints. A core HTTP run still contains AI starter auto-configuration; the Qdrant flags below matter even when AI endpoints are unused.

| URL at `http://localhost:8080` | Behavior |
| --- | --- |
| `/test/ping` | Diagnostic endpoint permitted when `environments.production=false` |
| `/welcome` | Development welcome page |
| `/swagger-ui/index.html`, `/api-docs` | Available when Springdoc is enabled and development routes are permitted |
| `/actuator/health`, `/actuator/prometheus`, `/actuator/info` | Configured monitoring exposure; security chain permits `/actuator/**` |

`/` and `/ping` controller mappings exist but are not public smoke-test routes under the current deny-by-default security chain. A successful compile does not verify startup or service connectivity.

## Authentication and API contracts

`POST /api/auth/login` sets cookies and returns tokens; `/loginApi` returns tokens without setting them. Both accept an unwrapped `LoginRequest` body, a registered `Accept-ApiClient`, and `User-Agent`:

```http
POST /api/auth/loginApi
Content-Type: application/json
Accept-ApiClient: <registered-client-name>
User-Agent: local-api-client

{"emailOrUsername":"<existing-user>","password":"<password>","loginFrom":"WEB"}
```

Protected requests normally send `Authorization: Bearer <authenticationToken>` plus `Accept-ApiClient`. Cookie authentication uses `_sid` to select `_session_<userId>` and `_slid_<userId>`; these names are configurable. `Accept-Language` selects localization. `X-Sync-Active: 1` requests session activity updates.

`AuthServiceImpl` issues access JWTs and stores SHA-256 hashes of refresh/session tokens. Rotation revokes the old row and creates a new one; reuse detection can revoke all of a user's sessions. Access verification checks signed claims, token type and the session's revocation state. Current implementation gaps are listed below; do not treat this description as a security guarantee.

Use the real response types: `BaseApiController.responseEntity` passes its object and HTTP status through. It does **not** add a universal `ApiResponse` envelope. Many list endpoints use `ResponseListDto` (`dataList`, `totalPages`, `totalElements`, `last`); file listings also return plain arrays. Message helpers return `message`, `status`, and timestamp fields. `GlobalExceptionHandler` returns `ApiError`; the JWT filter has a separate `{ "error": "..." }` response. SSE endpoints use their own event contract.

Paging is zero-based (`page`, `size`, `sort=field,asc`); configured default/max sizes are 10/50. `ControllerUtil` parses `_q` using **semicolon-separated conditions**, with comma-separated values interpreted as `IN`:

```text
/api/permission?page=0&size=10&sort=code,asc&_q=id>=10;code:app_user
_q=id=1,2,3
_keyword=user
```

URL-encode query values in real requests. `_keyword` searches the controller-supplied keyword columns. That column list is **not** an allow-list for `_q` fields. Operators parsed from requests are `=`, `!=`, `:`, `>`, `>=`, `<`, `<=`; additional `SearchOperation` values can be constructed in Java.

## AI, ingestion and face recognition

For Ollama, configure `spring.ai.ollama.base-url`, chat model and embedding model. The bundled initialization service pulls `bge-m3` only; pull the chat model selected in your profile as well:

```bash
docker compose -f docker-compose/ollama/docker-compose.yml up -d
# Replace CHAT_MODEL with the model name configured in your profile.
docker exec rag-ollama ollama pull CHAT_MODEL
```

For Qdrant, define `QDRANT__SERVICE__API_KEY` in `docker-compose/qdrant/.env`, start that Compose service, and configure the matching Spring AI key/host. Port 6334 is gRPC, 6333 is HTTP/dashboard.

There are distinct configuration controls:

- `spring.ai.vectorstore.qdrant.enabled=true` registers the project's named `documentVectorStore` and `schemaVectorStore` beans. They hardcode `rag_documents`, `table_schemas`, `doc_content`, and `initializeSchema(true)`; changing the generic collection/schema properties does not override these builders.
- Spring AI 2.0.0's own Qdrant auto-configuration uses `spring.ai.vectorstore.type`, not the custom `enabled` switch. For a core run without Qdrant, set `spring.ai.vectorstore.type=none` **and** custom `spring.ai.vectorstore.qdrant.enabled=false`. This disables the store wiring; chat/ingestion still require implementation changes before they can operate without vectors.
- `app.rag.qdrant-enabled` is a stale YAML key: the current `RagProperties` has no corresponding field and no Java code consumes it.
- `app.rag.database-tools.enabled` controls the Java schema/query tools. These use the main application `JdbcTemplate`; the separate read-only MCP connection does not isolate them.

Document ingestion resolves a stored file, extracts Tika text, splits it, writes Qdrant vectors, then saves `AiDocumentMeta`. It attempts vector compensation when metadata save fails. `delete-source-after-ingest` defaults to true. Image/video extraction produces descriptive placeholders; OCR/transcription is not implemented. `chunk-overlap` and `max-num-chunks` are not applied by the current splitter.

`POST /api/aiChat/stream` emits `ChatStreamEvent` JSON through SSE: `chat_id`, optional `title`, `thinking` when supplied by the model, `token`, `sources`, and `done`; handled model errors produce `error`. `sources.content` is itself a JSON-encoded string. Chat messages are persisted by the service; `DatabaseChatMemory.add/clear` are no-ops, and reads use the configured history window.

Face endpoints live at the existing spelling `/api/faceRegconition`. The Java client calls the Python service's `/extract-face` endpoint, while PostgreSQL stores the 512-dimensional face vectors. Face registration is limited to the authenticated user's account and requires the selected file to belong to that user. See the [face service README](docker-compose/face-verification-service/README.md) for its model files and container setup.

## Source generation

`POST /dev/development/generateSrc` inspects Hibernate metadata for `@GenSourceableTable` and generates Java/frontend files according to annotation options and `app.front-end.theme` (Nuxt UI, Nuxt Quasar, Quasar). It can also insert permission records.

This is a mutating development tool: inspect `DevelopmentContoller`, `ConstantData` output paths and FreeMarker templates before invoking it. It loops over annotated entities and can affect more than one resource. It is not necessary for normal compilation; annotation-processor output is generated by Gradle separately.

## Build, test and deployment

```bash
./gradlew test
./gradlew test --tests '*AuthControllerTest*'
./gradlew bootJar
java -jar build/libs/api-service-1.0.0.jar --spring.profiles.active=dev
docker build -t spring-api-service:local .
```

Use explicit `bootRun --args` for profiles. `runDev`/`runProd` configure the shared `bootRun` task; `runBuild` only depends on `bootJar` and does not bake a production profile into the JAR. `runDebug` is an empty placeholder.

The standard Dockerfile builds with JDK 25 and runs as UID/GID 1001, using `prod` and external configuration at `/usr/spring-data/env/`. There is no checked-in `application-prod.yml`. The shared file still contains a MySQL datasource and defaults to `dev`; supply PostgreSQL settings and `environments.production=true` explicitly for deployment. Address R1 before placing configuration under that storage root.

Root Compose contains Windows bind mounts. Adapt mounts and ownership for the target machine. Give each concurrently running instance a unique `WORKER_ID` from 0–1023; Snowflake IDs depend on it. `DockerfileLocal` runs as root. Native-image and Kubernetes artifacts need validation before use. The `build-*.sh` helpers remove images/artifacts and prune build cache, so inspect them before execution.

GitHub Actions builds and pushes JVM/native images to Docker Hub using secrets. Docker builds skip tests, the workflows have no separate test gate, and the native workflow references the disabled Gradle task. These files are deployment examples, not evidence of passing CI.

### Verified test baseline

An earlier `./gradlew test --rerun-tasks` run on 2026-09-07 compiled application/test sources and ran **32 tests: 31 passed, 1 failed**. Those tests were in [AuthControllerTest](src/test/java/com/bekaku/api/spring/controller/api/AuthControllerTest.java), which directly calls a controller with Mockito dependencies; it does not exercise the real security filter chain or database.

The earlier failure was `SwitchAccount.fastPathSwitchesOnValidTargetRefreshCookie` at line 393: the fixture returns a session owned by user 1 while switching to user 2. The controller returns `400 BAD_REQUEST` for that mismatch; the test expects `200 OK`. The current full suite cannot compile because `AuthController.signup` is commented out while that test class still calls it. Focused regression suites have since verified all 16 R2 file-containment cases and all 5 R3 ownership cases. Test reports are generated at `build/reports/tests/test/index.html`.

## Review findings

These are source-level findings, not a penetration-test report. R2 and R3 have since been fixed as noted below; the other findings remain open. High priority means an access/data boundary needs attention before exposing the feature; medium priority means a functional or deployment limitation.

1. **R1 — High: public storage overlaps private data.** [WebConfigurerAdapter](src/main/java/com/bekaku/api/spring/configuration/WebConfigurerAdapter.java) serves the whole `app.cdn-path`, and [WebSecurityConfig](src/main/java/com/bekaku/api/spring/configuration/WebSecurityConfig.java) permits it anonymously. The Docker configuration puts external profiles under the same root; logging defaults also use it. Existing readable files under those directories can be served as CDN resources. Separate public assets from configuration/logs/private uploads and narrow the resource mapping.

2. **R2 — Fixed: image endpoint path containment.** [FileManagerController.getImage](src/main/java/com/bekaku/api/spring/controller/api/FileManagerController.java) now requires a relative path, checks both normalized and real-path containment, and serves only readable regular files using the checked target. Absolute paths, traversal and symlinks escaping the upload root are rejected; malformed inputs return 400 and missing files/directories return 404. [FileManagerControllerTest](src/test/java/com/bekaku/api/spring/controller/api/FileManagerControllerTest.java) covers these cases and valid nested/symlinked images: all 16 tests pass. This fixes filesystem escape; existing access policy for files inside the root is unchanged, and the public-storage issue in R1 remains open. The tests were run with a temporary Gradle init script selecting only this test source because concurrent authentication edits commented out `signup`, leaving `AuthControllerTest` unable to compile. The standard full suite is therefore not green.

3. **R3 — Fixed: chat and face-registration ownership.** [AiChatController](src/main/java/com/bekaku/api/spring/controller/api/AiChatController.java) now verifies the authenticated user owns a chat before listing its messages and binds newly created chats to that user. [AiRagChatServiceImpl.streamAnswer](src/main/java/com/bekaku/api/spring/serviceImpl/AiRagChatServiceImpl.java) uses the same creator-scoped lookup before resuming a conversation. [FaceRegconitionController](src/main/java/com/bekaku/api/spring/controller/api/FaceRegconitionController.java) passes the authenticated actor into [FaceRecognitionServiceImpl.registerhFace](src/main/java/com/bekaku/api/spring/serviceImpl/FaceRecognitionServiceImpl.java), which rejects a different requested user or a file not owned by that actor before reading or replacing face data. The five focused ownership tests pass. Cross-user chat lookups deliberately return not found to avoid revealing another user's chat; no administrative face-registration override was added.

4. **R4 — High: authentication route/filter mismatch and incomplete checks.** [JwtTokenFilter](src/main/java/com/bekaku/api/spring/configuration/JwtTokenFilter.java) skips all `/api/auth/**`, but several of those routes require an authenticated principal in `WebSecurityConfig`; normal JWT requests cannot establish one there. Signup is not public in the allow-list. [JwtServiceImpl.jwtVerify](src/main/java/com/bekaku/api/spring/serviceImpl/JwtServiceImpl.java) checks session revocation but does not verify current user activity or bind that session to the supplied API-client name; `/refreshTokenApi` omits the inactive/deleted-user check present in cookie refresh. Test these paths through the real filter chain.

5. **R5 — High: access JWT includes the raw refresh credential.** [AuthServiceImpl](src/main/java/com/bekaku/api/spring/serviceImpl/AuthServiceImpl.java) passes `getRawToken()` as the JWT subject. A signed JWT is readable, so exposure of an access token also exposes the credential used for refresh. Use a separate non-secret session identifier for revocation lookups; keep refresh credentials separate.

6. **R6 — High when enabled: AI SQL has broad database access.** [PostgreSQLQueryTool](src/main/java/com/bekaku/api/spring/ai/PostgreSQLQueryTool.java) uses the application datasource and unbounded `queryForList`. [DatabaseQueryValidator](src/main/java/com/bekaku/api/spring/ai/DatabaseQueryValidator.java) checks SQL text but provides no table/column allow-list or per-user row policy. Read-only transactions do not restrict confidential reads. Isolate credentials, enforce permitted data, and bound query time/results before enabling tools.

7. **R7 — Medium: Qdrant-disabled chat/ingestion fails.** Optional qualified stores are converted to null, but retrieval/ingestion dereferences them. The stale application flag does not guard these calls. Define a deliberate disabled response/fallback and reconcile custom versus starter auto-configuration.

8. **R8 — Medium: database portability and migration assumptions.** [AppUserMybatis.xml](src/main/resources/mybatis/AppUserMybatis.xml) uses `user_role` where the current schema defines `app_user_role`; its `findAll` and [PermissionMybatis.xml](src/main/resources/mybatis/PermissionMybatis.xml) use MySQL-style pagination. Migration V1 assumes a particular owner; V3 assumes pgvector; V2 destroys old `login_from` values. A PostgreSQL driver alone does not make every legacy SQL path compatible.

9. **R9 — Medium: incomplete generic operations and cross-store recovery.** Several service methods still return null, including generic Specification methods in `FilesDirectoryServiceImpl` and conversions in `CoreServiceImpl`. In ingestion, a same-bean call bypasses method-level transaction interception, and vector compensation cannot cover every later commit failure. Chunk merging deletes parts while copying, before full validation/persistence succeeds. Inspect the exact operation and its failure recovery instead of copying these paths as finished patterns.

10. **R10 — Medium: coverage and inactive delivery paths.** The current Mockito tests cover selected controller/service boundaries but cannot detect the filter-chain, SQL, pgvector, CDN, full SSE or external-service issues above. Native builds are currently disabled and container workflows skip tests. Add focused integration tests for those boundaries when fixing them.

## Related documentation

- [Agent engineering guide](SKILLS.md)
- [Authentication](docs/AUTH.md), [users](docs/USER.md), [roles](docs/ROLE.md), [permissions](docs/PERMISSION.md), [headers and generator notes](docs/OHTER.md)
- Existing `README-TEMP.md` and `SKILLS-TEMP.md` preserve earlier notes. Legacy docs contain old request wrappers and query syntax; controllers, DTOs, configuration and tests take precedence when they differ.
