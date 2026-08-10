package com.fintrack.expense;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SharedExpense> create(@RequestBody @Valid SharedExpense req, Principal principal) {
        String currentUser = principal.getName();
        req.setCreatorId(currentUser);
        SharedExpense created = service.createSharedExpense(req);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/net-balances/{userId}")
    public ResponseEntity<List<NetBalance>> netBalances(@PathVariable String userId, Principal principal) {
        String currentUser = principal.getName();
        List<NetBalance> balances = service.getNetBalancesForUser(currentUser, userId);
        return ResponseEntity.ok(balances);
    }
}

