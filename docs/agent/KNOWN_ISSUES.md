# Known Issues / Legacy Exceptions

These are observed problems or exceptions. **Do not copy them as preferred patterns.** Read only when touching/debugging the affected area.

## 11. Known Issues / Legacy Exceptions (do NOT copy as patterns)

1. `JwtTokenFilter.SKIP_PATHS` now lists specific public auth paths, while protected account routes under `/api/auth/**` pass through the filter. Keep that list aligned with `WebSecurityConfig`; direct controller tests cannot prove chain enforcement.

2. `AppUserMybatis.findAll` and `PermissionMybatis.findAll` contain MySQL pagination; `selectUserData` references `user_role` instead of `app_user_role` — correct + DB-test before reuse.

3. `CustomPermissionEvaluator` stub returns false; `AuthorizationInterceptor` returns true — neither enforces authorization.

4. `DatabaseChatMemory.add/clear` no-ops; `chunk-overlap`/`max-num-chunks` unused; image/video extraction is placeholder text (no OCR/transcription); RAG compensation on commit failure is limited (orphan vectors possible).

5. Caching: `@EnableCaching` + `CacheConfig` (JCache/Ehcache) exist but zero `@Cacheable/@CacheEvict/@CachePut` usages and no `ehcache.xml` — verify named-cache creation before adding annotations. Redis Compose service is not an app-cache integration.

6. Queues: declarations active + non-durable + shared routing-key fan-out, but no active `@RabbitListener` (factory bean only); `AppUserServiceImpl.processAsyncTask` is demo-only; async executor is platform-thread pool despite virtual-thread setting.

7. Deps present but unused: POI (only `FileUtil.hasExcelFormat`), Firebase Admin (only `fcm_token` column + `FcmVo` + `refreshFcmToken`), WebSocket broker/Kafka/Undertow/native-image (disabled), `ChatClientConfig` advisor (disabled), MCP client (example config exists, but no `McpClient/ToolCallbackProvider` integration code).

8. Secrets in plain YAML (`app.encrypt-key`, `jwt.secret`, datasource/rabbitmq/mail/qdrant keys, MCP URL password); `.env` not globally ignored — inspect `git status` before staging config. `environments.production` gates routes separately from `spring.profiles.active`.

9. Storage root publicly mapped (`WebConfigurerAdapter`); `FileManagerController.getImage` path containment is not per-user ownership; chunk merge deletes chunks while copying (non-atomic); `app.cron.clean-old-temp-chunks` gates only temp-chunk cleanup, not all scheduled tasks.

10. The current tests under `src/test/java/com/bekaku/api/spring/` are isolated controller/service tests. They do not prove full filter-chain, database, or external-service integration; add the appropriate evidence when changing those boundaries.
