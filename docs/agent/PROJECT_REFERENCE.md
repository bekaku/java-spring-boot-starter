# Project Reference

Use only when repository structure, dependencies, naming evidence, or architecture context is required. Rules live in `skills/backend/*.md`; this file is a map.

## Repository map and scope

- `build.gradle`, `settings.gradle`, `gradlew`, `gradle/wrapper/` — one Gradle application (`api-service`), not a multi-module build.
- `src/main/java/com/bekaku/api/spring/` — backend code; `src/main/resources/` — config, migrations, MyBatis XML, i18n, templates, prompts; `src/test/java/com/bekaku/api/spring/` — tests.
- `docker-compose/`, `docker-compose.yml`, `kubernetes/`, Dockerfiles, `build-*.sh|ps1` — dev/deploy infrastructure. Change only when the task covers it.
- `spring-data/` — seed SQL, example data, local storage. Not a Java module. Check the public storage mapping before writing files there.
- Agent docs: `AGENTS.md`, `SKILLS.md`, `.agents/skills/` (playbooks), `skills/backend/` (references), `docs/agent/` (this folder), `tasks/` (template), `docs/tasks/` (task instances).
- No `backend/` or `frontend/` directory; the frontend is an external repository (`AGENTS.md §1`).

## Stack (from `build.gradle`)

Java 25 toolchain · Spring Boot 4.1.0 · Spring Web MVC (Tomcat) · Spring Security + JJWT 0.13.0 · Validation + commons-validator · Spring Data JPA + PostgreSQL + Flyway + HikariCP · MyBatis Spring Boot Starter 4.0.1 · MapStruct 1.6.3 + Lombok (+ `lombok-mapstruct-binding`) · Spring AI 2.0.0 BOM (Ollama chat + embeddings, Qdrant, PDF/Tika readers, MCP client, `spring-ai-advisors-vector-store`) · AMQP · Mail · Cache + Ehcache 3.12.0 · POI · Thumbnailator · metadata-extractor · Tika 3.3.1 · TwelveMonkeys WebP · Guava · Firebase Admin · Jsoup · uuid-creator · Gson · Actuator + Micrometer Prometheus · Log4j2 (Logback excluded) · springdoc-openapi webmvc-ui · Test: `spring-boot-starter-test`, `spring-security-test`, REST Docs MockMvc.

App entry: `SpringApiApplication` — `@EnableCaching @EnableAsync @EnableScheduling @EnableJpaAuditing` (`@EnableRabbit` commented out).

## Backend directory structure

```text
src/main/java/com/bekaku/api/spring/
├── SpringApiApplication.java
├── controller/
│   ├── api/            REST controllers + BaseApiController (response/paging helpers)
│   ├── dev/            DevelopmentContoller — code generator (spelling intentional)
│   ├── socket/         websocket controllers (broker disabled)
│   ├── test/           test-only endpoints (non-prod)
│   └── web/            thymeleaf/web endpoints
├── service/            service interfaces (BaseService<T, DTO>)
├── serviceImpl/        transactional implementations (spelling intentional)
├── repository/         Spring Data repos (BaseRepository), PermissionRepositoryCustom
├── repositoryImpl/     BaseRepositoryImpl, PermissionRepositoryCustomImpl
├── mybatis/            read-model mappers: AppUser, AppRole, Permission, AccessToken, FileManager, FilesDirectory
├── model/              JPA entities
│   └── superclass/     Id, SoftDeletedId, Auditable*, SoftDeletedAuditable*, Created*, CodeNameSoftDeletedAuditable
├── dto/                request/response DTOs (DtoId, ResponseListDto, AppUserDto, ChatStreamEvent, ...)
├── mapper/             MapStruct *Mapper interfaces
├── validator/          BaseValidator, RoleValidator, UserValidator, PermissionRequireValidator
├── configuration/      WebSecurityConfig, SecurityEnablerConfig, JwtTokenFilter, AsyncConfig, CacheConfig,
│                       AuditAwareImpl, AuditListener, WebConfigurerAdapter, LocaleResolverHeader, I18n,
│                       Snowflake*, CustomPermissionEvaluator (stub), Kafka*/Undertow/WebSocket (disabled)
├── exception/          GlobalExceptionHandler (active), CustomRestExceptionHandler (inactive),
│                       ApiException, ApiError, BaseResponseException, ChatStreamException
├── specification/      SearchSpecification, SearchCriteria, SearchOperation, DynamicFilterSpec, ...
├── properties/         typed @ConfigurationProperties (AppProperties `app`, RagProperties `app.rag`,
│                       JwtProperties, CookieProperties, AppCronProperties, PasswordResetProperties, ...)
├── util/               ControllerUtil, CookieUtil, FileUtil, AuthUtil, PermissionChecker, ConstantData,
│                       UrlUtil, HashUtil, DateUtil, SnowflakeIdHolder
├── ai/                 QdrantVectorStoreConfig, DatabaseChatMemory, AiChatToolContext, *Tool,
│                       DatabaseQueryValidator, AiFaceRegconitionServiceClient (spelling intentional)
├── extraction/         DocumentExtractor(+Factory), TikaDocumentExtractor, MediaPlaceholderDocumentExtractor
├── queue/              QueueConfig (exchange + 6 non-durable queues), QueueSender, dto/
├── scheduler/          CronScheduler (jobs commented out)
├── middleware/         AuthorizationInterceptor (returns true — not a boundary)
├── annotation/         GenSourceableTable, PermissionRequire, GeneratedUuidV7
├── enumtype/           enums
├── vo/                 Paging (MyBatis), other value objects
└── logger/

src/main/resources/
├── application.yml                 tracked base config (environments.production: true, Flyway disabled)
├── application-dev-example.yml     tracked dev template (production: false, ddl-auto: update)
├── application-dev.yml             local, git-ignored
├── application-localdocker.yml     host.docker.internal PostgreSQL override
├── db/migration/                   V1__init_current_schema.sql … V6__forgot_password_reset_hardening.sql
├── mybatis/                        AppUser, Permission, AccessToken, FileManager, FilesDirectory *.xml (no AppRole XML)
├── i18n/                           messages, error/, model/, permission/ — each EN + _th
├── prompts/                        system-rag.txt, system-rag-db-tools.txt, system-rag-generate-title.txt
├── templates/                      spring-{controller,service,service-impl,repository,mapper,dto}.ftl, mail templates
├── log4j2-prod.xml, log4j2-dev-example.xml (tracked); log4j2-dev.xml (local)
└── acl.json                        frontend menu → permission map

src/test/java/com/bekaku/api/spring/
├── controller/api/   AuthControllerTest, FileManagerControllerTest, AiChatControllerOwnershipTest
└── serviceImpl/      AiRagChatServiceOwnershipTest, FaceRecognitionServiceOwnershipTest
```

## Naming evidence

- Intentional legacy spellings: `serviceImpl`, `DevelopmentContoller`, `/api/faceRegconition`, `AiFaceRegconitionServiceClient`.
- DI: constructor injection via `@RequiredArgsConstructor` on controllers/services. Field `@Autowired` remains in `JwtTokenFilter`, `BaseApiController`, `BaseResponseException`, `QueueSender`.
- Logging: `@Slf4j` (Log4j2). Some legacy classes use `LoggerFactory.getLogger(...)` (`PermissionRequireValidator`, `AuthorizationInterceptor`, `EmailServiceImpl`).
- Entities: `@Table(comment = ..., indexes = @Index)`, `@Column(comment = ...)`, `@JoinColumn(comment = "FK -> Ref table: x (id)...")`, `public static Sort getSort()` (`model/AppUser.java`, `model/AppRole.java`).
- DTOs: Lombok `@Getter @Setter` (some `@Data`) + Jakarta constraints with i18n keys (`dto/AppRoleDto.java`, `dto/LoginRequest.java`).
- Mappers: `mapper/*Mapper.java`, `@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)`; ignores via `@Mapping(target = ..., ignore = true)` (`FileManagerMapper`).
- MyBatis: `mybatis/AppUserMybatis.java` ↔ `resources/mybatis/AppUserMybatis.xml` (`namespace = com.bekaku.api.spring.mybatis.AppUserMybatis`).
- Config: typed records under `properties/` with `@ConfigurationPropertiesScan`; legacy `@Value` remains (e.g. `EncryptServiceImpl` `app.encrypt-key`, `AppRoleController`).

## Architecture notes

1. Layering: `Controller → Service interface → ServiceImpl → Repository/MyBatis`; MapStruct at the transport boundary. The `ai/*Tool.java` classes are the only non-service DB/vector callers.
2. Transactions: services default to `@Transactional(readOnly = true)`; write methods declare `@Transactional`. Private methods and self-calls do not start proxy transactions.
3. DB transactions do not roll back filesystem, Qdrant, email, or remote effects (ingestion, chunk merge, signup need compensation order).
4. Search: JPA path = `ControllerUtil.buildSpecification` + `SearchSpecification` + `getPageable(pageable, Entity.getSort())`; MyBatis path = `getPaging(pageable, allowList)` + `LIMIT/OFFSET` in XML. Syntax in `skills/backend/API.md`.
5. Async: `@Async(ConstantData.ASYNC_TASK_NAME)` (`"asyncExecutor"`, `AsyncConfig`). RabbitMQ topology declared in `QueueConfig`, sender in `QueueSender`, no active `@RabbitListener`. Details in `skills/backend/ASYNC_MESSAGING.md`.
