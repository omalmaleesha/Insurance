# Insurance Backend

Spring Boot backend for an insurance workflow system with JWT-based authentication, quotation management, proposal handling, and inter-branch file transfer.

## Project Documentation

- Full project docs: https://docs.google.com/document/d/1MkOpTZyInCsSwoOvTZfuVIjtypVtyNK9QYwuObFTzko/edit?usp=sharing

## Tech Stack

- Java 21
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA
- MySQL
- Maven
- OpenPDF

## Main Modules

- **Authentication**: user registration and token generation (`/auth`)
- **Quotations**: create/update/calculate/approve/reject/issue and PDF download (`/api/quotations`)
- **Proposals**: create proposals and customer form submission flow (`/api/proposals`)
- **Proposal Emails**: send/re-send proposal related emails (`/api/proposal-emails`)
- **File Transfer**: shipment and document transfer between branches (`/api/file-transfer`)

## Prerequisites

- JDK 21
- MySQL 8+

## Configuration

Update database settings in:

- `/home/runner/work/insurance/insurance/src/main/resources/application.properties`

Key properties:

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`

Database schema and sample data are available in:

- `/home/runner/work/insurance/insurance/src/main/resources/schema.sql`
- `/home/runner/work/insurance/insurance/src/main/resources/data.sql`

## Run the Project

From `/home/runner/work/insurance/insurance`:

```bash
./mvnw spring-boot:run
```

## Run Tests

```bash
./mvnw test
```
