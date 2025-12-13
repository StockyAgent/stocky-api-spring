package dev.stocky.api.domain.batch;

import dev.stocky.api.domain.report.dto.RegularAnalysisRequestDto;
import dev.stocky.api.domain.user.User;
import dev.stocky.api.domain.user.UserRepository;
import dev.stocky.api.domain.watchlist.WatchListRepository;
import dev.stocky.api.global.sqs.SqsSender;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {

  private final UserRepository userRepository;
  private final WatchListRepository watchListRepository;
  private final SqsSender sqsSender;

  public int dispatchRegularReportEmail() {
    List<User> users = userRepository.findAll(); // TODO: 활성 유저만 조회하도록 수정 가능, 페이징 처리 고려
    int count = 0;

    log.info("📢 정기 리포트 배치 시작. 대상 유저 수: {}", users.size());

    for (User user : users) {
      // 1. 유저의 관심 종목(Symbol) 조회
      List<String> symbols = watchListRepository.findSymbolsByUser(user);

      if (symbols.isEmpty()) { // TODO: 관심 종목이 없는 경우 처리 방안
        continue;
      }

      // 2. 요청 DTO 생성
      RegularAnalysisRequestDto requestDto = RegularAnalysisRequestDto.builder()
          .userId(user.getId())
          .investmentStyle(user.getInvestmentStyle())
          .symbols(symbols)
          .build();

      // 3. SQS 전송
      sqsSender.sendRegularRequest(requestDto);
      count++;
    }

    log.info("✅ 정기 리포트 배치 완료. 전송된 요청 수: {}", count);
    return count;
  }
}
