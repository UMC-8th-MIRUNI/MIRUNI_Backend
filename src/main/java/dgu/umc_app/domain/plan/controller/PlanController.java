package dgu.umc_app.domain.plan.controller;

import dgu.umc_app.domain.plan.dto.request.*;
import dgu.umc_app.domain.plan.dto.response.*;
import dgu.umc_app.domain.plan.entity.Category;
import dgu.umc_app.domain.plan.repository.AiPlanRepository;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import dgu.umc_app.domain.plan.service.PlanCommandService;
import dgu.umc_app.domain.plan.service.PlanQueryService;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.global.authorize.CustomUserDetails;
import dgu.umc_app.global.authorize.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class PlanController implements PlanApi{

    private final PlanCommandService planCommandService;
    private final PlanQueryService planQueryService;
    private final AiPlanRepository aiPlanRepository;
    private final PlanRepository planRepository;

    @PostMapping
    public PlanCreateResponse createPlan(@RequestBody @Valid PlanCreateRequest request,
                                         @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return planCommandService.createPlan(request, userDetails.getUser());
    }

    @GetMapping
    public List<CalendarMonthResponse> getSchedulesByMonth(
            @RequestParam int year,
            @RequestParam int month,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return planQueryService.getSchedulesByMonth(year, month, userDetails.getUser());
    }

    @GetMapping("/delayed")
    public List<DelayedPlanResponse> getDelayedPlans(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return planQueryService.getDelayedPlans(userDetails.getUser());
    }

    @GetMapping("/day")
    public CalendarDayWrapperResponse getSchedulesByDay(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        return planQueryService.getSchedulesByDate(date, userDetails.getUser());
    }

    @PostMapping("/{planId}/split")
    public List<PlanSplitResponse> splitPlan(
            @PathVariable Long planId,
            @RequestBody @Valid PlanSplitRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("현재 로그인된 사용자 ID: {}", userDetails.getUser().getId()); // 이 로그로 확인
        return planCommandService.splitPlan(planId, request, userDetails.getUser());
    }

    @GetMapping("/unfinished")
    public List<UnstartedPlanResponse> getUnfinishedPlans(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return planQueryService.getUnstartedPlans(userDetails.getUser());
    }

    @PatchMapping("/{planId}")
    public PlanDetailResponse updatePlan(
            @PathVariable Long planId,
            @RequestBody @Valid PlanUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return planCommandService.updatePlan(planId, request, userDetails.getUser());
    }

    @GetMapping("/{planId}")
    public PlanDetailResponse getPlanDetail(
            @PathVariable Long planId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return planQueryService.getPlanDetail(planId, userDetails.getUser().getId());
    }

    @PatchMapping("/{planId}/delay")
    public PlanDelayResponse delayPlan(
            @PathVariable Long planId,
            @RequestBody @Valid PlanDelayRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        return (request.category() == Category.AI)
                ? planCommandService.delayAiPlan(planId, request, userDetails.getUser())
                : planCommandService.delayPlan(planId, request, userDetails.getUser());

    }

    @PatchMapping("/{planId}/status/in-progress")
    public PlanStartResponse startPlan(
            @PathVariable Long planId,
            @RequestBody @Valid PlanStartRequest request,
            @LoginUser Long userId
    ) {
        return (request.category() == Category.AI)
                ? planCommandService.startAiPlan(planId, userId)
                : planCommandService.startPlan(planId, userId);
    }

}
