# Backend Core — Reference

> **Role:** binding core rules + verified facts (WHAT / WHY).
> **Procedure (HOW):** `.agents/skills/backend-core/SKILL.md` — read that first, then this file.
> **Evidence style:** `path` + `Class#member`. Line numbers are avoided because they drift; search for the symbol.

## Enforcement

- These rules are authoritative unless the user explicitly overrides them in-session.
- If a task conflicts with a documented rule, surface the conflict instead of silently choosing.
- Do not invent conventions. Prefer evidence from existing code.
- Legacy code listed in `docs/agent/KNOWN_ISSUES.md` is not a pattern to copy.

## Architecture (binding)

- Layering: `controller/api/*Controller` → `service/*Service` (interface) → `serviceImpl/*ServiceImpl` → `repository/*Repository` (JPA) and/or `mybatis/*Mybatis` + `resources/mybatis/*.xml`.
- MapStruct (`mapper/*Mapper`) converts entity ↔ DTO at the transport boundary.
- Controllers never call `EntityManager`, `JdbcTemplate`, or a `VectorStore` directly. The `ai/*Tool.java` classes are the only documented exception.
- Controllers do HTTP only: bind, `@Valid`, permission annotation, resolve the current user, choose status. Business rules and multi-step writes belong in the service.
- Never call a controller from a service.
- Transactions: `serviceImpl` classes use class-level `@Transactional(readOnly = true)` and add `@Transactional` on each write method (`serviceImpl/AppRoleServiceImpl.java`). Private methods and same-bean self-calls do not start a proxy transaction.
- A DB transaction does not roll back filesystem, vector-store, email, queue, or remote-service effects. Multi-system writes need an explicit order and compensation.

## Where code goes

| You need | Package / path | Naming and base type | Example |
|---|---|---|---|
| REST endpoint | `controller/api/` | `{Model}Controller extends BaseApiController`, `@RequestMapping(path = "/api/{modelCamel}")` | `AppRoleController` |
| Service contract | `service/` | `{Model}Service extends BaseService<{Model}, {Model}Dto>` | `AppRoleService` |
| Service implementation | `serviceImpl/` | `{Model}ServiceImpl implements {Model}Service` | `AppRoleServiceImpl` |
| JPA repository | `repository/` | `{Model}Repository extends BaseRepository<{Model}, Long>, JpaSpecificationExecutor<{Model}>` | `AppRoleRepository` |
| Custom repository fragment | `repository/` + `repositoryImpl/` | `{Model}RepositoryCustom` + `{Model}RepositoryCustomImpl` | `PermissionRepositoryCustom` |
| MyBatis read model | `mybatis/` + `src/main/resources/mybatis/` | `{Model}Mybatis.java` + `{Model}Mybatis.xml` (same namespace) | `FileManagerMybatis` |
| Entity | `model/` | extends a `model/superclass/*` base | `AppRole` |
| Request/response DTO | `dto/` | `{Model}Dto extends DtoId` | `AppRoleDto` |
| Entity ↔ DTO mapper | `mapper/` | `{Model}Mapper` (MapStruct) | `AppRoleMapper` |
| Domain validation | `validator/` | `{Model}Validator` `@Component` `extends BaseValidator`; errors in a list local to each call | recipe §9 in `STANDARD_CRUD_SERVICE_REPOSITORY.md` |
| Typed config | `properties/` | record with `@ConfigurationProperties(prefix = "app....")` | `RagProperties` |
| Enum | `enumtype/` | `{Name}` or `{Name}Type` | `PermissionType` |
| Schema change | `src/main/resources/db/migration/` | `V{n}__{snake_case}.sql` | `V6__forgot_password_reset_hardening.sql` |
| User-facing text | `src/main/resources/i18n/**/messages*.properties` | add EN and `_th` keys | `i18n/error/messages.properties` |
| Tests | `src/test/java/com/bekaku/api/spring/{controller/api,serviceImpl,validator}/` | `{Subject}Test` | `AiChatControllerOwnershipTest` |

## Shared building blocks (reuse, do not re-create)

- **Responses** — `controller/api/BaseApiController.java`: `responseEntity(body, status)`, `responseDeleteMessage()`, `responseServerMessage(msg, status)`, `getPageable(pageable, {Model}.getSort())`, `getPaging(pageable, allowedSortFields)`.
- **Errors** — `exception/BaseResponseException.java` helpers *return* an `ApiException`; write `throw responseErrorNotfound();`. Helpers: `responseErrorNotfound()`, `responseErrorForbidden()`, `responseErrorBadRequest()`, `responseErrorDuplicate(label)`, `responseError(status, message, error)`.
  - Controllers inherit them through `BaseApiController`.
  - A service either extends `BaseResponseException` (`serviceImpl/FilesDirectoryServiceImpl.java`) or builds the exception directly: `new ApiException(new ApiError(HttpStatus.BAD_REQUEST, i18n.getMessage("error.error"), i18n.getMessage("<key>")))` (`PasswordResetServiceImpl#invalidCodeError`).
- **Current user** — controller parameter `@AuthenticationPrincipal AppUserDto auth` (`AppUserController#currentUserData`); pass `auth.getId()` into the service. For JWT callers the principal holds only `id`, `token`, `accessTokenId` (`JwtServiceImpl#jwtVerify`) — load the user through `AppUserService` for other fields. Inside request-thread code without a parameter: `util/AuthUtil#getAuthenticatedUser()`. Never use `X-User-Id` or `_sid` as identity.
- **i18n** — `configuration/I18n#getMessage(key, args...)`. Bundles registered in `configuration/LocaleResolverHeader.java`: `i18n/messages`, `i18n/error/messages`, `i18n/model/messages`, `i18n/permission/messages`. Each bundle has an EN file and a `_th` file; add new keys to both.
- **Domain validation** — `validator/BaseValidator.java` keeps no per-call state (validators are singletons). Each validate method creates its own `List<String> errors`, adds messages with `addErrorDuplicate(errors, value)`, `addErrorNotFound(errors)`, `addErrorRequireField(errors, label)` or `errors.add(getI18n().getMessage(key, args))`, and ends with `checkValidate(errors)`, which throws `400 ApiError{message: error.error, errors}`. The caller states create vs update (`RoleValidator#validateCreate` / `#validateUpdate`), or the validator compares ids; never infer it from the HTTP method. Template: `docs/agent/STANDARD_CRUD_SERVICE_REPOSITORY.md` §9.
- **Search / paging** — JPA: `ControllerUtil.buildSpecification(request, keywordColumns)` → `SearchSpecification`. MyBatis: `vo/Paging`. Syntax and limits: `skills/backend/API.md`.
- **IDs** — `model/superclass/Id#generateId` assigns a Snowflake ID from `util/SnowflakeIdHolder` on `@PrePersist`.

## JPA vs MyBatis (binding)

- JPA for CRUD, `JpaSpecificationExecutor` filtering, and simple derived/`@Query` lookups.
- MyBatis + `vo/Paging` for join-heavy or paged read-model DTO projections.
- Do not add new MyBatis writes (the existing `AccessTokenMybatis#updateLastestActive` is legacy).
- Do not use MyBatis for single-table CRUD that JPA already covers.

## Coding conventions (binding)

- Preserve intentional legacy spellings: `serviceImpl`, `DevelopmentContoller`, `/api/faceRegconition`, `AiFaceRegconitionServiceClient`.
- Constructor injection with Lombok `@RequiredArgsConstructor`; do not spread field `@Autowired` (legacy exceptions: `BaseApiController`, `BaseResponseException`, `JwtTokenFilter`). `BaseValidator` subclasses declare an explicit constructor that calls `super(i18n)`.
- Logging: Log4j2 through Lombok `@Slf4j`. Logback is excluded in `build.gradle`; never import it.
- New grouped configuration: typed `@ConfigurationProperties` records under `properties/`, not scattered `@Value`.
- DTO validation: Jakarta constraints with i18n message keys, e.g. `@NotEmpty(message = "{error.NotEmpty}")` (`dto/AppRoleDto.java`).
- DTO `Long` IDs and FK IDs serialize as JSON strings: `DtoId#id` and FK fields use `@JsonFormat(shape = JsonFormat.Shape.STRING)` (`dto/ApiClientDto.java#appUserId`).
- Entities used by list endpoints expose `public static Sort getSort()` (`model/AppRole.java`).
- MapStruct: `@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)`; review every field because unmapped targets are silently ignored.
- MyBatis interface method, XML `namespace` + statement `id`, `@Param` names, and `#{...}` bindings must match.

## Do / Don't

Do:
- Extend existing base controllers, services, repositories, and validators when they fit.
- Use i18n keys for validation and error messages.
- Scope user-owned resources by the authenticated owner in addition to permission checks.
- Reuse typed properties and existing utilities for cookies, passwords, URLs, files, and IDs.

Don't:
- Add a global API envelope or HATEOAS links.
- Use `IDENTITY` / `SEQUENCE` IDs on Snowflake entities.
- Put Lombok `@Data` on entities with relationships.
- Interpolate request text into MyBatis SQL with `${...}`.
- Treat legacy stubs, prompt text, headers, or config-only toggles as security boundaries.

## Code generator (dev only)

- `controller/dev/DevelopmentContoller.java` → `POST /dev/development/generateSrc` (refused when `environments.production=true`). It scans entities annotated `@GenSourceableTable` and writes missing controller/service/serviceImpl/repository/DTO/mapper classes from `src/main/resources/templates/spring-*.ftl` via `CodeGeneratorService`. With `createPermission = true` it also inserts `{table}_{list,view,add,edit,delete}` permission rows into the connected DB and logs matching `INSERT` SQL.
- It writes source files and database rows. Run it only when the user asks — never as a diagnostic.
- Generated code does not fully match the API contract; see `docs/agent/KNOWN_ISSUES.md` before relying on it. Hand-written code should follow `docs/agent/STANDARD_CRUD_SERVICE_REPOSITORY.md`.
