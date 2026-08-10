# fintrack-api

fintrack-api

Overview
--------
fintrack-api is a small Spring Boot 3 Java 17 project that demonstrates a transactional ledger and an expense-splitting module. It focuses on correct financial types (BigDecimal), user-scoped persistence, and layered architecture with tests.

Quick Start
-----------
Prerequisites: Java 17, Maven.

Build & test:
```bash
mvn -f fintrack-api/pom.xml clean test
```

Run locally:
```bash
mvn -f fintrack-api/pom.xml spring-boot:run
```

Project Structure
-----------------
- `src/main/java/com/fintrack/transaction` — transaction entity/service/controller and tests
- `src/main/java/com/fintrack/expense` — expense splitting module (entities, service, controller)
- `src/test/java/...` — unit and integration tests

Tech Stack
----------
- Java 17
- Spring Boot 3.2.0
- Spring Data JPA (Hibernate)
- Jakarta Validation
- H2 in-memory database for tests
- JUnit 5 + Spring Boot Test

Notes
-----
- All monetary values use `BigDecimal` with explicit `precision` and `scale` JPA settings.  
- Service methods enforce ownership via the authenticated `Principal` and repository queries that include `userId`.  
- Tests avoid fragile Mockito inline mocking on newer JDKs by using stubs or standalone MockMvc where appropriate.
