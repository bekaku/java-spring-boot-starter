# Java spring boot rest api

Requirements
------------

Only supported on Java 1.8 and up.

## Usage

### 1. Download this repository
```git
git clone https://github.com/bekaku/java-spring-boot-starter my-app
```

Repository will be downloaded into `my-app/` folder
## Database

Database file located at `my-app`/src/main/resources/files/bekaku.sql and you can use following command for restore to your db.

```batch
$ mysql -uroot -p your_db_name < your_backup_file_path
```
example on windows
```batch
$ mysql -uroot -p your_db_name < E:\bak\db\bekaku.sql
```
example on Ubuntu
```batch
$ mysql -uroot -p your_db_name < /var/tmp/bekaku.sql
```

Config your database connection at `my-app`/src/main/resources/`application.yml`
```yml
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:localhost}:3306/`your_db_name`?allowPublicKeyRetrieval=true&useSSL=false
    username: root
    password: `your_db_password`
```
---
## Getting started

**Project structure**

![image](https://user-images.githubusercontent.com/33171470/116986615-32423a00-acf8-11eb-88f7-db2e44a77b12.png)


Open Terminal and run following command 

```batch
gradle bootRun
```

To test that it works, open a browser tab at http://localhost:8084/welcome

---

## API Docs
Server run at port `8084` can config server port at /src/main/resources/`application.yml`  
```yml
server:
  port: 8084
```
## Login

```
METHOD : POST
URL : /api/auth/login
```

**Request Headers**

| Key                  | Value                            | Description   |
| -------------------- |----------------------------------| --------------|
| Content-Type         | application/json;charset=utf-8   |               |
| Accept-Language      | th                               |       th, en        |
| Accept-ApiClient     | default                          |               |

**Request Parameters**
```
Json root name : user
```
| Key                  | Data type                            | Description   |
| -------------------- |----------------------------------| --------------|
| email         | String  ||
| password      | String ||
| loginForm     | Int    |Login From – 1 : web browser , 2 : iOS, 3 : Android|

```json
{
  "user": {
    "email" : "admin@mydomain.com",
    "password" : "1234",
    "loginForm" : 1
  }
}
```

**Response success example** :tada:
```json
{
  "authenticationToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkZjQwYjMwMC02NGIxLTRiYjAtOWFlMS00ODNhZDBjMGM3MTkiLCJpYXQiOjE2MjAxMjM5MjEsImV4cCI6MTYyMjcxNTkyMX0.u3XIpyePy40AYk_E-F2WqEa_pKSg0FELzukrgwqfPJYQbKnpdxGsdyRhyVxGVEtFfZalU0Xn4ikEHL2Zq1LxGQ",
  "refreshToken": "df40b300-64b1-4bb0-9ae1-483ad0c0c719",
  "expiresAt": "2021-04-14T17:22:34.404501800Z",
  "email": "admin@mydomain.com",
  "username": "admin",
  "image": "https://static.productionready.io/images/smiley-cyrus.jpg"
}
```
**Response fail example** :imp:

```json
{
  "status": "UNAUTHORIZED",
  "message": "Error",
  "errors": [
    "Login failed please verify that your username and password are correct."
  ],
  "timestamp": "2021-05-04 17:19:32"
}
```
---
**The header for the endpoint requires login verification.**

| Key                  | Value                            | Description   |
| -------------------- |----------------------------------| --------------|
| Content-Type         | application/json;charset=utf-8   |               |
| Accept-Language      | th                               |       th, en        |
| Accept-ApiClient     | default                          |               |
| Authorization    |Bearer `YOUR_authenticationToken` ||

## Permission

### Retrieve data

```
METHOD : GET
URL : /api/permission?page={currentPage}&size={size}&sort={#sortField,#sortType}
EXAMPLE : /api/permission?page=0&size=2&sort=code,asc
```
**Request Parameter**

| Key                  | Data type                            | Description   |
| -------------------- |----------------------------------| --------------|
| page         | Int  ||
| size      | Int ||
| sort     | string,string    |example `createdDate,asc`|

**Response success example** :tada:
```json
{
  "dataList": [
    {
      "id": 2,
      "code": "api_client_add",
      "description": "api_client_list_add",
      "module": "AD"
    },
    {
      "id": 5,
      "code": "api_client_delete",
      "description": "api_client_delete",
      "module": "AD"
    }
  ],
  "totalPages": 10,
  "totalElements": 2,
  "last": false
}
```
### Create data

```
METHOD : POST
URL : /api/permission
```
**Request Parameter**
```
Json root name : permission
```
| Key                  | Data type                            | Description   |
| -------------------- |----------------------------------| --------------|
| code         | String  ||
| description      | String ||
| module     | String    ||

**Request example**
```json
{
  "permission": {
    "code": "read_report",
    "description": "user can read report",
    "module": "AD"
  }
}
```

### Update data

```
METHOD : PUT
URL : /api/permission
```
**Request Parameter**
```
Json root name : permission
```
| Key                  | Data type                            | Description   |
| -------------------- |----------------------------------| --------------|
| id         | Long  ||
| code         | String  ||
| description      | String ||
| module     | String    ||

**Request example**
```json
{
  "permision": {
    "id": 2,
    "code": "permission1",
    "description": "permission1_detail",
    "module": "app_user5"
  }
}
```

### Find one

```
METHOD : GET
URL : /api/permission/{id}
EXAMPLE : /api/permission/1
```
**Response success example** :tada:
```json
{
  "id": 1,
  "code": "api_client_list",
  "description": "api_client_list",
  "module": "AD"
}
```
### Delete data

```
METHOD : DELETE
URL : /api/permission/{id}
EXAMPLE : /api/permission/1
```
---

## Role

### Retrieve data

```
METHOD : GET
URL : /api/role?page={currentPage}&size={size}&sort={#sortField,#sortType}
EXAMPLE : /api/role?page=0&size=2&sort=code,asc
```
**Request Parameter**

| Key                  | Data type                            | Description   |
| -------------------- |----------------------------------| --------------|
| page         | Int  ||
| size      | Int ||
| sort     | string,string    |example `createdDate,asc`|

**Response success example** :tada:
```json
{
    "dataList": [
        {
            "id": 1,
            "name": "develop",
            "description": "developer",
            "status": true,
            "selectdPermissions": null
        }
    ],
    "totalPages": 1,
    "totalElements": 1,
    "last": true
}
```
### Create data

```
METHOD : POST
URL : /api/role
```
**Request Parameter**
```
Json root name : role
```
| Key                  | Data type                            | Description   |
| -------------------- |----------------------------------| --------------|
| code         | String  ||
| description      | String ||
| status     | Boolean    ||
| selectdPermissions     | Long[]    | |

**Request example**
```json
{
  "role": {
    "name": "develop",
    "description": "developer 555",
    "status": true,
    "selectdPermissions": [
      1,
      2,
      3,
      4
    ]
  }
}
```

### Update data

```
METHOD : PUT
URL : /api/role
```
**Request Parameter**
```
Json root name : permission
```
| Key                  | Data type                            | Description   |
| -------------------- |----------------------------------| --------------|
| id         | Long  ||
| code         | String  ||
| description      | String ||
| status     | Boolean    ||
| selectdPermissions     | Long[]    | |
**Request example**
```json
{
  "role": {
    "id": 55,
    "name": "develop",
    "description": "developer",
    "status": true,
    "selectdPermissions": [
      1,
      2
    ]
  }
}
```

### Find one

```
METHOD : GET
URL : /api/role/{id}
EXAMPLE : /api/role/1
```
**Response success example** :tada:
```json
{
  "id": 1,
  "name": "develop",
  "description": "developer",
  "status": true,
  "selectdPermissions": null
}
```

### Delete data

```
METHOD : DELETE
URL : /api/role/{id}
EXAMPLE : /api/role/1
```

---

## User
### Create data

```
METHOD : POST
URL : /api/user
```
**Request Parameter**
```
Json root name : userRegister
```
| Key                  | Data type                            | Description   |
| -------------------- |----------------------------------| --------------|
| email         | String  |unique|
| username      | String |unique|
| password     | String    ||
| selectedRoles     | Long[]    ||

```json
{
  "userRegister": {
    "email": "baekaku@gmail.com",
    "username": "baekaku",
    "password": "1234",
    "selectedRoles": [
      1,2
    ]
  }
}
```

**Response fail example** :imp:

```json
{
  "status": "BAD_REQUEST",
  "message": "Error",
  "errors": [
    "Username admin already exists. ",
    "Email admin@mydomain.com already exists. "
  ],
  "timestamp": "2021-05-05 09:02:24"
}
```
---
## ACL(Access control list)

**Access control list example usage**

Just add annotation `@PreAuthorize("isHasPermission('{PERMISSION_NAME}')")` to method in controller.


```java
package io.beka.controller.api;

@RequestMapping(path = "/api/role")
@RestController
@RequiredArgsConstructor
public class RoleController extends BaseApiController {
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final I18n i18n;
    private final RoleValidator roleValidator;

    Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Value("${app.loging.enable}")
    boolean logEnable;

    @PreAuthorize("isHasPermission('role_list')")
    @GetMapping
    public ResponseEntity<Object> findAll(Pageable pageable) {
        logger.info("/api/role > findAll, isLogEnable {}", logEnable);
        return this.responseEntity(roleService.findAllWithPaging(!pageable.getSort().isEmpty() ? pageable :
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Role.getSort())), HttpStatus.OK);
    }

    @PreAuthorize("isHasPermission('role_add')")
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody RoleDto dto) {
        Role role = roleService.convertDtoToEntity(dto);
        roleValidator.validate(role);
        setRolePermission(dto, role);
        roleService.save(role);
        return this.responseEntity(roleService.convertEntityToDto(role), HttpStatus.CREATED);
    }

    private void setRolePermission(RoleDto dto, Role role) {
        if (dto.getSelectdPermissions().length > 0) {
            Optional<Permission> permission;
            for (long permissionId : dto.getSelectdPermissions()) {
                permission = permissionService.findById(permissionId);
                permission.ifPresent(value -> role.getPermissions().add(value));
            }
        }
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
package io.beka.model;

import io.beka.annotation.GenSourceableTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@GenSourceableTable
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Role extends BaseEntity {

    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "description", columnDefinition = "text default null")
    private String description;

    @Column(name = "status", columnDefinition = "tinyint(1) default 1")
    private Boolean status;

    public static Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "name");
    }
}
```

### Generate starter source

Call `/dev/generateSrc` to auto generate source
```
METHOD : POST
URL : /dev/generateSrc
```

The system will generate the following files.

1. `RoleDto` dto Package
2. `RoleRepository` repository Package
3. `RoleService` service Package
4. `RoleServiceImpl` serviceImpl Package
5. `RoleController` api.controller Package
---