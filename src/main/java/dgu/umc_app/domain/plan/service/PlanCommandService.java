package dgu.umc_app.domain.plan.service;

import dgu.umc_app.domain.plan.dto.request.PlanCreateRequest;
import dgu.umc_app.domain.plan.dto.request.PlanSplitRequest;
import dgu.umc_app.domain.plan.dto.request.PlanUpdateRequest;
import dgu.umc_app.domain.plan.dto.response.PlanCreateResponse;
import dgu.umc_app.domain.plan.dto.response.PlanDetailResponse;
import dgu.umc_app.domain.plan.dto.response.PlanSplitResponse;
import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.entity.PlanType;
import dgu.umc_app.domain.plan.entity.Priority;
import dgu.umc_app.domain.plan.exception.AiPlanErrorCode;
import dgu.umc_app.domain.plan.exception.PlanErrorCode;
import dgu.umc_app.domain.plan.repository.AiPlanRepository;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.global.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanCommandService {

    private final PlanRepository planRepository;
    private final AiPlanRepository aiPlanRepository;
    private final AiSplitService aiSplitService;

    @Transactional
    public PlanCreateResponse createPlan(PlanCreateRequest request, User user) {

        LocalDateTime today = LocalDateTime.now();

        if (request.deadline().isBefore(today)
                || request.scheduledStart().isBefore(today)) {
            throw BaseException.type(PlanErrorCode.INVALID_DATE_RANGE);
        }

        Plan savedPlan = planRepository.save(request.toEntity(user));
        return PlanCreateResponse.from(savedPlan);
    }

    @Transactional
    public List<PlanSplitResponse> splitPlan(Long planId, PlanSplitRequest request, User user) {

        // 1. 임시로 등록된 plan 조회하기
        Plan plan = planRepository.findByIdWithUserId(planId, user.getId())
                .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));

        // 2. AI 분할 요청
        List<PlanSplitResponse> splitResponses = aiSplitService.requestSplitResponseOnly(
                plan.getTitle(),
                plan.getDeadline(),
                plan.getScheduledStart(),
                plan.getPriority(),
                request.planType(),
                request.taskRange(),
                request.detailRequest(),
                plan
        );

        // 3. 엔티티 변환 및 저장
        List<AiPlan> aiPlans = PlanSplitResponse.toEntities(
                splitResponses,
                plan,
                request.planType(),
                request.taskRange()
        );
        aiPlanRepository.saveAll(aiPlans);

        planRepository.save(plan);

        return splitResponses;
    }

    @Transactional
    public PlanDetailResponse updatePlan(Long planId, PlanUpdateRequest request, User user) {

        // 1) 소유자 검증 + 로드
        Plan plan = planRepository.findByIdWithUserId(planId, user.getId())
                .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));

        // 2) 상위 Plan 부분 수정
        request.applyToPlan(plan);

        // 3) 신규/부분수정 검증 (신규 AiPlan not-null, 시간 범위 등)
        validateForMerge(request);

        // 4) 기존 AiPlan 맵(id -> entity)
        Map<Long, AiPlan> existing = plan.getAiPlans().stream()
                .collect(Collectors.toMap(AiPlan::getId, a -> a));

        // 5) AiPlan 컬렉션 부분 수정/추가/삭제 (planType 보존, 신규만 기본값)
        request.mergeIntoAiPlan(plan, existing);

        // 6) stepOrder normalize (1..N)
        long order = 1L;
        for (AiPlan ap : plan.getAiPlans()) {
            ap.updateStepOrder(order++);
        }

        // 7) 저장 및 응답 (일반 일정/AI 일정에 따라 분기)
        if (plan.getAiPlans() == null || plan.getAiPlans().isEmpty()) {
            return PlanDetailResponse.fromPlan(plan);
        } else {
            return PlanDetailResponse.fromAiPlan(plan, plan.getAiPlans());
        }
    }
    private void validateForMerge(PlanUpdateRequest req) {
        if (req.plans() == null) return;

        for (var d : req.plans()) {
            // 신규 + 삭제 조합 방지
            if (d.id() == null && Boolean.TRUE.equals(d.aiDelete())) {
                throw new BaseException(AiPlanErrorCode.INVALID_REQUEST_STATE);
            }

            // 신규 생성 필수값 (AiPlan: description, expectedDuration, scheduledStart, scheduledEnd, planType(신규시 기본값으로 세팅), taskRange)
            if (d.id() == null) {
                boolean missing = d.description() == null
                        || d.expectedDuration() == null
                        || d.date() == null
                        || d.scheduledStartTime() == null
                        || d.scheduledEndTime() == null
                        || req.taskRange() == null;
                if (missing) throw new BaseException(AiPlanErrorCode.INVALID_AIPLAN_FIELDS);
            }

            // 시간 유효성
            if (d.date() != null && d.scheduledStartTime() != null && d.scheduledEndTime() != null) {
                var start = LocalDateTime.of(d.date(), d.scheduledStartTime());
                var end   = LocalDateTime.of(d.date(), d.scheduledEndTime());
                if (end.isBefore(start)) throw new BaseException(AiPlanErrorCode.INVALID_TIME_RANGE);
            }
        }
    }

}
