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
Json root name : appUser
```

| Key                  | Data type                            | Description   |
| -------------------- |----------------------------------| --------------|
| email         | String  ||
| password      | String ||
| loginForm     | Int    |Login From – 1 : web browser , 2 : iOS, 3 : Android|

```json
{
  "appUser": {
    "email": "admin@mydomain.com",
    "password": "1234",
    "loginForm": 1
  }
}
```

**Response success example** :tada:

```json
{
  "authenticationToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
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
## Logout

```
METHOD : POST
URL : /api/auth/logout
```

## Refresh token

**Request Parameters**

```
Json root name : refreshToken
```

| Key                  | Data type                            | Description   |
| -------------------- |----------------------------------| --------------|
| refreshToken         | String  ||
| email      | String ||

```json
{
  "refreshToken": {
    "refreshToken": "1c63e9db-5afe-4e26-8100-c9113334f790",
    "email": "admin@mydomain.com"
  }
}
```

## Remove login session

```
METHOD : GET
URL : /api/auth/removeAccessTokenSession/{id}
EXAMPLE : /api/auth/removeAccessTokenSession/1
```

**Response success example** :tada:

```json
{
  "authenticationToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1YWRiNjc5NC1jZjdlLTQ5MjQtOWZiNi01NzExNmYyNDA0MGMiLCJpYXQiOjE2NTk1MTMwOTcsImV4cCI6MTY5MTA0OTA5N30.l1dF0CIY4KK1JjaLmm6Nkv_ge_IUcvcmyBcPFIbQjXs",
  "refreshToken": "5adb6794-cf7e-4924-9fb6-57116f24040c",
  "expiresAt": "2023-08-03T07:51:37.084+00:00"
}
```
