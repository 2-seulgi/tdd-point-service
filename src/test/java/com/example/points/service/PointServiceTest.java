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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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
        long amount = 50L;
        PointAccount existingAccount = new PointAccount(1L, userId, 100L); // ID 있는 기존 계정

        given(pointAccountRepository.findByUserId(userId))
                .willReturn(Optional.of(existingAccount));
        given(pointAccountRepository.save(any(PointAccount.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        //when
        PointAccount result = pointService.earn(userId,amount);
        // 1. pointAccountRepository.findByUserId("user1") 호출 → 잔액 100인 계정 반환
        // 2. pointService.earn 호출 → 새로운 PointAccount(userId, 150L) 생성
        // 3. pointAccountRepository.save(충전된계정) 호출 → Mock이 그대로 반환

        //then
        assertThat(result.getId()).isEqualTo(1L); // 같은 ID 유지 (UPDATE 확인)
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getBalance()).isEqualTo(150L);
        // UPDATE가 호출되었는지 검증
        verify(pointAccountRepository).save(any(PointAccount.class));

    }

    @Test
    void 음수_충전시_예외발생(){
        // given
        String userId = "user1";
        long amount = -10L;
        PointAccount existingAccount = new PointAccount(1L, userId, 100L); // ID 있는 기존 계정
        given(pointAccountRepository.findByUserId(userId))
                .willReturn(Optional.of(existingAccount));

        //when & then
        assertThrows(IllegalArgumentException.class,
                () -> pointService.earn(userId, amount));

    }

    @Test
    void 천만원_초과_충전시_예외발생(){
        String userId = "user1";
        long amount = 10_000_001L;
        PointAccount existingAccount = new PointAccount(1L, userId, 100L);
        given(pointAccountRepository.findByUserId(userId))
                .willReturn(Optional.of(existingAccount));
        // when&then
        assertThrows(IllegalArgumentException.class,
                () -> pointService.earn(userId, amount));
    }


}
