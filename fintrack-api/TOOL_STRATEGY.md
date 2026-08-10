TOOL STRATEGY & USAGE LOG

Feature Usage Log (chronological)
1) Repo scaffold generation — used code generation to create initial Spring Boot structure (entities, controllers, pom). Verified and replaced unsafe patterns.  
2) Service implementation — used AI to write service methods; human rewrote ownership checks to call `findByUserId` and throw SecurityException on mismatch.  
3) Tests creation — generated unit tests; replaced fragile Mockito inline mocks with stubs when ByteBuddy failed under Java 25.  
4) Expense module generation — generated entities and net-balance logic; human ensured `creatorId` is set and DB constraints are satisfied.  
5) Documentation generation — created `REVIEW.md`, `PROMPTS.md`, and PR docs summarizing human-reviewed changes.  
6) CI verification — ran `mvn test` repeatedly; fixed failing tests by adjusting constructors, DTOs, and required fields.

Developer Scenarios & Assistant Responses
1) "I need a safe money type": Respond with: "Use `BigDecimal` with `precision/scale` in JPA columns. Use string constructors or `BigDecimal.valueOf` and define rounding for divisions (RoundingMode.HALF_UP)."  
2) "Controller tests failing due to security autoconfig": Respond with: "Use standalone MockMvc or `@WebMvcTest(excludeAutoConfiguration=...)` and mock only needed beans; prefer stubs for services when ByteBuddy issues occur."  
3) "How to prevent IDOR": Respond with: "Add `userId` scoping to repository queries (e.g., `findByIdAndUserId`) and enforce in service using the authenticated principal before returning data."  
4) "CI shows ByteBuddy errors on modern JVM": Respond with: "Avoid Mockito inline mocking or upgrade ByteBuddy/Mockito to supported versions; alternatively run tests with a supported JDK in CI."  
5) "I want net balances interpretation": Respond with: "Define canonical sign: positive `netAmount` means otherUser owes the target. Document and test this convention."  
6) "I need to add an audit for mass deletes": Respond with: "Disallow public mass-delete endpoints; implement admin-only APIs with explicit audit logs and confirmation flows, and require `@PreAuthorize('hasRole("ADMIN")')` plus audit events." 

Real AI Limitation Cases (examples to monitor)
1) Currency type suggestion: AI may default to `double` for simplicity. Human must replace with `BigDecimal` and adjust tests.  
2) Ownership checks: AI-generated examples sometimes omit IDOR protections; human review must add `userId` scoping and tests.  
3) Test runtime compatibility: AI may use Mockito inline mocks that rely on bytecode manipulation incompatible with the CI JDK; substitute stubs or configure compatible mocking strategies.
# Tool Strategy
Notes about tools and CI.
