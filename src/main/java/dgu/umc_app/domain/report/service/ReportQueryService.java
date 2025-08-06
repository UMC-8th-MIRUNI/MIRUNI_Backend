package dgu.umc_app.domain.report.service;

import com.google.api.gax.rpc.NotFoundException;
import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.repository.AiPlanRepository;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import dgu.umc_app.domain.report.dto.response.StoragePageResponse;
import dgu.umc_app.domain.report.repository.ReportRepository;
import dgu.umc_app.domain.review.repository.ReviewRepository;
import dgu.umc_app.domain.user.exception.UserErrorCode;
import dgu.umc_app.domain.user.repository.UserRepository;
import dgu.umc_app.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import dgu.umc_app.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportQueryService {

    private final UserRepository userRepository;
    private final AiPlanRepository aiPlanRepository;
    private final PlanRepository planRepository;
    private final ReportRepository reportRepository;

    public StoragePageResponse getStoragePage(Long userId, int year, int month) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
        int peanutCount = user.getPeanutCount();

        // 1. 이번달 일정 완료율 계산
        List<AiPlan> aiPlans = aiPlanRepository.findByUserIdAndMonth(userId, year, month);
        long doneAiCount = aiPlans.stream().filter(AiPlan::isDone).count();

        List<Plan> purePlans = planRepository.findIndependentPlans(userId, year, month);
        long donePlanCount = purePlans.stream().filter(Plan::isDone).count();

        long totalCount = aiPlans.size() + purePlans.size();
        int completionRate = totalCount > 0
                ? (int) Math.round((doneAiCount + donePlanCount) * 100.0 / totalCount)
                : 0;

        // 2. 이번달 리포트 오픈 여부
        boolean isOpenedThisMonth = reportRepository.existsByUserIdAndYearAndMonthAndIsOpenedTrue(userId, year, month);

        // 3. 저번달 리포트 오픈 여부
        LocalDate thisMonthDate = LocalDate.of(year, month, 1);
        LocalDate lastMonthDate = thisMonthDate.minusMonths(1);
        boolean isOpenedLastMonth = reportRepository.existsByUserIdAndYearAndMonthAndIsOpenedTrue(
                userId, lastMonthDate.getYear(), lastMonthDate.getMonthValue());

        // 4. 이번달 리포트 오픈 조건 충족 여부
        boolean canOpen = !isOpenedThisMonth && peanutCount >= 30 && completionRate >= 80;

        // 5. UI 구성용 데이터 파생
        String lockState = isOpenedThisMonth ? "열림" : "잠김";
        boolean isOpenButtonVisible = !isOpenedThisMonth && canOpen;

        return StoragePageResponse.from(
                peanutCount,
                completionRate,
                isOpenedThisMonth,
                canOpen,
                isOpenedLastMonth,
                lockState,
                isOpenButtonVisible
        );
    }

}
