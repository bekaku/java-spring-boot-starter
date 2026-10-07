# Task: Configurable Auth Cookie Domain (frontend on another host calls the API directly)

## Objective

```text
Let the HttpOnly auth cookies be shared between the frontend host (e.g. admin.<domain>) and the API
host (api.<domain>), so a frontend deployed elsewhere can call the API directly and still keep the
login after F5 / new tab / SSR.

1. New optional config app.cookie.domain (env APP_COOKIE_DOMAIN). Blank (default) = today's
   host-only cookies, byte-for-byte unchanged.
2. When set (e.g. .example.com) every auth cookie the backend sets AND clears carries
   Domain=<value> (leading dot stripped): access token, refresh token, current-user cookie.
   Covers login, linkAccount, switchAccount, refreshToken, removeLinkAccount, logout.
3. Validation of the value (hostname only; no scheme, port, path, wildcard, IP, whitespace).
4. Tests + Thai curl guide + deployment notes.
```

Why: cookies are host-only today (`CookieUtil` sets no `Domain`). If the frontend runs on a different
host than the API, the login cookie belongs to the API host only; a full page load of the frontend host
sends no login cookie to the frontend server (SSR), so the user lands on the login page. CORS
(`app.cors.allowed-origins`, `allowCredentials(true)`) already lets the browser call the API
cross-origin but cannot make the cookie visible to the frontend host. In dev it works only because both
run on `localhost` (cookies ignore the port).

Origin: ported from a downstream project that cloned this starter (its Task 044). Items that exist only
in that project (api-prod runbook, `frontend/docker-compose.yml`, nginx layouts) have no counterpart
here and are adapted to this repo's docs instead (see Decisions).

---

## Required Reading

```text
AGENTS.md
SKILLS.md
.agents/skills/backend-core/SKILL.md + skills/backend/CORE.md
docs/tasks/001-auth-cookie-domain.md   (this file)
[x] Security  .agents/skills/backend-security/SKILL.md        + skills/backend/SECURITY.md
[x] Testing   .agents/skills/backend-testing/SKILL.md         + skills/backend/TESTING.md
```

---

## Decisions

| # | Decision |
| --- | --- |
| D1 | Shared cookies via `Domain=<parent domain>` set by the backend (not nginx `proxy_cookie_domain`). |
| D2 | Default stays host-only (blank). Existing deployments and dev are unaffected unless `APP_COOKIE_DOMAIN` is set. |
| D3 | Accepted trade-off: when enabled, the login cookies are sent to **every** subdomain of the configured domain; frontend and API must share that parent domain. Documented. |
| D4 | Mismatch between `app.cookie.domain` and the host of `app.url` → startup **WARN** only, never fail (decision carried over from the downstream project). |
| D5 | Bad value → fail fast at property binding with a message naming `app.cookie.domain`. |
| D6 | Downstream-only artifacts (api-prod runbook §7.5, `frontend/docker-compose.yml` `WEB_PUBLIC_HOST`) are NOT recreated: this repo has no frontend and no production runbook. Deployment notes live in the curl guide §Deployment instead. |

---

## Existing Implementation to Inspect

```text
properties/CookieProperties.java        record(secure, sameSite), prefix app.cookie
properties/AppProperties.java           holds CookieProperties cookie, String url
util/CookieUtil.java                    setCookie / clearCookie (ResponseCookie builder) — the only ResponseCookie producer
controller/api/AuthController.java      setAuthCookie, switchAccount, setCurrentUserToAnother, deleteCookieByName
configuration/WebSecurityConfig.java    corsConfigurationSource (credentials true, exposes Set-Cookie)
resources/application.yml               app.cookie.secure / same-site
test/.../AuthControllerTest.java        mocks CookieUtil and uses new CookieProperties(false, "Lax")
```

Confirmed by grep: `ResponseCookie` is built only in `CookieUtil`; `AuthController` only consumes it.
`loginApi`, `refreshTokenApi`, `logoutApi` return tokens in the body and set no cookies — unchanged.

---

## Scope

- [x] Security / cookie attributes
- [x] Tests
- [x] Documentation

### Out of Scope

```text
- Changing SameSite / Secure defaults; CSRF tokens.
- nginx-side cookie rewriting.
- Frontend code (external repo) and downstream deployment files.
- The legacy path mismatch where AuthController clears _slid_<uid> with path /api/auth while it is
  set with path / (pre-existing; recorded under Known Issues, not changed here).
```

---

## Authoritative Contract

No new endpoint, request or response body change. Only `Set-Cookie` attributes change, and only when
`app.cookie.domain` is non-blank.

```text
APP_COOKIE_DOMAIN blank/unset -> Set-Cookie identical to today (no Domain attribute), set and clear.
APP_COOKIE_DOMAIN=.example.com or example.com ->
  every Set-Cookie for access / refresh / current-user cookie has Domain=example.com
  (Path=/, HttpOnly, Secure per app.cookie.secure, SameSite per app.cookie.same-site — unchanged).
  clearing Set-Cookie headers carry the same Domain and Max-Age=0.
Validation: rejects "://", "/", ":", "*", whitespace, IP-only value; accepts a hostname with or without
a leading dot. Message names app.cookie.domain.
```

### External Consumer Impact

```text
Request/response field changes: none.
Auth transport changed: Set-Cookie gains Domain=<value> only when configured.
Legacy callers affected: none while blank. When set, cookies set earlier as host-only stay until they
  expire or the user logs out once (browser keeps host-only and domain cookies as separate entries).
Frontend requirement: send requests with credentials (fetch credentials:'include' / axios
  withCredentials) and add the frontend origin to app.cors.allowed-origins.
```

---

## Progress Checklist

### Initial Setup & Inspection

- [x] Transition Resume State to `IN_PROGRESS` and record initial `Current Step` before editing code.
- [x] Confirm every cookie producer goes through `CookieUtil` (grep) and list call sites.

### Implementation

- [x] `CookieProperties.domain` + validation (keep 2-arg constructor for existing tests); `application.yml` key.
- [x] `CookieUtil` set/clear apply the domain when configured (+ D4 startup WARN).
- [x] Unit tests: `CookiePropertiesTest`, `CookieUtilTest`, `AuthControllerTest` login/logout with real `CookieUtil`.
- [x] `application-dev-example.yml` commented example.

### Documentation

- [x] `docs/tasks/001-curl-test.md` (Thai): login / refresh / logout with `-i`, both modes, deployment notes.
- [x] `skills/backend/SECURITY.md` cookies section notes the setting.

### Verification

- [x] Transition Resume State to `IMPLEMENTATION_COMPLETE` before final verification.
- [x] `./gradlew compileJava`
- [x] focused tests
- [x] full `./gradlew test` (config-properties record changed)
- [x] `git diff --check`, `git status`, `git diff --stat`
- [x] Transition to `COMPLETED` and fill the Completion Report.

---

## Verification

```bash
./gradlew compileJava
./gradlew test --tests '*CookieUtilTest' --tests '*CookiePropertiesTest' --tests '*AuthControllerTest'
./gradlew test
git diff --check
```

Written curl examples are not execution evidence; label executed vs not executed.

---

## Resume State

**Overall Status:** `COMPLETED`

**Current Step:**

```text
Done. Only live curl / real-browser checks remain (not executed, see curl guide §7).
```

**Last Successful Backend Verification:**

```text
2026-10-07 — ./gradlew compileJava OK; focused: CookieUtilTest 6, CookiePropertiesTest 24, AuthControllerTest
(incl. 3 new in "app.cookie.domain with the real CookieUtil") 0 failures; full ./gradlew test: 119 tests,
0 failures, 0 errors, 0 skipped; git diff --check clean.
```

**Completed Work:**

```text
- Inspected producers (CookieUtil only) and call sites (AuthController: setAuthCookie, switchAccount,
  setCurrentUserToAnother, deleteCookieByName).
- CookieProperties.domain (+normalize/validate, @ConstructorBinding, 2-arg constructor kept); application.yml
  app.cookie.domain: ${APP_COOKIE_DOMAIN:}; dev-example + root docker-compose.yml commented examples.
- CookieUtil applies Domain on set and clear; @PostConstruct WARN when domain does not cover app.url host.
- Tests: CookiePropertiesTest, CookieUtilTest, AuthControllerTest nested CookieDomain.
- Docs: docs/tasks/001-curl-test.md (Thai), skills/backend/SECURITY.md cookie section.
```

**Partially Completed Work:**

```text
- None
```

**Files Currently Being Modified:**

```text
- None
```

**Known Issues / Blockers:**

```text
- Pre-existing (not changed): AuthController clears _slid_<uid> with path /api/auth in
  deleteCookieByName calls inside setCurrentUserToAnother, but the cookie is set with path /. Those
  clears never match the real cookie.
```

**Next Recommended Steps:**

```text
1. Run curl guide §4 against a real backend and the browser check §5 on two hosts.
```

---

## Completion Report

```text
Implemented / reused:      optional app.cookie.domain (APP_COOKIE_DOMAIN) applied by CookieUtil to every auth
                          cookie on set and clear; every producer already went through CookieUtil.
API / contract changes:    Set-Cookie gains Domain=<value> only when configured; blank = unchanged. No body change.
Security / scope:          opt-in wider cookie scope (every subdomain) — documented trade-off (D3). HttpOnly/
                          Secure/SameSite untouched. Validation fails fast at binding; mismatch with app.url = WARN.
External consumer impact:  frontend must send credentials and be in app.cors.allowed-origins (already supported).
Backend verification:      compileJava; focused tests; full ./gradlew test 119/0 failures; git diff --check.
Integrations not executed: live curl against a running backend; real-browser F5/new-tab/logout on two hosts;
                          Spring context startup with the new @PostConstruct (no PostgreSQL available here).
Remaining risks:           cookies reach every *.<domain> host when enabled; old host-only cookies linger until
                          expiry/logout. Pre-existing path mismatch for _slid_ clears (Known Issues) untouched.
Final status:              COMPLETED (live verification outstanding)
```
