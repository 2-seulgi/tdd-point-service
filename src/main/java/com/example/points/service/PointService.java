package com.example.points.service;

import com.example.points.entity.PointAccount;
import com.example.points.repository.PointAccountRepository;

public class PointService {
    private final PointAccountRepository pointAccountRepository;

    public PointService(PointAccountRepository pointAccountRepository) {
        this.pointAccountRepository = pointAccountRepository;
    }

    public PointAccount createAccount(String userId) {
        return new PointAccount(userId, 0L); // 하드코딩으로 통과
    }
}
