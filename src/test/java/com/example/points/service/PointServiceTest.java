package com.example.points.service;

import com.example.points.repository.PointAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class) // 순수 단위 테스트용. Mockito가 @Mock 초기화. 스프링 컨텍스트 없음
public class PointServiceTest {
    @InjectMocks PointService pointService; // 목을 주입받은 서비스
    @Mock PointAccountRepository pointAccountRepository; // 외부 협력자 목

    // ========== 🔴 RED:  실패하는 테스트 ===========
    @Test
    void 신규계정_잔액은_0원_RED(){
        //given
        String userId = "user1";

        //when
        var account = pointService.createAccount(userId); //  에러!

        //then
        assertThat(account.getBalance()).isZero();
    }



}
