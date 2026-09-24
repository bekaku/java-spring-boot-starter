# Standard CRUD Resource — End-to-End Recipe

Step-by-step templates for a **standard CRUD resource**: a JPA entity with a `Long` Snowflake ID and ordinary Spring Data JPA persistence, exposed as admin CRUD under `/api/{modelCamel}`.

Does **not** apply to workflow/action services, ingestion/sync/async orchestration, composite or non-`Long` IDs (e.g. `FilesDirectoryPath`), or specialized persistence. Do not force generic CRUD methods onto those.

Source of truth for the base contracts: `service/BaseService.java`, `repository/BaseRepository.java`, `controller/api/BaseApiController.java`. Worked example: `AppRole*` (but see the notes on its legacy parts below).

## Placeholders

| Placeholder | Meaning | Example |
|---|---|---|
| `{Model}` | entity class | `Product` |
| `{model}` | lower-camel instance name | `product` |
| `{modelCamel}` | route segment | `product` (`/api/product`) |
| `{table}` | snake_case table name, also the permission prefix | `product` |

## Files you will create or touch

```text
src/main/java/com/bekaku/api/spring/
  model/{Model}.java                       1. entity
  dto/{Model}Dto.java                      4. DTO
  mapper/{Model}Mapper.java                5. MapStruct mapper
  repository/{Model}Repository.java        6. repository
  service/{Model}Service.java              7. service interface
  serviceImpl/{Model}ServiceImpl.java      8. service implementation
  validator/{Model}Validator.java          9. (optional) domain validator
  controller/api/{Model}Controller.java   10. controller
src/main/resources/
  db/migration/V{n}__create_{table}_table.sql     2. table + permission seed
  i18n/model/messages{,_th}.properties            3. labels
  i18n/permission/messages{,_th}.properties       3. permission labels
src/test/java/com/bekaku/api/spring/
  controller/api/{Model}ControllerTest.java      11. tests
  serviceImpl/{Model}ServiceImplTest.java        11. tests
```

---

## 1. Entity — `model/{Model}.java`

```java
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "{table}", comment = "...",
        indexes = {@Index(columnList = "deleted"), @Index(columnList = "created_user")})
@SQLDelete(sql = "UPDATE {table} SET deleted = true WHERE id=?")
@SQLRestriction("deleted=false")
public class {Model} extends SoftDeletedAuditable<Long> {

    @Column(name = "name", length = 125, nullable = false, comment = "...")
    private String name;

    @Column(name = "active", comment = "...")
    private Boolean active = true;

    /** Copies only client-writable fields. */
    public void update(String name, Boolean active) {
        this.name = name;
        this.active = active;
    }

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "name");
    }
}
```

- Base class choice: `skills/backend/DATA.md` → *Base classes*. Hard-deleted resource → `Auditable<Long>` and drop `@SQLDelete`/`@SQLRestriction`.
- No `@GeneratedValue`, no `@Data`, relations `LAZY`.

## 2. Migration — `db/migration/V{n}__create_{table}_table.sql`

List `src/main/resources/db/migration/` and use the next free `{n}`. Local dev does not run Flyway (`ddl-auto: update`), so this file is the only deployable schema record.

```sql
CREATE TABLE IF NOT EXISTS {table}
(
    id           bigint       NOT NULL PRIMARY KEY,
    deleted      boolean      DEFAULT false,
    created_user bigint,
    created_date timestamp(6),
    updated_user bigint,
    updated_date timestamp(6),
    name         varchar(125) NOT NULL,
    active       boolean
);
COMMENT ON TABLE {table} IS '...';
COMMENT ON COLUMN {table}.name IS '...';
CREATE INDEX IF NOT EXISTS idx_{table}_deleted ON {table} (deleted);
CREATE INDEX IF NOT EXISTS idx_{table}_created_user ON {table} (created_user);

-- permissions (IDs: see skills/backend-data Recipe D for the generator one-liner)
INSERT INTO permission (id, code, module, description, operation_type) VALUES
  (<id1>, '{table}_list',   '{table}', 'Permission for {table}_list',   'CRUD'),
  (<id2>, '{table}_view',   '{table}', 'Permission for {table}_view',   'CRUD'),
  (<id3>, '{table}_add',    '{table}', 'Permission for {table}_add',    'CRUD'),
  (<id4>, '{table}_edit',   '{table}', 'Permission for {table}_edit',   'CRUD'),
  (<id5>, '{table}_delete', '{table}', 'Permission for {table}_delete', 'CRUD')
ON CONFLICT (code) DO NOTHING;
```

Assign the permissions to a role (`role_permission`) only when the task asks for it.

## 3. i18n labels

Add to both EN and TH files:

```properties
# i18n/model/messages.properties  (+ messages_th.properties)
model.{modelCamel}={Model}
model.{modelCamel}.name=Name

# i18n/permission/messages.properties  (+ messages_th.properties)
permission.{table}_list={Model}(List)
permission.{table}_view={Model}(View)
permission.{table}_add={Model}(Add)
permission.{table}_edit={Model}(Edit)
permission.{table}_delete={Model}(Delete)
```

## 4. DTO — `dto/{Model}Dto.java`

```java
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class {Model}Dto extends DtoId {          // id serialized as a JSON string
    @NotEmpty(message = "{error.NotEmpty}")
    @Size(max = 125, message = "{error.SizeLimitMaxFormat}")
    private String name;
    private Boolean active;

    // FK ids: @JsonFormat(shape = JsonFormat.Shape.STRING) private Long parentId;
}
```

Use separate `{Model}CreateRequest` / `{Model}UpdateRequest` DTOs only when the writable fields differ.

## 5. Mapper — `mapper/{Model}Mapper.java`

```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface {Model}Mapper {
    {Model}Dto toDto({Model} entity);
    {Model} toEntity({Model}Dto dto);
}
```

Unmapped targets are silently ignored — check every field in both directions.

## 6. Repository — `repository/{Model}Repository.java`

```java
@Repository
public interface {Model}Repository extends BaseRepository<{Model}, Long>, JpaSpecificationExecutor<{Model}> {
    Optional<{Model}> findByName(String name);   // only real query needs
}
```

A custom fragment (`{Model}RepositoryCustom` + `repositoryImpl/{Model}RepositoryCustomImpl`) only for JDBC batches, set-based SQL, or locks.

## 7. Service interface — `service/{Model}Service.java`

```java
public interface {Model}Service extends BaseService<{Model}, {Model}Dto> {
    {Model}Dto create({Model}Dto dto);
    {Model}Dto update({Model} entity, {Model}Dto dto);
    Optional<{Model}> findByName(String name);
}
```

## 8. Service implementation — `serviceImpl/{Model}ServiceImpl.java`

```java
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class {Model}ServiceImpl implements {Model}Service {
    private final {Model}Repository {model}Repository;
    private final {Model}Mapper modelMapper;
    private final {Model}Validator validator;          // omit if no domain validation

    // ---- use-case methods -------------------------------------------------

    @Override
    @Transactional
    public {Model}Dto create({Model}Dto dto) {
        {Model} entity = modelMapper.toEntity(dto);
        entity.setId(null);                            // server assigns the Snowflake id
        validator.validate(entity);
        return convertEntityToDto({model}Repository.save(entity));
    }

    @Override
    @Transactional
    public {Model}Dto update({Model} entity, {Model}Dto dto) {
        entity.update(dto.getName(), dto.getActive()); // copy writable fields only; id stays
        validator.validate(entity);
        return convertEntityToDto({model}Repository.save(entity));
    }

    @Override
    public Optional<{Model}> findByName(String name) {
        return {model}Repository.findByName(name);
    }

    // ---- BaseService --------------------------------------------------------

    @Override
    public ResponseListDto<{Model}Dto> findAllWithPaging(Pageable pageable) {
        return getListFromResult({model}Repository.findAll(pageable));
    }

    @Override
    public ResponseListDto<{Model}Dto> findAllWithSearch(SearchSpecification<{Model}> specification, Pageable pageable) {
        return getListFromResult(findAllPageSearchSpecificationBy(specification, pageable));
    }

    @Override
    public ResponseListDto<{Model}Dto> findAllBy(Specification<{Model}> specification, Pageable pageable) {
        return getListFromResult(findAllPageSpecificationBy(specification, pageable));
    }

    @Override
    public Page<{Model}> findAllPageSpecificationBy(Specification<{Model}> specification, Pageable pageable) {
        return {model}Repository.findAll(specification, pageable);
    }

    @Override
    public Page<{Model}> findAllPageSearchSpecificationBy(SearchSpecification<{Model}> specification, Pageable pageable) {
        return {model}Repository.findAll(specification, pageable);
    }

    private ResponseListDto<{Model}Dto> getListFromResult(Page<{Model}> result) {
        return new ResponseListDto<>(result.getContent().stream().map(this::convertEntityToDto).collect(Collectors.toList()),
                result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Override
    public List<{Model}> findAll() {
        return {model}Repository.findAll();
    }

    @Override
    @Transactional
    public {Model} save({Model} {model}) {
        return {model}Repository.save({model});
    }

    @Override
    @Transactional
    public {Model} update({Model} {model}) {
        return {model}Repository.save({model});
    }

    @Override
    public Optional<{Model}> findById(Long id) {
        return {model}Repository.findById(id);
    }

    @Override
    @Transactional
    public void delete({Model} {model}) {
        {model}Repository.delete({model});   // soft delete via @SQLDelete
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        {model}Repository.deleteById(id);
    }

    @Override
    public {Model}Dto convertEntityToDto({Model} {model}) {
        return modelMapper.toDto({model});
    }

    @Override
    public {Model} convertDtoToEntity({Model}Dto dto) {
        return modelMapper.toEntity(dto);
    }
}
```

Every inherited `BaseService` method has real behavior — no `return null;` stubs (`AppRoleServiceImpl` still has three; do not copy them).

## 9. Validator (optional) — `validator/{Model}Validator.java`

Only for cross-row rules (duplicates, references). Field rules stay on the DTO.

```java
@Component
public class {Model}Validator extends BaseValidator {
    private final {Model}Repository repository;

    public {Model}Validator({Model}Repository repository, I18n i18n) {
        super(i18n);                                      // BaseValidator needs I18n: no @RequiredArgsConstructor
        this.repository = repository;
    }

    public void validate({Model} entity) {
        List<String> errors = new ArrayList<>();          // per call: the validator is a singleton
        repository.findByName(entity.getName())
                .filter(found -> !found.getId().equals(entity.getId()))
                .ifPresent(found -> addErrorDuplicate(errors, entity.getName()));
        checkValidate(errors);                            // 400 ApiError{message: error.error, errors}
    }
}
```

- Inject the repository (not the service) to avoid a service ↔ validator cycle.
- Validators are singletons shared by concurrent requests: keep errors in the local list and pass it to the `BaseValidator` helpers (`addErrorDuplicate`, `addErrorNotFound`, `addErrorRequireField`, or `errors.add(getI18n().getMessage(key, args))`). Never store them, or other request data, in a field.
- Comparing ids (not the HTTP method) tells create from update and also works outside a request; §8 clears the id before `create` validates. `RoleValidator` / `UserValidator` expose `validateCreate` / `validateUpdate` instead and let the caller choose.

## 10. Controller — `controller/api/{Model}Controller.java`

```java
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/{modelCamel}")
public class {Model}Controller extends BaseApiController {
    private final {Model}Service service;

    @GetMapping
    @PreAuthorize("@permissionChecker.hasPermission('{table}_list')")
    public ResponseEntity<ResponseListDto<{Model}Dto>> findAll(HttpServletRequest request, Pageable pageable) {
        SearchSpecification<{Model}> specification = ControllerUtil.buildSpecification(request, List.of("name"));
        return responseEntity(service.findAllWithSearch(specification, getPageable(pageable, {Model}.getSort())), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissionChecker.hasPermission('{table}_view')")
    public ResponseEntity<{Model}Dto> findOne(@PathVariable Long id) {
        {Model} found = service.findById(id).orElseThrow(this::responseErrorNotfound);
        return responseEntity(service.convertEntityToDto(found), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("@permissionChecker.hasPermission('{table}_add')")
    public ResponseEntity<{Model}Dto> create(@Valid @RequestBody {Model}Dto dto) {
        return responseEntity(service.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionChecker.hasPermission('{table}_edit')")
    public ResponseEntity<{Model}Dto> update(@PathVariable Long id, @Valid @RequestBody {Model}Dto dto) {
        {Model} found = service.findById(id).orElseThrow(this::responseErrorNotfound);
        return responseEntity(service.update(found, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionChecker.hasPermission('{table}_delete')")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        {Model} found = service.findById(id).orElseThrow(this::responseErrorNotfound);
        service.delete(found);
        return responseDeleteMessage();
    }
}
```

- Thin controller: bind, validate, permission, resolve id → entity, choose status. All writes go through one service call.
- `keywordColumns` (`List.of("name")`) lists the columns `_keyword` searches; use `List.of()` if none.
- Owner-scoped resource (users see only their own rows): add `@AuthenticationPrincipal AppUserDto auth` and use `findByIdAndCreator(id, auth.getId())` instead of `findById` (`backend-security` Recipe B).
- `AppRoleController` keeps permission-assignment logic and two `update` calls in the controller — legacy; new code puts that in the service.

## 11. Tests

Minimum for a new resource (`backend-testing`):

- Controller: `create` returns `201`; `findOne` / `update` / `delete` with a missing id throw `ApiException` with `404` (inject `I18n` into `BaseResponseException` — see `skills/backend/TESTING.md`).
- Service: `create` clears a client-supplied id and saves; `update` copies only writable fields; validator duplicate → `400`.

```bash
./gradlew test --tests '*{Model}ControllerTest' --tests '*{Model}ServiceImplTest'
./gradlew compileJava
```

## 12. Final checks

- [ ] Migration added (no existing migration edited); permissions seeded; i18n in EN + TH.
- [ ] Status codes: `POST 201`, `DELETE 200` message, others `200`.
- [ ] `@PreAuthorize` on every handler with `{table}_*` codes.
- [ ] No `return null;` stubs; writes are `@Transactional` in the service.
- [ ] Tests pass; report what was not verified (PostgreSQL migration, filter chain).

## Using the code generator instead

Only when the user asks: annotate the entity with `@GenSourceableTable`, call `POST /dev/development/generateSrc` on a non-production dev instance, then fix the generated controller before use (it returns raw DTOs with `200` for create and its `update` does not keep the path id — `docs/agent/KNOWN_ISSUES.md`). The generator inserts permission rows into the connected DB only; still add them to a migration.
