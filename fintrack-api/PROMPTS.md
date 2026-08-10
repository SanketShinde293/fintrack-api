PROMPTS LOG — fintrack-api

1) Role-Based Prompt
- Prompt: "You are a senior Java backend engineer. Implement a transaction service and controller for a Spring Boot 3 application that enforces user-scoped data access, uses BigDecimal for monetary amounts, and includes Jakarta Validation on DTOs. Provide minimal, testable code."  
- Technique: Assigns a persona and expected engineering standards to influence safe, production-oriented code generation.

2) Constraint Prompt
- Prompt: "Generate code only using Java 17, Spring Boot 3, JPA (Hibernate), and H2 for tests. Never use primitive floating types for currency. All public delete operations must be scoped by userId. Keep methods small and testable."  
- Technique: Hard constraints to avoid unsafe patterns (currency precision, unscoped deletes) and target toolchain.

3) Decomposition Prompt
- Prompt: "Break down the task: 1) scaffold entity/DTO/repository, 2) implement service with ownership checks, 3) implement controller using Principal, 4) add unit tests and a DataJpaTest. Output only files and changes per step."  
- Technique: Decomposes a larger feature into ordered sub-tasks to incrementally produce and verify code.

4) Few-Shot Prompt
- Prompt: "Example: (1) Entity: Transaction {id, userId, BigDecimal amount, Instant timestamp}, (2) Repository: findByUserId, (3) Service: create(dto, userId) returns Transaction, (4) Test: service.create sets user and timestamp. Now implement the full module following these examples."  
- Technique: Provide short examples to bias outputs to desired patterns and testing style.

Post-Generation Corrections
- After generation, manually inspect for: any `double`/`float` usages, unscoped delete endpoints, missing `@Valid` on controller inputs, and Mockito/ByteBuddy inline mocking in tests that might fail on modern JVMs. Replace problematic patterns with `BigDecimal`, `Principal`-scoped service methods, and standalone MockMvc tests or stubs when necessary.
# PROMPTS
Helpful prompts for contributors and copilot usage.
