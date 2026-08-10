PR: Implement transaction and expense modules, tests, and review docs

Summary
- Adds a production-oriented transaction module (entity, repository, service, controller, DTOs) with JPA persistence and validation.
- Adds an expense-splitting feature (entities, service, controller) supporting equal and custom splits and net-balance calculations.
- Adds unit and integration tests for transaction and expense code, and `REVIEW.md`, `PROMPTS.md`, and other documentation.

AI Tool Disclosure
- Tools used: GitHub Copilot code completions, chat/code edit features, and iterative re-generation for tests.
- Accepted vs overridden: Many structural suggestions were accepted (entity/service/controller scaffolding) but security/precision fixes were overridden by humans. Rough estimate: 60% AI / 40% manual edits.

Testing & Trade-offs
- Test coverage: Unit tests for services, `@DataJpaTest` for repositories, and controller slice tests using standalone `MockMvc` with stubs. All tests exercise ownership checks and monetary arithmetic; `mvn -f fintrack-api/pom.xml test` passed in the development environment.
- Trade-off: For test stability across CI JDKs we avoided inline Mockito in controller tests (stubs instead), which increased test maintenance minimaly but improved reliability.

Self-review checklist
- [ ] Monetary fields use `BigDecimal` and have `precision/scale` where appropriate.
- [ ] All service reads are scoped by `userId` or verify ownership before returning data.
- [ ] No public mass-delete endpoints exist without admin restriction and audit.
- [ ] Controllers validate DTOs via `@Valid` and map to domain models in services.
- [ ] Tests run on local environment and avoid known ByteBuddy/Mockito inline issues.

Peer Review Simulation Table

| Reviewer | Comment | AI Blind Spot Addressed |
|---|---:|---|
| Reviewer A | Verify that `TransactionService` enforces `userId`-scoped queries and add cross-user access tests. | IDOR omission in generated code — reviewer asks for explicit `findByIdAndUserId` tests. |
| Reviewer B | Confirm `BigDecimal` usage and add high-precision edge-case tests (tiny/large amounts). | AI tendency to use `double` for simplicity; reviewer asks for deterministic constructors and scale checks. |
| Reviewer C | Validate controllers use `@Valid`, and controller tests avoid unstable inline mocking patterns. | AI-generated controller tests used inline mocking; reviewer requests stubs/MockMvc to eliminate ByteBuddy issues. |

Section 6A — Why AI Misses The Penny-Remainder Blind Spot

AI models often prioritize concise, correct-seeming arithmetic but do not internalize domain-specific rounding policies (e.g., allocating penny remainders when splitting amounts). They may produce mathematically-correct averages but not domain-appropriate remainder allocation. Humans must define the canonical rounding rules (who gets the remainder, consistent sign conventions) and add deterministic tests. In this PR we implemented `RoundingMode.HALF_UP` for equal splits and added tests that assert total shares sum to the original `totalAmount`.

# PR Description Template
Use this template when opening pull requests.
