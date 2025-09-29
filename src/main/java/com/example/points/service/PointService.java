package com.example.points.service;

import com.example.points.entity.PointAccount;
import com.example.points.repository.PointAccountRepository;

public class PointService {
    private final PointAccountRepository pointAccountRepository;

    public PointService(PointAccountRepository pointAccountRepository) {
        this.pointAccountRepository = pointAccountRepository;
    }

    public PointAccount createAccount(String userId) {
        PointAccount account = new PointAccount(userId);
        return pointAccountRepository.save(account);
    }
}
