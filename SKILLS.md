# 🛠️ Project Skills & Technology Stack

This document outlines the core technologies, architectural patterns, and coding conventions used in this Java Spring Boot boilerplate.
**Note for AI Assistants (Copilot, Claude, Codex):** Please strictly adhere to these technologies, design patterns, and versions when generating code, creating new modules, or refactoring.

## 1. Core Technologies
* **Framework:** Spring Boot 4.0.5
* **Language:** Java (JDK 17 / 21)
* **Build Tool:** Gradle (Manage dependencies through the build.gradle file and use the ./gradlew command to run various tasks.)
* **API Documentation:** SpringDoc OpenAPI (Swagger UI)

## 2. Architecture & Patterns
* **Architecture:** Layered Architecture (Controller -> Service -> Repository)
* **Data Transfer Pattern:** Strict use of DTOs (Data Transfer Objects) for requests and responses. Entities must NEVER be exposed directly to the Controller layer.
* **Dependency Injection:** Constructor Injection preferred (utilizing Lombok's `@RequiredArgsConstructor`).
* **Database Access:** Spring Data JPA / Hibernate.
* **Security:** Spring Security with JWT (JSON Web Token) authentication.

## 3. 🤖 AI Code Generation Guidelines
When assisting with code generation in this project, AI agents must follow these rules:
1. **Lombok Usage:** Use Lombok annotations (`@Data`, `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Slf4j`) to reduce boilerplate code.
2. **Standardized Responses:** All REST API endpoints must return a unified wrapper object (e.g., `ApiResponse<T>`) rather than raw data.
3. **Exception Handling:** Do not use plain `try-catch` blocks in Controllers for business logic errors. Throw custom exceptions (e.g., `ResourceNotFoundException`) and let the global `@ControllerAdvice` / `@ExceptionHandler` handle the HTTP response.
4. **Validation:** Use `jakarta.validation.constraints` (e.g., `@NotNull`, `@NotBlank`, `@Email`) on Request DTOs, and ensure `@Valid` is used in the Controller.
5. **Transactions:** Apply `@Transactional` at the Service layer for methods that modify the database.

*Standard Controller Example Pattern:*
```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse<>("Success", user));
    }
}
```

## 4. Database & Infrastructure
- **Database:** PostgreSQL / MySQL (อ้างอิงตาม Configuration ของโปรเจกต์)
- **Caching/Session:** Redis (If applicable)
- **Containerization:** Docker & Docker Compose (docker-compose.yml for local development backing services like DB and Redis).