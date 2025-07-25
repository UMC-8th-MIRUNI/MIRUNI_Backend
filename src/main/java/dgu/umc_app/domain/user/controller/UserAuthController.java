package dgu.umc_app.domain.user.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import dgu.umc_app.domain.user.service.UserCommandService;
import dgu.umc_app.domain.user.service.UserQueryService;
import dgu.umc_app.domain.user.dto.request.UserSignupRequest;
import dgu.umc_app.domain.user.dto.request.UserLoginRequest;
import dgu.umc_app.domain.user.dto.response.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class UserAuthController implements UserAuthApi {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    
    @PostMapping("/signup")
    public UserResponse signup(@Valid @RequestBody UserSignupRequest request) {
        return userCommandService.signup(request);
    }

    @GetMapping("/signup/duplicate")
    public void duplicateCheck(@RequestParam @Email String email) {
        userQueryService.duplicateCheck(email);
    }
    
    @PostMapping("/auth/normal")
    public UserResponse login(@Valid @RequestBody UserLoginRequest request) {
        return userCommandService.login(request);
    }
}
