package com.example.points.service;

import com.example.points.entity.PointAccount;
import com.example.points.entity.PointHistory;
import com.example.points.repository.PointAccountRepository;
import com.example.points.repository.PointHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 순수 단위 테스트용. Mockito가 @Mock 초기화. 스프링 컨텍스트 없음
public class PointServiceTest {
    @InjectMocks PointService pointService; // 목을 주입받은 서비스
    @Mock PointAccountRepository pointAccountRepository; // 외부 협력자 목
    @Mock PointHistoryRepository pointHistoryRepository; // 외부 협력자 목

    // ========== 🔵 REFACTOR: 리팩토링 ===========
    @Test
    void 신규계정_잔액은_0원(){
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
        // 1) save가 어떤 값으로 호출됐는지 캡처해서 필드 검증
        ArgumentCaptor<PointAccount> captor = ArgumentCaptor.forClass(PointAccount.class);
        verify(pointAccountRepository).save(captor.capture());
        PointAccount saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getBalance()).isZero();

        // 2) 서비스 반환과 저장 인스턴스 동일성(선택: willAnswer로 같은 객체를 반환하므로 same)
        assertThat(account).isSameAs(saved);

        // 3) 불필요한 협력자 호출 없었는지 확인
        verifyNoInteractions(pointHistoryRepository);
        verifyNoMoreInteractions(pointAccountRepository);

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
        // 내부 동작(earn 메서드 안에서 일어나는 순서)
        // 1) pointAccountRepository.findByUserId("user1") 호출
        //    -> given(...) 스텁 덕분에 Optional.of(existingAccount) 반환
        // 2) existingAccount.earn(50) 호출
        //    -> balance: 100 -> 150 (엔티티 내부 상태 변경)
        // 3) pointAccountRepository.save(existingAccount) 호출
        //    -> willAnswer(...) 스텁 덕분에 전달받은 객체 그대로 반환
        // 4) result == existingAccount (동일 인스턴스, 상태만 변경)


        //then
        assertThat(result.getId()).isEqualTo(1L); // 같은 ID 유지 (UPDATE 확인)
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getBalance()).isEqualTo(150L);
        // 동일 인스턴스가 저장되었는지(인플레이스 업데이트) 확인
        verify(pointAccountRepository).save(same(existingAccount));

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

    @Test
    void 사용하면_잔액이_감소하고_내역이_기록된다(){
        //given
        String userId = "user1";
        long amount = 50L;
        PointAccount existingAccount = new PointAccount(1L, userId, 100L); // ID 있는 기존 계정
        given(pointAccountRepository.findByUserId(userId))
                .willReturn(Optional.of(existingAccount));
        given(pointAccountRepository.save(any(PointAccount.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
        given(pointHistoryRepository.save(any(PointHistory.class)))
               .willAnswer(invocation -> invocation.getArgument(0));
        //when
        PointAccount result = pointService.use(userId,amount);

        //then
        assertThat(result.getBalance()).isEqualTo(50L);
        verify(pointAccountRepository).save(org.mockito.ArgumentMatchers.same(existingAccount));

        // 사용 내역이 기록되었는지 검증
        ArgumentCaptor<PointHistory> historyCaptor = ArgumentCaptor.forClass(PointHistory.class);
        verify(pointHistoryRepository).save(historyCaptor.capture());
        PointHistory savedHistory = historyCaptor.getValue();

        assertThat(savedHistory.getUserId()).isEqualTo(userId);
        assertThat(savedHistory.getType()).isEqualTo(PointHistory.Type.USE);
        assertThat(savedHistory.getAmount()).isEqualTo(amount);
        assertThat(savedHistory.getBalanceAfter()).isEqualTo(50L);

    }

    @Test
    void 잔액보다_많이_사용하면_예외(){
        // given
        String userId = "user1";
        long useAmount = 200L;
        PointAccount existing = new PointAccount(1L, userId, 150L);
        given(pointAccountRepository.findByUserId(userId))
                .willReturn(Optional.of(existing));

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> pointService.use(userId, useAmount));

        // 저장 시도 없음
        verify(pointAccountRepository, never()).save(any());
        verify(pointHistoryRepository, never()).save(any());
    }


    @Test
    void 내역을_최신순으로_조회한다(){
        // given
        String userId = "user1";
        List<PointHistory> fake = List.of(
                new PointHistory(null, userId, PointHistory.Type.EARN, 50L, 120L, Instant.parse("2025-10-02T00:00:00Z")),
                new PointHistory(null, userId, PointHistory.Type.USE, 30L, 70L, Instant.parse("2025-10-01T00:00:00Z"))
        );
        // 저장소는 이미 최신순으로 반환한다고 가정
        given(pointHistoryRepository.findAllByUserIdOrderByOccurredAtDesc(userId))
                .willReturn(fake);

        // when
        List<PointHistory> histories = pointService.getHistories(userId);

        // then
        assertThat(histories).hasSize(2);
        assertThat(histories.get(0).getOccurredAt()).isAfter(histories.get(1).getOccurredAt()); // 최신순
        assertThat(histories.get(0).getType()).isEqualTo(PointHistory.Type.EARN);
        assertThat(histories.get(1).getType()).isEqualTo(PointHistory.Type.USE);
    }
}
