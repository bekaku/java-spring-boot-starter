# 001 — คู่มือทดสอบ Auth Cookie Domain (`app.cookie.domain`)

> Task: `docs/tasks/001-auth-cookie-domain.md`
> สถานะการทดสอบ: **คำสั่ง curl ในเอกสารนี้ยังไม่ได้รันจริง** (ดู §7) — unit tests รันแล้ว ผลอยู่ใน task file
> ใช้ข้อมูลสมมติเท่านั้น (`example.com`, `john`, `web`) — แก้ให้ตรงกับ DB ของคุณก่อนรัน

## 1. ทำไมต้องมี

Cookie ที่ backend ตั้ง (access token, refresh token, current-user) เป็น **host-only** เสมอ — ถ้า frontend
อยู่คนละ host กับ API (เช่น `admin.example.com` เรียก `api.example.com` ตรงๆ) cookie จะเป็นของ
`api.example.com` เท่านั้น เมื่อ F5 / เปิดแท็บใหม่ที่ `admin.example.com` เซิร์ฟเวอร์ frontend (SSR) จะไม่ได้รับ cookie
และผู้ใช้ถูกเด้งไปหน้า login

`app.cookie.domain` (env `APP_COOKIE_DOMAIN`) ทำให้ backend ใส่ `Domain=<ค่านี้>` ให้ทุก cookie ที่ตั้งและที่ล้าง
เพื่อให้ทุก subdomain ของโดเมนนั้นเห็น cookie เดียวกัน

| ค่า | ผลลัพธ์ |
| --- | --- |
| ว่าง / ไม่ตั้ง (ค่าเริ่มต้น) | เหมือนเดิมทุกประการ — ไม่มี `Domain` (host-only) |
| `example.com` หรือ `.example.com` | ทุก `Set-Cookie` มี `Domain=example.com` (ตัดจุดนำหน้าให้) |

ไม่มี endpoint ใหม่ ไม่มีการเปลี่ยน body ของ request/response — เปลี่ยนเฉพาะ attribute ของ `Set-Cookie`

## 2. การตั้งค่า

```yaml
# application-<profile>.yml
app:
  cookie:
    secure: true
    same-site: Lax
    domain: example.com      # หรือ ${APP_COOKIE_DOMAIN:}  (ใน application.yml)
```

หรือใช้ env: `APP_COOKIE_DOMAIN=example.com` (docker: ดูบรรทัดคอมเมนต์ใน `docker-compose.yml`)

กฎของค่า (ตรวจตอน start — ผิดแล้ว app ไม่ขึ้น และ error ระบุ `app.cookie.domain`):

- รับ: hostname ที่มีจุดอย่างน้อย 1 จุด จะมีจุดนำหน้าหรือไม่ก็ได้ (`example.com`, `.example.com`, `api.example.co.th`)
- ไม่รับ: `https://…`, มี `/` หรือ `:port`, มี `*`, มีช่องว่าง, `localhost` (host เดี่ยว), IP address
- ถ้าโดเมนไม่ครอบคลุม host ของ `app.url` จะมี **WARN** ใน log ตอน start (ไม่ทำให้ start ล้ม) —
  เบราว์เซอร์จะทิ้ง cookie ที่ API ตั้ง ถ้าโดเมนไม่ครอบคลุม host ของ API เอง

## 3. ข้อควรรู้ / ข้อแลกเปลี่ยน

- cookie login ถูกส่งไปยัง **ทุก subdomain** ของโดเมนที่ตั้ง (`*.example.com`) — ใช้เมื่อเชื่อถือทุก subdomain เท่านั้น
- frontend กับ API ต้องอยู่ใต้โดเมนหลักเดียวกัน (cross-site จริงๆ ใช้วิธีนี้ไม่ได้)
- frontend ต้องเรียก API แบบส่ง credential (`fetch(..., { credentials: 'include' })` / axios `withCredentials: true`)
  และต้องเพิ่ม origin ของ frontend ใน `app.cors.allowed-origins` (backend เปิด `allowCredentials(true)` อยู่แล้ว)
- ตอนผลิต `Secure` ต้องเป็น `true` (HTTPS) — `app.cookie.secure`; `SameSite` ไม่เปลี่ยนจากเดิม (`Lax`)
  การเรียกจาก `admin.example.com` ไป `api.example.com` เป็น same-site จึงใช้ `Lax` ได้
- cookie แบบ host-only ที่ตั้งไว้ก่อนเปิดใช้ค่านี้จะค้างอยู่จนหมดอายุหรือ logout 1 ครั้ง
  (เบราว์เซอร์เก็บ host-only กับ domain cookie แยกกัน) — ผู้ใช้เดิมควร logout/login ใหม่หลังเปิดใช้
- `loginApi`, `refreshTokenApi`, `logoutApi` คืน token ใน body และ **ไม่ตั้ง cookie** — ไม่ได้รับผลกระทบ

## 4. ทดสอบด้วย curl — ดู `Set-Cookie`

ตัวแปร (แก้ให้ตรง):

```bash
API=http://localhost:8080
CLIENT=web                       # ชื่อ ApiClient ที่มีใน DB (header Accept-Apiclient)
```

### 4.1 Login (`-i` เพื่อดู header, `-c` เก็บ cookie jar)

```bash
curl -i -c /tmp/cj.txt -X POST "$API/api/auth/login" \
  -H 'Content-Type: application/json' \
  -H "Accept-Apiclient: $CLIENT" \
  -H 'User-Agent: curl-test' \
  -d '{"emailOrUsername":"john","password":"<รหัสผ่านทดสอบ>"}'
```

คาดหวัง — ไม่ตั้ง `APP_COOKIE_DOMAIN` (3 บรรทัด **ไม่มี** `Domain`):

```text
Set-Cookie: _session_<uid>=<jwt>; Path=/; Max-Age=...; Expires=...; Secure; HttpOnly; SameSite=Lax
Set-Cookie: _slid_<uid>=<refresh>; Path=/; Max-Age=...; Expires=...; Secure; HttpOnly; SameSite=Lax
Set-Cookie: _sid=<uid>; Path=/; Max-Age=...; Expires=...; Secure; HttpOnly; SameSite=Lax
```

คาดหวัง — `APP_COOKIE_DOMAIN=example.com` (ทั้ง 3 บรรทัดมี `Domain=example.com`):

```text
Set-Cookie: _session_<uid>=<jwt>; Path=/; Domain=example.com; Max-Age=...; Expires=...; Secure; HttpOnly; SameSite=Lax
Set-Cookie: _slid_<uid>=<refresh>; Path=/; Domain=example.com; ...
Set-Cookie: _sid=<uid>; Path=/; Domain=example.com; ...
```

> ชื่อ cookie มาจาก `app.jwt.token-name` / `refresh-token-name` / `current-user-key`
> (ค่าเริ่มต้นใน `application.yml`: `_session_`, `_slid_`, `_sid`; ตรวจ profile ที่ใช้อยู่)
> บน HTTP (`secure: false` ใน dev) จะไม่มี `Secure`

### 4.2 Refresh

```bash
curl -i -b /tmp/cj.txt -c /tmp/cj.txt -X POST "$API/api/auth/refreshToken" \
  -H "Accept-Apiclient: $CLIENT" -H 'User-Agent: curl-test'
```

คาดหวัง: `200` และ `Set-Cookie` 3 บรรทัดชุดใหม่ ตามกฎเดียวกับ §4.1

### 4.3 Logout

```bash
curl -i -b /tmp/cj.txt -c /tmp/cj.txt -X POST "$API/api/auth/logout" \
  -H "Accept-Apiclient: $CLIENT" -H 'User-Agent: curl-test'
```

คาดหวัง: `200` และบรรทัดล้าง cookie ที่มี `Max-Age=0` (+ `Domain=example.com` เมื่อตั้งค่า — ต้อง **ตรงกับตอนตั้ง**
ไม่เช่นนั้นเบราว์เซอร์ไม่ลบ cookie)

```text
Set-Cookie: _session_<uid>=; Path=/; Domain=example.com; Max-Age=0; Expires=...; Secure; HttpOnly; SameSite=Lax
Set-Cookie: _slid_<uid>=; Path=/; Domain=example.com; Max-Age=0; ...
Set-Cookie: _sid=; Path=/; Domain=example.com; Max-Age=0; ...
```

### 4.4 ตรวจว่า cookie ที่ได้จาก `api.` ใช้กับ host อื่นใต้โดเมนเดียวกันได้

curl เองไม่มี DNS ของ `api.`/`admin.` ให้ใช้ `--resolve` ชี้มาที่เครื่องทดสอบ แล้วดูว่า cookie jar มี `Domain` เป็น
`.example.com` (บรรทัดใน `/tmp/cj.txt` ขึ้นต้นด้วย `#HttpOnly_.example.com`)

```bash
curl -i -c /tmp/cj.txt --resolve api.example.com:8080:127.0.0.1 \
  -X POST http://api.example.com:8080/api/auth/login \
  -H 'Content-Type: application/json' -H "Accept-Apiclient: $CLIENT" -H 'User-Agent: curl-test' \
  -d '{"emailOrUsername":"john","password":"<รหัสผ่านทดสอบ>"}'

# ใช้ cookie jar เดิมกับอีก host: ต้องได้ 200 (ถ้า host-only จะไม่ถูกส่ง → 401)
curl -i -b /tmp/cj.txt --resolve admin.example.com:8080:127.0.0.1 \
  -H "Accept-Apiclient: $CLIENT" -H 'User-Agent: curl-test' \
  -X POST http://admin.example.com:8080/api/auth/linkedAccounts
```

> หมายเหตุ: ใช้ HTTP กับ `secure: true` ไม่ได้ (curl จะไม่ส่ง cookie ที่มี `Secure` ผ่าน http) — ใน dev ตั้ง
> `app.cookie.secure=false` ชั่วคราวเมื่อทดสอบแบบนี้

### 4.5 ทดสอบค่าที่ไม่ถูกต้อง (ต้อง start ไม่ผ่าน)

```bash
APP_COOKIE_DOMAIN='https://example.com' ./gradlew bootRun --args='--spring.profiles.active=dev'
```

คาดหวัง: app ไม่ขึ้น และข้อความมี `Invalid app.cookie.domain 'https://example.com'`

## 5. ตรวจใน browser (ยังไม่ได้ทำ) — frontend `admin.example.com`, API `api.example.com`

1. ตั้ง `APP_COOKIE_DOMAIN=example.com`, เพิ่ม `https://admin.example.com` ใน `app.cors.allowed-origins`, `app.cookie.secure=true`
2. Login ที่ `admin.example.com` → DevTools → Application → Cookies ต้องเห็น `_session_*`, `_slid_*`, `_sid` ที่ `Domain=.example.com`
   (HttpOnly ติ๊กทั้งหมด)
3. F5 → ยังอยู่ในระบบ; เปิดแท็บใหม่ → ยังอยู่ในระบบ
4. Logout → F5 → กลับหน้า login และไม่มี cookie ทั้ง 3 ตัวของ `.example.com` เหลือ

## 6. ผลทดสอบอัตโนมัติ (unit — รันแล้ว)

```bash
./gradlew test --tests '*CookieUtilTest' --tests '*CookiePropertiesTest' --tests '*AuthControllerTest'
```

- `CookiePropertiesTest` — normalize/validate + การ bind ผ่าน `Binder` (รวมค่าว่างจาก `${APP_COOKIE_DOMAIN:}` และค่าผิด)
- `CookieUtilTest` — set/clear แบบ host-only และแบบมี domain, clear ใช้ domain เดียวกับ set
- `AuthControllerTest` › `app.cookie.domain with the real CookieUtil` — login (มี/ไม่มี domain) และ logout ผ่าน `CookieUtil` จริง

## 7. สถานะการทดสอบจริง

| รายการ | สถานะ |
| --- | --- |
| Unit tests (ด้านบน) | รันแล้ว — ดูผลใน Completion Report ของ task |
| curl §4.1–4.5 กับ backend ที่รันจริง | **ยังไม่ได้รัน** (ต้องใช้ PostgreSQL + ApiClient + user ทดสอบ) |
| Browser §5 สองโฮสต์จริง + CORS preflight | **ยังไม่ได้รัน** |
