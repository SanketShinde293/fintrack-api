TOOL STRATEGY & USAGE LOG

## Feature Usage Log

| # | Action / Feature Used | Copilot Feature Name | Purpose |
|--:|----------------------|----------------------|--------|
| 1 | Repo scaffold generation | Code completions (file generation) | Create initial Spring Boot structure (entities, controllers, pom). Human validated and hardened outputs. |
| 2 | Service implementation | Chat/code edit | Generate service method bodies and business logic scaffolding. Human enforced ownership checks and precision rules. |
| 3 | Test generation & iteration | Test template generation | Produce unit/integration test scaffolds; human replaced fragile inline mocking with stubs. |
| 4 | Expense module generation | Inline code suggestions | Create SharedExpense entity and net-balance logic; human ensured `creatorId` and constraints. |
| 5 | Documentation generation | Long-form text generation | Draft `REVIEW.md`, `PROMPTS.md`, and PR docs; human edited for accuracy and completeness. |
| 6 | CI troubleshooting | Regeneration + local testing | Iterate on failing tests and adjust code/tests for deterministic `BigDecimal` usage. |

## Developer Scenarios & Assistant Responses
1) "I need a safe money type": Respond: "Use `BigDecimal` with `precision/scale` in JPA columns. Use string constructors or `BigDecimal.valueOf` and define rounding for divisions (RoundingMode.HALF_UP)."
2) "Controller tests failing due to security autoconfig": Respond: "Use standalone MockMvc or `@WebMvcTest(excludeAutoConfiguration=...)` and mock only needed beans; prefer stubs for services when ByteBuddy issues occur."
3) "How to prevent IDOR": Respond: "Add `userId` scoping to repository queries (e.g., `findByIdAndUserId`) and enforce in service using the authenticated principal before returning data."
4) "CI shows ByteBuddy errors on modern JVM": Respond: "Avoid Mockito inline mocking or upgrade ByteBuddy/Mockito to supported versions; alternatively run tests with a supported JDK in CI."
5) "I want net balances interpretation": Respond: "Define canonical sign: positive `netAmount` means otherUser owes the target. Document and test this convention."
6) "I need to add an audit for mass deletes": Respond: "Disallow public mass-delete endpoints; implement admin-only APIs with explicit audit logs and confirmation flows, and require `@PreAuthorize('hasRole("ADMIN")')` plus audit events."

## Limitations Encountered Table

| # | Situation (what happened) | How Detected | Fix Applied |
|--:|---------------------------|--------------|------------|
| 1 | AI generated `double` for monetary fields | Code review and grep for `double` | Replaced with `BigDecimal` across model, DTOs, services, and tests; added precision/scale. |
| 2 | Controller tests used inline Mockito/ByteBuddy causing CI failures | Running `mvn test` on modern JDK surfaced ByteBuddy errors | Rewrote controller tests to use standalone `MockMvc` and hand-written stubs; pinned mocking deps in CI if needed. |
| 3 | Suggested mass-delete endpoint (`deleteAll`) in generated code | API review and security assessment | Removed or restricted endpoint; added `Principal` scoping or admin-only path with audit logs. |

## Tool Strategy
Notes about tools and CI: prefer iterative generation + human review; add repo checks to prevent `double`/`float` regressions and consider adding tests that verify total arithmetic invariants for SharedExpense splitting.
