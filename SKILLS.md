# 🛠️ Project Skills & Technology Stack

This document outlines the core technologies, architectural patterns, and coding conventions used in this Java Spring Boot boilerplate.
**Note for AI Assistants (Copilot, Claude, Codex):** Please strictly adhere to these technologies, design patterns, and versions when generating code, creating new modules, or refactoring.

## 1. Core Technologies
* **Framework:** Spring Boot 4.1.0
* **Language:** Java 25
* **Build Tool:** Gradle (Manage dependencies through the build.gradle file and use the ./gradlew command to run various tasks.)
* **API Documentation:** SpringDoc OpenAPI 3.0.3 (Swagger UI)
* **Logging:** Log4j2 (excluded Logback and SLF4J)

## 2. Architecture & Patterns
* **Architecture:** Layered Architecture (Controller -> Service -> Repository)
* **Data Transfer Pattern:** Strict use of DTOs (Data Transfer Objects) for requests and responses. Entities must NEVER be exposed directly to the Controller layer.
* **Dependency Injection:** Constructor Injection preferred (utilizing Lombok's `@RequiredArgsConstructor`).
* **Database Access:** Spring Data JPA / Hibernate + MyBatis 4.0.1
* **Security:** Spring Security with JWT (JSON Web Token) authentication (jjwt 0.13.0)
* **Async Processing:** `@EnableAsync` for asynchronous task execution
* **Scheduling:** `@EnableScheduling` for scheduled tasks
* **Caching:** Spring Cache with Ehcache 3.12.0
* **Auditing:** JPA Auditing for tracking entity creation/modification metadata

## 3. 🤖 AI Code Generation Guidelines
When assisting with code generation in this project, AI agents must follow these rules:
1. **Lombok Usage:** Use Lombok annotations (`@Data`, `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Slf4j`) to reduce boilerplate code.
2. **Standardized Responses:** All REST API endpoints must return a unified wrapper object (e.g., `ApiResponse<T>`) rather than raw data.
3. **Exception Handling:** Do not use plain `try-catch` blocks in Controllers for business logic errors. Throw custom exceptions (e.g., `ResourceNotFoundException`) and let the global `@ControllerAdvice` / `@ExceptionHandler` handle the HTTP response.
4. **Validation:** Use `jakarta.validation.constraints` (e.g., `@NotNull`, `@NotBlank`, `@Email`) on Request DTOs, and ensure `@Valid` is used in the Controller.
5. **Transactions:** Apply `@Transactional` at the Service layer for methods that modify the database. For methods that only perform select/read queries, strictly use `@Transactional(readOnly = true)` to optimize performance.

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
    public ResponseEntity<Object> findAll(Pageable pageable) {
        SearchSpecification<Resource> specification = new SearchSpecification<>(getSearchCriteriaList());
        return this.responseEntity(resourceService.findAllWithSearch(specification, getPageable(pageable, Resource.getSort())), HttpStatus.OK);
    }

    @PreAuthorize("@permissionChecker.hasPermission('resource_view')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") Long id) {
        Optional<Resource> resource = resourceService.findById(id);
        if (resource.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return this.responseEntity(resourceService.convertEntityToDto(resource.get()), HttpStatus.OK);
    }

    @PreAuthorize("@permissionChecker.hasPermission('resource_add')")
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ResourceDto dto) {
        return this.responseEntity(createProcess(dto), HttpStatus.CREATED);
    }

    private ResourceDto createProcess(ResourceDto dto) {
        Resource resource = resourceService.convertDtoToEntity(dto);
        resourceService.save(resource);
        return resourceService.convertEntityToDto(resource);
    }

    @PreAuthorize("@permissionChecker.hasPermission('resource_edit')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@Valid @RequestBody ResourceDto dto, @PathVariable("id") Long id) {
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
    public ResponseEntity<Object> delete(@PathVariable("id") Long id) {
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
@Transactional
@RequiredArgsConstructor
@Service
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<ResourceDto> findAllWithPaging(Pageable pageable) {
        Page<Resource> result = resourceRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<ResourceDto> findAllWithSearch(SearchSpecification<Resource> specification, Pageable pageable) {
        Page<Resource> result = resourceRepository.findAll(specification, pageable);
        return getListFromResult(result);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<ResourceDto> findAllBy(Specification<Resource> specification, Pageable pageable) {
        return getListFromResult(findAllPageSpecificationBy(specification, pageable));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Resource> findAllPageSpecificationBy(Specification<Resource> specification, Pageable pageable) {
        return resourceRepository.findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    @Override
    public List<Resource> findAll() {
        return resourceRepository.findAll();
    }

    @Override
    public Resource save(Resource resource) {
        return resourceRepository.save(resource);
    }

    @Override
    public Resource update(Resource resource) {
        return resourceRepository.save(resource);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Resource> findById(Long id) {
        return resourceRepository.findById(id);
    }

    @Override
    public void delete(Resource resource) {
        resourceRepository.delete(resource);
    }

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

    @Transactional(readOnly = true)
    @Override
    public Optional<Resource> findByName(String name) {
        return resourceRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Resource> findAllByActiveTrue() {
        return resourceRepository.findAllByActiveTrue();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Resource> findByCode(String code) {
        return resourceRepository.findByCode(code);
    }
}
```

## 4. Database & Infrastructure
- **Database:** PostgreSQL 42.7.11 (primary)
- **Connection Pool:** HikariCP 7.1.0
- **ORM:** Spring Data JPA with Hibernate + MyBatis 4.0.1 for complex queries
- **Caching:** Ehcache 3.12.0 (Spring Cache abstraction)
- **Message Queues:**
  - RabbitMQ (Spring AMQP)
  - Kafka (Spring Kafka with consumer/producer configurations)
- **In-memory Data:** Redis (for caching/sessions)
- **Containerization:** Docker & Docker Compose (docker-compose.yml for local development backing services)
- **Monitoring:** Spring Boot Actuator with Prometheus metrics
- **Email:** Spring Boot Mail (SMTP support)

## 5. Key Libraries & Frameworks
- **Object Mapping:** MapStruct 1.6.3 (DTO-Entity mapping)
- **JSON Processing:** Gson 2.14.0
- **Validation:** Jakarta Validation (spring-boot-starter-validation)
- **Image Processing:**
  - Thumbnailator 0.4.21 (thumbnail generation)
  - metadata-extractor 2.20.0 (EXIF/IPTC metadata)
  - Apache Tika 3.3.1 (file type detection)
  - TwelveMonkeys ImageIO 3.13.1 (WebP support)
- **Report Generation:** Apache POI 5.5.1 (Excel/Office documents)
- **ID Generation:**
  - UUID Creator 6.1.1 (including UUID v7)
  - Custom Snowflake ID generator
- **Web Scraping:** Jsoup 1.22.2
- **Google Services:**
  - Guava 33.6.0-jre
  - Firebase Admin 9.9.0

## 6. Key Features & Capabilities
- **Authentication & Authorization:**
  - JWT-based stateless authentication
  - Custom permission system with `@PermissionRequire` annotation
  - Role-based access control (RBAC)
  - IP-based API client restrictions
- **File Management:**
  - Multi-directory file storage
  - Image processing and thumbnail generation
  - MIME type detection and validation
  - File metadata extraction
- **WebSocket Support:** Real-time communication with user interceptors
- **Internationalization (i18n):** Header-based locale resolution
- **Audit Logging:** Automatic tracking of entity changes (created_by, updated_by, timestamps)
- **Async Processing:** Background task execution with `@Async`
- **Scheduled Tasks:** Cron-based job scheduling
- **Custom Annotations:**
  - `@PermissionRequire` - Method-level security
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
├── annotation/          # Custom annotations
├── configuration/       # Spring configuration classes
├── controller/          # REST controllers (api, web, socket, ai, dev)
├── dto/                 # Data Transfer Objects
├── enumtype/            # Enumerations
├── exception/           # Custom exceptions and global handlers
├── logger/              # Logging utilities
├── mapper/              # MapStruct mappers
├── middleware/          # Custom middleware
├── model/               # JPA entities
├── mybatis/             # MyBatis mapper interfaces
├── properties/          # Configuration properties classes
├── queue/               # Message queue consumers/producers
├── repository/          # JPA repositories
├── repositoryImpl/      # Custom repository implementations
├── scheduler/           # Scheduled tasks
├── service/             # Service interfaces
├── serviceImpl/         # Service implementations
├── specification/       # dynamic query specifications
├── util/                # Utility classes
├── validator/           # Custom validators
└── vo/                  # View objects

src/main/resources/
└── mybatis/             # MyBatis XML mapper files
```