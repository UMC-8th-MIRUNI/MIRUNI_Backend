package dgu.umc_app.domain.plan.controller;

import dgu.umc_app.domain.plan.dto.*;
import dgu.umc_app.domain.plan.service.PlanCommandService;
import dgu.umc_app.domain.plan.service.PlanQueryService;
import dgu.umc_app.global.authorize.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

}
