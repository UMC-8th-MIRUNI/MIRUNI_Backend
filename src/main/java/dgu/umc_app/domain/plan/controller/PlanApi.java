package dgu.umc_app.domain.plan.controller;

import dgu.umc_app.domain.plan.dto.PlanCreateRequest;
import dgu.umc_app.domain.plan.dto.PlanCreateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name="plan, description = 일정 등록/쪼개기/조회/관리 API")
public interface PlanApi {
    @Operation(
            summary = "일정 등록 API",
            description = "사용자가 새로운 일정을 등록합니다. 제목, 설명, 마감일 등을 포함합니다.")
    PlanCreateResponse createPlan(@RequestBody @Valid PlanCreateRequest request);

}
