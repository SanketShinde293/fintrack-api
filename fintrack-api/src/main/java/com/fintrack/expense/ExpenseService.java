package com.fintrack.expense;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class ExpenseService {

    @PersistenceContext
    private EntityManager em;

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    public SharedExpense createSharedExpense(SharedExpense expense) {
        if (expense.getParticipants() == null || expense.getParticipants().size() < 2) {
            throw new IllegalArgumentException("At least two participants are required");
        }

        BigDecimal total = expense.getTotalAmount();
        int n = expense.getParticipants().size();

        if (expense.getSplitType() == SplitType.EQUAL) {
            BigDecimal per = total.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);
            List<ParticipantSplit> adjusted = new ArrayList<>();
            for (ParticipantSplit p : expense.getParticipants()) {
                adjusted.add(new ParticipantSplit(p.userId(), per));
            }
            expense.setParticipants(adjusted);
        } else { // CUSTOM
            BigDecimal sum = expense.getParticipants().stream()
                    .map(ParticipantSplit::amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (sum.compareTo(total) != 0) {
                throw new IllegalArgumentException("Custom splits must sum to totalAmount");
            }
        }

        expense.setCreatedAt(LocalDateTime.now());
        em.persist(expense);
        em.flush();
        return expense;
    }

    public List<NetBalance> getNetBalancesForUser(String requestingUserId, String targetUserId) {
        if (requestingUserId == null || !requestingUserId.equals(targetUserId)) {
            throw new SecurityException("Access denied: requesting user does not match target user");
        }

        List<SharedExpense> all = em.createQuery("select e from SharedExpense e", SharedExpense.class).getResultList();

        Map<String, BigDecimal> net = new HashMap<>();

        for (SharedExpense e : all) {
            String creator = e.getCreatorId();
            if (creator.equals(targetUserId)) {
                // creator paid; others owe creator
                for (ParticipantSplit p : e.getParticipants()) {
                    if (!p.userId().equals(creator)) {
                        BigDecimal owe = p.amount();
                        net.put(p.userId(), net.getOrDefault(p.userId(), ZERO).add(owe.negate()));
                        net.put(creator, net.getOrDefault(creator, ZERO).add(owe));
                    }
                }
            } else {
                // check if target is a participant
                for (ParticipantSplit p : e.getParticipants()) {
                    if (p.userId().equals(targetUserId)) {
                        BigDecimal share = p.amount();
                        // target owes creator
                        net.put(creator, net.getOrDefault(creator, ZERO).add(share));
                        net.put(targetUserId, net.getOrDefault(targetUserId, ZERO).subtract(share));
                    }
                }
            }
        }

        // Build list of NetBalance for other users (excluding target)
        List<NetBalance> result = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> e : net.entrySet()) {
            String other = e.getKey();
            if (other.equals(targetUserId)) continue;
            BigDecimal otherBalance = e.getValue();
            // Interpret positive otherBalance == other owes target? Our accumulation: creator got positive when others owe.
            // We want netAmount such that positive means otherUser owes target.
            BigDecimal netAmount = otherBalance;
            result.add(new NetBalance(other, netAmount));
        }

        return result;
    }
}

