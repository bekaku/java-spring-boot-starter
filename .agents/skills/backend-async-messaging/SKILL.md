---
name: backend-async-messaging
description: Async execution, RabbitMQ producers and consumers, schedulers, and background processing rules. Load when async, queues, or scheduled jobs change.
---

# Backend Async / Messaging — Canonical

> Canonical async skill. Detailed rules: `skills/backend/ASYNC_MESSAGING.md`.
> Requires: `AGENTS.md`, `.agents/skills/backend-core/SKILL.md`.

## When to load

`@Async`, RabbitMQ, schedulers, queue consumers/producers, or background processing.

## Implementation path

1. Inspect the caller, `configuration/AsyncConfig.java`, `queue/QueueConfig.java`, and `queue/QueueSender.java`. Queue declarations and a listener factory exist; there is no active consumer.
2. Pass actor identity explicitly into background work. Do not assume a security context, auditor, or transaction propagates to an executor thread.
3. For a consumer, define an idempotency key, duplicate-delivery behavior, wired retry and DLQ handling. Account for the existing shared routing key and non-durable queues before changing topology.
4. Test producer serialization/routing or consumer duplicate and retry behavior as applicable; check startup wiring for configuration changes (`backend-testing`).

## Rules (summary — binding details in `skills/backend/ASYNC_MESSAGING.md`)

- Current checkout has no active `@RabbitListener`; YAML retry/concurrency settings alone create no consumer.
- Adding a consumer requires explicit idempotency + duplicate-delivery behavior and wired retry/DLQ (not YAML-only).
- Do not assume exactly-once delivery.
- Pass actor identity explicitly into async work; do not rely on thread-local security/audit propagation.
- Do not hold DB transactions open across remote calls, filesystem work, model inference, or message publication without deliberate design.
- Consumer changes require duplicate-delivery + retry/DLQ tests.
