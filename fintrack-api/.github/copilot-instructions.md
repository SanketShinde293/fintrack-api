# Copilot Instructions — fintrack-api (Java 17 / Spring Boot 3)

Purpose
- Provide unambiguous, enforceable guidance for code suggestions in this repository.
- Reject or flag suggestions that violate the rules below.

Strict rules (must be enforced by suggestions)
1. Language, framework & architecture
	- Target: Java 17 and Spring Boot 3 (Spring 6 / Jakarta packages).
	- Follow a layered architecture: Controller -> Service -> Repository -> Entity.
	- Use `jakarta.persistence`, `jakarta.validation`, `org.springframework` packages.

2. Monetary precision (MANDATORY)
	- ALWAYS use `java.math.BigDecimal` for monetary values, calculations and DTOs.
	- NEVER use `double` or `float` for any money-related field or calculation.
	- Use exact string-based BigDecimal construction (`new BigDecimal("12.34")`) or `BigDecimal.valueOf(long, int)` idioms.
	- When dividing, always specify scale and a `RoundingMode`.

3. Multi-tenancy & security (prevent IDOR)
	- All DB reads and writes must be scoped by `userId` (or tenant id).
	- Prefer repository methods that include `userId` in their signature (e.g. `findByIdAndUserId`, `findByUserId`, `deleteByUserId`).
	- Controllers and services must obtain the authenticated user's id from the security context (e.g. `Principal#getName()` or `SecurityContextHolder`) and use that id for scoping — never trust a client-provided `userId` for authorization.
	- For object access, either fetch by `id+userId` or fetch by `id` and compare `entity.getUserId().equals(currentUserId)` and throw a security exception on mismatch.

4. Validation
	- Use Jakarta Validation annotations on DTOs (`@NotNull`, `@NotBlank`, `@Size`, `@DecimalMin`, `@Positive`, etc.).
	- Controllers must accept DTOs annotated with `@Valid` and perform mapping in the service layer.

5. Testing (MANDATORY)
	- All service methods must have unit tests using JUnit 5 and Mockito.
	- Repository tests should use `@DataJpaTest` when needed.
	- Controller tests should use `@WebMvcTest` or `MockMvc`.
	- Tests must use deterministic `BigDecimal` values (e.g., `new BigDecimal("12.34")`).

Style & best practices (guidance)
- Entities: `@Entity` with `userId` column; prefer `BigDecimal` with appropriate `precision`/`scale`.
- Repositories: extend `JpaRepository` and include `userId`-scoped finders.
- Services: `@Service` classes contain business logic and perform ownership checks.
- Controllers: thin layers that validate DTOs, extract current user id from security principal, and call services.

Reject patterns
- Any suggestion that uses `double`/`float` for monetary data.
- DB operations without `userId` scoping or ownership checks.
- Business logic placed in controllers or repository query implementations.
- Use of `javax.*` (non-Jakarta) imports for new code targeting Spring Boot 3.

Examples (short)
Repository:
```
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  List<Transaction> findByUserId(String userId);
  Optional<Transaction> findByIdAndUserId(Long id, String userId);
  void deleteByUserId(String userId);
}
```

Service ownership check:
```
Transaction t = repo.findByIdAndUserId(id, currentUserId)
	 .orElseThrow(() -> new AccessDeniedException("Not found or not owned"));
```

Monetary arithmetic:
```
BigDecimal amount = new BigDecimal("12.34");
BigDecimal fee = amount.multiply(new BigDecimal("0.015")).setScale(2, RoundingMode.HALF_UP);
```

If a suggested snippet cannot satisfy all rules above, do not suggest it and instead ask for clarification.

-- End
