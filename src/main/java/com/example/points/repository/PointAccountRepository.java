package com.example.points.repository;

import com.example.points.entity.PointAccount;

import java.util.Optional;

//인터페이스: 구현은 아직 필요 없음.
public interface PointAccountRepository  {

    PointAccount save(PointAccount account);
    Optional<PointAccount> findByUserId(String userId);
}
