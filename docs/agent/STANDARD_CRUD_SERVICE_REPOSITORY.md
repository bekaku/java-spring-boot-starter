# Standard CRUD Service / Repository Template

Canonical file templates for a **standard CRUD resource**: a JPA model with a
`Long` Snowflake ID whose persistence is ordinary Spring Data JPA.

Scope follows `skills/backend/SKILL.md`: Controller → service interface →
serviceImpl → repository. This contract does **not** apply to workflow/action
services, ingestion/sync/async orchestration, models with non-Long or composite
IDs, or specialized persistence — do not force generic CRUD methods onto those
paths.

Source of truth: `service/BaseService.java`,
`repository/BaseRepository.java`. Worked example: `service/AppRoleService.java`,
`serviceImpl/AppRoleServiceImpl.java`, `repository/AppRoleRepository.java`.
(Do not copy the `null` stubs still present in `AppRoleServiceImpl` for
`findAllBy` / `findAllPageSpecificationBy` /
`findAllPageSearchSpecificationBy` — implement them as below.)

Conventions used below:

- `{Model}` = entity class, `{Model}Dto` = response DTO, `{Model}Mapper` =
  MapStruct mapper with `toDto` / `toEntity`.
- Constructor injection via Lombok `@RequiredArgsConstructor`.
- Class-level `@Transactional(readOnly = true)`; write methods own a
  `@Transactional` boundary.
- `getListFromResult` is a private helper that maps `Page<{Model}>` to the
  established `ResponseListDto` shape.

## Controller

```java
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/{modelName}")
public class {Model}Controller extends BaseApiController {
    private final {Model}Service service;

    @GetMapping
    @PreAuthorize("@permissionChecker.hasPermission('{table_name}_list')")
    public ResponseEntity<ResponseListDto<{Model}Dto>> findAll(HttpServletRequest request, Pageable pageable) {
        SearchSpecification<{Model}> specification = ControllerUtil.buildSpecification(request, List.of());
        return responseEntity(service.findAllWithSearch(specification, getPageable(pageable, {Model}.getSort())), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissionChecker.hasPermission('{table_name}_view')")
    public ResponseEntity<{Model}Dto> findOne(@PathVariable Long id) {
        Optional<{Model}> found = service.findById(id);
        if (found.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return responseEntity(service.convertEntityToDto(found.get()), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("@permissionChecker.hasPermission('{table_name}_add')")
    public ResponseEntity<{Model}Dto> create(@Valid @RequestBody {Model}CreateRequest request) {
        return responseEntity(service.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionChecker.hasPermission('{table_name}_edit')")
    public ResponseEntity<{Model}Dto> update(@PathVariable Long id,
                                             @Valid @RequestBody {Model}UpdateRequest request) {
        return responseEntity(service.update(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionChecker.hasPermission('{table_name}_delete')")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        Optional<{Model}> found = service.findById(id);
        if (found.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        service.delete(found.get());
        return responseDeleteMessage();
    }
}
```

Notes:

- Controller stays thin: it binds/validates requests, checks endpoint
  permission, resolves paging/search/sort and chooses HTTP responses.
  Domain checks and multi-step writes stay in the service.
- Existing example: `controller/api/AppRoleController.java` shows a
  `convertDtoToEntity` + `save`/`update` variant. Treat the template as a
  starting shape; check the current feature's controller and service contract.
  Its permission-assignment and update logic belongs in a service for new work.
- Keep `extends BaseApiController` and reuse its helpers:
  `responseEntity`, `responseErrorNotfound`, `responseDeleteMessage` and
  `getPageable(pageable, {Model}.getSort())`.
- Build search with `ControllerUtil.buildSpecification(request, List.of(...))`;
  pass entity keyword columns (e.g. `List.of("name")`) when the
  resource supports `_keyword`, otherwise `List.of()`.
- Choose permission codes that exist for the feature. Common CRUD resources
  use `{table_name}_{list,view,add,edit,delete}`, while file routes use
  `file_manager_create` instead of `_add`.
- `create` returns `201`; `delete` returns the standard delete message
  (`200` server-message body, not `204`).
- `create`/`update` request types are domain-specific
  (`{Model}CreateRequest` / `{Model}UpdateRequest`); the corresponding
  `create`/`update` methods are declared on the `{Model}Service` interface
  as extra methods alongside `BaseService`.

## Service interface

```java
public interface {Model}Service extends BaseService<{Model}, {Model}Dto> {

}
```

Domain-specific lookups (e.g. `findByName`) are declared here as extra methods.
The interface must extend `BaseService<{Model}, {Model}Dto>`; the controller
delegates to this interface, never directly to a repository.

## Service implementation

```java
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class {Model}ServiceImpl implements {Model}Service {
    private final {Model}Repository {model}Repository;
    private final {Model}Mapper modelMapper;

    @Override
    public ResponseListDto<{Model}Dto> findAllWithPaging(Pageable pageable) {
        Page<{Model}> result = {model}Repository.findAll(pageable);
        return getListFromResult(result);
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
        return new ResponseListDto<>(result.getContent()
                .stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList())
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
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

    @Transactional
    @Override
    public {Model} update({Model} {model}) {
        return {model}Repository.save({model});
    }

    @Override
    public Optional<{Model}> findById(Long id) {
        return {model}Repository.findById(id);
    }

    @Transactional
    @Override
    public void delete({Model} {model}) {
        {model}Repository.delete({model});
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        {model}Repository.deleteById(id);
    }

    @Override
    public {Model}Dto convertEntityToDto({Model} {model}) {
        return modelMapper.toDto({model});
    }

    @Override
    public {Model} convertDtoToEntity({Model}Dto {model}Dto) {
        return modelMapper.toEntity({model}Dto);
    }
}
```

Notes:

- Every inherited `BaseService` operation has real behavior — no `null` placeholder
  returns.
- `save` carries `@Override` like the other interface implementations.
- Field naming follows the existing example (`appRoleRepository`, `modelMapper`);
  adapt the repository field name to the model (`{model}Repository`).

## Repository

```java
public interface {Model}Repository extends BaseRepository<{Model}, Long>, JpaSpecificationExecutor<{Model}> {
}
```

Add derived/finder methods to the interface only for real query needs.
A custom fragment (`*{Repository}Custom` + `*{Repository}Impl`) is composed only
when justified by an implementation need (JDBC batches, set-based upserts,
advisory locks); ordinary lookups stay on the main interface.
