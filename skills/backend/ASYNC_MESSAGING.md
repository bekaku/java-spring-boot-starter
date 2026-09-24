# Async / Messaging / Scheduling — Reference

> **Role:** binding background-work rules + verified facts (WHAT / WHY).
> **Procedure (HOW):** `.agents/skills/backend-async-messaging/SKILL.md`.
> **Evidence style:** `path` + `Class#member` (no line numbers; search the symbol).

## Current state (verified)

### `@Async`

- `SpringApiApplication` has `@EnableAsync`.
- Executor bean: `configuration/AsyncConfig#asyncExecutor`, bean name `ConstantData.ASYNC_TASK_NAME` = `"asyncExecutor"`. It is a platform-thread `ThreadPoolTaskExecutor` (core 10, max 20, queue 100, prefix `async-thread-`) even though `spring.threads.virtual` is set.
- Use it as `@Async(ConstantData.ASYNC_TASK_NAME)` (`AppUserServiceImpl#processAsyncTask`, demo only).
- `@Async` works only when called through the Spring proxy (another bean), on a `public` method.

### RabbitMQ

- `queue/QueueConfig`: one `TopicExchange` and six **non-durable** queues, all bound with the **same** `ROUTING_KEY` (a publish to the exchange reaches every queue); `RabbitTemplate` with a JSON message converter; a `SimpleRabbitListenerContainerFactory` bean.
- `queue/QueueSender` (`@Component`): `sendNotify`, `calculateScore`, `calculateDe`, `calculateUserLevel`, `rewardTradeProcess`.
- **No active `@RabbitListener` anywhere.** `@EnableRabbit` is commented out in `SpringApiApplication`. The `spring.rabbitmq.listener` retry/concurrency YAML does not create a consumer.
- Message DTOs: `queue/dto/*`.

### Scheduling

- `@EnableScheduling` on `SpringApiApplication`.
- Active job: `FileManagerServiceImpl#cleanupOldTempChunks` — `@Scheduled(cron = "${app.cron.clean-file-expression}")`, returns early unless `app.cron.clean-old-temp-chunks=true` (`AppCronProperties`).
- `scheduler/CronScheduler` has only commented-out jobs.

### Disabled

Kafka (`KafkaConsumerConfig`, `KafkaProducerConfig`), WebSocket broker, Undertow — present in code but disabled/commented.

## Binding rules

- Background threads have **no security context**: `AuditAwareImpl` returns empty, `AuthUtil#getAuthenticatedUser()` returns `null`, `@PreAuthorize` has no user. Pass the actor id (and any tenant/owner id) as a method argument or in the message body.
- A `@Transactional` context does not propagate to the executor thread. Open a new transaction in the async/consumer method's own service call.
- Do not keep a DB transaction open across remote calls, filesystem work, model inference, or message publication without a deliberate design. Publish after commit when the message depends on committed data.
- A new consumer needs: an idempotency key, defined duplicate-delivery behavior, and retry + DLQ that are actually wired in code/config (not YAML only). Never assume exactly-once delivery.
- Before adding a consumer, account for the shared routing key and non-durable queues; changing topology affects every queue.
- Scheduled jobs respect an explicit feature flag (pattern: `AppCronProperties`) and must not duplicate an existing cleanup job.

## Verification

- Producer: focused test of message payload + exchange/routing key (mock `RabbitTemplate`).
- Consumer: duplicate delivery and retry/DLQ tests.
- Async: test the service method directly; assert the actor id is passed, not read from the security context.
- Config/wiring: `./gradlew compileJava` and a context-startup check when feasible.
