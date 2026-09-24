---
name: backend-async-messaging
description: Use when adding or changing background work in this Spring Boot backend — @Async methods, RabbitMQ producers or consumers (@RabbitListener), queue topology, @Scheduled jobs, or anything that runs outside the HTTP request thread.
---

# Backend Async / Messaging — Playbook

> **Role:** HOW to add background work safely. Binding facts + evidence: `skills/backend/ASYNC_MESSAGING.md` (read it too).
> **Requires:** `backend-core`. **Often paired with:** `backend-data` (persistence in the job), `backend-testing`.

## Know this first

- Executor name is `ConstantData.ASYNC_TASK_NAME` (`"asyncExecutor"`) — not `asyncTaskExecutor`.
- There is **no** `@RabbitListener` in the codebase and `@EnableRabbit` is commented out. Adding the first consumer is a design change.
- Off the request thread there is no logged-in user, no audit user, and no caller transaction.

## Recipe A — run work asynchronously

1. Put the method on a Spring bean different from the caller (self-calls bypass the proxy):

   ```java
   @Async(ConstantData.ASYNC_TASK_NAME)
   public CompletableFuture<Void> rebuildIndex(Long actorId, Long targetId) { ... }
   ```

2. Pass `actorId` (and owner/target ids) explicitly. Do not call `AuthUtil#getAuthenticatedUser()` inside.
3. Do DB writes through a `@Transactional` service method called from the async method.
4. Log failures with context (no secrets); decide whether the caller needs the `CompletableFuture`.

## Recipe B — publish a RabbitMQ message

1. Add a payload DTO in `queue/dto/` (ids and values only — no entities, no secrets).
2. Add a method to `queue/QueueSender` that calls `rabbitTemplate.convertAndSend(exchangeOrQueue, routingKey, dto)`.
3. Publish **after** the DB transaction commits if the consumer reads that data.
4. Remember: all queues share one routing key today — a topic publish fans out to every queue.

## Recipe C — add a consumer (first one in the repo)

1. Confirm with the user; this enables listener infrastructure (`@EnableRabbit` / listener factory).
2. Define: idempotency key (e.g. a message id stored in a processed table with a unique constraint), duplicate behavior (skip), retry policy, DLQ exchange/queue — all wired in Java config.
3. Consider making the target queue durable and giving it a dedicated routing key (topology change → note impact on other queues).
4. The listener method gets actor/owner ids from the message, calls a `@Transactional` service, and is safe to run twice.
5. Tests: same message twice → one effect; failing handler → retried then dead-lettered.

## Recipe D — add a scheduled job

1. Add a flag to `properties/AppCronProperties` and a cron expression under `app.cron` in `application.yml`.
2. `@Scheduled(cron = "${app.cron.<name>-expression}")` on a bean method; return early when the flag is off (pattern: `FileManagerServiceImpl#cleanupOldTempChunks`).
3. Make the job idempotent and bounded (batch size, time window). Multiple instances will all run it unless you add locking.

## Done checklist

- [ ] Correct executor name; async method on a different bean, `public`.
- [ ] Actor/owner ids passed explicitly; no reliance on security/audit context.
- [ ] No DB transaction held across remote/file/model/publish steps.
- [ ] Consumers: idempotent, duplicate-safe, retry + DLQ wired in code.
- [ ] Scheduled jobs: feature flag, idempotent, not duplicating an existing job.
- [ ] Tests per `ASYNC_MESSAGING.md` → *Verification*.

## Common mistakes

| Mistake | Fix |
|---|---|
| `@Async("asyncTaskExecutor")` | `@Async(ConstantData.ASYNC_TASK_NAME)` |
| Calling an `@Async` method from the same class | Move it to another bean |
| `created_user` is `null` for async inserts | Pass the actor id and set it explicitly |
| Adding `spring.rabbitmq.listener.retry` YAML and calling it done | Retry/DLQ must be wired in Java config and tested |
| Publishing inside the transaction, consumer reads stale data | Publish after commit |
