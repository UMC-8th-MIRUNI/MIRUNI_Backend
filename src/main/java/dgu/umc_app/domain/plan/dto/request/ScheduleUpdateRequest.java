package dgu.umc_app.domain.plan.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dgu.umc_app.domain.plan.entity.Category;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "일정 수정 요청 (BASIC/AI)",
        oneOf = { PlanUpdateRequest.class, AiPlanUpdateRequest.class },
        discriminatorProperty = "category",
        discriminatorMapping = {
                @DiscriminatorMapping(value = "BASIC", schema = PlanUpdateRequest.class),
                @DiscriminatorMapping(value = "AI", schema = AiPlanUpdateRequest.class)
        }
)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "category",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PlanUpdateRequest.class, name = "BASIC"),
        @JsonSubTypes.Type(value = AiPlanUpdateRequest.class, name = "AI")
})
public interface ScheduleUpdateRequest {
    Category category();
}
