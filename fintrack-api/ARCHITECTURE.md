ARCHITECTURE — fintrack-api

Relationship Summary (Transactions vs SharedExpenses)

Transactions represent discrete, user-scoped financial records: deposits, withdrawals, or other ledger entries tied to a single `userId`. SharedExpenses represent group payments where a creator covers a `totalAmount` and multiple `ParticipantSplit` entries record how that total is divided among participants. The two models are related conceptually (both represent money movement) but are modeled separately: `Transaction` is a single-user ledger row while `SharedExpense` is an aggregate that yields derived `NetBalance` values between users. Services are separated so transaction CRUD and expense-splitting business logic are isolated; both layers enforce `userId` scoping, use `BigDecimal` for all monetary math, and persist via Spring Data JPA.

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
