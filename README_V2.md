# Java Spring Boot Starter Template

A production-ready, enterprise-grade Spring Boot REST API starter template with comprehensive features including AI integration, advanced security, auto-code generation, and modern architecture patterns.

## 🌟 Overview

This starter template provides a robust foundation for building modern Java-based REST APIs using Spring Boot 4.1.0 and Java 25. It includes enterprise-level features such as JWT authentication, role-based access control, Spring AI integration, MyBatis for complex queries, automated code generation, and comprehensive monitoring capabilities.

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Quick Start](#-quick-start)
- [Configuration](#-configuration)
- [Database Setup](#-database-setup)
- [API Documentation](#-api-documentation)
- [Security](#-security)
- [AI Integration](#-ai-integration)
- [MyBatis Usage](#-mybatis-usage)
- [Auto Code Generation](#-auto-code-generation)
- [Docker Deployment](#-docker-deployment)
- [Monitoring](#-monitoring)
- [Development Guidelines](#-development-guidelines)

## ✨ Features

### Core Features
- **Spring Boot 4.1.0** - Latest Spring Boot with Java 25 support
- **Virtual Threads** - Project Loom enabled for improved concurrency
- **Layered Architecture** - Controller → Service → Repository pattern
- **DTO Pattern** - Strict separation between entities and API contracts
- **Global Exception Handling** - Centralized error management with `@ControllerAdvice`
- **Input Validation** - Jakarta Bean Validation with custom validators

### Security & Authentication
- **JWT Authentication** - Stateless auth with access & refresh tokens (jjwt 0.13.0)
- **Role-Based Access Control (RBAC)** - Fine-grained permission system
- **Method-Level Security** - `@PreAuthorize` with custom permission checker
- **IP-Based Restrictions** - API client access control
- **Audit Logging** - Automatic tracking of entity changes

### Data Access
- **Spring Data JPA** - Hibernate ORM with repository pattern
- **MyBatis 4.0.1** - Complex queries with XML mappers
- **Multi-Database Support** - PostgreSQL (primary), MySQL 8+
- **HikariCP 7.1.0** - High-performance connection pooling
- **Soft Delete** - Logical deletion with `@SQLDelete`
- **Snowflake IDs** - Distributed unique ID generation

### AI Integration
- **Spring AI 2.0.0** - AI framework integration
- **Ollama** - Local LLM support
- **Qdrant Vector Store** - Vector database for embeddings
- **PDF Document Reader** - Document processing for AI
- **Vector Store Advisors** - AI-powered query enhancement

### File Management
- **Multi-Directory Storage** - Configurable file storage paths
- **Image Processing** - Thumbnailator for image resizing
- **Metadata Extraction** - EXIF/IPTC metadata support
- **MIME Type Detection** - Apache Tika integration
- **WebP Support** - TwelveMonkeys ImageIO
- **File Validation** - Comprehensive MIME type checking

### Messaging & Queues
- **RabbitMQ** - Spring AMQP with consumer/producer configurations
- **Kafka** - Spring Kafka with JSON serialization
- **Async Processing** - `@EnableAsync` for background tasks
- **Scheduled Tasks** - `@EnableScheduling` with cron support

### Caching & Performance
- **Ehcache 3.12.0** - Spring Cache abstraction
- **Hibernate Batch Processing** - Optimized batch inserts/updates
- **Query Optimization** - Hibernate query optimizer enabled
- **Connection Pooling** - HikariCP with tuned settings

### Monitoring & Observability
- **Spring Boot Actuator** - Health checks and metrics
- **Prometheus** - Metrics export for monitoring
- **Log4j2** - Structured logging (excluded Logback/SLF4J)
- **Custom Logging** - Application-specific logging utilities

### Development Tools
- **Auto Code Generation** - CRUD operations and frontend code generation
- **OpenAPI 3.0.3** - Interactive Swagger UI documentation
- **MapStruct 1.6.3** - Type-safe DTO-Entity mapping
- **Lombok** - Boilerplate code reduction
- **Internationalization (i18n)** - Multi-language support
- **WebSocket Support** - Real-time communication

### DevOps & Deployment
- **Docker Support** - Complete containerization with Docker Compose
- **Kubernetes** - K8s deployment configurations
- **Gradle Build System** - Modern build with custom tasks
- **Environment Profiles** - Dev, prod, localdocker configurations
- **Health Checks** - Actuator endpoints for monitoring

## 🛠 Tech Stack

### Core Framework
| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 4.1.0 | Application framework |
| Java | 25 | Programming language |
| Gradle | 8.x | Build tool |
| Spring Security | 6.x | Security framework |

### Database & ORM
| Technology | Version | Purpose |
|------------|---------|---------|
| PostgreSQL | 42.7.11 | Primary database |
| MySQL | 9.5.0 | Alternative database |
| HikariCP | 7.1.0 | Connection pooling |
| Hibernate | - | JPA ORM |
| MyBatis | 4.0.1 | SQL mapping |

### Security
| Technology | Version | Purpose |
|------------|---------|---------|
| jjwt | 0.13.0 | JWT token handling |
| Spring Security | 6.x | Authentication/Authorization |

### AI & ML
| Technology | Version | Purpose |
|------------|---------|---------|
| Spring AI | 2.0.0 | AI framework |
| Ollama | - | Local LLM |
| Qdrant | - | Vector database |

### Utilities
| Technology | Version | Purpose |
|------------|---------|---------|
| Lombok | 1.18.46 | Code generation |
| MapStruct | 1.6.3 | Object mapping |
| Gson | 2.14.0 | JSON processing |
| Guava | 33.6.0-jre | Google utilities |

### File Processing
| Technology | Version | Purpose |
|------------|---------|---------|
| Thumbnailator | 0.4.21 | Image thumbnails |
| metadata-extractor | 2.20.0 | EXIF metadata |
| Apache Tika | 3.3.1 | File type detection |
| TwelveMonkeys | 3.13.1 | WebP support |
| Apache POI | 5.5.1 | Office documents |

### Messaging
| Technology | Version | Purpose |
|------------|---------|---------|
| Spring AMQP | - | RabbitMQ integration |
| Spring Kafka | - | Kafka integration |

### Caching
| Technology | Version | Purpose |
|------------|---------|---------|
| Ehcache | 3.12.0 | Caching provider |

### Documentation
| Technology | Version | Purpose |
|------------|---------|---------|
| SpringDoc OpenAPI | 3.0.3 | API documentation |

### Monitoring
| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot Actuator | - | Application metrics |
| Micrometer Prometheus | - | Metrics export |

## 🏗 Architecture

### Layered Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Controller Layer                         │
│  (REST Endpoints, Validation, @PreAuthorize)                │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────┐
│                      Service Layer                           │
│  (Business Logic, @Transactional, DTO Conversion)            │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────┐
│                    Repository Layer                          │
│  (Spring Data JPA, MyBatis Mappers)                          │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────┐
│                      Database Layer                          │
│  (PostgreSQL/MySQL with Hibernate & MyBatis)                │
└─────────────────────────────────────────────────────────────┘
```

### Security Flow

```
Client Request
     │
     ▼
JWT Filter (Token Validation)
     │
     ▼
@PreAuthorize (Permission Check)
     │
     ▼
Controller (Request Handling)
     │
     ▼
Service (Business Logic)
     │
     ▼
Repository (Data Access)
     │
     ▼
Database
```

## 🚀 Quick Start

### Prerequisites

- **Java 25** - Required for Spring Boot 4.1.0
- **PostgreSQL 14+** or **MySQL 8+** - Database
- **Gradle 8.x** - Build tool (included via wrapper)
- **Docker** - Optional, for containerized deployment

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/bekaku/java-spring-boot-starter.git
   cd java-spring-boot-starter
   ```

2. **Configure database**
   Update `src/main/resources/application.yml` with your database credentials:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/spring_starter_postgres
       username: your_username
       password: your_password
       driver-class-name: org.postgresql.Driver
   ```

3. **Import database schema**
   ```bash
   # PostgreSQL
   psql -U your_user_name -d your_db_name -f spring-data/files/spring_starter_postgres.sql
   
   # MySQL
   mysql -uroot -p your_db_name < spring-data/files/spring_starter_mysql.sql
   ```

4. **Run the application**
   ```bash
   ./gradlew bootRun
   # Or use the custom task
   ./gradlew runDev
   ```

5. **Verify installation**
   Open `http://localhost:8080/welcome` in your browser

### Gradle Tasks

```bash
# Run with development profile
./gradlew runDev

# Run with production profile
./gradlew runProd

# Build production JAR
./gradlew runBuild

# Build native image (requires GraalVM)
# ./gradlew bootBuildImage
```

## ⚙️ Configuration

### Environment Profiles

The application supports multiple environments:

- **dev** - Development environment with detailed logging
- **prod** - Production environment with optimized settings
- **localdocker** - Local Docker development

Set active profile in `application.yml`:
```yaml
spring:
  profiles:
    active: dev
```

### Key Configuration Sections

#### Database Configuration
```yaml
spring:
  datasource:
    hikari:
      pool-name: App-Instance-${WORKER_ID:1}
      connection-timeout: 30000
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 300000
      max-lifetime: 1800000
```

#### JWT Configuration
```yaml
app:
  jwt:
    secret: TzIGu7DC5IhL/hYNpQi7V0CATXCVaDcoHNExNef9H70=
    session-time: 86400  # 24 hours
    session-refresh-time: 2592000  # 30 days
```

#### File Upload Configuration
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 250MB
      max-request-size: 250MB
```

#### CORS Configuration
```yaml
app:
  cors:
    allowed-origins:
      - https://your-production-webapp.com
      - http://localhost:3000
```

## 🗄️ Database Setup

### Supported Databases

- **PostgreSQL** (Primary) - Recommended for production
- **MySQL 8+** - Alternative option
- **H2** - In-memory for testing

### Schema Management

The project uses:
- **Hibernate DDL** - Set to `none` for production (manual schema management)
- **SQL Scripts** - Located in `spring-data/files/`
- **Flyway/Liquibase** - Not included (manual migration management)

### Connection Pooling

HikariCP configuration optimized for production:
- **Minimum Idle**: 5 connections
- **Maximum Pool Size**: 20 connections
- **Connection Timeout**: 30 seconds
- **Max Lifetime**: 30 minutes
- **Keepalive**: Every 2 minutes

## 📚 API Documentation

### Swagger UI

Access interactive API documentation at:
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8080/api-docs`

**Note**: Swagger is disabled in production by default. Enable in configuration:
```yaml
springdoc:
  swagger-ui:
    enabled: true
  api-docs:
    enabled: true
```

### Key Endpoints

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/auth/login` | POST | User authentication | No |
| `/api/appUser/currentUserData` | GET | Current user info | Yes |
| `/api/permission` | GET, POST, PUT, DELETE | Permission management | Yes |
| `/api/appRole` | GET, POST, PUT, DELETE | Role management | Yes |
| `/api/appUser` | GET, POST, PUT, DELETE | User management | Yes |
| `/actuator/health` | GET | Health check | Yes |
| `/actuator/prometheus` | GET | Prometheus metrics | Yes |

## 🔐 Security

### Authentication Flow

1. **Login Request**
   ```bash
   POST /api/auth/login
   Content-Type: application/json
   
   {
     "appUser": {
       "emailOrUsername": "admin@mydomain.com",
       "password": "P@ssw0rd",
       "loginForm": 1
     }
   }
   ```

2. **Response**
   ```json
   {
     "userId": 1,
     "authenticationToken": "eyJhbGciOiJIUzUxMiJ9...",
     "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
     "expiresAt": "2024-10-29T01:24:46.019+00:00"
   }
   ```

### Authorization Headers

```http
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json
Accept-Language: en
Accept-ApiClient: default
```

### Method-Level Security

```java
@PreAuthorize("@permissionChecker.hasPermission('role_list')")
@GetMapping
public ResponseEntity<Object> findAll(Pageable pageable) {
    // Implementation
}

@PreAuthorize("@permissionChecker.hasAnyPermission('role_add', 'user_manage')")
@PostMapping
public ResponseEntity<Object> create(@Valid @RequestBody RoleDto dto) {
    // Implementation
}
```

### Permission System

The project uses a custom permission system:
- Permissions are stored in the `permission` table
- Roles are associated with multiple permissions
- Users can have multiple roles
- Method-level security checks permissions before execution

## 🤖 AI Integration

### Spring AI Configuration

The project integrates Spring AI 2.0.0 with:
- **Ollama** - Local LLM inference
- **Qdrant** - Vector database for embeddings
- **PDF Document Reader** - Document processing

### Usage Example

```java
@Autowired
private ChatClient chatClient;

public String generateResponse(String prompt) {
    return chatClient.prompt()
        .user(prompt)
        .call()
        .content();
}
```

### Vector Store

```java
@Autowired
private VectorStore vectorStore;

public void addDocument(Document document) {
    vectorStore.add(List.of(document));
}

public List<Document> searchSimilar(String query) {
    return vectorStore.similaritySearch(SearchRequest.query(query));
}
```

## 🗃️ MyBatis Usage

### When to Use MyBatis

Use MyBatis for:
- Complex queries with multiple JOINs
- Queries requiring UNION operations
- Performance-critical queries
- Database-specific features
- Complex result mapping

### Mapper Interface Example

```java
@Mapper
public interface ExampleMybatis {
    List<ExampleDto> findAll(@Param("page") Paging page);
    Optional<ExampleDto> findById(@Param("id") Long id);
    void updateField(@Param("field") String field, @Param("id") Long id);
}
```

### XML Mapper Example

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.bekaku.api.spring.mybatis.ExampleMybatis">
    <sql id="selectData">
        SELECT id, name, description FROM example_table
    </sql>

    <select id="findById" resultType="com.bekaku.api.spring.dto.ExampleDto">
        <include refid="selectData"/>
        WHERE id = #{id} AND deleted is false
    </select>
</mapper>
```

### Current Mappers

- `AccessTokenMybatis` - Token management
- `AppUserMybatis` - User queries with role associations
- `FileManagerMybatis` - File and directory queries
- `FilesDirectoryMybatis` - Directory path queries
- `PermissionMybatis` - Permission queries
- `AppRoleMybatis` - Role operations

## 🔧 Auto Code Generation

### Enable Auto Generation

1. **Annotate your entity**
   ```java
   @GenSourceableTable(createFrontend = true)
   @Entity
   public class YourEntity extends SoftDeletedAuditable<Long> {
       // Entity fields
   }
   ```

2. **Generate source code**
   ```bash
   POST http://localhost:8080/dev/development/generateSrc
   ```

### Generated Components

The system automatically generates:
- **DTO classes** - Request/Response objects
- **Repository interfaces** - JPA repositories
- **Service interfaces** - Business logic contracts
- **Service implementations** - Business logic
- **REST controllers** - API endpoints
- **MapStruct mappers** - Entity-DTO converters
- **Frontend components** - Vue.js forms (optional)

## 🐳 Docker Deployment

### Build and Run

1. **Build the application**
   ```bash
   ./gradlew bootJar
   ```

2. **Build Docker image**
   ```bash
   docker-compose build
   ```

3. **Run with Docker Compose**
   ```bash
   docker-compose up -d
   ```

### Docker Compose Services

The `docker-compose.yml` includes:
- **Application container** - Spring Boot app
- **Database container** - PostgreSQL/MySQL
- **Volume mapping** - Persistent data storage
- **Network configuration** - Service communication

### Native Image Support

GraalVM native image support (commented out in build.gradle):
```bash
./gradlew bootBuildImage
```

## 📊 Monitoring

### Actuator Endpoints

- `/actuator/health` - Application health status
- `/actuator/info` - Application information
- `/actuator/prometheus` - Prometheus metrics
- `/actuator/metrics` - Application metrics

### Prometheus Metrics

Enabled metrics:
- HikariCP connection pool metrics
- JVM metrics
- HTTP request metrics
- Custom business metrics

### Logging

Log4j2 configuration:
- **Development**: `log4j2-dev.xml` - Detailed logging
- **Production**: `log4j2-prod.xml` - Optimized logging
- **Log Location**: `${app.cdn-directory}logs`

### Health Checks

Custom health indicators can be added:
```java
@Component
public class CustomHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // Custom health check logic
    }
}
```

## 📖 Development Guidelines

### Code Standards

Refer to `SKILLS.md` for detailed coding standards:
- Use Lombok annotations to reduce boilerplate
- Follow layered architecture pattern
- Use DTOs for all API communications
- Apply `@Transactional` at service layer
- Use constructor injection with `@RequiredArgsConstructor`

### Standard Controller Pattern

```java
@Slf4j
@RequestMapping(path = "/api/resource")
@RestController
@RequiredArgsConstructor
public class ResourceController extends BaseApiController {
    
    private final ResourceService resourceService;
    
    @PreAuthorize("@permissionChecker.hasPermission('resource_list')")
    @GetMapping
    public ResponseEntity<Object> findAll(HttpServletRequest request, Pageable pageable) {
        SearchSpecification<Resource> specification = ControllerUtil.buildSpecification(request, List.of());
        return this.responseEntity(
            resourceService.findAllWithSearch(specification, getPageable(pageable, Resource.getSort())), 
            HttpStatus.OK
        );
    }
}
```

### Standard Entity Pattern

```java
@GenSourceableTable
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "resource")
@SQLDelete(sql = "UPDATE resource SET deleted = true WHERE id=?")
@SQLRestriction("deleted=false")
@EntityListeners(AuditListener.class)
public class Resource extends SoftDeletedAuditable<Long> {
    
    @Column(name = "name", length = 125, nullable = false)
    private String name;
    
    private Boolean active = true;
}
```

### ID Generation

Entities extend `SoftDeletedAuditable<Long>` which includes:
- Automatic Snowflake ID generation via `@PrePersist`
- Soft delete support
- Audit fields (created_by, updated_by, timestamps)

## 📞 Support & Resources

### Documentation
- **SKILLS.md** - Detailed coding standards and patterns
- **API Documentation** - Swagger UI (when enabled)
- **Code Comments** - Inline documentation

### Compatible Frontend Applications

This API works seamlessly with:
- **[Nuxt.js + Nuxt Ui](https://github.com/bekaku/nuxt-ui-starter)** - SSR Vue.js
- **[Nuxt.js + Quasar](https://github.com/bekaku/nuxt-quasar-example-app)** - SSR Vue.js
- **[Vue.js 3 + Quasar 2+](https://github.com/bekaku/quasar-starter-template)** - Vue.js SPA
- **[Vue.js 3 + Ionic 8](https://github.com/bekaku/vue-ionic-example-app)** - Mobile-first

### Issues & Contributions

- **GitHub Issues**: [Report bugs](https://github.com/bekaku/java-spring-boot-starter/issues)
- **Pull Requests**: Contributions welcome

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Built with ❤️ using Spring Boot 4.1.0 and Java 25**
