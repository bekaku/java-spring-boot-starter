## Http headers for request

**The header for the endpoint requires login verification.**

| Key                  | Value                            | Description   |
| -------------------- |----------------------------------| --------------|
| Content-Type         | application/json;charset=utf-8   |               |
| Accept-Language      | th                               |       th, en        |
| Accept-ApiClient     | default                          |               |
| Authorization    |Bearer `YOUR_authenticationToken` ||

## ACL(Access control list)

**Access control list example usage**

Just add an annotation `@PreAuthorize("isHasPermission('{PERMISSION_NAME}')")` to method in controller.

```java
package com.bekaku.api.spring.controller.api;

@RequestMapping(path = "/api/appRole")
@RestController
@RequiredArgsConstructor
public class RoleController extends BaseApiController {
    
    @PreAuthorize("isHasPermission('role_list')")
    @GetMapping
    public ResponseEntity<Object> findAll(Pageable pageable) {
    }
    @PreAuthorize("isHasPermission('role_add')")
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody RoleDto dto) {
    }
}
```

**Access is denied response example** :imp:

```json
{
  "status": "INTERNAL_SERVER_ERROR",
  "message": "Access is denied",
  "errors": [
    "error occurred"
  ],
  "timestamp": "2021-05-05 09:03:45"
}
```
## Auto generate starter source

### Create model class

Add an annotation `@GenSourceableTable` to indicate that you want to create Auto source.

```java
package com.bekaku.api.spring.model;

@GenSourceableTable
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Role extends Id {
}
```
### Generate starter source

Call `/dev/development/generateSrc` to auto generate source
```
METHOD : POST
URL : /dev/development/generateSrc
```

The system will generate the following files.

1. `RoleDto` dto Package
2. `RoleRepository` repository Package
3. `RoleService` service Package
4. `RoleServiceImpl` serviceImpl Package
5. `RoleController` api.controller Package
---
