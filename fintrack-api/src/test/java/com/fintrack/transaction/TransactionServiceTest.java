package com.fintrack.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {
    private TransactionRepository repo;
    private TransactionService service;

    @BeforeEach
    void setUp() {
        repo = mock(TransactionRepository.class);
        service = new TransactionService(repo);
    }

    @Test
    void create_setsUserAndTimestamp_andSaves() {
        CreateTransactionDto dto = new CreateTransactionDto(new BigDecimal("12.34"), "coffee");
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        Transaction saved = service.createTransaction(dto, "user-1");

        assertNotNull(saved);
        assertEquals("user-1", saved.getUserId());
        assertEquals(new BigDecimal("12.34"), saved.getAmount());
        assertNotNull(saved.getCreatedAt());
        verify(repo, times(1)).save(any(Transaction.class));
    }

    @Test
    void getByUser_returnsRepoResults() {
        Transaction t = new Transaction("user-1", new BigDecimal("5.00"), "note", LocalDateTime.now());
        when(repo.findByUserId("user-1")).thenReturn(List.of(t));

        List<Transaction> res = service.getTransactionsByUser("user-1", "user-1");

        assertEquals(1, res.size());
        assertEquals("user-1", res.get(0).getUserId());
    }

    @Test
    void getTransactionsByUser_throws_on_mismatch() {
        Transaction t = new Transaction("user-1", new BigDecimal("5.00"), "note", LocalDateTime.now());
        when(repo.findByUserId("user-1")).thenReturn(List.of(t));

        SecurityException ex = assertThrows(SecurityException.class, () ->
            service.getTransactionsByUser("user-2", "user-1")
        );
        assertNotNull(ex.getMessage());
    }
}
