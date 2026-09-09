# Async / Messaging / Scheduling

Read this for `@Async`, RabbitMQ, schedulers, queue consumers/producers, or background processing.

## Current architecture

- `@Async("asyncTaskExecutor")` uses the configured platform-thread executor even if virtual-thread settings exist elsewhere.
- Current async usage is mostly/demo-oriented; do not assume a mature background-job framework exists.
- RabbitMQ topology and sender configuration exist, but this checkout has no active `@RabbitListener`.
- YAML retry/concurrency settings alone do not create a consumer.
- Kafka, WebSocket broker, Undertow, and several related configs are disabled/commented.

## Rules

- Adding a consumer requires explicit idempotency and duplicate-delivery behavior.
- Define retry/DLQ behavior in code/config that is actually wired, not only YAML comments/settings.
- Do not assume exactly-once delivery.
- Pass authenticated actor/user identity explicitly into async work; thread-local security/audit context may not propagate.
- Do not keep DB transactions open across remote calls, filesystem work, model inference, or message publication unless there is a deliberate transactional design.
- Scheduled tasks must respect existing feature gates and must not introduce ad-hoc cleanup schedulers when a canonical task already exists.

## Verification

- Producer changes: focused serialization/routing tests.
- Consumer changes: duplicate delivery + retry/DLQ tests.
- Config/wiring changes: `./gradlew compileJava` and a context/startup check when feasible.
