package com.fintrack.expense;

import com.fintrack.transaction.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ExpenseServiceTest {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private TransactionService transactionService;

    @Test
    void equalSplitAmongThree() {
        SharedExpense e = new SharedExpense();
        e.setDescription("Group lunch");
        e.setTotalAmount(new BigDecimal("30.00"));
        e.setSplitType(SplitType.EQUAL);
        e.setCreatorId("u1");
        e.setParticipants(List.of(
                new ParticipantSplit("u1", BigDecimal.ZERO),
                new ParticipantSplit("u2", BigDecimal.ZERO),
                new ParticipantSplit("u3", BigDecimal.ZERO)
        ));

        SharedExpense saved = expenseService.createSharedExpense(e);

        assertThat(saved.getParticipants()).hasSize(3);
        BigDecimal expected = new BigDecimal("10.00");
        for (ParticipantSplit p : saved.getParticipants()) {
            assertThat(p.amount().setScale(2)).isEqualByComparingTo(expected.setScale(2));
        }
    }

    @Test
    void customSplitMatchingTotal() {
        SharedExpense e = new SharedExpense();
        e.setDescription("Dinner");
        e.setTotalAmount(new BigDecimal("30.00"));
        e.setSplitType(SplitType.CUSTOM);
        e.setCreatorId("u1");
        e.setParticipants(List.of(
                new ParticipantSplit("u1", new BigDecimal("10.00")),
                new ParticipantSplit("u2", new BigDecimal("20.00"))
        ));

        SharedExpense saved = expenseService.createSharedExpense(e);

        assertThat(saved.getParticipants()).hasSize(2);
        assertThat(saved.getParticipants().get(0).amount()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(saved.getParticipants().get(1).amount()).isEqualByComparingTo(new BigDecimal("20.00"));
    }

    @Test
    void customSplitTotalMismatchFailure() {
        SharedExpense e = new SharedExpense();
        e.setDescription("Mismatch");
        e.setTotalAmount(new BigDecimal("30.00"));
        e.setSplitType(SplitType.CUSTOM);
        e.setCreatorId("u1");
        e.setParticipants(List.of(
                new ParticipantSplit("u1", new BigDecimal("10.00")),
                new ParticipantSplit("u2", new BigDecimal("5.00"))
        ));

        assertThrows(IllegalArgumentException.class, () -> expenseService.createSharedExpense(e));
    }

    @Test
    void netBalanceBetweenTwoUsersAcrossExpenses() {
        // Expense 1: A pays 20 split A,B => per 10
        SharedExpense e1 = new SharedExpense();
        e1.setDescription("E1");
        e1.setTotalAmount(new BigDecimal("20.00"));
        e1.setSplitType(SplitType.EQUAL);
        e1.setParticipants(List.of(
                new ParticipantSplit("A", BigDecimal.ZERO),
                new ParticipantSplit("B", BigDecimal.ZERO)
        ));
        e1.setCreatorId("A");
        expenseService.createSharedExpense(e1);

        // Expense 2: B pays 30 split A,B => per 15
        SharedExpense e2 = new SharedExpense();
        e2.setDescription("E2");
        e2.setTotalAmount(new BigDecimal("30.00"));
        e2.setSplitType(SplitType.EQUAL);
        e2.setParticipants(List.of(
                new ParticipantSplit("A", BigDecimal.ZERO),
                new ParticipantSplit("B", BigDecimal.ZERO)
        ));
        e2.setCreatorId("B");
        expenseService.createSharedExpense(e2);

        List<NetBalance> balances = expenseService.getNetBalancesForUser("A", "A");
        // According to implemented aggregation logic, B should have net +5.00
        assertThat(balances).anyMatch(nb -> nb.otherUserId().equals("B") && nb.netAmount().compareTo(new BigDecimal("5.00")) == 0);
    }

    @Test
    void oneParticipantEdgeCaseFailure() {
        SharedExpense e = new SharedExpense();
        e.setDescription("Solo");
        e.setTotalAmount(new BigDecimal("10.00"));
        e.setSplitType(SplitType.EQUAL);
        e.setCreatorId("solo");
        e.setParticipants(List.of(new ParticipantSplit("solo", BigDecimal.ZERO)));

        assertThrows(IllegalArgumentException.class, () -> expenseService.createSharedExpense(e));
    }

    @Test
    void unauthorizedAccessAttemptOnTransactionService() {
        // TransactionService enforces requestingUserId == targetUserId
        assertThrows(SecurityException.class, () -> transactionService.getTransactionsByUser("user-x", "other-user"));
    }
}

