# คู่มือ Deploy API ด้วย Docker Compose (`docker-compose/api-prod`)

> ไฟล์ที่เกี่ยวข้อง: `docker-compose/api-prod/docker-compose.yml`, `docker-compose/api-prod/.env.example`
> สถานะการทดสอบ: ตรวจแล้วว่า `docker compose config` ผ่านและชื่อ env ทุกตัว bind ถูก property —
> **ยังไม่ได้ build image / start container จริงบนเครื่อง production** (ดู §9)

## 1. ภาพรวม

รัน API 3 container เหมือนกัน (`api-1`, `api-2`, `api-3`) หลัง reverse proxy (nginx upstream) ตั้งค่าทั้งหมดผ่านไฟล์ `.env`
ไม่ต้องแก้ `application.yml` และไม่ต้องใส่ secret ลงใน image

| Container | พอร์ต host (default) | `WORKER_ID` | scheduled job | Flyway |
| --- | --- | --- | --- | --- |
| `spring-api-1` | 8081 | 1 | รันได้ (ตามค่าใน `.env`) | รันได้ (`SPRING_FLYWAY_ENABLED`) |
| `spring-api-2` | 8082 | 2 | ปิดเสมอ | ปิดเสมอ |
| `spring-api-3` | 8083 | 3 | ปิดเสมอ | ปิดเสมอ |

- `api-2/3` เริ่มหลัง `api-1` healthy เท่านั้น เพื่อให้ Flyway migrate เสร็จก่อน โค้ดใหม่จึงไม่รันกับ schema เก่า
- `WORKER_ID` ต้องไม่ซ้ำกัน เพราะใช้สร้าง Snowflake ID (`app.worker.id`, ช่วง 0–1023)
- ทั้ง 3 container ใช้ `/usr/spring-data` ร่วมกัน (ไฟล์ที่อัปโหลดผ่านตัวหนึ่งต้องอ่านได้จากตัวอื่น) ส่วน logs แยกโฟลเดอร์ต่อ container

## 2. ขั้นตอนครั้งแรก

```bash
cd docker-compose/api-prod
cp .env.example .env
```

1. แก้ `.env` อย่างน้อย: `HOST_DATA_DIR`, `HOST_LOG_DIR`, `SPRING_DATASOURCE_*`, `SPRING_RABBITMQ_*`, `SPRING_MAIL_*`,
   `APP_URL`, `APP_CORS_ALLOWEDORIGINS`, และ secret ทุกตัวที่ขึ้นต้น `CHANGE_ME_`
2. สร้างโฟลเดอร์บน host และให้สิทธิ์ user ใน container (uid 1001):

```bash
sudo mkdir -p /mnt/cdn/spring-data /mnt/cdn/spring-logs/api-1 /mnt/cdn/spring-logs/api-2 /mnt/cdn/spring-logs/api-3
sudo chown -R 1001:1001 /mnt/cdn/spring-data /mnt/cdn/spring-logs
```

3. สร้าง network (ครั้งเดียว ชื่อตาม `DOCKER_NETWORK`) แล้ว build และ start:

```bash
docker network create docker-network
docker compose build
docker compose up -d
docker compose ps
```

สร้าง secret:

```bash
openssl rand -base64 64   # APP_JWT_SECRET
openssl rand -hex 16      # APP_ENCRYPTKEY (ต้องยาว 16, 24 หรือ 32 ตัวอักษร)
```

> เปลี่ยน `APP_JWT_SECRET` = ผู้ใช้ทุกคนถูก logout, เปลี่ยน `APP_ENCRYPTKEY` = ข้อมูลที่เข้ารหัสด้วยคีย์เก่าถอดไม่ได้

## 3. `.env` override `application.yml` อย่างไร

`env_file: .env` ส่งทุกบรรทัดเข้า container เป็น environment variable จริง และ Spring Boot ให้ env var มี priority สูงกว่า
`application.yml` ค่าที่ไม่ได้ใส่ใน `.env` จึงใช้ค่าใน yml ตามเดิม

ชื่อ env ต้องตรงกับกฎ relaxed binding: **ตัวพิมพ์ใหญ่ + จุดเป็น `_` + ตัด `-` ทิ้ง**

| property ใน yml | env var |
| --- | --- |
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` |
| `spring.datasource.hikari.maximum-pool-size` | `SPRING_DATASOURCE_HIKARI_MAXIMUMPOOLSIZE` |
| `app.jwt.access-token-ttl-minutes` | `APP_JWT_ACCESSTOKENTTLMINUTES` |
| `app.cors.allowed-origins` (list) | `APP_CORS_ALLOWEDORIGINS` (คั่นด้วย `,`) |
| `app.mail-config.tokenExpire` (camelCase, อ่านด้วย `@Value`) | `APP_MAIL_CONFIG_TOKENEXPIRE` |

ข้อควรระวัง:

- **สะกดชื่อผิดแล้วไม่ error** — Spring แค่ไม่ override ค่า ถ้าเพิ่ม env ใหม่ให้ตรวจชื่อตามตารางข้างบน
- **list ถูกแทนที่ทั้งก้อน** — `APP_CORS_ALLOWEDORIGINS` ทิ้ง origin ทั้งหมดใน yml ต้องใส่ origin ของ hybrid app
  (`capacitor://localhost`, `ionic://localhost`, `http(s)://localhost`) ไว้เอง
- **ค่าที่มี `$`** ต้องครอบด้วย single quote (`'...'`) ไม่งั้น compose จะ expand
- **key ที่เป็น case-sensitive** เช่น `mail.smtp.socketFactory.port` ตั้งผ่าน env ไม่ได้ (ดู §4)
- `-D` ใน `ENTRYPOINT` ของ Dockerfile ชนะ env แต่ที่ตั้งไว้มีแค่ `spring.config.additional-location` กับ `spring.profiles.active=prod`

compose ตรวจ 5 ตัวนี้ก่อน start และหยุดถ้าว่าง: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_PASSWORD`, `SPRING_RABBITMQ_PASSWORD`,
`APP_JWT_SECRET`, `APP_ENCRYPTKEY` ส่วนตัวอื่น (เช่น `APP_QUEUE_KEY`, `APP_DEFAULTS_USERPWD`) ถ้าลืมตั้งจะใช้ค่า default ใน yml
เงียบๆ จึงควรตั้งทุกตัวที่เป็น secret

## 4. `/usr/spring-config` (external config)

Dockerfile ใช้ `-Dspring.config.additional-location=/usr/spring-config/` แบบไม่ optional จึงต้อง mount โฟลเดอร์นี้เสมอ
compose mount `${HOST_CONFIG_DIR:-./spring-config}` แบบ read-only (`create_host_path: false`) ซึ่งเป็นโฟลเดอร์ว่างที่ commit ไว้
(`spring-config/.gitkeep`)

ไม่ต้องใส่อะไรลงไปก็ได้ ใช้เมื่อต้องตั้งค่าที่ env ตั้งไม่ได้เท่านั้น เช่น `app.menus`, `app.allow-mimes`,
`spring.mail.properties.mail.smtp.socketFactory.port` (กรณี SMTP ไม่ใช่พอร์ต 465) ค่าใน `.env` ยังชนะไฟล์ในโฟลเดอร์นี้เสมอ
และ `.gitignore` กันไม่ให้ไฟล์ yml ในโฟลเดอร์นี้ถูก commit

## 5. ฐานข้อมูลและ service ภายนอก

- `application.yml` ยัง default เป็น MySQL แต่ `build.gradle` มีแค่ driver PostgreSQL จึงต้องตั้ง
  `SPRING_DATASOURCE_URL` และ `SPRING_DATASOURCE_DRIVERCLASSNAME=org.postgresql.Driver` (มีใน `.env.example` แล้ว)
- เข้าถึง PostgreSQL / RabbitMQ ที่รันบน host ด้วย `host.docker.internal` (compose ใส่ `extra_hosts` ให้แล้ว) หรือใช้ชื่อ container
  ถ้าอยู่ใน `DOCKER_NETWORK` เดียวกัน
- connection pool คิดต่อ container: จำนวน connection รวม = `SPRING_DATASOURCE_HIKARI_MAXIMUMPOOLSIZE` × 3 ต้องน้อยกว่า
  `max_connections` ของ PostgreSQL
- Kafka และ Redis ไม่ได้ใส่ใน `.env.example` เพราะ dependency ของทั้งสองถูก comment ออก/ไม่มีใน `build.gradle`

## 6. Scheduled job และ Flyway

- `app.cron.enable` ใน yml **ไม่มีโค้ดอ่านค่านี้** งานที่ทำงานจริงคือ `FileManagerServiceImpl#cleanupOldTempChunks` ซึ่งคุมด้วย
  `app.cron.clean-old-temp-chunks` (`APP_CRON_CLEANOLDTEMPCHUNKS`) ไม่มี leader election จึงเปิดเฉพาะ `api-1`
- Flyway ปิดเป็น default (ตาม `application.yml`) release ที่มี migration ใหม่ (`db/migration/V*.sql`):

```bash
# 1) ตั้ง SPRING_FLYWAY_ENABLED=true ใน .env
docker compose up -d
docker compose logs -f api-1        # รอจน Flyway migrate เสร็จและ api-1 healthy
# 2) ตั้ง SPRING_FLYWAY_ENABLED=false กลับ แล้วรัน docker compose up -d อีกครั้ง
```

ตรวจ migration บน PostgreSQL ชั่วคราวก่อนเสมอ (ดู `AGENTS.md` §8)

## 7. Deploy เวอร์ชันใหม่ (rolling)

```bash
git pull
docker compose build
docker compose up -d --no-deps api-1      # รอ healthy
docker compose up -d --no-deps api-2
docker compose up -d --no-deps api-3
```

แต่ละ container ปิดแบบ graceful สูงสุด 35 วินาที (`stop_grace_period`) ให้ request ที่ค้างอยู่ทำงานเสร็จ ให้ nginx พัก upstream
ตัวที่กำลัง restart ด้วย

## 8. ตรวจสอบและแก้ปัญหา

```bash
docker compose ps                                   # ต้องเห็น (healthy)
docker compose logs -f api-1
curl -f http://localhost:8081/actuator/health
docker exec spring-api-1 env | grep -E '^(APP_|SPRING_)' | sort   # ดู env ที่เข้า container (มี secret)
```

| อาการ | สาเหตุที่พบบ่อย |
| --- | --- |
| `required variable ... is missing a value` | ยังไม่ได้ตั้ง 5 ตัวที่บังคับใน `.env` หรือ `HOST_DATA_DIR` / `HOST_LOG_DIR` |
| `network docker-network declared as external, but could not be found` | ยังไม่ได้ `docker network create` หรือชื่อไม่ตรง `DOCKER_NETWORK` |
| bind source path does not exist (`spring-config`) | `HOST_CONFIG_DIR` ชี้ไปโฟลเดอร์ที่ไม่มีอยู่ (`create_host_path: false`) |
| `api-2/3` ไม่ start | `api-1` ไม่ healthy — ดู `docker compose logs api-1` (DB, RabbitMQ, Flyway) |
| `api-1` unhealthy ทั้งที่แอปทำงาน | health check ของ SMTP DOWN → ตั้ง `MANAGEMENT_HEALTH_MAIL_ENABLED=false` |
| อัปโหลดไฟล์ไม่ได้ / permission denied | host dir ไม่ได้ `chown 1001:1001` |
| ค่าใน `.env` ไม่มีผล | ชื่อ env สะกดผิดกฎ relaxed binding (§3) |

## 9. ข้อจำกัดที่ควรรู้

- `API_BIND` default `0.0.0.0` ถ้า nginx อยู่เครื่องเดียวกันให้ตั้งเป็น `127.0.0.1` เพราะ `/actuator/**` เข้าถึงได้โดยไม่ต้องล็อกอิน
- `/usr/spring-data` ถูกเปิดสาธารณะที่ `/cdn/**` อย่าเก็บ log หรือ secret ไว้ในนั้น (ดู `skills/backend/FILES.md`)
- ยังไม่ได้ทดสอบ build/start จริงกับ DB และ RabbitMQ ของ production ให้ทดสอบบน staging ก่อน และตรวจ
  `docker compose ps` กับ `/actuator/health` ทุกครั้งหลัง deploy
