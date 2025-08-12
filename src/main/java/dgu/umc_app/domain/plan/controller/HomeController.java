package dgu.umc_app.domain.plan.controller;

import dgu.umc_app.domain.plan.dto.response.HomeResponse;
import dgu.umc_app.domain.plan.service.PlanQueryService;
import dgu.umc_app.global.authorize.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HomeController implements HomeApi {

    private final PlanQueryService planQueryService;

    @Override
    @GetMapping("/homePage")
    public HomeResponse getHomePage(@LoginUser Long userId) {
        return planQueryService.getHomePage(userId);
    }
}
