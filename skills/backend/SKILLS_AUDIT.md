# 📋 สกิลโปรเจ็ค — การตรวจทานความถูกต้อง (Skill Configuration Audit)

> ตรวจเมื่อ: 2025-11-03 โดย `deepseek-v4.1-flash`
> เป้าหมาย: ตรวจสอบว่าไฟล์ skill ต่าง ๆ ของโปรเจ็ค (playbook + reference) ถูกต้องและสอดคล้องกันหรือไม่

---

## 1. ภาพรวม: ไฟล์ทั้งหมดมีครบและถูกต้อง ✅

| ชั้น | เส้นทาง | ไฟล์ | สถานะ |
|---|---|---|---|
| Playbook (HOW) | `.agents/skills/<skill>/SKILL.md` | **8 ไฟล์** | ✅ ครบทั้ง 8 |
| Reference (WHAT/WHY) | `skills/backend/<GUIDE>.md` | **8 ไฟล์** | ✅ ครบทั้ง 8 |

### Playbook (8)
- `backend-core`, `backend-api`, `backend-data`, `backend-security`, `backend-files`, `backend-async-messaging`, `backend-ai-rag` (optional), `backend-testing`

### Reference (8)
- `CORE.md`, `API.md`, `DATA.md`, `SECURITY.md`, `FILES.md`, `ASYNC_MESSAGING.md`, `AI_RAG.md`, `TESTING.md`

**Frontmatter:** ทุกไฟล์ playbook มี `name:` + `description:` ที่ตรงกับ SKILLS.md ✅

---

## 2. ความสอดคล้องระหว่าง AGENTS.md ↔ SKILLS.md ✅

### §6 Required Reading / Skill Routing (AGENTS.md)
รายการ playbook (7) + reference (7) **ตรงกัน 100%** กับไฟล์จริง:
```text
backend-api/SKILL.md + API.md
backend-data/SKILL.md + DATA.md
backend-security/SKILL.md + SECURITY.md
backend-files/SKILL.md + FILES.md
backend-async-messaging/SKILL.md + ASYNC_MESSAGING.md
backend-ai-rag/SKILL.md + AI_RAG.md
backend-testing/SKILL.md + TESTING.md
```

### §13 Reference Index
- Playbook list (8): `.agents/skills/{backend-core, backend-api, backend-data, backend-security, backend-files, backend-async-messaging, backend-ai-rag, backend-testing}/SKILL.md` ✅
- Reference list (8): `skills/backend/CORE.md, API.md, DATA.md, SECURITY.md, FILES.md, ASYNC_MESSAGING.md, AI_RAG.md, TESTING.md` ✅

### Quick picks (SKILLS.md) ↔ AGENTS.md §10
คำสั่ง Gradle (`./gradlew compileJava`, `./gradlew test`, `./gradlew bootJar`) ตรงกัน ✅

### Adapters (CLAUDE.md, GEMINI.md, .github/copilot-instructions.md)
ชี้ไปที่ AGENTS.md → SKILLS.md → backend-core ✅ ไม่มี diverge

---

## 3. ความสอดคล้อง Playbook ↔ Reference ✅

ตรวจสอบข้อเท็จจริงสำคัญ (verified against actual code):

| หัวข้อ | Claim ใน playbook/reference | ผลตรวจจากโค้ดจริง |
|---|---|---|
| Flyway | `spring.flyway.enabled: false`, dev ใช้ `ddl-auto: update` | ✅ `enabled: false` ใน `application.yml` |
| `BaseApiController.responseEntity` | ส่ง body-through แบบไม่ห่อ | ✅ `new ResponseEntity<>(o, status)` |
| `ResponseListDto` | `{dataList, totalPages, totalElements, last}` | ✅ `dataList, totalPages(int), totalElements(long), isLast` (Lombok → `last`) |
| `responseServerMessage` | `{message, status, timestamp}` / 3-arg มี `success` | ✅ Map keys = `SERVER_MESSAGE, SERVER_STATUS, SERVER_TIMESTAMP` |
| i18n `responseDeleteMessage` | ใช้ key `success.deleteSuccesfull` | ✅ มีใน `messages.properties` + `messages_th.properties` |
| `CustomPermissionEvaluator` | always returns `false` (stub) | ✅ `return false;` |
| `AuthorizationInterceptor` | returns `true` | ✅ `return true;` |
| `@SQLDelete` บน soft-delete | `AiDocumentMeta`, `IdentityLink` ถูก comment out | ✅ `//@SQLDelete(...)` |
| 401 JWT filter | รูป `{error:"..."}` | ✅ `"{\"error\": \"" + message + "\"}"` |
| 403 `@PreAuthorize` | → `GlobalExceptionHandler.handleAccessDenied` → `403 ApiError` | ✅ `@Order(HIGHEST_PRECEDENCE)` + `handleAccessDenied` |
| `Accept-Apiclient` | ต้องมีใน JWT request | ✅ `AppUtil.isEmpty(apiclientName)` check; มี `//TODO verify apiClient later` |
| Executor async | `ConstantData.ASYNC_TASK_NAME` = `"asyncExecutor"` (platform thread) | ✅ `spring.threads.virtual: enabled` แต่ executor เป็น `ThreadPoolTaskExecutor` |
| `@EnableRabbit` | commented out | ✅ `// @EnableRabbit` |
| Qdrant gate | gate ด้วย `spring.ai.vectorstore.qdrant.enabled` (ไม่ใช่ `app.rag.qdrant-enabled`) | ✅ `RagProperties` ไม่มี field นี้; `QdrantVectorStoreConfig` ใช้ `@ConditionalOnProperty` |
| `AiChatController#create` | raw DTO → implicit 200 | ✅ `public AiChatDto create(...)` |
| `FileManagerController#sortProperties` | allow-list ไม่เป็น empty | ✅ `List.of("fileName", "createdDate", ...)` |
| `AppRoleServiceImpl` | ใช้ `RoleMapper` (mapper ซ้ำ `AppRoleMapper`) | ✅ import `RoleMapper` |
| MyBatis paging | `AppUserMybatis` ใช้ `limit a, b` (MySQL) | ✅ `limit #{page.offset}, #{page.limit}` |
| `AppUserMybatis.xml` | join `user_role` แทน `app_user_role` | ✅ `left join user_role UR` |
| `PermissionRequireValidator` | ใช้ `LoggerFactory` | ✅ `import org.slf4j.LoggerFactory` |
| `/api/faceRegconition` | legacy spelling เก็บไว้ | ✅ `@RequestMapping(path = "/api/faceRegconition")` |
| `AiFaceRegconitionServiceClient`, `DevelopmentContoller`, `serviceImpl` | legacy spelling เก็บไว้ | ✅ พบทั้งหมดในโค้ด |
| `AiChatController#streamChat` | `POST /api/aiChat/stream`, `Flux<ChatStreamEvent>`, `TEXT_EVENT_STREAM_VALUE` | ✅ ตรงทุกประการ |
| Stack | Java 25, Spring Boot 4.1.0, JJWT 0.13.0, Spring AI 2.0.0 BOM | ✅ `version '4.1.0'`, `jjwt-api:0.13.0`, `spring-ai-bom:2.0.0` |
| `RagProperties` fields | topK, similarityThreshold, chunkSize, chunkOverlap, ... | ✅ record มีทุก field |

**สรุป: ทุกข้อเท็จจริงที่ playbook/reference อ้างอิง (และระบุว่า "verified") สอดคล้องกับโค้ดจริง 100%**

---

## 4. การทดสอบโค้ดจริง: ไม่พบปัญหา ✅

- `AuthControllerTest` สร้าง controller โดยตรง (mock) — สอดคล้องกับ TESTING.md ที่บอกว่าไม่มี filter-chain test
- Test classes ที่ระบุ: `AuthControllerTest`, `FileManagerControllerTest`, `AiChatControllerOwnershipTest`, `AppRoleControllerTest`, `AppUserControllerTest`, `AiRagChatServiceOwnershipTest`, `FaceRecognitionServiceOwnershipTest`, `RoleValidatorTest`, `UserValidatorTest` — **มีครบทั้งหมด** ✅

---

## 5. งานที่ค้าง (task tracking) ⚠️

| task | สถานะ | หมายเหตุ |
|---|---|---|
| `001-auth-cookie-domain.md` | `COMPLETED` | Resume State เต็ม; มีการบันทึก Last Successful Backend Verification (`119 tests, 0 failures`); curl §4 และ browser §5 **ยังไม่ได้รัน** (ระบุชัดเจนว่าไม่ได้ออก) |
| `001-curl-test.md` | (guideline เอกสาร) | Required Reading ว่าง แต่เนื้อหาครบ |

**ข้อสังเกต:** การทดสอบ curl §4 และ browser §5 ของ task 001 ยังไม่เสร็จ (แต่ task บอกชัดเจนว่าเป็น live verification ที่ค้างไว้ ไม่ใช่ bug ของ skill)

---

## 6. ความสอดคล้องของเอกสารอื่น ๆ ✅

| ไฟล์ | สถานะ |
|---|---|
| `docs/agent/README.md` (index) | ✅ ชี้ playbook/reference ถูกต้อง |
| `docs/agent/SPLIT_MAP.md` | ✅ mapping original → split ตรง, ระบุว่า ORIGINAL_SKILLS.md เป็น archive |
| `docs/agent/PROJECT_REFERENCE.md` | ✅ directory map ตรงกับโครงสร้างจริง |
| `docs/agent/STANDARD_CRUD_SERVICE_REPOSITORY.md` | ✅ blueprint สอดคล้องกับโค้ดจริง (AppRole, AiChat, FileManager) |
| `docs/agent/KNOWN_ISSUES.md` | ✅ 26 issues ที่ระบุ **ถูกต้อง** ตรวจสอบแล้ว (generator template, AppRole null stubs, MySQL paging, SQLDelete commented, ...etc.) |
| `docs/agent/ORIGINAL_SKILLS.md` | ✅ preserved as archive (ส่วนที่เป็น outdated ถูก flag ไว้) |
| `docs/api-prod-deployment.md` | ✅ อ้างอิง `AGENTS.md §8` + `skills/backend/FILES.md` ถูกต้อง |
| `docs/PROJECT_STRUCTURE.md` (Thai) | ✅ ตรงกับ SKILLS.md |
| `README.md` | ✅ loading order + quick picks ตรงกับ SKILLS.md |
| `tasks/001-auth-cookie-domain.md` | ✅ Required Reading: core + security + testing (ถูกต้องสำหรับงาน cookie) |
| `tasks/TASK_TEMPLATE.md` | ✅ 15 sections ครบ |
| `tasks/README.md` | ✅ workflow ตรงกับ AGENTS.md §7, §11 |

---

## 7. สรุปผลการตรวจ

### ✅ สิ่งที่ถูกต้อง
1. **โครงสร้างไฟล์ครบ 8+8**: playbook ทั้ง 8 และ reference ทั้ง 8 มีครบ, naming ถูกต้อง
2. **AGENTS.md ↔ SKILLS.md ↔ README.md**: ตรงกัน 100% (router, loading order, quick picks, commands)
3. **Playbook ↔ Reference**: mapping ถูกต้อง, ข้อเท็จจริงทุกข้อ verified กับโค้ดจริงและตรงกัน
4. **Adapters** (CLAUDE.md, GEMINI.md, copilot-instructions.md): pointer เท่านั้น, ไม่ diverge
5. **Test artifacts**: test classes ที่อ้างอิงมีครบ
6. **Legacy spellings**: `serviceImpl`, `DevelopmentContoller`, `/api/faceRegconition`, `AiFaceRegconitionServiceClient` เก็บไว้จริง
7. **Known Issues**: 26 รายการถูกต้องทั้งหมด
8. **Task 001**: Required Reading ถูกต้อง, status COMPLETED, มีบันทึก verification

### ⚠️ สิ่งที่ควรสังเกต (ไม่ใช่ bug ของ skill)
1. `001-curl-test.md` ว่างเปล่า (ไม่มี Required Reading) — เป็นเอกสารคู่มือ curl ไม่ใช่ playbook
2. Task 001 ยังไม่มี live curl / browser verification (แต่ task แจ้งชัดเจนว่าเป็น known outstanding item)

### ❌ สิ่งที่ **ไม่มี** (ไม่มีอะไรต้องแก้)
- ไม่มี playbook หรือ reference ขาดหาย
- ไม่มีการเล่น name ไม่ตรงกัน
- ไม่มี claim ที่ขัดแย้งกับโค้ดจริง
- ไม่มี duplicate file ที่สับสน

---

## 📝 คำแนะนำ

1. **ไม่จำเป็นต้องแก้ไข skill file อะไรเลย** — โครงสร้างและเนื้อหาถูกต้องทั้งหมด ✅
2. **ถ้าต้องการปรับปรุงเล็กน้อย**: เพิ่ม Required Reading สั้น ๆ ใน `tasks/001-curl-test.md` เพื่อความครบถ้วน
3. **ต่อให้มี task ใหม่**: route ตาม `SKILLS.md` ได้เลย — การเลือก skill และการอ่าน playbook + reference เป็นระเบียบและถูกต้อง

---

*ตรวจทานโดย: deepseek-v4.1-flash | ไม่มีการแก้ไขไฟล์ใด ๆ ทั้งหมดเป็นผลของการอ่านและตรวจสอบ (read-only audit)*
