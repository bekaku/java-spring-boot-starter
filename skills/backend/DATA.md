# Data Layer

Read this for JPA entities/repositories, MyBatis, PostgreSQL, Flyway, MapStruct persistence mapping, IDs, audit, or soft-delete work.

**## 6. Data Layer Rules**

- ****IDs:**** all entities extend `model/superclass/Id.java` — Snowflake `Long` assigned in `@PrePersist` only when null (`SnowflakeIdHolder.generator().nextId()`). Never mix with `IDENTITY/SEQUENCE`. `SoftDeletedAuditable<Long>` type param is the auditor type, not PK type.

- ****Audit:**** `SoftDeletedAuditable<U>/Auditable<U>` use `AuditingEntityListener` + `@CreatedBy/@CreatedDate/@LastModifiedBy/@LastModifiedDate` (all `@JsonIgnore`); resolver is `configuration/AuditAwareImpl` reading `AppUserDto` from security context — pass actor ID explicitly in async/Reactor paths. Selective audit-trail logging via `configuration/AuditListener` (`@PrePersist/@PreUpdate/@PreRemove → auditLogService`) enabled per entity with `@EntityListeners(AuditListener.class)` (e.g. `AppRole`, `Permission`).

- ****Soft delete:**** requires all three: `deleted` boolean field (default false, e.g. `SoftDeletedId.java`), entity-level `@SQLDelete` + `@SQLRestriction("deleted=false")` (verify per entity — `FileManager` also nulls FK: `UPDATE file_manager SET deleted=true, files_directory_id=null WHERE id=?`). Base field alone deletes nothing. MyBatis/native SQL must add explicit `deleted=false` + ownership predicates.

- ****Relations:**** prefer `fetch=LAZY`, DTO projections, bounded fetching; check N+1 before adding relations to list responses. Never use Lombok `@Data` on entities with relationships (equality/logging/serialization traversal); nearby entities use proxy-safe identity equality.

- ****Repositories:**** extend `BaseRepository<Entity,Long>` (`@NoRepositoryBean`, `src/main/java/com/bekaku/api/spring/repository/BaseRepository.java`) plus `JpaSpecificationExecutor` where filtering is needed. Example custom: `repository/PermissionRepositoryCustom.java` + `repositoryImpl/PermissionRepositoryCustomImpl.java` (EntityManager + JdbcTemplate + `BeanPropertyRowMapper`).

- ****Flyway:**** location `src/main/resources/db/migration/`, naming `V{version}__{snake_case}.sql`. Only V1–V4 exist in this checkout (`V1__init_current_schema.sql` … `V4__create_unanswered_prompt_log_table.sql`). Add the next migration; never rewrite deployed migrations. Validate PG-specific SQL (casts, vector operators) on disposable PostgreSQL + pgvector — H2 cannot validate them.

- ****MapStruct:**** new mappers go in `mapper/`, name `*Mapper.java`, `@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.IGNORE)` — and because unmapped targets are ignored, review every field intentionally. Separate create/update request DTOs when allowed fields differ.

- ****Permissions data:**** new permissions require explicit permission records + intentional role assignments; a new permission grants nothing by itself. Update UI ACL/localized labels only where the feature needs them.
