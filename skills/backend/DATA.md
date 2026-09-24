# Data Layer — Reference

> **Role:** binding persistence rules + verified facts (WHAT / WHY).
> **Procedure (HOW):** `.agents/skills/backend-data/SKILL.md`.
> **Evidence style:** `path` + `Class#member` (no line numbers; search the symbol).

## Schema management — how it actually runs

- `application.yml`: `spring.flyway.enabled: false`, `spring.jpa.hibernate.ddl-auto: none`.
- `application-dev-example.yml` (template for the ignored `application-dev.yml`): `ddl-auto: update`, and Flyway stays disabled.
- Consequence: in local dev, Hibernate auto-updates the schema from entities and **Flyway does not run**. A migration file is still required for every schema change, because it is the deployable record. The app starting locally does **not** prove the migration is correct.
- Migrations: `src/main/resources/db/migration/V{n}__{snake_case}.sql`. Current tracked set ends at `V6__forgot_password_reset_hardening.sql` — list the directory before picking `n`. Never edit a migration that may already be deployed; add a new one.
- `V1__init_current_schema.sql` is a PostgreSQL dump (uses `COPY ... FROM stdin`, pgvector types). H2 cannot run it.

## IDs (binding)

- Every entity extends `model/superclass/Id` (directly or through a base below). `Id#generateId` (`@PrePersist`) assigns `SnowflakeIdHolder.generator().nextId()` when `id` is null. Never add `@GeneratedValue` / `IDENTITY` / `SEQUENCE`.
- Layout (`configuration/SnowflakeIdGenerator`): `((epochMillis - 1672531200000) << 22) | (workerId << 12) | sequence`. Hand-written seed rows in migrations use IDs of the same shape (see `V3__create_app_user_face_table.sql`).
- The `<U>` type parameter on audit bases is the auditor (user id) type, not the PK type.
- Exception: `FilesDirectoryPath` uses a composite key (`FilesDirectoryPathId`); it is not a standard CRUD entity.

## Base classes (`model/superclass/`)

| Base | Adds columns | Use for |
|---|---|---|
| `Id` | `id` | lookup/log tables without audit |
| `Created` / `CreatedUpdated` | `created_date` (+ `updated_date`) | timestamps without user |
| `AuditableCreated<Long>` / `AuditableUpdated<Long>` | created or updated user + date | partial audit |
| `Auditable<Long>` | `created_user`, `created_date`, `updated_user`, `updated_date` | hard-deleted audited rows (`ApiClient`, `AppUserFace`) |
| `SoftDeletedId` | `id`, `deleted` | soft delete without audit |
| `SoftDeletedAuditable<Long>` | audit columns + `deleted` | the usual choice for business entities (`AppRole`, `AppUser`, `FileManager`) |
| `SoftDeletedAuditableCreated<Long>` / `SoftDeletedAuditableUpdated<Long>` | partial audit + `deleted` | |
| `CodeNameSoftDeletedAuditable` | `SoftDeletedAuditable<Long>` + `code`, `name` | code/name master data |

## Audit

- `@EnableJpaAuditing` on `SpringApiApplication`; `configuration/AuditAwareImpl` (`AuditorAware<Long>`) reads `AppUserDto#id` from the security context. Audit fields are `@JsonIgnore`.
- On async / Reactor / scheduler threads there is no security context → `created_user` becomes `null`. Pass the actor id explicitly and set it, or accept `null` deliberately.
- Row-level audit trail: add `@EntityListeners(AuditListener.class)` (`configuration/AuditListener` → `AuditLogService`). Used by `AppRole`, `Permission`, `Province`, `District`, `SubDistrict`.

## Soft delete (binding)

- All three are required: a `deleted` column (from a `SoftDeleted*` base), `@SQLDelete(sql = "UPDATE {table} SET deleted = true WHERE id=?")`, and `@SQLRestriction("deleted=false")` on the entity.
- The base field alone deletes nothing: `AiDocumentMeta` and `IdentityLink` extend soft-delete bases but their `@SQLDelete` is commented out, so `delete()` is a hard delete.
- `FileManager` also clears an FK: `UPDATE file_manager SET deleted = true, files_directory_id = null WHERE id=?`.
- MyBatis and native SQL bypass `@SQLRestriction`: write `deleted is false` (or `= false`) and the ownership predicate explicitly.

## Entities

- `@Table(name = "...", comment = "...", indexes = {@Index(columnList = "...")})`, `@Column(name = ..., comment = ...)`, `@JoinColumn(name = ..., comment = "FK -> Ref table: x (id). ...")` (`model/AppRole.java`).
- Lombok `@Getter @Setter @NoArgsConstructor` — never `@Data` on entities with relationships. Override `toString()` without relations.
- Relations: `fetch = FetchType.LAZY`; check N+1 before exposing a relation in a list response; prefer DTO projections.
- `public static Sort getSort()` provides the default list order.
- `@GenSourceableTable(...)` marks an entity for the dev code generator (see `CORE.md`).

## Repositories

- `repository/BaseRepository<T, ID>` (`@NoRepositoryBean`, extends `JpaRepository`). Add `JpaSpecificationExecutor<T>` when the entity is listed/filtered.
- Owner-scoped lookup example: `AiChatRepository#findByIdAndCreator` = `@Query("SELECT e FROM AiChat e WHERE e.id = ?1 AND e.createdUser = ?2")`.
- Custom fragment only for real needs (JDBC batch, set-based SQL): `PermissionRepositoryCustom` + `repositoryImpl/PermissionRepositoryCustomImpl`.

## MyBatis (read models only)

- Interface in `mybatis/` annotated `@Mapper`; XML in `src/main/resources/mybatis/` with `namespace` = interface FQCN; statement `id` = method name; `@Param("x")` ↔ `#{x}`.
- PostgreSQL paging: `LIMIT #{page.limit} OFFSET #{page.offset}` (`FileManagerMybatis.xml`). `AppUserMybatis.xml` / `PermissionMybatis.xml` still use MySQL `limit a, b` — do not copy (`KNOWN_ISSUES.md`).
- `${...}` is allowed only for `${page.sortfield} ${page.sortmode}` produced by `BaseApiController#getPaging(pageable, allowList)`. Everything else uses `#{...}`.
- No new MyBatis writes. `AccessTokenMybatis#updateLastestActive` is legacy.

## MapStruct

- `mapper/{Model}Mapper`: `@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)` with `toDto` / `toEntity`. Because unmapped targets are ignored, check every field on both sides.
- Ignore server-owned fields on the way in (`@Mapping(target = "...", ignore = true)`, e.g. `FileManagerMapper`). Use separate create/update request DTOs when the writable fields differ.
- Two mappers exist for `AppRole` (`AppRoleMapper`, `RoleMapper`); `AppRoleServiceImpl` uses `RoleMapper`. New code uses one `{Model}Mapper`.

## Permissions as data

- A permission is a row in `permission (id, code, module, description, operation_type)`; `code` is unique. `operation_type` ∈ `CRUD | REPORT | OTHER | FEATURE`.
- Users get permissions only through `role_permission (app_role, permission)` → `app_user_role`. `PermissionServiceImpl#isHasPermission` has no super-admin bypass: a new permission grants nothing until assigned to a role.
- Seed permissions in a migration (pattern: `V3__create_app_user_face_table.sql`) and add labels `permission.{code}` to `i18n/permission/messages.properties` + `messages_th.properties`.
- The only seeded role in `V1` is `Developer` (`id = 350888314967953409`). Assign new permissions to it only when the task asks for it.
