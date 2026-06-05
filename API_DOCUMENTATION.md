# Utility Billing System - API Documentation

**Base URL:** `/api/v1`  
**API Version:** 1.0  
**Default Profile:** `local`  
**Database:** PostgreSQL  
**Authentication:** Bearer JWT Token

---

## Table of Contents

1. [Overview](#overview)
2. [Authentication Endpoints](#authentication-endpoints)
3. [Products Endpoints](#products-endpoints)
4. [Customers Endpoints](#customers-endpoints)
5. [Meters Endpoints](#meters-endpoints)
6. [Meter Readings Endpoints](#meter-readings-endpoints)
7. [Tariffs Endpoints](#tariffs-endpoints)
8. [Bills Endpoints](#bills-endpoints)
9. [Payments Endpoints](#payments-endpoints)
10. [Notifications Endpoints](#notifications-endpoints)
11. [Users Endpoints](#users-endpoints)
12. [Response Format](#response-format)
13. [Error Handling](#error-handling)
14. [Status Codes](#status-codes)

---

## Overview

### Response Wrapper Format

All API responses follow a standardized wrapper format:

```json
{
  "data": {},
  "message": "Localized success/error message",
  "error": null,
  "status": "OK",
  "timestamp": "2026-05-31T10:30:00"
}
```

### Authentication

- **Type:** Bearer Token (JWT)
- **Location:** Authorization header
- **Format:** `Authorization: Bearer {access_token}`
- **Token Lifetime:** Configurable via properties
- **Refresh:** Use refresh endpoint to obtain new access token

### Pagination

List endpoints support pagination with the following parameters:

- **page** (integer, default: 0) - Zero-based page number
- **size** (integer, default: 20) - Number of records per page
- **sort** (string, default: "createdAt,desc") - Sort criteria

Example: `?page=0&size=20&sort=createdAt,desc`

### Localization

- Supported locales: `en`, `fr`, `rw`
- Default locale: Configured in `LocaleResolverConfig`
- Set locale via `Accept-Language` header

---

## Authentication Endpoints

### Base Path: `/api/v1/auth`

All authentication endpoints are **public** (no JWT required).

#### 1. Register User

**Endpoint:** `POST /api/v1/auth/register`

**Description:** Register a new user in the system.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123!",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+250788123456"
}
```

**Response:** `200 OK`
```json
{
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "message": "User registered successfully",
  "error": null,
  "status": "OK",
  "timestamp": "2026-05-31T10:30:00"
}
```

**Validations:**
- Email must be unique and valid format
- Password must meet complexity requirements
- First name and last name are required
- Phone number (optional but recommended)

**Errors:**
- `400 Bad Request` - Validation failure or duplicate email
- `500 Internal Server Error` - Server error

---

#### 2. Authenticate User (Login)

**Endpoint:** `POST /api/v1/auth/authenticate`

**Description:** Authenticate user and obtain JWT tokens.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123!"
}
```

**Response:** `200 OK`
```json
{
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "message": "User authenticated successfully",
  "error": null,
  "status": "OK",
  "timestamp": "2026-05-31T10:30:00"
}
```

**Validations:**
- Email and password are required
- Email must be registered
- Password must be correct

**Errors:**
- `400 Bad Request` - Invalid credentials
- `401 Unauthorized` - Authentication failed
- `500 Internal Server Error` - Server error

---

#### 3. Change Password (Authenticated)

**Endpoint:** `POST /api/v1/auth/change-password`

**Authentication:** Required (Bearer JWT)

**Description:** Change password for the authenticated user.

**Request Body:**
```json
{
  "currentPassword": "OldPassword123!",
  "newPassword": "NewPassword456!",
  "confirmPassword": "NewPassword456!"
}
```

**Response:** `200 OK`
```json
{
  "data": null,
  "message": "Password changed successfully",
  "error": null,
  "status": "OK",
  "timestamp": "2026-05-31T10:30:00"
}
```

**Validations:**
- Current password must be correct
- New password must be different from current
- New password must meet complexity requirements
- Confirm password must match new password

**Errors:**
- `400 Bad Request` - Validation failure
- `401 Unauthorized` - Invalid current password
- `500 Internal Server Error` - Server error

---

#### 4. Forgot Password

**Endpoint:** `POST /api/v1/auth/forgot-password`

**Description:** Initiate password reset process. Sends OTP to registered email.

**Request Body:**
```json
{
  "email": "user@example.com"
}
```

**Response:** `200 OK`
```json
{
  "data": null,
  "message": "Password reset OTP sent to your email",
  "error": null,
  "status": "OK",
  "timestamp": "2026-05-31T10:30:00"
}
```

**Validations:**
- Email must be registered in system

**Errors:**
- `400 Bad Request` - Email not found
- `500 Internal Server Error` - Email service failure (non-blocking)

---

#### 5. Reset Password

**Endpoint:** `POST /api/v1/auth/reset-password`

**Description:** Reset password using OTP received via email.

**Request Body:**
```json
{
  "email": "user@example.com",
  "otp": "123456",
  "newPassword": "NewPassword456!",
  "confirmPassword": "NewPassword456!"
}
```

**Response:** `200 OK`
```json
{
  "data": null,
  "message": "Password reset successfully",
  "error": null,
  "status": "OK",
  "timestamp": "2026-05-31T10:30:00"
}
```

**Validations:**
- Email must be registered
- OTP must be valid and not expired
- OTP must not have been used before
- New password must meet complexity requirements

**Errors:**
- `400 Bad Request` - Invalid OTP or validation failure
- `401 Unauthorized` - OTP expired or already used
- `500 Internal Server Error` - Server error

---

#### 6. Refresh Token

**Endpoint:** `POST /api/v1/auth/refresh-token`

**Description:** Obtain new access token using refresh token.

**Headers:**
```
Authorization: Bearer {refresh_token}
```

**Response:** `200 OK` (Sets cookies and/or returns tokens)

**Errors:**
- `401 Unauthorized` - Invalid or expired refresh token
- `500 Internal Server Error` - Server error

---

## Products Endpoints

### Base Path: `/api/v1/products`

#### 1. List Products

**Endpoint:** `GET /api/v1/products`

**Authentication:** Not required

**Description:** List all products with optional filtering and pagination.

**Query Parameters:**
- `page` (integer, default: 0) - Page number
- `size` (integer, default: 20) - Records per page
- `sort` (string, default: "createdAt,desc") - Sort criteria
- `id` (UUID, optional) - Filter by product ID
- `name` (string, optional) - Filter by product name
- `sku` (string, optional) - Filter by SKU
- `description` (string, optional) - Filter by description
- `price` (double, optional) - Filter by price

**Example Request:**
```
GET /api/v1/products?page=0&size=20&name=water&sort=createdAt,desc
```

**Response:** `200 OK`
```json
{
  "data": {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "sku": "WATER-001",
        "name": "Water Supply",
        "description": "Monthly water supply service",
        "price": 5000.00,
        "createdAt": "2026-05-30T08:15:00",
        "updatedAt": "2026-05-30T08:15:00"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 20,
    "hasNext": false,
    "hasPrevious": false
  },
  "message": "Products retrieved successfully",
  "error": null,
  "status": "OK",
  "timestamp": "2026-05-31T10:30:00"
}
```

**Errors:**
- `400 Bad Request` - Invalid filter parameters
- `500 Internal Server Error` - Server error

---

#### 2. Get Product by ID

**Endpoint:** `GET /api/v1/products/{id}`

**Authentication:** Not required

**Description:** Retrieve a single product by ID.

**Path Parameters:**
- `id` (UUID, required) - Product ID

**Example Request:**
```
GET /api/v1/products/550e8400-e29b-41d4-a716-446655440000
```

**Response:** `200 OK`
```json
{
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "sku": "WATER-001",
    "name": "Water Supply",
    "description": "Monthly water supply service",
    "price": 5000.00,
    "createdAt": "2026-05-30T08:15:00",
    "updatedAt": "2026-05-30T08:15:00"
  },
  "message": "Product retrieved successfully",
  "error": null,
  "status": "OK",
  "timestamp": "2026-05-31T10:30:00"
}
```

**Errors:**
- `404 Not Found` - Product not found
- `500 Internal Server Error` - Server error

---

#### 3. Create Product

**Endpoint:** `POST /api/v1/products`

**Authentication:** Not required

**Description:** Create a new product.

**Request Body:**
```json
{
  "sku": "WATER-001",
  "name": "Water Supply",
  "description": "Monthly water supply service",
  "price": 5000.00
}
```

**Response:** `201 Created`
```json
{
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "sku": "WATER-001",
    "name": "Water Supply",
    "description": "Monthly water supply service",
    "price": 5000.00,
    "createdAt": "2026-05-31T10:30:00",
    "updatedAt": "2026-05-31T10:30:00"
  },
  "message": "Product created successfully",
  "error": null,
  "status": "CREATED",
  "timestamp": "2026-05-31T10:30:00"
}
```

**Validations:**
- `sku` - Required, max 100 characters, must be unique
- `name` - Required, max 255 characters
- `description` - Required, max 1000 characters
- `price` - Required, must be >= 0

**Errors:**
- `400 Bad Request` - Validation failure or duplicate SKU
- `500 Internal Server Error` - Server error

---

#### 4. Update Product

**Endpoint:** `PUT /api/v1/products/{id}`

**Authentication:** Not required

**Description:** Update an existing product.

**Path Parameters:**
- `id` (UUID, required) - Product ID

**Request Body:**
```json
{
  "sku": "WATER-002",
  "name": "Water Supply Updated",
  "description": "Updated description",
  "price": 6000.00
}
```

**Response:** `200 OK`
```json
{
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "sku": "WATER-002",
    "name": "Water Supply Updated",
    "description": "Updated description",
    "price": 6000.00,
    "createdAt": "2026-05-30T08:15:00",
    "updatedAt": "2026-05-31T10:30:00"
  },
  "message": "Product updated successfully",
  "error": null,
  "status": "OK",
  "timestamp": "2026-05-31T10:30:00"
}
```

**Errors:**
- `404 Not Found` - Product not found
- `400 Bad Request` - Validation failure
- `500 Internal Server Error` - Server error

---

#### 5. Delete Product

**Endpoint:** `DELETE /api/v1/products/{id}`

**Authentication:** Not required

**Description:** Soft delete a product (logical deletion).

**Path Parameters:**
- `id` (UUID, required) - Product ID

**Response:** `200 OK`
```json
{
  "data": null,
  "message": "Product deleted successfully",
  "error": null,
  "status": "OK",
  "timestamp": "2026-05-31T10:30:00"
}
```

**Errors:**
- `404 Not Found` - Product not found
- `500 Internal Server Error` - Server error

---

## Customers Endpoints

### Base Path: `/api/v1/customers`

#### 1. List Customers

**Endpoint:** `GET /api/v1/customers`

**Authentication:** Required - Roles: `ADMIN`, `FINANCE`, `OPERATOR`

**Description:** List all customers with optional filtering and pagination.

**Query Parameters:**
- `page` (integer, default: 0) - Page number
- `size` (integer, default: 20) - Records per page
- `sort` (string, default: "createdAt,desc") - Sort criteria
- `id` (UUID, optional) - Filter by customer ID
- `firstName` (string, optional) - Filter by first name
- `lastName` (string, optional) - Filter by last name
- `phone` (string, optional) - Filter by phone
- `email` (string, optional) - Filter by email
- `customerType` (enum, optional) - Filter by type (RESIDENTIAL, COMMERCIAL, INDUSTRIAL)

**Example Request:**
```
GET /api/v1/customers?page=0&size=20&firstName=John&sort=createdAt,desc
```

**Response:** `200 OK` (Paginated list of customers)

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `400 Bad Request` - Invalid filter parameters
- `500 Internal Server Error` - Server error

---

#### 2. Get Customer by ID

**Endpoint:** `GET /api/v1/customers/{id}`

**Authentication:** Required - Roles: `ADMIN`, `FINANCE`, `OPERATOR`, `CUSTOMER`

**Description:** Retrieve a single customer by ID.

**Path Parameters:**
- `id` (UUID, required) - Customer ID

**Response:** `200 OK`

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Customer not found
- `500 Internal Server Error` - Server error

---

#### 3. Create Customer

**Endpoint:** `POST /api/v1/customers`

**Authentication:** Required - Role: `ADMIN`

**Description:** Create a new customer.

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "+250788123456",
  "location": "Kigali",
  "customerType": "RESIDENTIAL"
}
```

**Response:** `201 Created`

**Validations:**
- All fields are required
- Email must be unique and valid
- Phone must be valid format
- Customer type must be valid enum value

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `400 Bad Request` - Validation failure or duplicate email
- `500 Internal Server Error` - Server error

---

#### 4. Update Customer

**Endpoint:** `PUT /api/v1/customers/{id}`

**Authentication:** Required - Role: `ADMIN`

**Description:** Update an existing customer.

**Path Parameters:**
- `id` (UUID, required) - Customer ID

**Request Body:**
```json
{
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@example.com",
  "phone": "+250798654321",
  "location": "Kigali",
  "customerType": "COMMERCIAL"
}
```

**Response:** `200 OK`

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Customer not found
- `400 Bad Request` - Validation failure
- `500 Internal Server Error` - Server error

---

## Meters Endpoints

### Base Path: `/api/v1/meters`

#### 1. List Meters

**Endpoint:** `GET /api/v1/meters`

**Authentication:** Required - Roles: `ADMIN`, `OPERATOR`, `FINANCE`

**Description:** List all meters with optional filtering and pagination.

**Query Parameters:**
- `page` (integer, default: 0) - Page number
- `size` (integer, default: 20) - Records per page
- `sort` (string, default: "createdAt,desc") - Sort criteria
- `id` (UUID, optional) - Filter by meter ID
- `customerId` (UUID, optional) - Filter by customer ID
- `meterNumber` (string, optional) - Filter by meter number
- `meterType` (enum, optional) - Filter by type (WATER, ELECTRICITY, GAS)
- `status` (enum, optional) - Filter by status (ACTIVE, INACTIVE, FAULTY)

**Response:** `200 OK` (Paginated list of meters)

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `500 Internal Server Error` - Server error

---

#### 2. Get Meter by ID

**Endpoint:** `GET /api/v1/meters/{id}`

**Authentication:** Required - Roles: `ADMIN`, `OPERATOR`, `FINANCE`

**Description:** Retrieve a single meter by ID.

**Path Parameters:**
- `id` (UUID, required) - Meter ID

**Response:** `200 OK`

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Meter not found
- `500 Internal Server Error` - Server error

---

#### 3. Create Meter

**Endpoint:** `POST /api/v1/meters`

**Authentication:** Required - Role: `ADMIN`

**Description:** Register a new meter for a customer.

**Request Body:**
```json
{
  "customerId": "550e8400-e29b-41d4-a716-446655440001",
  "meterNumber": "M12345678",
  "meterType": "WATER",
  "installationDate": "2026-05-01T00:00:00",
  "location": "House - Main entrance"
}
```

**Response:** `201 Created`

**Validations:**
- Customer ID must exist
- Meter number must be unique
- Meter type must be valid enum value
- Installation date is required

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `400 Bad Request` - Validation failure or duplicate meter number
- `404 Not Found` - Customer not found
- `500 Internal Server Error` - Server error

---

#### 4. Update Meter

**Endpoint:** `PUT /api/v1/meters/{id}`

**Authentication:** Required - Role: `ADMIN`

**Description:** Update meter details.

**Path Parameters:**
- `id` (UUID, required) - Meter ID

**Request Body:**
```json
{
  "customerId": "550e8400-e29b-41d4-a716-446655440001",
  "meterNumber": "M12345679",
  "meterType": "WATER",
  "installationDate": "2026-05-01T00:00:00",
  "location": "House - Back entrance"
}
```

**Response:** `200 OK`

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Meter not found or customer not found
- `400 Bad Request` - Validation failure
- `500 Internal Server Error` - Server error

---

## Meter Readings Endpoints

### Base Path: `/api/v1/meter-readings`

#### 1. List Meter Readings

**Endpoint:** `GET /api/v1/meter-readings`

**Authentication:** Required - Roles: `ADMIN`, `OPERATOR`, `FINANCE`

**Description:** List all meter readings with optional filtering and pagination.

**Query Parameters:**
- `page` (integer, default: 0) - Page number
- `size` (integer, default: 20) - Records per page
- `sort` (string, default: "createdAt,desc") - Sort criteria
- `id` (UUID, optional) - Filter by reading ID
- `meterId` (UUID, optional) - Filter by meter ID
- `readingMonth` (integer, optional) - Filter by month (1-12)
- `readingYear` (integer, optional) - Filter by year
- `status` (enum, optional) - Filter by status

**Response:** `200 OK` (Paginated list of readings)

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `500 Internal Server Error` - Server error

---

#### 2. Get Meter Reading by ID

**Endpoint:** `GET /api/v1/meter-readings/{id}`

**Authentication:** Required - Roles: `ADMIN`, `OPERATOR`, `FINANCE`

**Description:** Retrieve a single meter reading by ID.

**Path Parameters:**
- `id` (UUID, required) - Reading ID

**Response:** `200 OK`

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Reading not found
- `500 Internal Server Error` - Server error

---

#### 3. Capture Meter Reading

**Endpoint:** `POST /api/v1/meter-readings`

**Authentication:** Required - Roles: `ADMIN`, `OPERATOR`

**Description:** Record a new meter reading.

**Request Body:**
```json
{
  "meterId": "550e8400-e29b-41d4-a716-446655440002",
  "readingValue": 1250,
  "readingMonth": 5,
  "readingYear": 2026,
  "readingDate": "2026-05-31T14:30:00",
  "notes": "Normal reading"
}
```

**Response:** `201 Created`

**Validations:**
- Meter ID must exist
- Reading value must be positive
- Month must be 1-12
- Year must be valid
- Reading date must not be in future

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `400 Bad Request` - Validation failure
- `404 Not Found` - Meter not found
- `500 Internal Server Error` - Server error

---

## Tariffs Endpoints

### Base Path: `/api/v1/tariffs`

#### 1. List Tariffs

**Endpoint:** `GET /api/v1/tariffs`

**Authentication:** Required - Roles: `ADMIN`, `FINANCE`

**Description:** List all tariffs with optional filtering and pagination.

**Query Parameters:**
- `page` (integer, default: 0) - Page number
- `size` (integer, default: 20) - Records per page
- `sort` (string, default: "createdAt,desc") - Sort criteria
- `id` (UUID, optional) - Filter by tariff ID
- `productId` (UUID, optional) - Filter by product ID
- `tariffName` (string, optional) - Filter by name
- `status` (enum, optional) - Filter by status (ACTIVE, INACTIVE)

**Response:** `200 OK` (Paginated list of tariffs)

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `500 Internal Server Error` - Server error

---

#### 2. Get Tariff by ID

**Endpoint:** `GET /api/v1/tariffs/{id}`

**Authentication:** Required - Roles: `ADMIN`, `FINANCE`

**Description:** Retrieve a single tariff by ID.

**Path Parameters:**
- `id` (UUID, required) - Tariff ID

**Response:** `200 OK`

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Tariff not found
- `500 Internal Server Error` - Server error

---

#### 3. Configure Tariff

**Endpoint:** `POST /api/v1/tariffs`

**Authentication:** Required - Role: `ADMIN`

**Description:** Create a new tariff configuration.

**Request Body:**
```json
{
  "productId": "550e8400-e29b-41d4-a716-446655440003",
  "tariffName": "Residential Water Tariff",
  "description": "Standard tariff for residential water consumption",
  "effectiveDate": "2026-06-01T00:00:00",
  "status": "ACTIVE",
  "tariffTiers": [
    {
      "minUnit": 0,
      "maxUnit": 50,
      "pricePerUnit": 150.00
    },
    {
      "minUnit": 51,
      "maxUnit": 100,
      "pricePerUnit": 200.00
    }
  ]
}
```

**Response:** `201 Created`

**Validations:**
- Product ID must exist
- Tariff name is required
- Effective date must be valid
- At least one tariff tier must be provided
- Tiers must not overlap

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `400 Bad Request` - Validation failure
- `404 Not Found` - Product not found
- `500 Internal Server Error` - Server error

---

## Bills Endpoints

### Base Path: `/api/v1/bills`

#### 1. List Bills

**Endpoint:** `GET /api/v1/bills`

**Authentication:** Required - Roles: `ADMIN`, `FINANCE`

**Description:** List all bills with optional filtering and pagination.

**Query Parameters:**
- `page` (integer, default: 0) - Page number
- `size` (integer, default: 20) - Records per page
- `sort` (string, default: "createdAt,desc") - Sort criteria
- `id` (UUID, optional) - Filter by bill ID
- `customerId` (UUID, optional) - Filter by customer ID
- `meterId` (UUID, optional) - Filter by meter ID
- `billStatus` (enum, optional) - Filter by status (DRAFT, APPROVED, SENT, PAID, OVERDUE)
- `billMonth` (integer, optional) - Filter by month
- `billYear` (integer, optional) - Filter by year

**Response:** `200 OK` (Paginated list of bills)

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `500 Internal Server Error` - Server error

---

#### 2. Get Bill by ID

**Endpoint:** `GET /api/v1/bills/{id}`

**Authentication:** Required - Roles: `ADMIN`, `FINANCE`, `CUSTOMER`

**Description:** Retrieve a single bill by ID.

**Path Parameters:**
- `id` (UUID, required) - Bill ID

**Response:** `200 OK`

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Bill not found
- `500 Internal Server Error` - Server error

---

#### 3. Generate Bill

**Endpoint:** `POST /api/v1/bills/generate`

**Authentication:** Required - Roles: `ADMIN`, `FINANCE`

**Description:** Generate a bill for a meter reading.

**Request Body:**
```json
{
  "meterId": "550e8400-e29b-41d4-a716-446655440002",
  "readingId": "550e8400-e29b-41d4-a716-446655440004",
  "billMonth": 5,
  "billYear": 2026,
  "dueDate": "2026-06-15T00:00:00"
}
```

**Response:** `201 Created`

**Validations:**
- Meter ID must exist
- Reading ID must exist and belong to meter
- Month must be 1-12
- Year must be valid
- Due date must be in future

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `400 Bad Request` - Validation failure or bill already exists
- `404 Not Found` - Meter or reading not found
- `500 Internal Server Error` - Server error

---

#### 4. Approve Bill

**Endpoint:** `POST /api/v1/bills/{id}/approve`

**Authentication:** Required - Roles: `ADMIN`, `FINANCE`

**Description:** Approve a bill for sending to customer.

**Path Parameters:**
- `id` (UUID, required) - Bill ID

**Response:** `200 OK`

**Validations:**
- Bill must be in DRAFT status

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Bill not found
- `400 Bad Request` - Bill not in DRAFT status
- `500 Internal Server Error` - Server error

---

## Payments Endpoints

### Base Path: `/api/v1/payments`

#### 1. List Payments

**Endpoint:** `GET /api/v1/payments`

**Authentication:** Required - Roles: `ADMIN`, `FINANCE`

**Description:** List all payments with optional filtering and pagination.

**Query Parameters:**
- `page` (integer, default: 0) - Page number
- `size` (integer, default: 20) - Records per page
- `sort` (string, default: "createdAt,desc") - Sort criteria
- `id` (UUID, optional) - Filter by payment ID
- `customerId` (UUID, optional) - Filter by customer ID
- `billId` (UUID, optional) - Filter by bill ID
- `paymentMethod` (enum, optional) - Filter by method (BANK_TRANSFER, CASH, MOBILE_MONEY, CHEQUE)
- `paymentStatus` (enum, optional) - Filter by status (PENDING, COMPLETED, FAILED, REVERSED)

**Response:** `200 OK` (Paginated list of payments)

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `500 Internal Server Error` - Server error

---

#### 2. Record Payment

**Endpoint:** `POST /api/v1/payments`

**Authentication:** Required - Roles: `ADMIN`, `FINANCE`

**Description:** Record a new payment from customer.

**Request Body:**
```json
{
  "billId": "550e8400-e29b-41d4-a716-446655440005",
  "customerId": "550e8400-e29b-41d4-a716-446655440001",
  "amount": 10000,
  "paymentMethod": "BANK_TRANSFER",
  "paymentDate": "2026-05-31T10:30:00",
  "referenceNumber": "TXN123456789",
  "notes": "Full payment received"
}
```

**Response:** `201 Created`

**Validations:**
- Bill ID must exist
- Customer ID must exist and match bill customer
- Amount must be positive and <= bill amount
- Payment method must be valid enum
- Payment date must not be in future
- Reference number must be unique

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `400 Bad Request` - Validation failure
- `404 Not Found` - Bill or customer not found
- `500 Internal Server Error` - Server error

---

## Notifications Endpoints

### Base Path: `/api/v1/notifications`

#### 1. List Notifications

**Endpoint:** `GET /api/v1/notifications`

**Authentication:** Required - Roles: `ADMIN`, `FINANCE`

**Description:** List all notifications with optional filtering and pagination.

**Query Parameters:**
- `page` (integer, default: 0) - Page number
- `size` (integer, default: 20) - Records per page
- `sort` (string, default: "createdAt,desc") - Sort criteria
- `id` (UUID, optional) - Filter by notification ID
- `recipientId` (UUID, optional) - Filter by recipient user ID
- `notificationType` (enum, optional) - Filter by type (BILL_GENERATED, PAYMENT_RECEIVED, ALERT, REMINDER)
- `isRead` (boolean, optional) - Filter by read status

**Response:** `200 OK` (Paginated list of notifications)

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions
- `500 Internal Server Error` - Server error

---

## Users Endpoints

### Base Path: `/api/v1/users`

#### 1. List Users

**Endpoint:** `GET /api/v1/users`

**Authentication:** Required - Role: `ADMIN`

**Description:** List all system users with optional filtering and pagination.

**Query Parameters:**
- `page` (integer, default: 0) - Page number
- `size` (integer, default: 20) - Records per page
- `sort` (string, default: "createdAt,desc") - Sort criteria
- `id` (UUID, optional) - Filter by user ID
- `email` (string, optional) - Filter by email
- `firstName` (string, optional) - Filter by first name
- `lastName` (string, optional) - Filter by last name
- `role` (enum, optional) - Filter by role (ADMIN, FINANCE, OPERATOR, CUSTOMER)
- `status` (enum, optional) - Filter by status (ACTIVE, INACTIVE, SUSPENDED)

**Response:** `200 OK` (Paginated list of users)

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions (not ADMIN)
- `500 Internal Server Error` - Server error

---

#### 2. Create User

**Endpoint:** `POST /api/v1/users`

**Authentication:** Required - Role: `ADMIN`

**Description:** Create a new system user.

**Request Body:**
```json
{
  "email": "operator@example.com",
  "firstName": "John",
  "lastName": "Operator",
  "phone": "+250788123456",
  "role": "OPERATOR",
  "department": "Field Operations",
  "status": "ACTIVE"
}
```

**Response:** `201 Created`

**Validations:**
- Email must be unique and valid
- First name and last name are required
- Role must be valid enum value
- Phone format must be valid
- Status must be valid enum value

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions (not ADMIN)
- `400 Bad Request` - Validation failure or duplicate email
- `500 Internal Server Error` - Server error

---

#### 3. Update User Status

**Endpoint:** `PATCH /api/v1/users/{id}/status`

**Authentication:** Required - Role: `ADMIN`

**Description:** Update user account status.

**Path Parameters:**
- `id` (UUID, required) - User ID

**Request Body:**
```json
{
  "status": "INACTIVE"
}
```

**Response:** `200 OK`

**Validations:**
- Status must be valid enum value (ACTIVE, INACTIVE, SUSPENDED)

**Errors:**
- `401 Unauthorized` - Invalid or missing JWT token
- `403 Forbidden` - Insufficient permissions (not ADMIN)
- `404 Not Found` - User not found
- `400 Bad Request` - Invalid status value
- `500 Internal Server Error` - Server error

---

## Response Format

### Success Response

```json
{
  "data": {},
  "message": "Localized success message",
  "error": null,
  "status": "OK",
  "timestamp": "2026-05-31T10:30:00"
}
```

### Error Response

```json
{
  "data": null,
  "message": "Localized error message",
  "error": {
    "field": "error description",
    "anotherField": "another error description"
  },
  "status": "BAD_REQUEST",
  "timestamp": "2026-05-31T10:30:00"
}
```

### Paginated Response

```json
{
  "data": {
    "content": [],
    "totalElements": 100,
    "totalPages": 5,
    "currentPage": 0,
    "pageSize": 20,
    "hasNext": true,
    "hasPrevious": false
  },
  "message": "Records retrieved successfully",
  "error": null,
  "status": "OK",
  "timestamp": "2026-05-31T10:30:00"
}
```

---

## Error Handling

### Common Error Codes

| Code | Message | Description |
|------|---------|-------------|
| `400` | Bad Request | Request validation failed or invalid parameters |
| `401` | Unauthorized | Missing or invalid authentication token |
| `403` | Forbidden | Authenticated user lacks required permissions |
| `404` | Not Found | Requested resource not found |
| `409` | Conflict | Duplicate record or constraint violation |
| `500` | Internal Server Error | Unexpected server error |

### Validation Error Response

```json
{
  "data": null,
  "message": "Validation failed",
  "error": {
    "email": "Email must be valid format",
    "password": "Password must be at least 8 characters",
    "firstName": "First name is required"
  },
  "status": "BAD_REQUEST",
  "timestamp": "2026-05-31T10:30:00"
}
```

---

## Status Codes

| Code | Status | Use Case |
|------|--------|----------|
| `200` | OK | Successful GET, PUT, or PATCH request |
| `201` | Created | Successful POST request (resource created) |
| `204` | No Content | Successful DELETE request |
| `400` | Bad Request | Validation error or malformed request |
| `401` | Unauthorized | Missing or invalid JWT token |
| `403` | Forbidden | User lacks required permissions |
| `404` | Not Found | Resource not found |
| `409` | Conflict | Duplicate record or business logic violation |
| `500` | Internal Server Error | Server error |

---

## Authentication Flow

### 1. User Registration

```
POST /api/v1/auth/register
↓
Creates new user account
↓
Returns access_token & refresh_token
```

### 2. User Login

```
POST /api/v1/auth/authenticate
↓
Validates credentials
↓
Returns access_token & refresh_token
```

### 3. Using Access Token

```
Include JWT in Authorization header:
Authorization: Bearer {access_token}
```

### 4. Token Refresh

```
POST /api/v1/auth/refresh-token
Header: Authorization: Bearer {refresh_token}
↓
Returns new access_token
```

### 5. Password Change

```
POST /api/v1/auth/change-password
Header: Authorization: Bearer {access_token}
Body: currentPassword, newPassword, confirmPassword
```

---

## Implementation Notes

### Request Headers

Include these headers in all requests:

```
Content-Type: application/json
Accept: application/json
Accept-Language: en (or fr, rw)
Authorization: Bearer {token} (for protected endpoints)
```

### Pagination Best Practices

- Default page size is 20 records
- Maximum page size is typically 100
- Use `sort=createdAt,desc` for reverse chronological order
- Use `sort=createdAt,asc` for chronological order

### Error Handling Best Practices

1. Always check `status` field in response
2. Use `message` for user-facing communication
3. Use `error` object for detailed field-level validation failures
4. Log timestamps for audit trails
5. Implement exponential backoff for retries on `5xx` errors

### Security Recommendations

1. Store JWT tokens securely (HTTP-only cookies recommended)
2. Never log sensitive data (passwords, OTPs, JWTs)
3. Implement rate limiting on authentication endpoints
4. Use HTTPS for all API communications
5. Validate all input data on the client side and server side
6. Implement CORS policies appropriate for your frontend domain

---

## Swagger/OpenAPI Documentation

The API includes interactive API documentation available at:

```
{base_url}/swagger-ui.html
{base_url}/v3/api-docs
```

This documentation is auto-generated from Spring annotations and available for testing endpoints directly.

---

**Last Updated:** June 5, 2026  
**Version:** 1.0  
**API Base URL:** `/api/v1`

