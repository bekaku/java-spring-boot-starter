# Security / Auth / Ownership — Reference

> **Role:** binding security rules + verified facts (WHAT / WHY).
> **Procedure (HOW):** `.agents/skills/backend-security/SKILL.md`.
> **Evidence style:** `path` + `Class#member` (no line numbers; search the symbol).

## The three boundaries (trace all three for any access change)

| # | Boundary | Where | What it decides |
|---|---|---|---|
| 1 | Route rules | `configuration/WebSecurityConfig#filterChain` | public vs authenticated vs denied |
| 2 | Credential filter | `configuration/JwtTokenFilter` (`SKIP_PATHS`, `doFilterInternal`) | who the caller is (sets the principal) |
| 3 | Method + data checks | `@PreAuthorize("@permissionChecker...")` + owner predicates in services/repositories | what the caller may do / see |

Not boundaries: `middleware/AuthorizationInterceptor` (always returns `true`), `configuration/CustomPermissionEvaluator` (always returns `false`), prompt text, `UrlUtil` alone, request headers such as `X-User-ID`.

## 1. Route rules — `WebSecurityConfig`

- Stateless, CSRF disabled, `JwtTokenFilter` runs before `UsernamePasswordAuthenticationFilter`. Method security is enabled in `SecurityEnablerConfig` (`@EnableMethodSecurity`).
- Public: `OPTIONS`, `/favicon.ico`, `/_websocket/**`, `/actuator/**`, `/css/**`, `/{app.cdn-path-alias}/**` (default `cdn`), `ASYNC` dispatches, `GET /api/public/**`, `GET /schedule/**`, and `POST /api/auth/{login, loginApi, logout, logoutApi, refreshToken, refreshTokenApi, requestVerifyCodeToResetPwd, sendVerifyCodeToResetPwd, resetPassword}`.
- Non-production only (`environments.production=false`): `/test/**`, `/dev/development/**`, `/welcome`, `/swagger-ui/**`, `/api-docs/**`, `/theymeleaf`.
- `/api/**` → authenticated. Anything else → `denyAll()`.

## 2. Credential filter — `JwtTokenFilter`

- `shouldNotFilter` skips `SKIP_PATHS` (the public list above, plus dev paths and a hard-coded `/cdn/**`) and `OPTIONS`. Protected `/api/auth/*` routes (`linkedAccounts`, `linkAccount`, `switchAccount/{id}`, `removeLinkAccount/{id}`) are **not** skipped.
- Resolution order for protected requests:
  1. access-token cookie `_session_<uid>` (`CookieUtil#getCurrentUserAccessToken`), else `Authorization: Bearer` → `JwtServiceImpl#jwtVerify(apiClient, token, X-Sync-Active)`;
  2. if neither exists: `X-API-KEY` + `Accept-Apiclient` → `ApiKeyAuthServiceImpl#authenticate`;
  3. else `401 {"error": "..."}` via `sendUnauthorizedResponse`.
- `jwtVerify` requires a non-empty `Accept-Apiclient` header (its value is not checked yet — `//TODO verify apiClient later`), `sub`, `JwtType == Authen`, `uid`, and a live non-revoked `AccessToken` session. **Every authenticated request must send `Accept-Apiclient`**, or it gets `401` even with a valid token.
- `jwtVerify` does not re-check that the user is active. A deactivated user keeps access until the access token expires (`app.jwt.access-token-ttl-minutes`); refresh then fails because `AuthController` refresh checks `isActive()` / `getDeleted()`. Sessions are revoked explicitly only on password change with `logoutAllDevice` (`AppUserController`).
- The JWT principal (`AppUserDto`) carries only `id`, `token` (session key), and `accessTokenId` — not username, email, roles, or permissions. Load the user through `AppUserService` when you need more.
- `ApiKeyAuthServiceImpl#authenticate` hashes the raw key for lookup and checks client name (must equal `Accept-Apiclient`), expiry, and an active owner (`api_client.app_user`) before building an `AppUserDto`.
- The principal is an `AppUserDto` with **no granted authorities** (`Collections.emptyList()`). `hasRole(...)` / `hasAuthority(...)` never pass — use `@permissionChecker`.
- `_sid` cookie / `X-User-ID` header are logged as hints only. They are never identity.
- Keep `SKIP_PATHS` and the `WebSecurityConfig` public list in sync. A route public in one and not the other is broken or unsafe.

## 3. Authorization and ownership

- Permission check: `@PreAuthorize("@permissionChecker.hasPermission('{table}_{action}')")` — also `hasAnyPermission(...)`, `hasAllPermissions(...)` (`util/PermissionChecker`). It queries `PermissionServiceImpl#isHasPermission(userId, code)` via `role_permission`; there is no super-admin bypass.
- Common codes: `{table}_{list,view,add,edit,delete}` (e.g. `app_role_*` on `AppRoleController`). File routes use their own codes (e.g. `file_manager_manage`) — check existing codes before inventing one.
- Failed `@PreAuthorize` → `AccessDeniedException` → `403 ApiError` (`GlobalExceptionHandler#handleAccessDenied`).
- Owner-scoped rows (chat, files, faces, sessions) also need an owner predicate, e.g. `AiChatService#findByIdAndCreator(id, auth.getId())` in `AiChatController`. Not found and not owned both return `404` so existence is not leaked.

## Sessions, tokens, cookies

- Cookies (`util/CookieUtil`, `AuthController`): `_session_<uid>` access token (minutes), `_slid_<uid>` refresh token (days), `_sid` current user id (days). All `httpOnly`, `secure`, `SameSite=Lax`, `path=/`. Settings: `app.cookie` (secure, same-site) and `app.jwt` (TTLs, cookie names) in `application.yml`.
- Refresh (binding lifecycle): `AuthController` `POST /api/auth/refreshToken` (cookie) and `/refreshTokenApi` (body) → `AccessTokenServiceImpl#handleRefreshTokenReuse(presented)` — a revoked token triggers `revokeTokenByUserId(userId)` (reuse detection) → require a live session, active non-deleted user, unexpired token (else clear cookies + `403`) → `AuthServiceImpl#refreshToken` revokes the old `AccessToken`, inserts a new one + cloned `LoginLog`, returns `{authenticationToken, refreshToken, expiresAt, userId}`.
- Token services hash raw tokens internally — pass raw values, do not double-hash.
- Passwords: `EncryptService#encrypt` / `#check` (BCrypt). AES-GCM data encryption is separate. Legacy MD5 helpers exist; auto-migration is commented out.

## Secrets (binding)

Never log, return, or put in exception messages: raw access/refresh tokens, `app.jwt.secret`, `app.encrypt-key`, password hashes, raw `X-API-KEY` values, MCP DB URL credentials. `api_client.api_token` stores a SHA-256 hash; `api_token_mask` is display-only.

## Signup / OTP / password reset

- The signup endpoint in `AuthController` is commented out. If re-enabled: assign configured default roles only (never caller-chosen), and add intentional route + filter changes with tests.
- Password-reset flow: `AuthController` `POST /api/auth/requestVerifyCodeToResetPwd` → `sendVerifyCodeToResetPwd` → `resetPassword`, implemented by `PasswordResetServiceImpl#requestReset` → `#verifyCode` → `#resetPassword`; settings in `properties/PasswordResetProperties`.
- OTP throttling is process-local and keyed by email — not distributed rate limiting.
- CSRF is disabled while cookie auth is supported: revisit the browser threat model before changing SameSite/CORS or adding cookie-authenticated state-changing routes.
