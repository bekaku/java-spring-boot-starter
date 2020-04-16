# Java spring boot rest api

Requirements
------------

Only supported on Java 1.8 and up.

## Usage

### 1. Download this repository
```
git clone https://github.com/bekaku/beka-spring-boot-rest-api-starter my-app
```

Repository will be downloaded into `my-app/` folder
## Database

Database file located at `my-app`/src/main/resources/files/bekaku.sql and you can use following command for restore to your db.

```sql
$ mysql -uroot -p your_db_name < your_backup_file_path
```
example on windows
```sql
$ mysql -uroot -p bekaku_php < E:\bak\db\bekaku.sql
```
example on Ubuntu
```sql
$ mysql -uroot -p bekaku_php < /var/tmp/bekaku.sql
```
default admin username and password
```
Username : admin
Password : P@ssw0rd
```
Config your database connection at `my-app`/src/main/resources/`application.properties`

## Getting started

Open Terminal and run following command 

```
gradle bootRun
```
