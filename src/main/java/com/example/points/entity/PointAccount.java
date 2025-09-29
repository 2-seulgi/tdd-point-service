package com.example.points.entity;

public class PointAccount {
    private final String userId;
    private long balance = 0L; // 포인트

    public String getUserId() {return userId;}
    public long getBalance() {return balance;}

    public PointAccount(String userId) {
        this.userId = userId;
    }

    public void earn(long amount) {
        if (amount <= 0) throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        if (amount > 10_000_000) throw new IllegalArgumentException("충전 최대 금액은 1천만원입니다.");
        balance += amount;
    }

}
