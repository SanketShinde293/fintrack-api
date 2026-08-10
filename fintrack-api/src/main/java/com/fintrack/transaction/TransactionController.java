package com.fintrack.transaction;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    public Transaction create(@RequestBody @Valid CreateTransactionDto dto, Principal principal) {
        String currentUser = principal.getName();
        return service.createTransaction(dto, currentUser);
    }

    @GetMapping("/{userId}")
    public List<Transaction> getByUser(@PathVariable("userId") String userId, Principal principal) {
        String currentUser = principal.getName();
        return service.getTransactionsByUser(currentUser, userId);
    }
}
