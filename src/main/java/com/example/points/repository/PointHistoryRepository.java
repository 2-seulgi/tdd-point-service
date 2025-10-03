package com.example.points.repository;

import com.example.points.entity.PointHistory;

import java.util.List;

public interface PointHistoryRepository {
    PointHistory save(PointHistory history);
    List<PointHistory> findAllByUserIdOrderByOccurredAtDesc(String userId);

}
