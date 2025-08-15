package dgu.umc_app.domain.plan.dto.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dgu.umc_app.domain.plan.dto.request.AiPlanUpdateRequest;
import dgu.umc_app.domain.plan.dto.request.PlanUpdateRequest;
import dgu.umc_app.domain.plan.entity.Category;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "일정 조회 응답 (BASIC/AI)",
        oneOf = { PlanDetailResponse.class, AiPlanDetailResponse.class },
        discriminatorProperty = "category",
        discriminatorMapping = {
                @DiscriminatorMapping(value = "BASIC", schema = PlanDetailResponse.class),
                @DiscriminatorMapping(value = "AI", schema = AiPlanDetailResponse.class)
        }
)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "category",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PlanDetailResponse.class, name = "BASIC"),
        @JsonSubTypes.Type(value = AiPlanDetailResponse.class, name = "AI")
})
public interface ScheduleDetailResponse {
    Category category();
}
