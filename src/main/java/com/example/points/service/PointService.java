package com.example.points.service;

import com.example.points.entity.PointAccount;
import com.example.points.repository.PointAccountRepository;

public class PointService {
    private final PointAccountRepository pointAccountRepository;

    public PointService(PointAccountRepository pointAccountRepository) {
        this.pointAccountRepository = pointAccountRepository;
    }

    // 신규계정 생성
    public PointAccount createAccount(String userId) {
        PointAccount account = new PointAccount(userId,0L);
        return pointAccountRepository.save(account);
    }

    // 포인트 충전
    public PointAccount earn(String userId, long amount) {
        // 1. 기존 계정 조회
        PointAccount acc = pointAccountRepository.findByUserId(userId)
                .orElseThrow(()-> new IllegalArgumentException("계정을 찾을 수 없습니다."));

        // 2. 포인트 충전
        acc.earn(amount);

        // 3. 저장 후 반환
        return pointAccountRepository.save(acc);
    }
}
