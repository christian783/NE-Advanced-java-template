# Utility Billing System Design

This document captures the backend deliverables for the WASAC/REG utility billing exam implementation.

## ERD

The implementation follows the supplied database architecture and maps it to PostgreSQL-friendly JPA entities.

```mermaid
erDiagram
  USERS {
    UUID id PK
    VARCHAR full_name
    VARCHAR email UK
    VARCHAR phone_number
    VARCHAR password
    ENUM role
    ENUM status
  }

  CUSTOMERS {
    UUID id PK
    VARCHAR full_names
    VARCHAR national_id UK
    VARCHAR email
    VARCHAR phone_number
    VARCHAR address
    ENUM status
  }

  METERS {
    UUID id PK
    UUID customer_id FK
    VARCHAR meter_number UK
    ENUM meter_type
    DATE installation_date
    ENUM status
  }

  METER_READINGS {
    UUID id PK
    UUID meter_id FK
    UUID operator_id FK
    DECIMAL previous_reading
    DECIMAL current_reading
    DATE reading_date
    INTEGER reading_month
    INTEGER reading_year
    DECIMAL consumption
  }

  TARIFFS {
    UUID id PK
    ENUM meter_type
    ENUM tariff_type
    DECIMAL unit_price
    DECIMAL fixed_charge
    DECIMAL vat_rate
    DECIMAL penalty_rate
    INTEGER tariff_version
    DATE effective_from
    BOOLEAN active
  }

  TARIFF_TIERS {
    UUID id PK
    UUID tariff_id FK
    DECIMAL min_units
    DECIMAL max_units
    DECIMAL unit_price
  }

  BILLS {
    UUID id PK
    VARCHAR reference UK
    UUID customer_id FK
    UUID meter_id FK
    UUID meter_reading_id FK
    UUID tariff_id FK
    INTEGER bill_month
    INTEGER bill_year
    DECIMAL consumption_units
    DECIMAL unit_charge
    DECIMAL fixed_charge
    DECIMAL vat_amount
    DECIMAL penalty_amount
    DECIMAL total_amount
    DECIMAL amount_paid
    DECIMAL outstanding_balance
    ENUM status
    UUID approved_by FK
    TIMESTAMP approved_at
    DATE due_date
  }

  PAYMENTS {
    UUID id PK
    UUID bill_id FK
    UUID customer_id FK
    DECIMAL amount_paid
    ENUM payment_method
    VARCHAR reference_number UK
    DATE payment_date
    DECIMAL balance_before
    DECIMAL balance_after
  }

  NOTIFICATIONS {
    UUID id PK
    UUID customer_id FK
    UUID bill_id FK
    UUID payment_id FK
    ENUM type
    TEXT message
    BOOLEAN sent
    TIMESTAMP sent_at
    TIMESTAMP created_at
  }

  CUSTOMERS ||--o{ METERS : owns
  CUSTOMERS ||--o{ BILLS : receives
  CUSTOMERS ||--o{ PAYMENTS : makes
  CUSTOMERS ||--o{ NOTIFICATIONS : receives
  METERS ||--o{ METER_READINGS : records
  METERS ||--o{ BILLS : billed
  METER_READINGS ||--|| BILLS : generates
  TARIFFS ||--o{ TARIFF_TIERS : contains
  TARIFFS ||--o{ BILLS : prices
  BILLS ||--o{ PAYMENTS : settled_by
  BILLS ||--o{ NOTIFICATIONS : notifies
  PAYMENTS ||--o{ NOTIFICATIONS : notifies
  USERS ||--o{ METER_READINGS : captures
  USERS ||--o{ BILLS : approves
```

## Spring Boot Flow

```mermaid
flowchart TD
  Client["Postman / Swagger / API Client"] --> Security["Spring Security JWT Filter"]
  Security --> Auth["AuthenticationController"]
  Security --> Controllers["Domain Controllers"]
  Auth --> AuthService["AuthenticationService"]
  Controllers --> Services["Customer, Meter, Reading, Tariff, Bill, Payment Services"]
  Services --> Repositories["Spring Data JPA Repositories"]
  Repositories --> Database["PostgreSQL"]
  Services --> Notifications["NotificationService"]
  Notifications --> Database
  Services --> Mail["MailService / Thymeleaf Templates"]
  Controllers --> Wrapper["ApiWrapper Response"]
  Wrapper --> Client
```

## Main API Groups

- `POST /api/v1/auth/register`: customer signup. New users are active `CUSTOMER` users by default.
- `POST /api/v1/auth/authenticate`: login and JWT issuance.
- `POST /api/v1/auth/change-password`, `/forgot-password`, `/reset-password`, `/refresh-token`, `/logout`: authentication support.
- `GET /api/v1/users`, `POST /api/v1/users`, `PATCH /api/v1/users/{id}/status`: admin user management.
- `CRUD /api/v1/customers`: customer management.
- `CRUD /api/v1/meters`: meter management.
- `POST /api/v1/meter-readings`: operator/admin reading capture.
- `POST /api/v1/tariffs`: admin tariff configuration with versioning and future-cycle enforcement.
- `POST /api/v1/bills/generate`: finance/admin bill generation from meter readings.
- `POST /api/v1/bills/{id}/approve`: finance/admin bill approval.
- `POST /api/v1/payments`: finance/admin partial or full payment recording.
- `GET /api/v1/notifications`: notification inspection.

## Business Rules Implemented

- Duplicate users by email, customers by national ID, and meters by meter number are rejected.
- Inactive customers cannot receive meters or bills.
- Meter readings require active meters and active customers.
- Current reading must be greater than previous reading.
- Only one reading per meter per month/year is allowed.
- Tariffs are versioned per meter type.
- Tariffs must start no earlier than the next billing cycle.
- Flat tariffs use `unitPrice`; tiered tariffs require at least one tier.
- Bills are generated from readings, priced by the applicable tariff, and start as `PENDING_APPROVAL`.
- Bill generation creates a notification.
- Payments support partial and full settlement.
- Overpayments are rejected.
- Full payment marks the bill `PAID` and creates a customer notification.

## Security Model

- All endpoints are secured except `/api/v1/auth/**` and Swagger/OpenAPI resources.
- Method-level authorization uses:
  - `ADMIN`: configure tariffs, manage users/customers/meters, approve bills.
  - `OPERATOR`: capture meter readings.
  - `FINANCE`: approve bills and record payments.
  - `CUSTOMER`: view customer-related bills, payments, meters, and notifications.
- JWT tokens must be valid and present in the token store as not expired and not revoked.

## Swagger

Swagger UI is configured at:

- `/myApp/swagger-ui.html`
- `/myApp/v3/api-docs`
