PROMPTS LOG — fintrack-api

## Prompt Chain Table

| Prompt # | Prompt Text | Copilot Feature Used | Prompting Technique Applied | Why This Approach? |
|---:|-----------|---------------------|---------------------------|-------------------|
| 1 | You are a senior Java backend engineer. Implement a transaction service and controller for a Spring Boot 3 application that enforces user-scoped data access, uses BigDecimal for monetary amounts, and includes Jakarta Validation on DTOs. Provide minimal, testable code. | Chat completions / code gen | Role-based | Assigning a persona biases outputs toward production-quality decisions and guards against simplistic examples. |
| 2 | Generate code only using Java 17, Spring Boot 3, JPA (Hibernate), and H2 for tests. Never use primitive floating types for currency. All public delete operations must be scoped by userId. Keep methods small and testable. | Constraint injection in code gen | Constraint | Hard constraints prevent unsafe patterns from being suggested and keep the generated code compatible with the intended toolchain. |
| 3 | Break down the task: 1) scaffold entity/DTO/repository, 2) implement service with ownership checks, 3) implement controller using Principal, 4) add unit tests and a DataJpaTest. Output only files and changes per step. | Guided generation / stepwise refinement | Decomposition | Produces incremental, verifiable outputs that are easier to review and test. |
| 4 | Example: (1) Entity: Transaction {id, userId, BigDecimal amount, Instant timestamp}, (2) Repository: findByUserId, (3) Service: create(dto, userId) returns Transaction, (4) Test: service.create sets user and timestamp. Now implement the full module following these examples. | Few-shot examples | Few-shot | Short examples bias code shape and test style toward desired patterns and ensure generated tests align with expectations. |

## Section 5A — Post-Generation Corrections

- Issue: Copilot produced `double` or `float` for currency in early generations.
	- Why it was wrong: Floating primitives lose precision, causing rounding errors in financial calculations.
	- How it was fixed: Replaced with `BigDecimal` in entities, DTOs, services, and tests; added JPA `precision/scale` and deterministic `BigDecimal` constructors in tests.

- Issue: Generated controller tests used inline Mockito/ByteBuddy mocks that fail on newer JDKs.
	- Why it was wrong: ByteBuddy instrumentation can be incompatible with CI JDK; tests become flaky or fail to start.
	- How it was fixed: Converted controller tests to standalone `MockMvc` with hand-written service stubs, or pinned Mockito/ByteBuddy to compatible versions in CI.

- Issue: Copilot suggested unscoped `deleteAll()` endpoints.
	- Why it was wrong: Mass deletes can cause catastrophic data loss and violate multi-tenant scoping.
	- How it was fixed: Removed or restricted mass-delete endpoints; enforce `Principal` scoping or admin-only operations with audit logs.

Helpful prompts for contributors and copilot usage.
