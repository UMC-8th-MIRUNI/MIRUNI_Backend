package dgu.umc_app.domain.plan.dto.response;

import dgu.umc_app.domain.plan.entity.Status;

import java.time.LocalDateTime;

public record HomeTaskRow(
        Long aiPlanId,
        Long planId,
        String planTitle,
        String description, // AiPlan: a.description, Plan: p.description
        LocalDateTime scheduledStart,
        Status status,
        LocalDateTime stoppedAt,
        Long reviewId
) {}
