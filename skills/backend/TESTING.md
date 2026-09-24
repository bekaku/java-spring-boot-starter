# Testing / Verification — Reference

> **Role:** evidence rules + verified facts about the test setup (WHAT / WHY).
> **Procedure (HOW):** `.agents/skills/backend-testing/SKILL.md`.
> **Evidence style:** `path` + `Class#member` (no line numbers; search the symbol).

## Current test setup (verified)

- Location: `src/test/java/com/bekaku/api/spring/` — `controller/api/`, `serviceImpl/` and `validator/` (mirroring the main packages).
- Existing tests: `AuthControllerTest`, `FileManagerControllerTest`, `AiChatControllerOwnershipTest`, `AppRoleControllerTest`, `AppUserControllerTest` (controller); `AiRagChatServiceOwnershipTest`, `FaceRecognitionServiceOwnershipTest` (service); `RoleValidatorTest`, `UserValidatorTest` (validator).
- Frameworks: JUnit Jupiter (`useJUnitPlatform()`), Mockito (`@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`; `AuthControllerTest` adds `@MockitoSettings(strictness = LENIENT)`), AssertJ, Spring `MockMvc` standalone setup.
- Not used anywhere yet: `@SpringBootTest`, `@WebMvcTest`, `@WithMockUser`, Testcontainers, H2. Test deps in `build.gradle`: `spring-boot-starter-test`, `spring-security-test`, `spring-restdocs-mockmvc`. Spring Boot 4 splits test slices into separate modules, so `@WebMvcTest` / slice tests may need a new test dependency — that is a dependency change; confirm with the user first.
- Every existing test is an isolated unit test. None proves the real filter chain, database, or external services.

## Known setup gotcha

`BaseResponseException` (parent of `BaseApiController` and some services) field-injects `I18n`. When you construct a controller/service with `new`, the error helpers throw `NullPointerException` unless you inject it:

```java
private static void injectExceptionI18n(BaseResponseException target, I18n i18n) throws Exception {
    Field field = BaseResponseException.class.getDeclaredField("i18n");
    field.setAccessible(true);
    field.set(target, i18n);
}
```

Pattern source: `AiChatControllerOwnershipTest#injectExceptionI18n`. `BaseApiController` has its own private `@Autowired` fields (`request`, `i18n`) — inject those the same way if the code under test uses them. Validators need no reflection: they take `I18n` through the constructor (`new RoleValidator(appRoleService, i18n)` in `RoleValidatorTest`).

HTTP-level tests and curl examples: a JWT request (cookie or Bearer) also needs a non-empty `Accept-Apiclient` header, otherwise `JwtTokenFilter` returns `401 {"error": "Invalid or missing token"}` even for a valid token (`JwtServiceImpl#jwtVerify`).

## Evidence matrix (binding)

| Change | Required evidence | Pattern to copy |
|---|---|---|
| Service / controller logic | Focused unit test incl. failure path | `AiChatControllerOwnershipTest` |
| Owner-scoped data | Not-owned id → `404`; `verifyNoInteractions(...)` on the downstream dependency | `AiChatControllerOwnershipTest`, `AiRagChatServiceOwnershipTest`, `FaceRecognitionServiceOwnershipTest` |
| Auth / routes / filter | HTTP test through the real filter chain for cookie **and** Bearer (and `X-API-KEY` when the filter changes). Direct controller construction is insufficient (`AuthControllerTest` does this) | none yet — see gotcha above about dependencies |
| SQL / schema / pgvector / migration | Run against disposable PostgreSQL + pgvector (H2 cannot run casts, vector ops, or the `COPY`-based `V1` dump) | none yet |
| File operations | `@TempDir` tests for containment, ownership, partial failure | `FileManagerControllerTest` (standalone MockMvc — not chain proof) |
| RAG / SSE | Mock model + vector store; verify event order, disabled Qdrant, error event, persistence | `AiRagChatServiceOwnershipTest` |
| Config / wiring | `./gradlew compileJava`; context-startup check when feasible | — |
| Docs only | Verify paths, links, commands, claims against code; no runtime claim | — |

## Commands

```bash
./gradlew test --tests '*AiChatControllerOwnershipTest'   # focused
./gradlew compileJava                                      # main sources compile
./gradlew test                                             # full suite, only when justified
./gradlew bootJar                                          # packaging, only when affected
./gradlew bootRun --args='--spring.profiles.active=dev'    # needs local PostgreSQL (+ Qdrant/Ollama for AI)
```

- `--tests` filters which tests run; all test sources still compile, so an unrelated broken test file fails the command.
- `bootRun` in dev uses `ddl-auto: update` and Flyway disabled — it is not migration evidence.

## Reporting (binding)

Report: changed files, behavior change, each command run with its actual result, and what was not verified (filter chain, DB, external services, deployment). Never report an unexecuted command as passing.
