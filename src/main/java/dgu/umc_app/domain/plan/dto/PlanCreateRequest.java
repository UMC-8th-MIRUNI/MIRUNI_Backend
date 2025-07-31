package dgu.umc_app.domain.plan.dto;

import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PlanCreateRequest(

        @NotBlank(message = "일정 제목은 필수입니다.")
        @Size(max = 50, message = "일정 제목은 50자 이내여야 합니다.")
        @Schema(description = "일정 제목")
        String title,

        @NotNull(message = "마감 기한은 필수입니다.")
        @Schema(description = "마감 기한")
        LocalDateTime deadline,

        @NotNull(message = "일정 수행 날짜는 필수입니다.")
        @Schema(description = "일정 수행 날짜")
        LocalDateTime executeDate,

        @NotBlank(message = "일정 내용은 필수입니다.")
        @Schema(description = "일정 간단설명")
        String description

) {
    public Plan toEntity(User user) {
        return Plan.builder()
                .title(title)
                .description(description)
                .deadline(deadline)
                .executeDate(executeDate)
                .isDone(false)
                .user(user)
                .build();
    }
}
