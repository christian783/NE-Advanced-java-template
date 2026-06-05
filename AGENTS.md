# AGENTS.md

This file is the operating guide for agents working in this repository. Read it before changing code, and keep changes aligned with the existing Spring Boot patterns unless the user explicitly asks for a larger redesign.

## Project Snapshot

- Java 17 Spring Boot API in package `io.app.my_app`.
- Maven build using Spring Boot `4.0.6`, Spring Web, Validation, Data JPA, Security, Mail, Thymeleaf, Springdoc OpenAPI, MapStruct, Lombok, PostgreSQL, jjwt, and ZXing.
- Main entry point: `src/main/java/io/app/my_app/MyAppApplication.java`.
- Default active profile is `local` from `src/main/resources/application.properties`.
- Runtime config is property-driven. Do not hard-code database, mail, JWT, or OpenAPI values in Java code.
- PostgreSQL is the intended database. `docker-compose.yml` provides a local PostgreSQL service, but always verify port, database name, and environment overrides before assuming the compose service matches the active datasource.

## Repository Map

- `controller/`: REST endpoints. Keep base routes under `/api/v1/...`.
- `service/`: business logic, transactions, auth/password reset workflows, mail dispatch.
- `model/`: JPA entities and domain objects.
- `model/dtos/`: request/response/filter DTOs with Bean Validation annotations.
- `model/enums/`: roles, permissions, token/deletion enums.
- `repository/`: Spring Data repositories.
- `mapper/`: MapStruct mappers. Prefer mapper updates over manual repetitive mapping.
- `specification/`: JPA `Specification` filters and soft-delete predicates.
- `security/`: JWT filter/service/logout and HTTP security rules.
- `exception/`: custom exceptions and `GlobalExceptionHandler`.
- `config/`: Spring beans for security, JPA/auditing, locale, mail, Jackson, and OpenAPI.
- `audits/`: audit base classes and current-user auditor.
- `resources/i18n/`: locale bundles used by `MessageSource` and validation messages.
- `resources/templates/email/`: Thymeleaf email templates.

## Preserve These Contracts

- Product-style CRUD responses use `ApiWrapper<T>` with `data`, `message`, `error`, `status`, and `timestamp`.
- Authentication endpoints currently return `AuthenticationResponse` or empty `ResponseEntity<Void>` directly. Do not wrap them unless intentionally changing the API contract.
- `/api/v1/auth/**` and Swagger/OpenAPI paths are whitelisted in `SecurityConfiguration`; other endpoints require Bearer JWT unless security rules are deliberately updated.
- JWT access tokens are persisted in `Token`. `JwtAuthenticationFilter` accepts a JWT only if it is cryptographically valid and the stored token is not expired/revoked. Logout marks tokens expired and revoked.
- Entities that extend `InitiatorAudit` inherit `createdAt`, `updatedAt`, `deletionStatus`, `deletedAt`, `createdBy`, and `updatedBy`.
- Soft-delete entities by setting `DeletionStatus.INACTIVE` and `deletedAt`; do not physically delete records unless the feature explicitly requires it.
- Read paths for soft-deletable entities must exclude inactive/deleted rows using `SoftDeleteSpec.notDeleted()` or repository methods like `findByIdAndDeletionStatusAndDeletedAtIsNull`.
- `ApplicationAuditAware` expects the authenticated principal to be the local `User` entity. Be careful when changing authentication principals.
- `spring.jpa.open-in-view=false`; do not rely on lazy loading in controllers or JSON serialization. Fetch/map required data inside transactions.

## Feature Implementation Pattern

When adding a domain feature, follow the existing product feature shape:

1. Add/update the JPA entity under `model/`. Use `UUID` IDs where consistent, and extend `InitiatorAudit` if the entity should be audited and soft-deletable.
2. Add request, response, and filter DTOs under `model/dtos/...`. Put Bean Validation on request DTOs.
3. Add a MapStruct mapper under `mapper/`. Ignore ID and audit fields on create/update mappings, and normalize string fields when needed.
4. Add a repository under `repository/`. Use `JpaSpecificationExecutor` when list filtering is needed.
5. Add specifications under `specification/`. Start list specs with `SoftDeleteSpec.notDeleted()` for soft-deletable entities.
6. Add service methods under `service/` with `@Transactional(readOnly = true)` for reads and `@Transactional` for writes.
7. Throw existing custom exceptions with locale keys, for example `EntityNotFoundException`, `DuplicateRecordException`, or `BadRequestException`.
8. Add controller endpoints under `controller/`; return `ApiWrapper` for CRUD/domain responses and localize success messages with `MessageSource`.
9. Add or update role/permission/security rules only when the endpoint's access model changes.
10. Add or update tests for service behavior, validation, security, and controller response shape.

## Localization And Validation

- Message basename is `i18n/messages`; default locale comes from `LocaleResolverConfig`.
- Any key used in `MessageSource.getMessage(...)`, exception constructors, or validation annotations like `{validation.product.name.required}` must exist in `src/main/resources/i18n/messages.properties`.
- Keep `messages_en.properties`, `messages_fr.properties`, and `messages_rw.properties` in sync for user-facing keys whenever practical. Existing bundles are not perfectly identical, so verify before and after changes.
- Prefer stable key names:
  - `responses.<feature>.<action>`
  - `exceptions.<feature>.<reason>`
  - `validation.<feature>.<field>.<rule>`
- Do not return raw exception messages to clients when a localized key should be used.
- Before finishing locale-related work, search both quoted keys and validation annotation keys:
  - `rg -n '"(exceptions|responses|validation|error)\.[^"]+"|\{(exceptions|responses|validation|error)\.[^}]+\}' src/main/java`

## Error Handling

- Preserve `GlobalExceptionHandler` as the central API error mapper.
- Keep error response shape consistent with `ApiWrapper`.
- For enum binding failures, keep the existing `validation.invalid.enum.value` handling.
- For resource not found, duplicate record, bad request, malformed JSON, unauthorized, and internal errors, add message keys instead of embedding final user-facing text in code.
- If introducing a new exception type, add a targeted `@ExceptionHandler` and tests for status code, message, and error body.

## Security And Auth

- Passwords must always be encoded with the configured `PasswordEncoder`.
- Do not log passwords, OTP values, JWTs, authorization headers, mail credentials, or datasource credentials.
- OTP reset flow stores only the encoded OTP hash and marks used OTPs with `usedAt`; preserve one-time use semantics.
- If adding roles or permissions, update `Permission`, `Role`, and `SecurityConfiguration` together. Check both role-based and authority-based access.
- If adding a public endpoint, add only the minimal path to `WHITE_LIST_URL`.
- Keep session management stateless.

## Persistence And Data Safety

- The project currently uses `spring.jpa.hibernate.ddl-auto=update`; there is no migration tool in place. Avoid destructive schema changes, column renames, or data rewrites unless the user asks for them and understands the risk.
- Prefer additive schema changes and backward-compatible DTO changes.
- Keep repository queries aligned with entity field names and soft-delete rules.
- Do not introduce N+1-prone lazy-loading behavior into controllers. Map responses in services while transactions are open.

## Mail And Templates

- `MailService` renders Thymeleaf templates from `src/main/resources/templates/email`.
- When adding an email, add the template, pass a small explicit model map, and avoid leaking secrets or tokens in logs.
- Mail failures in auth registration/reset currently do not fail the main request; keep that behavior unless explicitly changed.

## Build, Run, And Verify

- Preferred test command on Windows: `.\mvnw.cmd test`.
- Preferred package command on Windows: `.\mvnw.cmd -DskipTests package`.
- Preferred run command on Windows: `.\mvnw.cmd spring-boot:run`.
- Local database helper: `docker compose up -d postgres`, then verify datasource env vars and ports.
- There are currently no committed tests under `src/test`. Add focused tests when changing behavior.
- In some sandboxed environments the Maven wrapper may fail before Maven starts with `Cannot start maven from wrapper`. If that happens, report it clearly and still run static checks/searches that do not require Maven.

## Working Safely

- Do not edit `target/`, logs, IDE files, or generated build output.
- Do not expose or copy secrets from property files into logs, docs, commits, issue comments, or final responses.
- `application-local.properties` is ignored by `.gitignore` but may exist locally. Treat it as environment-specific; edit it only when the task is explicitly about local config.
- Avoid broad dependency upgrades, Spring Boot parent changes, Java version changes, package renames, or response-contract changes without explicit approval.
- Keep changes scoped. Do not reformat unrelated files or rewrite working code while adding a feature.
- Before finishing, check `git status --short` and make sure only intended files changed.

## Quick Review Checklist

- API route and response shape match existing conventions.
- Security rules are correct and minimal.
- Service methods have appropriate transaction annotations.
- Soft-delete and audit behavior are preserved.
- DTO validation exists and all validation keys are present.
- Success and exception locale keys exist in the default bundle and relevant locale bundles.
- MapStruct mappings ignore ID/audit fields and normalize input where appropriate.
- No secrets, JWTs, passwords, or OTPs are logged or documented.
- Tests or equivalent verification were run, or the blocker is documented.
