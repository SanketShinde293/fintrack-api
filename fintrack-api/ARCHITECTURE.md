ARCHITECTURE — fintrack-api

Layered System Flow (ASCII diagram)

Client
  |
  v
REST Controller Layer (TransactionController / ExpenseController)
  |
  v
Service Layer (TransactionService, ExpenseService)
  - Enforces ownership, validation, business rules
  |
  v
Repository / Persistence (Spring Data JPA / EntityManager)
  - Entities: Transaction, SharedExpense (+ ParticipantSplit)
  |
  v
Relational DB (H2 for tests, replaceable with production DB)

ASCII Diagram

Client -> [Controller] -> [Service] -> [Repository] -> [Database]

Domain Model Interactions
- Transaction: `id:Long`, `userId:String`, `amount:BigDecimal`, `description:String`, `createdAt:LocalDateTime`  
- SharedExpense: `id:Long`, `creatorId:String`, `description:String`, `totalAmount:BigDecimal`, `splitType:ENUM`, `participants:List<ParticipantSplit>`, `createdAt:LocalDateTime`  
- ParticipantSplit (embeddable): `userId:String`, `amount:BigDecimal`  
- NetBalance (derived): `otherUserId:String`, `netAmount:BigDecimal` — produced by aggregating `SharedExpense` participant shares and creator-paid amounts.

Notes on Flow
- Controllers accept DTOs (validated with Jakarta Validation) and obtain the caller identity via `Principal`.  
- Services enforce authorization and business logic (e.g., BigDecimal arithmetic with explicit rounding for equal splits).  
- Repositories perform scoped queries (e.g., `findByUserId`) to avoid IDOR vulnerabilities.  
- Tests: unit tests for services and `@DataJpaTest` for repository behavior; controllers use standalone MockMvc where mocking frameworks cause runtime issues.
# Architecture
High-level architecture notes for fintrack-api.
