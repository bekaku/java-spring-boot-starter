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

## Getting started

**Project structure**

![image](https://user-images.githubusercontent.com/33171470/116986615-32423a00-acf8-11eb-88f7-db2e44a77b12.png)


Open Terminal and run following command 

```batch
gradle bootRun
```

To test that it works, open a browser tab at http://localhost:8084/welcome, Or You can test from Postman


## API Test

### 1. Login

```
METHOD : POST
URL : http://localhost:8084/api/auth/login
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
Body : raw JSON
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
  "user": {
    "email" : "admin@mydomain.com",
    "password" : "1234",
    "loginForm" : 1
  }
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