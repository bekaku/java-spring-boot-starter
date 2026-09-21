---
name: backend-async-messaging
description: Async execution, RabbitMQ producers and consumers, schedulers, and background processing rules. Load when async, queues, or scheduled jobs change.
---

# Backend Async / Messaging — Canonical

> Canonical async skill. Detailed rules: `skills/backend/ASYNC_MESSAGING.md`.
> Requires: `AGENTS.md`, `.agents/skills/backend-core/SKILL.md`.

## When to load

`@Async`, RabbitMQ, schedulers, queue consumers/producers, or background processing.

## Rules (summary — binding details in `skills/backend/ASYNC_MESSAGING.md`)

- Current checkout has no active `@RabbitListener`; YAML retry/concurrency settings alone create no consumer.
- Adding a consumer requires explicit idempotency + duplicate-delivery behavior and wired retry/DLQ (not YAML-only).
- Do not assume exactly-once delivery.
- Pass actor identity explicitly into async work; do not rely on thread-local security/audit propagation.
- Do not hold DB transactions open across remote calls, filesystem work, model inference, or message publication without deliberate design.
- Consumer changes require duplicate-delivery + retry/DLQ tests.
