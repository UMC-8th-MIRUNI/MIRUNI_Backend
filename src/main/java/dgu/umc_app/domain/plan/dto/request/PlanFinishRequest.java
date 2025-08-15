package dgu.umc_app.domain.plan.dto.request;

import dgu.umc_app.domain.plan.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PlanFinishRequest(
      @Schema(description = "BASIC or AI")
      @NotNull
      Category category,

      @Schema(description = "수행한 시간")
      @NotNull
      int executeTime,

      @Schema(description = "실제로 수행 시작한 시각")
      @NotNull
      LocalDateTime actualStart
) {
}
