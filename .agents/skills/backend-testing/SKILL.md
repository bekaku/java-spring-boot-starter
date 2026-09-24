---
name: backend-testing
description: Use before finishing any backend implementation in this Spring Boot repo — to choose the right evidence (unit, ownership, filter-chain, PostgreSQL, @TempDir, SSE), write focused JUnit 5 + Mockito + AssertJ tests, run the narrowest Gradle command, and report results honestly.
---

# Backend Testing — Playbook

> **Role:** HOW to prove a change. Binding evidence rules + setup facts: `skills/backend/TESTING.md` (read it too).
> **Requires:** `backend-core`.

## Step 1 — pick the evidence

Match the boundary you changed to a row of the evidence matrix in `TESTING.md`. Typical picks:

| You changed | Minimum evidence |
|---|---|
| Service method | Unit test: happy path + one failure path |
| Controller handler | Unit test on the controller with mocked service (status + body + error) |
| Owner-scoped read/write | Not-owned id → `404` and `verifyNoInteractions(downstream)` |
| Route access / filter / tokens | Filter-chain HTTP test (cookie + Bearer). If no harness exists, say so in the report |
| Migration / native SQL / MyBatis XML | Run on disposable PostgreSQL, or report "not validated on PostgreSQL" |
| Files | `@TempDir` test: traversal rejected, other owner rejected, cleanup on failure |

## Step 2 — write the test

Place it next to the existing ones: `src/test/java/com/bekaku/api/spring/{controller/api|serviceImpl}/{Subject}Test.java`.

```java
@ExtendWith(MockitoExtension.class)
class {Model}ControllerTest {

    @Mock {Model}Service service;
    @Mock I18n i18n;
    private {Model}Controller controller;

    @BeforeEach
    void setUp() throws Exception {
        controller = new {Model}Controller(service);
        Field f = BaseResponseException.class.getDeclaredField("i18n");   // error helpers need it
        f.setAccessible(true);
        f.set(controller, i18n);
    }

    @Test
    void findOneReturns404WhenMissing() {
        when(i18n.getMessage("error.dataNotfound")).thenReturn("Data not found");
        when(service.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.findOne(1L))
            .isInstanceOfSatisfying(ApiException.class,
                e -> assertThat(e.getApiError().getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }
}
```

- Name tests by behavior (`rejectsChatNotOwnedByUser`, `createReturns201`).
- Cover the denied/failure path, not only the happy path.
- Standalone MockMvc (`MockMvcBuilders.standaloneSetup(controller)`) is fine for request/response shape; it is not security evidence.

## Step 3 — run, narrowest first

```bash
./gradlew test --tests '*{Model}ControllerTest'
./gradlew compileJava
```

Run the full `./gradlew test` only for shared base classes, security chain, migrations, or build config changes. Run `./gradlew bootJar` only when packaging is affected.

## Step 4 — report

```text
Commands run:      <exact command> → <PASS/FAIL + counts>
Evidence type:     unit | ownership | filter-chain | PostgreSQL | @TempDir | SSE
Not verified:      <e.g. real filter chain, PostgreSQL migration, external service>
```

For documentation-only changes: verify paths, links, and claims against the code; state that no runtime validation applies.

## Common mistakes

| Mistake | Fix |
|---|---|
| `NullPointerException` from `responseErrorNotfound()` in a test | Inject `I18n` into `BaseResponseException` via reflection |
| `401` in a filter-chain test or curl with a valid token | Send an `Accept-Apiclient` header too |
| Claiming route security from a controller unit test | Say "filter chain not verified" or add a real chain test |
| "App started, migration OK" | Dev uses `ddl-auto: update`; Flyway is off |
| Using H2 for PostgreSQL SQL | Disposable PostgreSQL + pgvector |
| Running the whole suite by default | Focused test + `compileJava` first |
| Adding `@WebMvcTest` / Testcontainers dependencies silently | Dependency change — ask first |
