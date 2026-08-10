package com.fintrack.expense;

import java.math.BigDecimal;

public record NetBalance(String otherUserId, BigDecimal netAmount) {}
