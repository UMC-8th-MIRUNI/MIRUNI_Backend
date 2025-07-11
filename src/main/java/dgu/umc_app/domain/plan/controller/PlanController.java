package dgu.umc_app.domain.plan.controller;

import dgu.umc_app.domain.plan.dto.PlanCreateRequest;
import dgu.umc_app.domain.plan.dto.PlanCreateResponse;
import dgu.umc_app.domain.plan.service.PlanCommandService;
import dgu.umc_app.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class PlanController implements PlanApi{

    private final PlanCommandService planCommandService;

    @PostMapping
    public PlanCreateResponse createPlan(@RequestBody @Valid PlanCreateRequest request) {
        Long userId = 1L; // 테스트용으로 user 테이블에 실제 insert한 ID 값
        return planCommandService.createPlan(request, userId);
    }
}
