package dgu.umc_app.domain.plan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlanCreateRequest {

    @NotBlank(message = "일정 제목은 필수입니다.")
    @Size(max = 50 , message = "일정 제목은 50자 이내여야 합니다.")
    private String title;

    @NotBlank(message = "일정 내용은 필수입니다.")
    private String description;

    @NotNull(message = "마감 기한은 필수입니다.")
    private LocalDateTime deadline;
}
