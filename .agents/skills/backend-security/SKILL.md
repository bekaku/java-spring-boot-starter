---
name: backend-security
description: Authentication, JWT, API keys, refresh sessions, cookies, permissions, ownership, signup and OTP. Load when access behavior changes.
---

# Backend Security — Canonical

> Canonical security skill. Detailed rules: `skills/backend/SECURITY.md`.
> Requires: `AGENTS.md`, `.agents/skills/backend-core/SKILL.md`.

## When to load

Authentication, JWT, refresh/session lifecycle, cookies, permissions, ownership, CORS/SameSite-adjacent work, signup, or OTP changes.

## Implementation path

1. Trace the route through `configuration/WebSecurityConfig.java`, `configuration/JwtTokenFilter.java`, and controller/service permission and owner checks. The filter skips specific public auth routes, not the whole `/api/auth/**` path.
2. Identify the affected credential path: cookie, Bearer token, or `X-API-KEY` plus `Accept-Apiclient` (`ApiKeyAuthServiceImpl`). `X-User-Id` is request input, never identity.
3. For owner-scoped rows, use creator/owner predicates in the service or repository as well as method permissions. Preserve refresh rotation, reuse detection, client/session ownership, and active-user checks.
4. Test access through the real filter chain for affected credential paths. A direct controller unit test cannot prove route/filter enforcement (`backend-testing`).

## Rules (summary — binding details in `skills/backend/SECURITY.md`)

- Trace all three boundaries: (1) `WebSecurityConfig` routes, (2) `JwtTokenFilter` skip-list + verification, (3) method/service ownership checks. `AuthorizationInterceptor` and `CustomPermissionEvaluator` enforce nothing.
- Admin resources: `@PreAuthorize("@permissionChecker.hasPermission('...')")`. Owner-scoped rows: additionally scope by creator/owner (`findByIdAndCreator`).
- Preserve refresh rotation + reuse detection + ownership checks; test inactive/deleted users, revoked/expired sessions, wrong client/owner on cookie and API paths.
- Never log/expose raw refresh tokens, JWT/AES secrets, password hashes, API keys, or MCP credentials. `X-User-Id` is input, never identity.
- Signup must assign configured default roles, never caller-selected privileges.
