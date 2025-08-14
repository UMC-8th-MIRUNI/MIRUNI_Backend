package dgu.umc_app.domain.plan.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import dgu.umc_app.domain.plan.entity.*;
import dgu.umc_app.domain.plan.exception.AiPlanErrorCode;
import dgu.umc_app.domain.plan.exception.PlanErrorCode;
import dgu.umc_app.global.exception.BaseException;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public record PlanUpdateRequest(

        @Schema(description = "일정 제목")
        String title,

        @Schema(description = "마감기한")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate deadline,

        @Schema(description = "일정 범위")
        String taskRange,

        @Schema(description = "우선 순위")
        Priority priority,

        @Schema(description = "일반 일정의 내용")
        String description,

        @Schema(description = "일정 수행 날짜")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate date,

        @Schema(description = "예상 시작 시간")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime startTime,

        @Schema(description = "예상 종료 시간")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime endTime,

        @Schema(description = "상세 일정 리스트")
        List<AiPlanUpdate> plans
) {

    public void applyToPlan(Plan plan) {
        if (title != null) plan.updateTitle(title);
        if(deadline != null) plan.updateDeadline(deadline.atTime(23, 59, 59));
        if (priority != null) plan.updatePriority(priority);
        if (description != null) plan.updateDescription(description);

        if (date != null && startTime != null) {
            plan.updateScheduledStart(LocalDateTime.of(date, startTime));
        }
        if (date != null && endTime != null) {
            plan.updateScheduledEnd(LocalDateTime.of(date, endTime));
        }

        // 시간 검증
        if (date != null && startTime != null && endTime != null) {
            LocalDateTime start = LocalDateTime.of(date, startTime);
            LocalDateTime end   = LocalDateTime.of(date, endTime);
            if (end.isBefore(start)) {
                throw new BaseException(PlanErrorCode.INVALID_DATE_RANGE);
            }
        }
    }

    private void assertWithinDeadline(Plan plan, LocalDateTime start, LocalDateTime end) {
        if (end.isAfter(plan.getDeadline())) {
            throw new BaseException(AiPlanErrorCode.AFTER_DEADLINE);
        }
    }

    public void mergeIntoAiPlan(Plan plan, Map<Long, AiPlan> existing) {
        if (plans == null || plans.isEmpty()) return;

        final Priority propagatedPriority = (priority != null ? priority : plan.getPriority());

        for (int i = 0; i < plans.size(); i++) {
            var d = plans.get(i);

            // 삭제: d.aiDelete() == true면 제거
            if (d.aiDelete() != null && d.aiDelete()) {
                if (d.id() != null && existing.containsKey(d.id())) {
                    plan.removeAiPlan(existing.get(d.id()));
                }
                continue;
            }

            // 신규 생성
            if (d.id() == null) {
                LocalDateTime start = LocalDateTime.of(d.date(), d.scheduledStartTime());
                LocalDateTime end   = LocalDateTime.of(d.date(), d.scheduledEndTime());
                assertWithinDeadline(plan, start, end);

                AiPlan.AiPlanBuilder b = AiPlan.builder()
                        .plan(plan)
                        .stepOrder((long) (i + 1))
                        .priority(propagatedPriority)
                        .taskRange(taskRange)
                        .description(d.description())
                        .expectedDuration(d.expectedDuration())
                        .scheduledStart(start)
                        .scheduledEnd(end)
                        .status(Status.NOT_STARTED)
                        .isDelayed(false)
                        .planType(PlanType.IMMERSIVE);

                plan.addAiPlan(b.build());
                continue;
            }

            // 기존 항목 부분 수정 (planType은 유지)
            AiPlan target = existing.get(d.id());
            if (target == null) continue;

            LocalDateTime newStart = target.getScheduledStart();
            LocalDateTime newEnd   = target.getScheduledEnd();

            if (d.date() != null && d.scheduledStartTime() != null)
                target.updateScheduleStart(LocalDateTime.of(d.date(), d.scheduledStartTime()));
            if (d.date() != null && d.scheduledEndTime() != null)
                target.updateScheduleEnd(LocalDateTime.of(d.date(), d.scheduledEndTime()));

            if (!newEnd.equals(target.getScheduledEnd())) {
                assertWithinDeadline(plan, newStart, newEnd);
            }

            if (d.description() != null) target.updateDescription(d.description());
            if (d.expectedDuration() != null) target.updateExpectedDuration(d.expectedDuration());
            if (d.date() != null && d.scheduledStartTime() != null) target.updateScheduleStart(newStart);
            if (d.date() != null && d.scheduledEndTime() != null)   target.updateScheduleEnd(newEnd);
            if (priority != null) target.updatePriority(propagatedPriority);
            if (taskRange != null) target.updateTaskRange(taskRange);
        }
    }
}



