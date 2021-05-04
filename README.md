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

To test that it works, open a browser tab at http://localhost:8084/welcome, Or You can test from Postman

---

## API Test
Server run at port `8084` can config server port at /src/main/resources/`application.yml`  
```yml
server:
  port: 8084
```
## 1. Login

```
METHOD : POST
URL : /api/auth/login
```

**Resquest Header**

| Key                  | Value                            | Description   |
| -------------------- |----------------------------------| --------------|
| Content-Type         | application/json;charset=utf-8   |               |
| Accept-Language      | th                               |       th, en        |
| Accept-ApiClient     | default                          |               |

**Resquest Parameter**
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
**Resquest Header after logined**

| Key                  | Value                            | Description   |
| -------------------- |----------------------------------| --------------|
| Content-Type         | application/json;charset=utf-8   |               |
| Accept-Language      | th                               |       th, en        |
| Accept-ApiClient     | default                          |               |
| Authorization    |Bearer `YOUR_authenticationToken` ||

## 2. Permission

### Retrieve data

```
METHOD : GET
URL : /api/permission?page={currentPage}&size={size}&sort={#sortField,#sortType}
EXAMPLE : /api/permission?page=0&size=2&sort=code,asc
```
**Resquest Parameter**

| Key                  | Data type                            | Description   |
| -------------------- |----------------------------------| --------------|
| page         | Int  ||
| size      | Int ||
| sort     | string,string    |exampel `createdDate,asc`|

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
**Resquest Parameter**
```
Json root name : permission
```
| Key                  | Data type                            | Description   |
| -------------------- |----------------------------------| --------------|
| code         | String  ||
| description      | String ||
| module     | String    ||

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
**Resquest Parameter**
```
Json root name : permission
```
| Key                  | Data type                            | Description   |
| -------------------- |----------------------------------| --------------|
| id         | Long  ||
| code         | String  ||
| description      | String ||
| module     | String    ||

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