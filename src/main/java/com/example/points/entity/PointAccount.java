package com.example.points.entity;

public class PointAccount {
    private String userId;
    private long balance = 0L; // 포인트

    public String getUserId() {return userId;}
    public long getBalance() {return balance;}

    public PointAccount(String userId, long l) {
        this.userId = userId;
        this.balance = l;
    }

}
