package com.example.points.service;

import com.example.points.entity.PointAccount;
import com.example.points.repository.PointAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class) // 순수 단위 테스트용. Mockito가 @Mock 초기화. 스프링 컨텍스트 없음
public class PointServiceTest {
    @InjectMocks PointService pointService; // 목을 주입받은 서비스
    @Mock PointAccountRepository pointAccountRepository; // 외부 협력자 목

    // ========== 🔵 REFACTOR: 리팩토링 ===========
    @Test
    void 신규계정_잔액은_0원_REFACTOR(){
        //given
        String userId = "user1";

        // Mock 설정 추가
        given(pointAccountRepository.save(any(PointAccount.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        //when
        var account = pointService.createAccount(userId);
        // 1. new PointAccount("user1") 생성
        // 2. pointAccountRepository.save(account) 호출
        // 3. Mock 설정 통해 save가 "넘긴 객체 그대로" 반환

        //then
        assertThat(account.getBalance()).isZero();
        assertThat(account.getUserId()).isEqualTo(userId);
    }

    @Test
    void 충전하면_잔액이_증가한다(){
        //given
        String userId = "user1";
        long amount = 500_000L;
        PointAccount account = new PointAccount(userId);
        given(pointAccountRepository.findByUserId(userId))
                .willReturn(Optional.of(account));
        //when
        pointService.earn(userId,amount);

        //then
        assertThat(account.getUserId()).isEqualTo(userId);
        assertThat(account.getBalance()).isEqualTo(amount);
    }





}
