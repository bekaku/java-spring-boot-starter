---
name: backend-data
description: Use when adding or changing a JPA entity, column, relation, repository query, MyBatis read model, Flyway migration, MapStruct mapper, Snowflake ID, audit field, soft delete, or permission seed data in this Spring Boot backend.
---

# Backend Data — Playbook

> **Role:** HOW to change persistence safely. Binding facts + evidence: `skills/backend/DATA.md` (read it too).
> **Requires:** `backend-core`. **Often paired with:** `backend-api`, `backend-security`, `backend-testing`.

## Know this first

- Local dev runs Hibernate `ddl-auto: update` and **Flyway is disabled**. Your app starting locally proves nothing about the migration. You must still write the migration.
- IDs are Snowflake `Long` from `Id#generateId`. Never add `@GeneratedValue`.
- JPA = CRUD, filters, simple lookups. MyBatis = joined/paged DTO read models only. No new MyBatis writes.

## Recipe A — add a column to an existing entity

1. Add the field to `model/{Model}.java` with `@Column(name = "snake_case", comment = "...")`.
2. Add a migration `V{next}__add_{column}_to_{table}.sql` (list `src/main/resources/db/migration/` for `{next}`):

   ```sql
   ALTER TABLE {table} ADD COLUMN IF NOT EXISTS {column} character varying(255);
   COMMENT ON COLUMN {table}.{column} IS '...';
   ```

3. Add the field to the DTO (with validation + i18n message key) and check the mapper maps it in both directions.
4. If the column is searchable by `_keyword`, add it to the controller's `buildSpecification(request, List.of(...))`.

## Recipe B — add a new entity (table)

1. Choose the base class from the table in `DATA.md` (usually `SoftDeletedAuditable<Long>`).
2. Write the entity:

   ```java
   @GenSourceableTable(createPermission = false)   // optional: only if the dev generator should see it
   @NoArgsConstructor @Getter @Setter
   @Entity
   @Table(name = "{table}", comment = "...",
          indexes = {@Index(columnList = "deleted"), @Index(columnList = "created_user")})
   @SQLDelete(sql = "UPDATE {table} SET deleted = true WHERE id=?")
   @SQLRestriction("deleted=false")
   public class {Model} extends SoftDeletedAuditable<Long> {
       @Column(name = "name", length = 125, nullable = false, comment = "...")
       private String name;

       @ManyToOne(fetch = FetchType.LAZY)
       @JoinColumn(name = "parent_id", comment = "FK -> Ref table: parent (id). ...")
       private Parent parent;

       public static Sort getSort() { return Sort.by(Sort.Direction.ASC, "name"); }
   }
   ```

3. Migration `V{next}__create_{table}_table.sql`: `CREATE TABLE` with `id bigint NOT NULL PRIMARY KEY`, the audit columns of the base class, `deleted boolean DEFAULT false`, FKs, indexes, and `COMMENT ON` lines (copy the style of `V3__create_app_user_face_table.sql`).
4. If the entity has admin CRUD: seed permissions (Recipe D).
5. Continue with the repository/service/controller in `docs/agent/STANDARD_CRUD_SERVICE_REPOSITORY.md`.

## Recipe C — add a MyBatis read model (joins / projections)

1. `mybatis/{Model}Mybatis.java`:

   ```java
   @Mapper
   public interface {Model}Mybatis {
       List<{Model}Dto> findAllByOwner(@Param("page") Paging page, @Param("ownerId") Long ownerId);
   }
   ```

2. `src/main/resources/mybatis/{Model}Mybatis.xml` — `namespace` = the interface FQCN, `id` = method name:

   ```xml
   <select id="findAllByOwner" resultType="com.bekaku.api.spring.dto.{Model}Dto">
       SELECT t.id, t.name, p.name AS parentName
       FROM {table} t LEFT JOIN parent p ON p.id = t.parent_id
       WHERE t.deleted is false AND t.created_user = #{ownerId}
       <if test="page.sortfield != null">ORDER BY ${page.sortfield} ${page.sortmode}</if>
       LIMIT #{page.limit} OFFSET #{page.offset}
   </select>
   ```

3. The controller builds `Paging` with `getPaging(pageable, SORT_FIELDS)` and a non-empty allow-list. Every other value uses `#{...}`.
4. Write `deleted is false` and the owner predicate yourself — `@SQLRestriction` does not apply to MyBatis.

## Recipe D — seed permissions for a new resource

1. Generate IDs (one per row, same Snowflake shape as the app):

   ```bash
   python3 -c "import time; b=(int(time.time()*1000)-1672531200000)<<22; print('\n'.join(str(b+i) for i in range(5)))"
   ```

2. In the table's migration (or a new one):

   ```sql
   INSERT INTO permission (id, code, module, description, operation_type) VALUES
     (<id1>, '{table}_list',   '{table}', 'Permission for {table}_list',   'CRUD'),
     (<id2>, '{table}_view',   '{table}', 'Permission for {table}_view',   'CRUD'),
     (<id3>, '{table}_add',    '{table}', 'Permission for {table}_add',    'CRUD'),
     (<id4>, '{table}_edit',   '{table}', 'Permission for {table}_edit',   'CRUD'),
     (<id5>, '{table}_delete', '{table}', 'Permission for {table}_delete', 'CRUD')
   ON CONFLICT (code) DO NOTHING;
   ```

3. Only if the task asks for it, grant them to a role:

   ```sql
   INSERT INTO role_permission (app_role, permission)
   SELECT 350888314967953409, id FROM permission WHERE module = '{table}'
   ON CONFLICT DO NOTHING;
   ```

4. Add labels to `i18n/permission/messages.properties` and `messages_th.properties`: `permission.{table}_list=...` etc.

## Done checklist

- [ ] Every schema change has a new `V{n}__*.sql`; no existing migration was edited.
- [ ] Entity uses the right base; soft-delete entities have both `@SQLDelete` and `@SQLRestriction`.
- [ ] No `@GeneratedValue`, no `@Data` on entities with relations, relations are `LAZY`.
- [ ] Mapper reviewed field by field (unmapped targets are silently ignored).
- [ ] MyBatis SQL has `deleted is false` + owner predicate; only allow-listed sort uses `${}`.
- [ ] Writes happen in a `@Transactional` service method.
- [ ] PostgreSQL-specific SQL was validated on real PostgreSQL, or the report says it was not (`backend-testing`).

## Common mistakes

| Mistake | Fix |
|---|---|
| "It started locally, so the migration works" | Dev uses `ddl-auto: update`; validate the SQL on disposable PostgreSQL or say it was not validated |
| Editing `V1`–`V6` | Add `V{next}` |
| `limit #{offset}, #{limit}` (MySQL) | `LIMIT #{page.limit} OFFSET #{page.offset}` |
| Relying on `deleted` column alone | Add `@SQLDelete` + `@SQLRestriction` |
| New permission but nobody can use it | Assign via `role_permission` (only when asked) — there is no admin bypass |
| `ON CONFLICT (id)` on permission seeds | `ON CONFLICT (code)` — `code` is the unique business key |
