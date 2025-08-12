package dgu.umc_app.domain.user.dto.response;

import dgu.umc_app.domain.user.entity.UserSurvey;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "사용자 설문조사 결과 응답")
public record UserSurveyResponse(
    
    @Schema(description = "미루는 상황들")
    Set<String> situationDescriptions,
    
    @Schema(description = "미루는 정도")
    String levelDescription,
    
    @Schema(description = "미루는 이유들")
    Set<String> reasonDescriptions
    
) {
    public static UserSurveyResponse from(UserSurvey survey) {
        return new UserSurveyResponse(
            survey.getSituationDescriptions(),
            survey.getLevelDescription(),
            survey.getReasonDescriptions()
        );
    }
}
