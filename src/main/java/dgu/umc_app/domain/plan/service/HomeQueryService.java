package dgu.umc_app.domain.plan.service;

import dgu.umc_app.domain.plan.dto.response.HomeResponse;
import dgu.umc_app.domain.plan.dto.response.HomeTaskRow;
import dgu.umc_app.domain.plan.dto.response.NextTask;
import dgu.umc_app.domain.plan.dto.response.Tasks;
import dgu.umc_app.domain.plan.entity.Status;
import dgu.umc_app.domain.plan.repository.AiPlanRepository;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.domain.user.exception.UserErrorCode;
import dgu.umc_app.domain.user.repository.UserRepository;
import dgu.umc_app.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeQueryService {

    private final UserRepository userRepository;
    private final AiPlanRepository aiPlanRepository;
    private final PlanRepository planRepository;

    public HomeResponse getHomePage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusNanos(1);
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        // 오늘 할 일 리스트화
        List<HomeTaskRow> aiRows = aiPlanRepository.findTodayAiPlanRows(userId, start, end);
        List<HomeTaskRow> pRows = planRepository.findTodayStandalonePlanRows(userId, start, end);
        List<HomeTaskRow> rows = new ArrayList<>(aiRows.size() + pRows.size());
        rows.addAll(aiRows);
        rows.addAll(pRows);

        // 할 일 개수 계산
        int totalCnt = (int) rows.stream().filter(r -> r.status() != Status.FINISHED).count(); // 남은 할 일 개수
        int completed  = (int) rows.stream().filter(r -> r.status() == Status.FINISHED).count(); // 완료
        int paused = (int) rows.stream().filter(r -> r.status() == Status.PAUSED).count(); // 중지
        int scheduled = (int) rows.stream().filter(r -> r.status() == Status.NOT_STARTED).count(); // 미완료
        int rate = (rows.isEmpty()) ? 0 : (int) Math.round(completed * 100.0 / rows.size());

        // 시간 포맷터
        DateTimeFormatter koreanTime = DateTimeFormatter.ofPattern("a h:mm", java.util.Locale.KOREAN);
        DateTimeFormatter dateDots = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        List<Tasks.NotStartedItem> notStarted = new ArrayList<>();
        List<Tasks.PausedItem> pausedList = new ArrayList<>();
        List<Tasks.FinishedItem> finished = new ArrayList<>();

        for (HomeTaskRow r : rows) {
            switch (r.status()) {
                case NOT_STARTED -> notStarted.add(Tasks.NotStartedItem.from(r, koreanTime));
                case PAUSED -> pausedList.add(Tasks.PausedItem.from(r, koreanTime));
                case FINISHED -> finished.add(Tasks.FinishedItem.from(r, koreanTime));
//                case IN_PROGRESS -> {} // 진행 중일 경우 홈페이지 접속 불가하기에 생략
            }
        }

        Optional<HomeTaskRow> nextOpt = rows.stream()
                .filter(r -> {
                    var st = Optional.ofNullable(r.status()).orElse(Status.NOT_STARTED);
                    return st == Status.NOT_STARTED || st == Status.PAUSED;
                })
                .filter(r -> !r.scheduledStart().isBefore(now))
                .min(Comparator.comparing(HomeTaskRow::scheduledStart));

        List<NextTask> nextTask = nextOpt
                .map(r -> List.of(NextTask.from(r, dateDots, koreanTime)))
                .orElseGet(List::of);

        return HomeResponse.of(user, totalCnt, scheduled, paused, completed, rate, new Tasks(notStarted, pausedList, finished), nextTask);
    }
}
