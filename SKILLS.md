# 🛠️ Project Skills & Technology Stack

This document outlines the core technologies, architectural patterns, and coding conventions used in this Java Spring Boot boilerplate.
**Note for AI Assistants (Copilot, Claude, Codex):** Please strictly adhere to these technologies, design patterns, and versions when generating code, creating new modules, or refactoring.

## 1. Core Technologies
* **Framework:** Spring Boot 4.1.0
* **Language:** Java 25 (Gradle toolchain)
* **Build Tool:** Gradle (Manage dependencies through the build.gradle file and use the ./gradlew command to run various tasks.)
* **API Documentation:** SpringDoc OpenAPI 3.0.3 (Swagger UI)
* **Logging:** Log4j2 (excluded Logback and SLF4J)
* **AI:** Spring AI 2.x (Ollama model, Qdrant vector store, PDF/Tika document readers)

## 2. Architecture & Patterns
* **Architecture:** Layered Architecture (Controller -> Service -> Repository)
* **Data Transfer Pattern:** Strict use of DTOs (Data Transfer Objects) for requests and responses. Entities must NEVER be exposed directly to the Controller layer.
* **Dependency Injection:** Constructor Injection preferred (utilizing Lombok's `@RequiredArgsConstructor`).
* **Database Access:** Spring Data JPA / Hibernate + MyBatis 4.0.1
* **Security:** Spring Security with JWT authentication via `JwtTokenFilter` (cookie or `Authorization` header). Sessions are tracked in the `access_token` table (refresh tokens stored SHA-256 hashed), so every request is revocation-checked; refresh-token rotation revokes the old row and detects token reuse by revoking all of a user's sessions.
* **Async Processing:** `@EnableAsync` for asynchronous task execution
* **Scheduling:** `@EnableScheduling` for scheduled tasks (cron expressions from `app.cron.*` properties, e.g. old-file and old-temp-chunk cleanup in `FileManagerServiceImpl`)
* **Caching:** Spring Cache with Ehcache 3.12.0
* **Auditing:** JPA Auditing for tracking entity creation/modification metadata
* **Configuration Properties:** Immutable Java `record`s annotated with `@ConfigurationProperties` (e.g. `AppProperties`, `JwtProperties`, `AppDefaultsProperties`)

## 3. 🤖 AI Code Generation Guidelines
When assisting with code generation in this project, AI agents must follow these rules:
1. **Lombok Usage:** Use Lombok annotations (`@Data`, `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Slf4j`) to reduce boilerplate code.
2. **Standardized Responses:** All REST API endpoints must return a unified wrapper object (e.g., `ApiResponse<T>`) rather than raw data. Use the `BaseApiController` response helpers (`responseEntity`, `responseServerMessage`) — they honor the `HttpStatus` passed in.
3. **Exception Handling:** Do not use plain `try-catch` blocks in Controllers for business logic errors. Throw `ApiException` (built via the `BaseResponseException` helpers: `responseErrorNotfound()`, `responseErrorForbidden()`, `responseErrorBadRequest()`, `throwError(...)`) and let the global `ExceptionResolver` (`@RestControllerAdvice`, `exception/ExceptionResolver.java`) handle the HTTP response. Error bodies are `ApiError` objects; the 500 catch-all logs the stack server-side and returns a generic message — never leak exception details to clients.
4. **Validation:** Use `jakarta.validation.constraints` (e.g., `@NotNull`, `@NotBlank`, `@Email`) on Request DTOs, and ensure `@Valid` is used in the Controller.
5. **Transactions:** Apply `@Transactional` at the Service layer for methods that modify the database. For methods that only perform select/read queries, strictly use `@Transactional(readOnly = true)` to optimize performance. Write paths in a service with class-level `readOnly = true` MUST have their own method-level `@Transactional`.
6. **Security:**
   - Never log raw tokens, passwords, or secrets (log presence/booleans instead).
   - Any user-supplied file path/name must be validated before use in `Path.resolve()` / `File` construction (see `FileManagerController.isValidFileName` / `isFileAccessAllowed` and `util/UrlUtil.validatePublicUrl` for outbound URLs).
   - Endpoints that touch per-user data must enforce ownership (`appUserService.requireTheSameUser(...)`).
   - Public signup must never accept client-selected roles — assign the configured default role only (`app.defaults.role`).
   - Use `SecureRandom` (via `AppUtil.generateRandomNumber`) for OTPs/codes; apply rate limiting to sensitive public endpoints.
7. **Tests:** New controller/service behavior should be covered by JUnit 5 + Mockito unit tests (see `src/test/java/com/bekaku/api/spring/controller/api/AuthControllerTest.java`). Run them with `./gradlew test --tests "*ClassName*"`.

*Standard Controller Example Pattern:*
```java
@Slf4j
@RequestMapping(path = "/api/resource")
@RestController
@RequiredArgsConstructor
public class ResourceController extends BaseApiController {

    private final ResourceService resourceService;
    private final I18n i18n;

    @PreAuthorize("@permissionChecker.hasPermission('resource_list')")
    @GetMapping
    public ResponseEntity<ResponseListDto<ResourceDto>> findAll(HttpServletRequest request, Pageable pageable) {
        SearchSpecification<Resource> specification = ControllerUtil.buildSpecification(request, List.of());
        return this.responseEntity(resourceService.findAllWithSearch(specification, getPageable(pageable, Resource.getSort())), HttpStatus.OK);
    }

    @PreAuthorize("@permissionChecker.hasPermission('resource_view')")
    @GetMapping("/{id}")
    public ResponseEntity<ResourceDto> findOne(@PathVariable("id") Long id) {
        Optional<Resource> resource = resourceService.findById(id);
        if (resource.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return this.responseEntity(resourceService.convertEntityToDto(resource.get()), HttpStatus.OK);
    }

    @PreAuthorize("@permissionChecker.hasPermission('resource_add')")
    @PostMapping
    public ResponseEntity<ResourceDto> create(@Valid @RequestBody ResourceDto dto) {
        return this.responseEntity(createProcess(dto), HttpStatus.CREATED);
    }

    private ResourceDto createProcess(ResourceDto dto) {
        Resource resource = resourceService.convertDtoToEntity(dto);
        resourceService.save(resource);
        return resourceService.convertEntityToDto(resource);
    }

    @PreAuthorize("@permissionChecker.hasPermission('resource_edit')")
    @PutMapping("/{id}")
    public ResponseEntity<ResourceDto> update(@Valid @RequestBody ResourceDto dto, @PathVariable("id") Long id) {
        Optional<Resource> resource = resourceService.findById(id);
        if (resource.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return this.responseEntity(updateProcess(resource.get(), dto), HttpStatus.OK);
    }

    private ResourceDto updateProcess(Resource resource, ResourceDto dto) {
        resource.update(dto.getName(), dto.isActive());
        resourceService.update(resource);
        return resourceService.convertEntityToDto(resource);
    }

    @PreAuthorize("@permissionChecker.hasPermission('resource_delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        Optional<Resource> resource = resourceService.findById(id);
        if (resource.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        resourceService.delete(resource.get());
        return this.responseDeleteMessage();
    }
}
```

*Standard Entity Example Pattern:*
```java
@GenSourceableTable
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "resource",
        indexes = {
                @Index(columnList = "deleted"),
                @Index(columnList = "updated_user"),
                @Index(columnList = "created_user"),
        }
)
@SQLDelete(sql = "UPDATE resource SET deleted = true WHERE id=?")
@SQLRestriction("deleted=false")
@EntityListeners(AuditListener.class)
public class Resource extends SoftDeletedAuditable<Long> {

    public Resource(String name, Boolean active) {
        this.name = name;
        this.active = active;
    }

    public void update(String name, Boolean active) {
        this.name = name;
        this.active = active;
    }

    @Column(name = "name", length = 125, nullable = false)
    private String name;

    private Boolean active = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "resource_related",
            joinColumns = {@JoinColumn(name = "resource")},
            inverseJoinColumns = {@JoinColumn(name = "related_id")})
    private Set<RelatedEntity> relatedEntities = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ParentEntity parent;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Resource that = (Resource) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "name");
    }
}
```

**ID Generation Explanation:**
- Entities extend `SoftDeletedAuditable<Long>` which extends `SoftDeletedId` which extends `Id`
- The `Id` superclass contains a `@PrePersist` method that automatically generates Snowflake IDs
- Snowflake ID generation is handled by `SnowflakeIdHolder.generator().nextId()` before entity persistence
- No manual ID assignment is required - the ID is automatically generated when the entity is first saved
- Snowflake IDs are distributed unique IDs that are sortable by time and avoid collisions in distributed systems

*Standard Service Example Pattern:*

**Service Interface:**
```java
public interface ResourceService extends BaseService<Resource, ResourceDto> {

    Optional<Resource> findByName(String name);

    List<Resource> findAllByActiveTrue();

    Optional<Resource> findByCode(String code);
}
```

**Service Implementation:**
```java
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    @Override
    public ResponseListDto<ResourceDto> findAllWithPaging(Pageable pageable) {
        Page<Resource> result = resourceRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Override
    public ResponseListDto<ResourceDto> findAllWithSearch(SearchSpecification<Resource> specification, Pageable pageable) {
        Page<Resource> result = resourceRepository.findAll(specification, pageable);
        return getListFromResult(result);
    }

    @Override
    public ResponseListDto<ResourceDto> findAllBy(Specification<Resource> specification, Pageable pageable) {
        return getListFromResult(findAllPageSpecificationBy(specification, pageable));
    }

    @Override
    public Page<Resource> findAllPageSpecificationBy(Specification<Resource> specification, Pageable pageable) {
        return resourceRepository.findAll(specification, pageable);
    }

    @Override
    public Page<Resource> findAllPageSearchSpecificationBy(SearchSpecification<Resource> specification, Pageable pageable) {
        return resourceRepository.findAll(specification, pageable);
    }

    private ResponseListDto<ResourceDto> getListFromResult(Page<Resource> result) {
        return new ResponseListDto<>(result.getContent()
                .stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList())
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Override
    public List<Resource> findAll() {
        return resourceRepository.findAll();
    }

    @Transactional
    @Override
    public Resource save(Resource resource) {
        return resourceRepository.save(resource);
    }
    
    @Transactional
    @Override
    public Resource update(Resource resource) {
        return resourceRepository.save(resource);
    }

    @Override
    public Optional<Resource> findById(Long id) {
        return resourceRepository.findById(id);
    }

    @Transactional
    @Override
    public void delete(Resource resource) {
        resourceRepository.delete(resource);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        resourceRepository.deleteById(id);
    }

    @Override
    public ResourceDto convertEntityToDto(Resource resource) {
        return resourceMapper.toDto(resource);
    }

    @Override
    public Resource convertDtoToEntity(ResourceDto resourceDto) {
        return resourceMapper.toEntity(resourceDto);
    }

    @Override
    public Optional<Resource> findByName(String name) {
        return resourceRepository.findByName(name);
    }

    @Override
    public List<Resource> findAllByActiveTrue() {
        return resourceRepository.findAllByActiveTrue();
    }

    @Override
    public Optional<Resource> findByCode(String code) {
        return resourceRepository.findByCode(code);
    }
}
```

## 4. Database & Infrastructure
- **Database:** PostgreSQL (primary; Flyway migrations via `spring-boot-starter-flyway` + `flyway-database-postgresql`)
- **Connection Pool:** HikariCP 7.1.0
- **ORM:** Spring Data JPA with Hibernate + MyBatis 4.0.1 for complex queries
- **Caching:** Ehcache 3.12.0 (Spring Cache abstraction)
- **Message Queues:**
  - RabbitMQ (Spring AMQP, `queue/QueueSender` + `QueueConfig`)
  - Kafka (`KafkaConsumerConfig`/`KafkaProducerConfig`; starter dependency currently commented out in build.gradle — enable when needed)
- **In-memory Data:** Redis (provided via docker-compose for caching/sessions)
- **Containerization:** Docker & Docker Compose — `docker-compose/` has per-service files (postgres, mysql, redis, rabbitmq, kafka, qdrant, grafana-prometheus, face-verification-service) plus a root `docker-compose.yml`
- **Monitoring:** Spring Boot Actuator with Prometheus metrics
- **Email:** Spring Boot Mail (SMTP support)

## 5. Key Libraries & Frameworks
- **Object Mapping:** MapStruct 1.6.3 (DTO-Entity mapping)
- **JSON Processing:** Gson 2.14.0
- **JWT:** jjwt 0.13.0 (api/impl/jackson)
- **Validation:** Jakarta Validation (spring-boot-starter-validation)
- **Commons:** Apache Commons Validator 1.10.1 (email validation), Commons Lang3
- **Image Processing:**
  - Thumbnailator 0.4.21 (thumbnail generation)
  - metadata-extractor 2.20.0 (EXIF/IPTC metadata)
  - Apache Tika 3.3.1 (file type detection)
  - TwelveMonkeys ImageIO 3.13.1 (WebP support)
- **Report Generation:** Apache POI 5.5.1 (Excel/Office documents)
- **ID Generation:**
  - UUID Creator 6.1.1 (including UUID v7)
  - Custom Snowflake ID generator (`SnowflakeIdGenerator`/`SnowflakeIdHolder`)
- **Web Scraping:** Jsoup 1.22.2 (always fetch through `util/UrlUtil.validatePublicUrl` to prevent SSRF)
- **AI / RAG:**
  - `org.springframework.ai:spring-ai-starter-model-ollama` (chat model)
  - `spring-ai-starter-vector-store-qdrant` (vector store)
  - `spring-ai-pdf-document-reader`, `spring-ai-tika-document-reader`, `spring-ai-advisors-vector-store`
- **Google Services:**
  - Guava 33.6.0-jre (includes `RateLimiter` used by streaming endpoints)
  - Firebase Admin 9.9.0 (FCM push notifications)
- **Testing:** spring-boot-starter-test (JUnit 5, Mockito, AssertJ), spring-security-test

## 6. Key Features & Capabilities
- **Authentication & Authorization:**
  - JWT access + refresh tokens issued by `JwtServiceImpl` (jjwt 0.13.0); `JwtTokenFilter` authenticates via cookie or `Authorization` header
  - Session store in `access_token` table: refresh tokens stored SHA-256 hashed (`HashUtil`), revocation checked on every request
  - Refresh-token rotation (`AuthServiceImpl.refreshToken`) revokes the old row and issues a new session row; `handleRefreshTokenReuse` revokes all of a user's sessions when a revoked token is replayed
  - Account linking/switching (`IdentityLinkService`) always validates user↔target linkage
  - Custom permission system with `@PermissionRequire` annotation and `@PreAuthorize("@permissionChecker.hasPermission('...')")`
  - Role-based access control (RBAC) with permission codes like `<table>_list|view|add|edit|delete`
  - IP-based API client restrictions (`ApiClientIp`)
  - Password hashing with BCrypt (`EncryptService.encrypt/check`); AES-GCM encryption helpers (`EncryptService.encryptData/decryptData`) with per-message random IV
- **File Management:**
  - Multi-directory file storage under `app.upload-path` (year/month folders per MIME type)
  - Chunked upload (`uploadChunkApi`/`mergeChunkApi`) with strict filename allowlist, chunk-number bounds, and merged-content MIME validation
  - Authenticated file/video streaming with Range support and Guava `RateLimiter` throttling; path-traversal guards (canonical-path containment)
  - Image processing, resizing and thumbnail generation (Thumbnailator)
  - MIME type detection and allowlist validation (Apache Tika)
  - File metadata extraction (metadata-extractor)
  - Scheduled cleanup of old files and stale `temp-chunks/` parts (cron `app.cron.clean-file-expression`, gated by `app.cron.clean-old-file` / `app.cron.clean-old-temp-chunks`)
- **AI / RAG Module (`ai/`, Spring AI):**
  - Ollama chat model with streaming (`AiChatController /stream`)
  - Qdrant vector store for document ingestion & retrieval (`AiDocumentIngestionService`, `AiRagChatService`)
  - Database chat memory (`DatabaseChatMemory`), DB schema/SQL tools, user activity tool, face-recognition client
- **Document Extraction (`extraction/`):** Tika-based `DocumentExtractor` factory for PDF/media text extraction
- **WebSocket Support:** Real-time communication with user interceptors
- **Internationalization (i18n):** Header-based locale resolution via `I18n`
- **Audit Logging:** Automatic tracking of entity changes (created_by, updated_by, timestamps)
- **Async Processing:** Background task execution with `@Async`
- **Scheduled Tasks:** Cron-based job scheduling (`scheduler/CronScheduler` plus service-level `@Scheduled` jobs)
- **Custom Annotations:**
  - `@PermissionRequire` - Method-level security (validated by `PermissionRequireValidator`)
  - `@GeneratedUuidV7` - UUID v7 generation
  - `@GenSourceableTable` - Custom table generation
- **REST Client:** External API integration service
- **Email Service:** Templated email sending capability

## 7. MyBatis Integration
This project uses MyBatis 4.0.1 for complex database queries that are difficult to express with JPA/Hibernate. MyBatis is used alongside Spring Data JPA, providing flexibility for custom SQL operations.

### MyBatis Structure
- **Mapper Interfaces:** Located in `src/main/java/com/bekaku/api/spring/mybatis/`
- **XML Mappers:** Located in `src/main/resources/mybatis/`
- **Annotation:** All mapper interfaces use `@Mapper` from `org.apache.ibatis.annotations.Mapper`

### Standard MyBatis Usage Pattern

**Mapper Interface Example:**
```java
@Mapper
public interface ExampleMybatis {
    List<ExampleDto> findAll(@Param("page") Paging page);
    Optional<ExampleDto> findById(@Param("id") Long id);
    void updateField(@Param("field") String field, @Param("id") Long id);
}
```

**XML Mapper Example:**
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.bekaku.api.spring.mybatis.ExampleMybatis">
    <!-- Reusable SQL fragments -->
    <sql id="selectData">
        SELECT id, name, description, created_date
        FROM example_table
    </sql>

    <!-- Simple select with result type -->
    <select id="findById" resultType="com.bekaku.api.spring.dto.ExampleDto">
        <include refid="selectData"/>
        WHERE id = #{id} AND deleted is false
    </select>

    <!-- Complex select with result map -->
    <select id="findAll" resultMap="exampleResult">
        <include refid="selectData"/>
        WHERE deleted is false
        <if test="page.sortfield != null and page.sortmode != null">
            order by ${page.sortfield} ${page.sortmode}
        </if>
        <if test="page.offset != null and page.limit != null">
            LIMIT #{page.limit} OFFSET #{page.offset}
        </if>
    </select>

    <!-- Update with dynamic set -->
    <update id="updateField">
        UPDATE example_table
        <set>
            field = #{field}
        </set>
        WHERE id = #{id}
    </update>

    <!-- Result map for complex object mapping -->
    <resultMap id="exampleResult" type="com.bekaku.api.spring.dto.ExampleDto">
        <id column="id" property="id"/>
        <result column="name" property="name"/>
        <result column="description" property="description"/>
        <result column="created_date" property="createdDate"/>
        <!-- Collection mapping for one-to-many relationships -->
        <collection property="items" javaType="list" ofType="string">
            <result column="item"/>
        </collection>
    </resultMap>
</mapper>
```

### Key MyBatis Features Used

- **SQL Fragments (`<sql>`):** Reusable query components defined once and included with `<include refid="..."/>`
- **Dynamic SQL:** Use `<if>`, `<choose>`, `<when>`, `<otherwise>` for conditional query building
- **Result Maps (`<resultMap>`):** Complex object mapping including collections and nested objects
- **Parameter Binding:** Use `@Param` annotation in interfaces and `#{paramName}` in XML for safe parameter binding
- **Direct SQL:** Use `${paramName}` for direct SQL injection (use carefully, typically for column names in ORDER BY)
- **Pagination:** Custom `Paging` VO object with `offset` and `limit` properties for pagination
- **Database Compatibility:** XML mappers support both MySQL and PostgreSQL syntax (commented alternatives provided)

### Current Mappers
- `AccessTokenMybatis` - Token management operations
- `AppUserMybatis` - User queries with role associations
- `FileManagerMybatis` - File and directory queries with UNION operations
- `FilesDirectoryMybatis` - Directory path queries with hierarchical mapping
- `PermissionMybatis` - Permission queries with pagination
- `AppRoleMybatis` - Role operations (placeholder for future use)

### When to Use MyBatis vs JPA
Use MyBatis for:
- Complex queries with multiple JOINs that are difficult to optimize with JPA
- Queries requiring UNION operations
- Performance-critical queries where SQL control is needed
- Queries with complex result mapping (collections, nested objects)
- Database-specific features not well-supported by JPA

Use JPA for:
- Simple CRUD operations
- Standard entity relationships
- Queries that can be expressed with Specification/Query DSL
- When you want database-agnostic queries

## 8. Project Structure
```
src/main/java/com/bekaku/api/spring/
├── ai/                     # Spring AI: RAG chat, Qdrant config, DB tools, face-recognition client
├── annotation/             # Custom annotations (@GenSourceableTable, @GeneratedUuidV7, @PermissionRequire)
├── configuration/          # Spring configuration classes (security, JWT filter, cache, Kafka, WebSocket, AI ChatClient, ...)
├── controller/             # REST controllers
│   ├── api/                #   Main API controllers (extends BaseApiController)
│   ├── dev/                #   Dev-only code generator (blocked in production)
│   ├── socket/             #   WebSocket controllers
│   ├── test/               #   Test/demo endpoints (non-production)
│   └── web/                #   Web (Thymeleaf) controllers
├── dto/                    # Data Transfer Objects
├── enumtype/               # Enumerations
├── exception/              # ApiException/ApiError, BaseResponseException helpers, ExceptionResolver (@RestControllerAdvice)
├── extraction/             # Tika-based document text extraction (DocumentExtractor factory)
├── logger/                 # Logging utilities
├── mapper/                 # MapStruct mappers
├── middleware/             # Custom middleware (interceptors)
├── model/                  # JPA entities
├── mybatis/                # MyBatis mapper interfaces
├── properties/             # @ConfigurationProperties records (AppProperties, JwtProperties, ...)
├── queue/                  # Message queue consumers/producers (RabbitMQ/Kafka)
├── repository/             # JPA repositories
├── repositoryImpl/         # Custom repository implementations
├── scheduler/              # Scheduled tasks (CronScheduler)
├── service/                # Service interfaces
├── serviceImpl/            # Service implementations
├── specification/          # Dynamic query specifications (SearchSpecification)
├── util/                   # Utility classes (AppUtil, UrlUtil, CookieUtil, FileUtil, HashUtil, ...)
├── validator/              # Custom validators
└── vo/                     # View objects (Paging, LinkPreview, IpAddress, ...)

src/main/resources/
├── mybatis/                # MyBatis XML mapper files
├── i18n/                   # Message bundles for internationalization
└── application*.yml        # Profiles: default (prod defaults), dev, localdocker

src/test/java/com/bekaku/api/spring/
└── controller/api/         # Controller unit tests (JUnit 5 + Mockito), e.g. AuthControllerTest
```

## 9. Exception Handling Pattern
Global handling is centralized in `exception/ExceptionResolver` (`@RestControllerAdvice`, `@Order(HIGHEST_PRECEDENCE)`). Controllers never catch business exceptions themselves — they throw and let the resolver produce an `ApiError` body with the correct HTTP status.

**Throwing errors from controllers/services** (via `BaseResponseException` inherited by `BaseApiController`):
```java
throw this.responseError(HttpStatus.BAD_REQUEST, "Error Message");
throw this.responseErrorNotfound();                       // 404
throw this.responseErrorForbidden("custom message");      // 403
throw this.responseErrorBadRequest();                     // 400
this.throwError(HttpStatus.CONFLICT, null, i18n.getMessage("error.duplicate", value)); // any status

// Or construct directly:
throw new ApiException(new ApiError(HttpStatus.TOO_MANY_REQUESTS,
        i18n.getMessage("error.error"), "Too many requests"));
```

Rules:
- `ApiError` carries `status`, `message`, `errors[]`, `timestamp`; the resolver returns it with its own status.
- The resolver's catch-all logs the full stack server-side but returns only a generic message — do not include `ex.getLocalizedMessage()` in client responses.
- Client-disconnect scenarios (`ClientAbortException`, broken pipe during streaming) are handled gracefully by the resolver and must not be turned into error responses.
- `responseServerMessage(msg, status)` honors the given status (e.g. `HttpStatus.BAD_REQUEST`) — do not return error payloads with HTTP 200.

## 10. Testing Conventions
- **Stack:** JUnit 5 + Mockito + AssertJ (from `spring-boot-starter-test`). Run: `./gradlew test --tests "*AuthControllerTest"`.
- **Style:** Plain unit tests that instantiate the controller/service directly with mocked dependencies (no Spring context). See `AuthControllerTest` as the reference:
  - Nested `@DisplayName` classes per endpoint.
  - `@ExtendWith(MockitoExtension.class)` + `@MockitoSettings(strictness = Strictness.LENIENT)`.
  - `@ConfigurationProperties` records are mocked with Mockito (`when(appProperties.jwt()).thenReturn(new JwtProperties(...))`) or built as real records when convenient.
  - Fields injected via `@Autowired` in superclasses (e.g. `I18n` in `BaseApiController`/`BaseResponseException`) must be set by reflection in test setup.
  - Cover every endpoint plus security-relevant branches: auth failures, ownership checks, rate limiting, enumeration-safe responses, and role assignment restrictions.