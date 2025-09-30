package com.example.points.entity;

public class PointAccount {
    private Long id;
    private String userId;
    private long balance = 0L; // 포인트

    public Long getId() { return id; }
    public String getUserId() {return userId;}
    public long getBalance() {return balance;}

    // 기본 생성자 (JPA 필수)
    protected PointAccount() {}

    public PointAccount(String userId, Long balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public PointAccount(Long id, String userId, Long balance) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
    }

    public PointAccount earn(long amount) {
        if (amount <= 0) throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        if (amount > 10_000_000) throw new IllegalArgumentException("충전 최대 금액은 1천만원입니다.");
        this.balance += amount; // 현재 객체의 balance 수정
        return this;
    }

}
