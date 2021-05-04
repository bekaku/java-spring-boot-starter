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

Open Terminal and run following command 

```batch
gradle bootRun
```

To test that it works, open a browser tab at http://localhost:8084/welcome
Or You can test from Postman
```
Content-Type : application/json
Accept-Language : th
```

default admin username and password
```
Username : admin@mydomain.com
Password : 1234
```
