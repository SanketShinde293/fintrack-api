package com.fintrack.expense;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public record ParticipantSplit(
    @Column(name = "user_id") String userId,
    @Column(name = "amount", precision = 19, scale = 4) BigDecimal amount
) {}
