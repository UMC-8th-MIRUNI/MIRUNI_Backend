package dgu.umc_app.domain.plan.controller;

import dgu.umc_app.domain.plan.dto.request.PlanCreateRequest;
import dgu.umc_app.domain.plan.dto.request.PlanSplitRequest;
import dgu.umc_app.domain.plan.dto.response.*;
import dgu.umc_app.global.authorize.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name="plan", description = "일정 등록/쪼개기/조회/관리 API")
public interface PlanApi {
    @Operation(
            summary = "일정 등록 API",
            description = "사용자가 새로운 일정을 등록합니다. 제목, 설명, 마감일 등을 포함합니다.")
    PlanCreateResponse createPlan(
            @RequestBody @Valid PlanCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "월 단위 일정 조회 API",
            description = "사용자가 특정 연도와 월에 등록한 일반/AI 일정을 모두 조회합니다. 쿼리 파라미터 year, month 사용."
    )
    List<CalendarMonthResponse> getSchedulesByMonth(
            @Parameter(description = "조회할 연도", example = "2025") @RequestParam int year,
            @Parameter(description = "조회할 월", example = "7") @RequestParam int month,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(summary = "미룬 일정 조회", description = "수행날짜가 지났지만 완료되지 않은 일정을 조회합니다.")
    @GetMapping("/delayed")
    List<DelayedPlanResponse> getDelayedPlans(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "일자별 일정 조회",
            description = "특정 날짜의 일반 일정 및 AI 쪼개기 일정을 함께 조회합니다."
    )
    CalendarDayWrapperResponse getSchedulesByDay(
            @Parameter(description = "조회할 날짜", example = "2025-07-13") @RequestParam LocalDate day,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "AI 일정 쪼개기 API",
            description = "AI 프롬프트로 하위(세부) 일정을 분할합니다."
    )
    List<PlanSplitResponse> splitPlan(
            @PathVariable Long planId,
            @RequestBody @Valid PlanSplitRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

}
