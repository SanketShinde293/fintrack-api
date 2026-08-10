PR: Implement transaction and expense modules, tests, and review docs

Summary
- Adds a production-oriented transaction module (entity, repository, service, controller, DTOs) with JPA persistence and validation.  
- Adds an expense-splitting feature (entities, service, controller) supporting equal and custom splits and net-balance calculations.  
- Adds unit and integration tests for transaction and expense code, and `REVIEW.md`, `PROMPTS.md`, and other documentation.

AI Tool Disclosure
- Code and tests were generated and iteratively refined with the assistance of an AI coding assistant. Human review and corrections were applied to address financial precision (replacing `double` with `BigDecimal`), ownership enforcement (IDOR fixes), and test compatibility with the runtime JVM (avoiding inline Mockito/ByteBuddy mocking where it failed).

Risk Analysis
- Precision risk: initial AI suggestions used primitive floats for currency. Mitigation: replaced with `BigDecimal` everywhere and set JPA `precision/scale`.  
- Authorization risk: initial AI patterns included unscoped delete methods and lookups by id without `userId`. Mitigation: service methods require a requesting user id and use repository queries scoped to `userId`.  
- Test fragility: CI runtime JVM may conflict with inline Mockito byte-buddy instrumentation. Mitigation: prefer standalone MockMvc and hand-written stubs in controller tests; avoid Mockito inline where unsupported.  
- Raw SQL risk: avoid introducing raw JDBC in transaction/expense packages to preserve scoping and ORM-managed transactions.

Peer Review Simulation
1. Reviewer A: "Verify that `TransactionService` methods enforce ownership via repository queries that include `userId` and that any `findById` usages are removed or replaced with `findByIdAndUserId`. Add tests that attempt to access another user's data."  
2. Reviewer B: "Confirm that all monetary arithmetic uses `BigDecimal` with explicit rounding where needed. Add edge-case tests for very small/large amounts and currency scale."  
3. Reviewer C: "Check controller layer for `@Valid` usage and that DTOs enforce constraints. Confirm slice tests don't accidentally bring in security auto-configuration — adjust to standalone MockMvc if needed." 
# PR Description Template
Use this template when opening pull requests.
