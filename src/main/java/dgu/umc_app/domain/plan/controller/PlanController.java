package dgu.umc_app.domain.plan.controller;

import dgu.umc_app.domain.plan.dto.request.PlanCreateRequest;
import dgu.umc_app.domain.plan.dto.request.PlanSplitRequest;
import dgu.umc_app.domain.plan.dto.response.*;
import dgu.umc_app.domain.plan.service.PlanCommandService;
import dgu.umc_app.domain.plan.service.PlanQueryService;
import dgu.umc_app.global.authorize.CustomUserDetails;
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
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class PlanController implements PlanApi{

    private final PlanCommandService planCommandService;
    private final PlanQueryService planQueryService;

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
        log.info("현재 로그인된 사용자 ID: {}", userDetails.getUser().getId()); // 🔍 이 로그로 확인
        return planCommandService.splitPlan(planId, request, userDetails.getUser());
    }

}
