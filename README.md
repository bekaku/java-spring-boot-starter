# Java spring boot rest api

Requirements
------------

Only supported on Java 1.8 and up.

## Usage

### 1. Download this repository
```
git clone https://github.com/bekaku/java-spring-boot-starter my-app
```

Repository will be downloaded into `my-app/` folder
## Database

Database file located at `my-app`/src/main/resources/files/bekaku.sql and you can use following command for restore to your db.

```sql
$ mysql -uroot -p your_db_name < your_backup_file_path
```
example on windows
```sql
$ mysql -uroot -p your_db_name < E:\bak\db\bekaku.sql
```
example on Ubuntu
```sql
$ mysql -uroot -p your_db_name < /var/tmp/bekaku.sql
```

Config your database connection at `my-app`/src/main/resources/`application.yml`

## Getting started

Open Terminal and run following command 

```
gradle bootRun
```

To test that it works, open a browser tab at http://localhost:8080/welcome
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
