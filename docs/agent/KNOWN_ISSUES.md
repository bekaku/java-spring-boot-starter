# Known Issues / Legacy Exceptions

These are observed problems or exceptions. **Do not copy them as preferred patterns.** Read only when touching/debugging the affected area.

**## 11. Known Issues / Legacy Exceptions (do NOT copy as patterns)**

1. `JwtTokenFilter.SKIP_PATHS` blanket-skips `/api/auth/**` while protected account routes live under it — direct controller tests conceal the hole; fix needs route/filter agreement + chain tests.

2. `AppUserMybatis.findAll` and `PermissionMybatis.findAll` contain MySQL pagination; `selectUserData` references `user_role` instead of `app_user_role` — correct + DB-test before reuse.

3. `CustomPermissionEvaluator` stub returns false; `AuthorizationInterceptor` returns true — neither enforces authorization.

4. `DatabaseChatMemory.add/clear` no-ops; `chunk-overlap`/`max-num-chunks` unused; image/video extraction is placeholder text (no OCR/transcription); RAG compensation on commit failure is limited (orphan vectors possible).

5. Caching: `@EnableCaching` + `CacheConfig` (JCache/Ehcache) exist but zero `@Cacheable/@CacheEvict/@CachePut` usages and no `ehcache.xml` — verify named-cache creation before adding annotations. Redis Compose service is not an app-cache integration.

6. Queues: declarations active + non-durable + shared routing-key fan-out, but no active `@RabbitListener` (factory bean only); `AppUserServiceImpl.processAsyncTask` is demo-only; async executor is platform-thread pool despite virtual-thread setting.

7. Deps present but unused: POI (only `FileUtil.hasExcelFormat`), Firebase Admin (only `fcm_token` column + `FcmVo` + `refreshFcmToken`), WebSocket broker/Kafka/Undertow/native-image (disabled), `ChatClientConfig` advisor (disabled), MCP client (config-only in `application-dev.yml`, no `McpClient/ToolCallbackProvider` code).

8. Secrets in plain YAML (`app.encrypt-key`, `jwt.secret`, datasource/rabbitmq/mail/qdrant keys, MCP URL password); `.env` not globally ignored — inspect `git status` before staging config. `environments.production` gates routes separately from `spring.profiles.active`.

9. Storage root publicly mapped (`WebConfigurerAdapter`); `FileManagerController.getImage` path containment is not per-user ownership; chunk merge deletes chunks while copying (non-atomic); `app.cron.clean-old-temp-chunks` gates only temp-chunk cleanup, not all scheduled tasks.

10. Baseline fragility: full test compilation has been blocked when `AuthController.signup` is commented out while `AuthControllerTest` still calls it; ownership/file tests are isolated unit tests, not chain/DB/external-service proof.
