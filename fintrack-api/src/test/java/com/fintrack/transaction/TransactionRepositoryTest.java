package com.fintrack.transaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository repo;

    @Test
    void save_and_findByUserId() {
        Transaction t1 = new Transaction("user-a", new BigDecimal("10.00"), "t1", LocalDateTime.now());
        Transaction t2 = new Transaction("user-b", new BigDecimal("5.00"), "t2", LocalDateTime.now());
        repo.save(t1);
        repo.save(t2);

        List<Transaction> userA = repo.findByUserId("user-a");
        assertThat(userA).hasSize(1);
        Transaction fetched = userA.get(0);

        assertThat(fetched.getUserId()).isEqualTo("user-a");
        assertThat(repo.findByUserId("user-b")).hasSize(1);
    }
}
