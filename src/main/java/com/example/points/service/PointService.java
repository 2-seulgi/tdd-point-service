package com.example.points.service;

import com.example.points.entity.PointAccount;
import com.example.points.entity.PointHistory;
import com.example.points.repository.PointAccountRepository;
import com.example.points.repository.PointHistoryRepository;

import java.time.Instant;
import java.util.List;

public class PointService {
    private final PointAccountRepository pointAccountRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public PointService(PointAccountRepository pointAccountRepository, PointHistoryRepository pointHistoryRepository) {
        this.pointAccountRepository = pointAccountRepository;
        this.pointHistoryRepository = pointHistoryRepository;
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
        PointAccount saved = pointAccountRepository.save(acc);
        // 3. 히스토리 기록
        pointHistoryRepository.save(new PointHistory(
                null, userId, PointHistory.Type.EARN, amount, saved.getBalance(), Instant.now()
        ));

        // 4. 저장 후 반환
        return saved;
    }

    // 포인트 사용
    public PointAccount use(String userId, long amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("사용 포인트는 0보다 커야 합니다.");
        }
        // 1. 기존 계정 조회
        PointAccount acc = pointAccountRepository.findByUserId(userId)
                .orElseThrow(()-> new IllegalArgumentException("계정을 찾을 수 없습니다."));
        //2. 잔액 검증
        if(acc.getBalance() < amount) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        // 3. 포인트 사용
        acc.use(amount);
        PointAccount saved = pointAccountRepository.save(acc);

        // 4. 히스토리 기록
        pointHistoryRepository.save(new PointHistory(
                null, userId, PointHistory.Type.USE, amount, saved.getBalance(), Instant.now()
        ));

        // 5. 저장 후 반환
        return saved;
    }

    public List<PointHistory> getHistories(String userId) {
        return pointHistoryRepository.findAllByUserIdOrderByOccurredAtDesc(userId);
    }
}
