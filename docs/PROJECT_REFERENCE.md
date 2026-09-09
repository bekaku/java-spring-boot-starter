# Project Reference

Use only when repository structure, dependencies, naming evidence, or architecture context is required.

**## 1. Backend Overview**

\- Single Gradle project \`api-service\`, base package \`com.bekaku.api.spring\` (\`build.gradle:8\`, \`src/main/java/com/bekaku/api/spring/SpringApiApplication.java\`).

\- Stack (verified in \`build.gradle:47-149\`): Java 25 toolchain, Spring Boot 4.1.0, Servlet Spring Web MVC (Tomcat), Spring Security + JJWT 0.13.0, Validation + commons-validator, Spring Data JPA + PostgreSQL + Flyway + HikariCP, MyBatis Spring Boot Starter 4.0.1, MapStruct 1.6.3 + Lombok (+ \`lombok-mapstruct-binding\`), Spring AI 2.0.0 BOM (Ollama chat+embeddings, Qdrant vector store, PDF reader, Tika reader, MCP client, \`spring-ai-advisors-vector-store:2.0.0-M8\`), AMQP + Mail, Cache + Ehcache 3.12.0, POI, Thumbnailator, metadata-extractor, Tika core/parsers-standard 3.3.1, TwelveMonkeys WebP, Guava, Firebase Admin, Jsoup, uuid-creator, Gson, Actuator + Micrometer Prometheus, Log4j2, springdoc-openapi webmvc-ui, JUnit Jupiter + REST Docs MockMvc + spring-security-test.

\- Architecture style: layered servlet MVC — \`controller/api/\*\` → \`service/\*\` interface → \`serviceImpl/\*\` → \`repository/\*\` (JPA) and/or \`mybatis/\*\` + \`src/main/resources/mybatis/\*.xml\`. DTO mapping at transport boundary via \`mapper/\*\` (MapStruct). No WebFlux server; streaming chat is MVC SSE returning Reactor \`Flux\` (\`src/main/java/com/bekaku/api/spring/controller/api/AiChatController.java:53-56\`).

\- App entry: \`src/main/java/com/bekaku/api/spring/SpringApiApplication.java\` carries \`@EnableCaching @EnableAsync @EnableScheduling\` (Rabbit \`@EnableRabbit\` commented out).

**## 2. Directory Structure**

Annotated tree (relative to \`./\`):

\`\`\`

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

  model/ — \~30 JPA entities; model/superclass/ — 11 base classes (Id, SoftDeletedId, Auditable\*, SoftDeletedAuditable\*,

    Created\*, CodeNameSoftDeletedAuditable)

  dto/ — request/response DTOs (LoginRequest, AppRoleDto, ChatRequest, ChatStreamEvent, ResponseListDto, AppUserDto, ...)

  mapper/ — 15 MapStruct interfaces (\*Mapper.java)

  ai/ — QdrantVectorStoreConfig, DatabaseChatMemory, AiChatToolContext, DatabaseSchemaTool, PostgreSQLQueryTool,

    DatabaseQueryValidator, UserActivityTool, AiFaceRegconitionServiceClient (spelling intentional)

  extraction/ — DocumentExtractor, DocumentExtractorFactory, TikaDocumentExtractor, MediaPlaceholderDocumentExtractor

  configuration/ — WebSecurityConfig, SecurityEnablerConfig, JwtTokenFilter, CustomPermissionEvaluator (stub),

    AsyncConfig, CacheConfig, AuditAwareImpl, AuditListener, WebConfigurerAdapter, I18n

  exception/ — GlobalExceptionHandler (active), CustomRestExceptionHandler (inactive), ApiException, ApiError,

    BaseResponseException, ChatStreamException

  specification/ — BaseSpecification, SearchSpecification, DynamicFilterSpec, Filter, SearchCriteria, SearchOperation

  properties/ — typed @ConfigurationProperties records (AppProperties prefix \`app\`, RagProperties prefix \`app.rag\`, JwtProperties,

    CookieProperties, MailProperties, QueueConfig, UploadImageConfig, AppCorsProperties, AppCronProperties, ...)

  util/ — ControllerUtil, CookieUtil, FileUtil, AuthUtil, PermissionChecker (via util/), ConstantData, UrlUtil, HashUtil, DateUtil

  validator/ — BaseValidator, RoleValidator, UserValidator, PermissionRequireValidator

  middleware/AuthorizationInterceptor.java — returns true; NOT an authorization boundary (see §9)

  queue/ — QueueConfig (TopicExchange + 6 non-durable queues + RabbitTemplate), QueueSender (no active @RabbitListener)

  scheduler/, vo/Paging.java, enumtype/, annotation/GenSourceableTable.java, logger/

src/main/resources/

  application.yml — prod base (profiles.active: dev, hikari, mail, rabbitmq, app.\*)

  application-dev.yml — local PG localhost:5432, ddl-auto\:update, ollama/mcp/qdrant, cdn-directory absolute path

  application-localdocker.yml — host.docker.internal PG override only

  application-dev-example.yml — sanitized template; encrypted.yml — ENC(...) placeholder

  db/migration/V1\_\_init\_current\_schema.sql … V4\_\_create\_unanswered\_prompt\_log\_table.sql

  mybatis/\*.xml — AppUserMybatis.xml, PermissionMybatis.xml, AccessTokenMybatis.xml, FileManagerMybatis.xml,

    FilesDirectoryMybatis.xml (no AppRoleMybatis.xml)

  log4j2-dev.xml, log4j2-prod.xml, log4j2-dev-example.xml

  prompts/system-rag.txt, system-rag-db-tools.txt, system-rag-generate-title.txt

  templates/spring-{controller,service,service-impl,repository,mapper,dto}.ftl + mail templates + error.html

src/test/java/com/bekaku/api/spring/

  controller/api/AuthControllerTest.java, FileManagerControllerTest.java, AiChatControllerOwnershipTest.java

  serviceImpl/AiRagChatServiceOwnershipTest.java, FaceRecognitionServiceOwnershipTest.java

\`\`\`

**## 3. Coding Conventions**

\- **\*\*Packages/naming:\*\*** \`serviceImpl\` (capital I), \`DevelopmentContoller\` (one \`r\`), route \`/api/faceRegconition\` (missing \`o\`), \`AiFaceRegconitionServiceClient\` — all intentional legacy spellings. Do not rename in unrelated work (evidence: \`src/main/java/com/bekaku/api/spring/serviceImpl/\`, \`src/main/java/com/bekaku/api/spring/controller/dev/DevelopmentContoller.java\`, \`src/main/java/com/bekaku/api/spring/ai/AiFaceRegconitionServiceClient.java\`).

\- **\*\*DI:\*\*** constructor injection with Lombok \`@RequiredArgsConstructor\` on controllers/services; \`JwtTokenFilter\` uses field \`@Autowired\` (\`src/main/java/com/bekaku/api/spring/configuration/JwtTokenFilter.java:36-40\`). \`BaseApiController\` uses field \`@Autowired HttpServletRequest + I18n\` (\`src/main/java/com/bekaku/api/spring/controller/api/BaseApiController.java:28-32\`).

\- **\*\*Logging:\*\*** Log4j2 only. \`@Slf4j\` everywhere; some classes use \`LoggerFactory.getLogger(...)\` (\`src/main/java/com/bekaku/api/spring/validator/\*\`, \`src/main/java/com/bekaku/api/spring/middleware/AuthorizationInterceptor.java\`, \`src/main/java/com/bekaku/api/spring/serviceImpl/EmailServiceImpl.java\`, \`src/main/java/com/bekaku/api/spring/controller/test/TestController.java:48\` uses \`login-log\` logger). Config via \`logging.config: classpath\:log4j2-prod.xml\` (\`src/main/resources/application.yml:241-243\`) and \`log4j2-dev.xml\` in dev. Logback is excluded in \`build.gradle:28-30\`; never import \`ch.qos.logback\`.

\- **\*\*Entities:\*\*** \`@Table(comment=..., indexes=@Index)\`, lazy relations, \`@JoinColumn(name=..., comment="FK -> Ref table: X (id)...")\`, \`public static Sort getSort()\` default-sort helper. Example \`src/main/java/com/bekaku/api/spring/model/AppUser.java\`:

  \`\`\`java

  @SQLDelete(sql="UPDATE app\_user SET deleted=true WHERE id=?")

  @SQLRestriction("deleted=false")

  public class AppUser extends *SoftDeletedAuditable*\<Long> {

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="avatar\_file\_id") private FileManager avatarFile;

    @ManyToMany(fetch = FetchType.LAZY) @JoinTable(name="app\_user\_role", ...) private Set\<AppRole> appRoles;

  }

  \`\`\`

\- **\*\*DTOs:\*\*** Lombok \`@Data/@Getter/@Setter\` + Jakarta constraints with i18n keys. Example \`src/main/java/com/bekaku/api/spring/dto/AppRoleDto.java:20\`: \`@Size(min=3,max=100,message="{error.Size3Limit100}")\`; \`src/main/java/com/bekaku/api/spring/dto/LoginRequest.java:23\`: \`@NotBlank(message="{error.validateRequire}")\`.

\- **\*\*Mappers:\*\*** \`src/main/java/com/bekaku/api/spring/mapper/\*Mapper.java\`, always \`@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.IGNORE)\`. Simple (\`src/main/java/com/bekaku/api/spring/mapper/AppUserMapper.java\`): \`AppUserDto toDto(AppUser e); AppUser toEntity(AppUserDto d);\`. With ignores (\`src/main/java/com/bekaku/api/spring/mapper/FileManagerMapper.java\`): \`@Mapping(target="fileMime", ignore=true)\`.

\- **\*\*MyBatis:\*\*** interface method ↔ XML \`namespace + statement id\` must match; \`@Param\` names must match \`#{...}\` bindings. Example \`src/main/java/com/bekaku/api/spring/mybatis/AppUserMybatis.java\`: \`List\<AppUserDto> findAll(@Param("page") Paging page);\` ↔ \`src/main/resources/mybatis/AppUserMybatis.xml: namespace=com.bekaku.api.spring.mybatis.AppUserMybatis\`.

\- **\*\*Config:\*\*** new settings go in typed records under \`src/main/java/com/bekaku/api/spring/properties/\` (e.g. \`AppProperties.java\`, \`RagProperties.java\`) with \`@ConfigurationPropertiesScan\`, never \`@Value\` sprawl for new groups (legacy \`EncryptServiceImpl.java:33 @Value(${app.encrypt-key})\` is exception).

**## 4. Architecture Rules**

1\. Layering is \`Controller → Service interface → ServiceImpl → Repository/MyBatis\`; MapStruct at transport boundary. Controllers call services, never \`EntityManager\`/\`JdbcTemplate\`/\`VectorStore\` directly. AI tools (\`src/main/java/com/bekaku/api/spring/ai/\*Tool.java\`) are the only non-service DB/vector callers and share app \`JdbcTemplate\`.

2\. Transaction boundaries: services own transactions. Many service classes default \`@Transactional(readOnly=true)\`; write entry points must declare \`@Transactional\`. Private methods and same-bean self-calls do not create a new proxy transaction.

3\. DB transactions do not roll back filesystem, Qdrant, email, or remote-service effects. Multi-step writes (ingestion, file merge, signup) must define compensation order (see §8 RAG lifecycle).

4\. Dependency direction: \`controller → service → repository/mapper\`; \`specification/\*\`, \`util/ControllerUtil\`, \`vo/Paging\` are shared helpers. Do not call controllers from services; do not put business writes in controllers.

5\. **\*\*JPA-vs-MyBatis decision rule (binding):\*\*** JPA for CRUD + \`JpaSpecificationExecutor\` filtering + simple \`@Query\`/derived lookups (\`src/main/java/com/bekaku/api/spring/repository/AppUserRepository.java\`). MyBatis + \`vo/Paging\` for join/paging read-model projections returning DTOs (\`src/main/java/com/bekaku/api/spring/mybatis/\*.java\`, \`src/main/resources/mybatis/\*.xml\`). Do not add new MyBatis writes; do not use MyBatis for single-table CRUD that JPA already covers.

6\. Search: JPA path uses \`ControllerUtil.getSearchCriteriaList\` + \`SearchSpecification\` + \`getPageable(pageable, Entity.getSort())\` (\`src/main/java/com/bekaku/api/spring/util/ControllerUtil.java:23-61\`, \`src/main/java/com/bekaku/api/spring/controller/api/BaseApiController.java:116-123\`). MyBatis path uses \`getPaging(pageable, acceptSortField)\` allow-list (\`BaseApiController.java:140-155\`) and \`LIMIT/OFFSET\` in XML. \`\_q\` syntax uses \`;\` separators, comma = \`IN\`, \`\_keyword\` searches controller-specified columns only.

7\. Async/messaging: \`@Async("asyncTaskExecutor")\` pool is defined in \`src/main/java/com/bekaku/api/spring/configuration/AsyncConfig.java:13-21\` (platform-thread pool despite virtual-thread setting). Only demo usage exists (\`AppUserServiceImpl.java:207 processAsyncTask\`). RabbitMQ topology is declared (\`src/main/java/com/bekaku/api/spring/queue/QueueConfig.java:20-28\`, \`QueueSender.java:34-66\`, \`application.yml:153-169\`) but there is **\*\*no active \`@RabbitListener\`\*\*** — retry/concurrency YAML alone implements nothing. Kafka, WebSocket broker, Undertow configs are disabled/commented. If adding consumers, define idempotency + duplicate-delivery tests.
