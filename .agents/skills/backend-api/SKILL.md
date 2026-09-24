---
name: backend-api
description: Use when adding or changing a REST controller, route, request/response DTO, bean validation, domain validator, list/search/paging behavior, or error response in this Spring Boot backend. Pair with backend-data for query changes and backend-security for route access or owner-scoped data.
---

# Backend API — Playbook

> **Role:** HOW to build or change an endpoint. Binding facts + evidence: `skills/backend/API.md` (read it too).
> **Requires:** `backend-core`. **Often paired with:** `backend-data`, `backend-security`, `backend-testing`.

## Use when / skip when

- Use: new or changed endpoint, DTO field, validation rule, list filter/sort/paging, status code, error body.
- Skip: pure persistence change with no contract change (`backend-data` only).

## Recipe A — add an endpoint to an existing controller

1. Open the controller in `controller/api/` and one sibling endpoint that does something similar. Copy its shape.
2. Decide the contract first: method, path, request DTO, response DTO, status (table in `API.md`), permission code, owner scoping.
3. Write the handler:

   ```java
   @PreAuthorize("@permissionChecker.hasPermission('{table}_{action}')")
   @PostMapping("/{id}/archive")
   public ResponseEntity<{Model}Dto> archive(@AuthenticationPrincipal AppUserDto auth,
                                             @PathVariable Long id,
                                             @Valid @RequestBody {Model}ArchiveRequest request) {
       return responseEntity(service.archive(id, request, auth.getId()), HttpStatus.OK);
   }
   ```

4. Put the logic in the service method (`@Transactional` if it writes). The controller only calls it.
5. Not found → `throw responseErrorNotfound();`. Cross-row validation → a `*Validator` (Recipe C).
6. New user-facing text → i18n key in EN + `_th` files.

## Recipe B — new CRUD controller

Follow `docs/agent/STANDARD_CRUD_SERVICE_REPOSITORY.md` (full controller template, permissions, i18n, tests). Do not rely on the code generator's controller template without fixing it (`docs/agent/KNOWN_ISSUES.md`).

## Recipe C — domain validator (duplicates / existence)

```java
@Component
public class {Model}Validator extends BaseValidator {
    private final {Model}Repository repository;

    public {Model}Validator({Model}Repository repository, I18n i18n) {
        super(i18n);                                      // explicit constructor: BaseValidator needs I18n
        this.repository = repository;
    }

    public void validate({Model} entity) {
        List<String> errors = new ArrayList<>();          // local list: validators are singletons
        repository.findByName(entity.getName())
                  .filter(found -> !found.getId().equals(entity.getId()))
                  .ifPresent(found -> addErrorDuplicate(errors, entity.getName()));
        checkValidate(errors);                            // 400 ApiError{message: error.error, errors}
    }
}
```

Never keep errors or request data in a validator field; one bean serves all concurrent requests. Do not decide create vs update from the HTTP method: compare ids as above, or give the validator separate `validateCreate` / `validateUpdate` methods for the caller to choose (`RoleValidator`, `UserValidator`).

## Recipe D — list endpoint

- JPA: `ControllerUtil.buildSpecification(request, List.of("name"))` + `getPageable(pageable, {Model}.getSort())` → `service.findAllWithSearch(spec, pageable)` → `ResponseListDto`.
- MyBatis (joins/projections): `getPaging(pageable, SORT_FIELDS)` with a **non-empty** `private static final List<String> SORT_FIELDS`. See `backend-data` Recipe C.
- Owner-scoped list: pass `auth.getId()` down and filter by owner in the query.

## Done checklist

- [ ] Status codes match the table in `API.md` (`POST` → `201`, `DELETE` → `200` message).
- [ ] `@Valid @RequestBody` on every mutating handler; no class-level `@Validated`.
- [ ] `@PreAuthorize` permission present (or the route is intentionally public in both security configs).
- [ ] Owner-scoped data filtered by `auth.getId()`, not by a header.
- [ ] Response DTO does not expose secrets, hashes, raw tokens, or internal errors; `Long` IDs serialize as strings.
- [ ] Dynamic sort fields are allow-listed.
- [ ] i18n keys added in EN and `_th`.
- [ ] If the contract changed for the external frontend, record it under `External Consumer Impact` in the task file.

## Common mistakes

| Mistake | Fix |
|---|---|
| Returning the DTO directly from `@PostMapping` (implicit `200`) | `return responseEntity(dto, HttpStatus.CREATED);` |
| Business writes or multiple saves in the controller | Move into one `@Transactional` service method |
| Returning `204` on delete | `return responseDeleteMessage();` |
| Wrapping bodies in a new `{data: ...}` envelope | Return the DTO / `ResponseListDto` as-is |
| Reading `X-User-Id` for identity | `@AuthenticationPrincipal AppUserDto auth` |
| Expecting JSON property `isLast` | It serializes as `last` |
