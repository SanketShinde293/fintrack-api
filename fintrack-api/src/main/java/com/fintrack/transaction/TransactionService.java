package com.fintrack.transaction;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository repo;

    public TransactionService(TransactionRepository repo) {
        this.repo = repo;
    }

    public Transaction createTransaction(CreateTransactionDto dto, String userId) {
        Transaction t = new Transaction(userId, dto.getAmount(), dto.getDescription(), LocalDateTime.now());
        return repo.save(t);
    }

    public List<Transaction> getTransactionsByUser(String requestingUserId, String targetUserId) {
        if (requestingUserId == null || !requestingUserId.equals(targetUserId)) {
            throw new SecurityException("Access denied: requesting user does not match target user");
        }
        return repo.findByUserId(targetUserId);
    }
}
