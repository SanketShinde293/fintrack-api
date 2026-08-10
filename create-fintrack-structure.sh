#!/usr/bin/env bash
set -euo pipefail

ROOT="fintrack-api"

echo "Creating project structure in ./${ROOT}"

# Directories
mkdir -p "${ROOT}/.github"
mkdir -p "${ROOT}/src/main/java/com/fintrack/transaction"
mkdir -p "${ROOT}/src/main/java/com/fintrack/expense"
mkdir -p "${ROOT}/src/main/resources"
mkdir -p "${ROOT}/src/test/java/com/fintrack/expense"

# Files: .github
cat > "${ROOT}/.github/copilot-instructions.md" <<'EOF'
# Copilot Instructions
Provide helpful code suggestions for the fintrack-api project.
EOF

# Files: Application and transaction package
cat > "${ROOT}/src/main/java/com/fintrack/Application.java" <<'EOF'
package com.fintrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
EOF

cat > "${ROOT}/src/main/java/com/fintrack/transaction/Transaction.java" <<'EOF'
package com.fintrack.transaction;

import java.math.BigDecimal;
import java.time.Instant;

public class Transaction {
    private String id;
    private BigDecimal amount;
    private Instant timestamp;
    // getters/setters omitted for brevity
}
EOF

cat > "${ROOT}/src/main/java/com/fintrack/transaction/TransactionRepository.java" <<'EOF'
package com.fintrack.transaction;

import java.util.List;

public interface TransactionRepository {
    Transaction save(Transaction t);
    List<Transaction> findAll();
}
EOF

cat > "${ROOT}/src/main/java/com/fintrack/transaction/TransactionService.java" <<'EOF'
package com.fintrack.transaction;

import java.util.List;

public class TransactionService {
    private final TransactionRepository repo;
    public TransactionService(TransactionRepository repo) {
        this.repo = repo;
    }
    public Transaction create(Transaction t) {
        return repo.save(t);
    }
    public List<Transaction> list() {
        return repo.findAll();
    }
}
EOF

cat > "${ROOT}/src/main/java/com/fintrack/transaction/TransactionController.java" <<'EOF'
package com.fintrack.transaction;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService service;
    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    public Transaction create(@RequestBody Transaction t) {
        return service.create(t);
    }

    @GetMapping
    public List<Transaction> list() {
        return service.list();
    }
}
EOF

# Files: expense package
cat > "${ROOT}/src/main/java/com/fintrack/expense/SplitType.java" <<'EOF'
package com.fintrack.expense;

public enum SplitType {
    EQUAL, PERCENTAGE, EXACT
}
EOF

cat > "${ROOT}/src/main/java/com/fintrack/expense/ParticipantSplit.java" <<'EOF'
package com.fintrack.expense;

import java.math.BigDecimal;

public class ParticipantSplit {
    private String participantId;
    private BigDecimal amount;
    // getters/setters omitted
}
EOF

cat > "${ROOT}/src/main/java/com/fintrack/expense/SharedExpense.java" <<'EOF'
package com.fintrack.expense;

import java.math.BigDecimal;
import java.util.List;

public class SharedExpense {
    private String id;
    private BigDecimal total;
    private List<ParticipantSplit> splits;
    private SplitType splitType;
    // getters/setters omitted
}
EOF

cat > "${ROOT}/src/main/java/com/fintrack/expense/NetBalance.java" <<'EOF'
package com.fintrack.expense;

import java.math.BigDecimal;

public class NetBalance {
    private String participantId;
    private BigDecimal balance;
    // getters/setters omitted
}
EOF

cat > "${ROOT}/src/main/java/com/fintrack/expense/ExpenseService.java" <<'EOF'
package com.fintrack.expense;

import java.util.List;

public class ExpenseService {
    public List<NetBalance> calculateNetBalances(SharedExpense expense) {
        return List.of(); // placeholder
    }
}
EOF

cat > "${ROOT}/src/main/java/com/fintrack/expense/ExpenseController.java" <<'EOF'
package com.fintrack.expense;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService service;
    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    @PostMapping("/split")
    public List<NetBalance> split(@RequestBody SharedExpense expense) {
        return service.calculateNetBalances(expense);
    }
}
EOF

# Resources
cat > "${ROOT}/src/main/resources/application.yml" <<'EOF'
spring:
  application:
    name: fintrack-api
server:
  port: 8080
EOF

# Test
cat > "${ROOT}/src/test/java/com/fintrack/expense/ExpenseServiceTest.java" <<'EOF'
package com.fintrack.expense;

import org.junit.jupiter.api.Test;

public class ExpenseServiceTest {

    @Test
    void placeholderTest() {
        ExpenseService svc = new ExpenseService();
        // add real tests later
    }
}
EOF

# Root docs and config
cat > "${ROOT}/README.md" <<'EOF'
# fintrack-api

Java 17 Spring Boot API for expense tracking.
EOF

cat > "${ROOT}/REVIEW.md" <<'EOF'
# REVIEW
Guidelines for reviewing pull requests.
EOF

cat > "${ROOT}/PROMPTS.md" <<'EOF'
# PROMPTS
Helpful prompts for contributors and copilot usage.
EOF

cat > "${ROOT}/PR_DESCRIPTION.md" <<'EOF'
# PR Description Template
Use this template when opening pull requests.
EOF

cat > "${ROOT}/TOOL_STRATEGY.md" <<'EOF'
# Tool Strategy
Notes about tools and CI.
EOF

cat > "${ROOT}/ARCHITECTURE.md" <<'EOF'
# Architecture
High-level architecture notes for fintrack-api.
EOF

# Minimal pom.xml for Java 17 + Spring Boot
cat > "${ROOT}/pom.xml" <<'EOF'
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.fintrack</groupId>
  <artifactId>fintrack-api</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <packaging>jar</packaging>

  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
    <relativePath/> <!-- lookup parent from repository -->
  </parent>

  <properties>
    <java.version>17</java.version>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
</project>
EOF

echo "Structure creation complete: ./${ROOT}"
echo "Created files:"
find "${ROOT}" -type f | sed 's|^|  |'
