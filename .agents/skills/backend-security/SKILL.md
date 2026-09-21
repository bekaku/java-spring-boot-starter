---
name: backend-security
description: Authentication, JWT, refresh and session lifecycle, cookies, RBAC permissions, ownership scoping, signup and OTP. Load when auth, routes, cookies, or ownership change.
---

# Backend Security — Canonical

> Canonical security skill. Detailed rules: `skills/backend/SECURITY.md`.
> Requires: `AGENTS.md`, `.agents/skills/backend-core/SKILL.md`.

## When to load

Authentication, JWT, refresh/session lifecycle, cookies, permissions, ownership, CORS/SameSite-adjacent work, signup, or OTP changes.

## Rules (summary — binding details in `skills/backend/SECURITY.md`)

- Trace all three boundaries: (1) `WebSecurityConfig` routes, (2) `JwtTokenFilter` skip-list + verification, (3) method/service ownership checks. `AuthorizationInterceptor` and `CustomPermissionEvaluator` enforce nothing.
- Admin resources: `@PreAuthorize("@permissionChecker.hasPermission('...')")`. Owner-scoped rows: additionally scope by creator/owner (`findByIdAndCreator`).
- Preserve refresh rotation + reuse detection + ownership checks; test inactive/deleted users, revoked/expired sessions, wrong client/owner on cookie and API paths.
- Never log/expose raw refresh tokens, JWT/AES secrets, password hashes, API keys, or MCP credentials. `X-User-Id` is input, never identity.
- Signup must assign configured default roles, never caller-selected privileges.
