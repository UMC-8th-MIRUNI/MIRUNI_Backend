    package dgu.umc_app.domain.user.dto.request;

    import jakarta.validation.constraints.Max;
    import jakarta.validation.constraints.Min;
    import jakarta.validation.constraints.NotEmpty;
    import jakarta.validation.constraints.NotNull;
    import java.util.List;

    public record SurveyRequest(

        @NotEmpty(message = "미루는 상황 선택은 필수입니다.")
        List<String> delaySituation,

        @NotNull(message = "미루는 정도 선택은 필수입니다.")
        @Min(value = 1, message = "두번째 설문은 1 이상이어야 합니다")
        @Max(value = 5, message = "두번째 설문은 5 이하여야 합니다")
        Integer delayDegree,

        @NotEmpty(message = "미루는 이유 선택은 필수입니다.")
        List<String> delayReason
        
    ){  
    }