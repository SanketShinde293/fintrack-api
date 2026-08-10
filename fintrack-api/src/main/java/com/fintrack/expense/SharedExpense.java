package com.fintrack.expense;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shared_expenses")
public class SharedExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String creatorId;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SplitType splitType;

    @ElementCollection
    @CollectionTable(name = "shared_expense_participants", joinColumns = @JoinColumn(name = "expense_id"))
    private List<ParticipantSplit> participants = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public SharedExpense() {}

    public SharedExpense(String creatorId, String description, BigDecimal totalAmount, SplitType splitType, List<ParticipantSplit> participants, LocalDateTime createdAt) {
        this.creatorId = creatorId;
        this.description = description;
        this.totalAmount = totalAmount;
        this.splitType = splitType;
        this.participants = participants == null ? new ArrayList<>() : participants;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public SplitType getSplitType() {
        return splitType;
    }

    public void setSplitType(SplitType splitType) {
        this.splitType = splitType;
    }

    public List<ParticipantSplit> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantSplit> participants) {
        this.participants = participants == null ? new ArrayList<>() : participants;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

