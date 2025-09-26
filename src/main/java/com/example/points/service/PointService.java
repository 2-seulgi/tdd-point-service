package com.example.points.service;

import com.example.points.entity.PointAccount;
import com.example.points.repository.PointAccountRepository;

public class PointService {
    private final PointAccountRepository pointAccountRepository;

    public PointService(PointAccountRepository pointAccountRepository) {
        this.pointAccountRepository = pointAccountRepository;
    }

    public PointAccount createAccount(String userId) {
        // 최소 구현: 일단 실패를 유발하기 위해 null 리턴 (실행 RED)
        return null;
    }
}
