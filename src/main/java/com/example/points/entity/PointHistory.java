package com.example.points.entity;

import java.time.Instant;

public class PointHistory {
    public enum Type { EARN, USE }

    private final Long id;
    private final String userId;
    private final Type type;
    private final long amount;
    private final long balanceAfter;
    private final Instant occurredAt;

    public PointHistory(Long id, String userId, Type type, long amount, long balanceAfter, Instant occurredAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.occurredAt = occurredAt;
    }

    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public Type getType() { return type; }
    public long getAmount() { return amount; }
    public long getBalanceAfter() { return balanceAfter; }
    public Instant getOccurredAt() { return occurredAt; }
}

