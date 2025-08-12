package dgu.umc_app.domain.user.dto.response;

import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.domain.user.entity.DelaySituation;
import dgu.umc_app.domain.user.entity.DelayReason;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;
import java.util.stream.Collectors;

@Schema(description = "사용자 설문조사 결과 응답")
public record UserSurveyResponse(
    
    @Schema(description = "미루는 상황들")
    Set<String> situationDescriptions,
    
    @Schema(description = "미루는 정도")
    String levelDescription,
    
    @Schema(description = "미루는 이유들")
    Set<String> reasonDescriptions
    
) {
    public static UserSurveyResponse from(User user) {
        return new UserSurveyResponse(
            user.getDelaySituations().stream()
                .map(DelaySituation::getDescription)
                .collect(Collectors.toSet()),
            user.getDelayLevel() != null ? user.getDelayLevel().getDescription() : "",
            user.getDelayReasons().stream()
                .map(DelayReason::getDescription)
                .collect(Collectors.toSet())
        );
    }
}
