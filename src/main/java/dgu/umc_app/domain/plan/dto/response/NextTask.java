package dgu.umc_app.domain.plan.dto.response;

import dgu.umc_app.domain.plan.entity.Category;

import java.time.format.DateTimeFormatter;

public record NextTask(
        Long planId,
        Long aiPlanId,
        Category category,
        String title,
        String description,
        String startDate, // 2025.08.08
        String startTime // 오전 12:00
) {
    public static NextTask from(HomeTaskRow r, DateTimeFormatter dateFormatter, DateTimeFormatter timeFormatter) {
        Category category = (r.aiPlanId() == null) ? Category.BASIC : Category.AI;
        return new NextTask(
                r.planId(),
                r.aiPlanId(),
                category,
                r.planTitle(),
                r.description(),
                r.scheduledStart().toLocalDate().format(dateFormatter),
                r.scheduledStart().format(timeFormatter)
        );
    }
}
