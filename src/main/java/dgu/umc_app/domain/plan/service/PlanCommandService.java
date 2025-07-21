package dgu.umc_app.domain.plan.service;

import dgu.umc_app.domain.plan.dto.PlanCreateRequest;
import dgu.umc_app.domain.plan.dto.PlanCreateResponse;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.exception.PlanErrorCode;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.domain.user.exception.UserErrorCode;
import dgu.umc_app.domain.user.repository.UserRepository;
import dgu.umc_app.global.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanCommandService {

    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    @Transactional
    public PlanCreateResponse createPlan(PlanCreateRequest request, User user) {

        if (request.deadline().isBefore(java.time.LocalDate.now())){
            throw BaseException.type(PlanErrorCode.INVALID_DATE_RANGE);
        }

        Plan plan = request.toEntity().toBuilder()
                .user(user)
                .build();

        Plan savedPlan = planRepository.save(plan);
        return PlanCreateResponse.from(savedPlan);
    }
}
