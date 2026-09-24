---
name: backend-security
description: Use when a change affects who can call an endpoint or see a row — public vs protected routes, JWT/cookie/Bearer/X-API-KEY authentication, refresh tokens and sessions, @PreAuthorize permission codes, owner scoping, signup, OTP, or password reset — in this Spring Boot backend.
---

# Backend Security — Playbook

> **Role:** HOW to change access behavior without opening holes. Binding facts + evidence: `skills/backend/SECURITY.md` (read it too).
> **Requires:** `backend-core`. **Often paired with:** `backend-api`, `backend-data`, `backend-testing`.

## Know this first

- Access is decided in three places: route rules (`WebSecurityConfig`), the credential filter (`JwtTokenFilter`), and method/data checks (`@PreAuthorize` + owner predicates). Check all three.
- The principal is `AppUserDto` with **no authorities**. Use `@permissionChecker.hasPermission('code')`, never `hasRole` / `hasAuthority`.
- The principal holds only `id`, `token`, `accessTokenId`. `auth.getUsername()` / `getEmail()` are `null` — load the user via `AppUserService` if needed.
- JWT requests must also send an `Accept-Apiclient` header (any non-empty value today), or the filter returns `401`. Include it in tests and curl examples.
- `AuthorizationInterceptor` and `CustomPermissionEvaluator` do nothing. `X-User-Id` / `_sid` are not identity.

## Recipe A — protect a new admin endpoint

1. Pick the permission code (`{table}_{list|view|add|edit|delete}` or an existing code). Grep the code in `controller/` and migrations first.
2. Annotate the handler: `@PreAuthorize("@permissionChecker.hasPermission('{code}')")`.
3. If the code is new: seed it in a migration + add `permission.{code}` i18n labels (`backend-data` Recipe D). A new code grants nothing until a role has it.
4. Test: allowed user passes, user without the permission gets `403`.

## Recipe B — owner-scoped resource (user sees only their rows)

1. Controller takes `@AuthenticationPrincipal AppUserDto auth` and passes `auth.getId()` to the service.
2. Repository/service looks up by id **and** owner:

   ```java
   @Query("SELECT e FROM {Model} e WHERE e.id = ?1 AND e.createdUser = ?2")
   Optional<{Model}> findByIdAndCreator(Long id, Long ownerId);
   ```

3. Empty result → `throw responseErrorNotfound();` (`404`, same as not existing).
4. List queries filter by owner too (JPA spec or MyBatis `AND created_user = #{ownerId}`).
5. New rows get the owner from the authenticated user (audit `created_user` or an explicit field), never from the request body.
6. Test: another user's id → `404`, and the downstream service/repository is not called (`verifyNoInteractions`).

## Recipe C — make a route public (or change route access)

1. Stop and confirm the requirement with the user if it is not explicit in the task.
2. Add the exact method + path to `WebSecurityConfig#filterChain` **and** to `JwtTokenFilter.SKIP_PATHS`. Prefer exact paths over `/**` wildcards; never skip all of `/api/auth/**`.
3. The handler cannot use `@AuthenticationPrincipal` (it will be `null`) or `@permissionChecker`.
4. Test through the real filter chain (`backend-testing`): anonymous allowed, neighbours still `401`.

## Recipe D — change login / refresh / session / API key

1. Read the flow in `SECURITY.md` → *Sessions, tokens, cookies* and trace `AuthController` → `AuthServiceImpl` / `AccessTokenServiceImpl` / `JwtServiceImpl` / `ApiKeyAuthServiceImpl`.
2. Keep refresh rotation, reuse detection (revoke all on reuse), active/non-deleted user checks, and client/session ownership.
3. Cover both transports when shared behavior changes: cookie **and** Bearer (and `X-API-KEY` when the filter changes).
4. Test: inactive user, deleted user, revoked session, expired token, wrong client, reused refresh token.

## Done checklist

- [ ] All three boundaries checked; `WebSecurityConfig` and `SKIP_PATHS` agree.
- [ ] `@PreAuthorize` uses `@permissionChecker` with a code that exists (or is seeded).
- [ ] Owner-scoped reads/writes filter by `auth.getId()`; not-owned → `404`.
- [ ] No secret, raw token, hash, or API key in logs, responses, or exception messages.
- [ ] Signup (if touched) assigns default roles only.
- [ ] Tests cover the denied path, not only the happy path.

## Common mistakes

| Mistake | Fix |
|---|---|
| `@PreAuthorize("hasRole('ADMIN')")` | `@PreAuthorize("@permissionChecker.hasPermission('{code}')")` |
| Permission check only on an owner-scoped row | Add the owner predicate as well |
| Route added to `WebSecurityConfig` only | Also add it to `JwtTokenFilter.SKIP_PATHS` (or the filter returns `401`) |
| `/api/auth/**` in `SKIP_PATHS` | List exact public auth paths |
| Trusting `userId` from body/header | Use `auth.getId()` |
| Controller unit test as proof of route security | Filter-chain HTTP test (`backend-testing`) |
