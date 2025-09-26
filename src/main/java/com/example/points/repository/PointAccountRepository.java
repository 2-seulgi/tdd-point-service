package com.example.points.repository;

import com.example.points.entity.PointAccount;

import java.util.Optional;

public interface PointAccountRepository  {
    PointAccount save(PointAccount account);
}
