package dgu.umc_app.domain.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dgu.umc_app.domain.user.service.UserService;
import dgu.umc_app.domain.user.dto.request.UserSignupRequest;
import dgu.umc_app.domain.user.dto.request.UserLoginRequest;
import dgu.umc_app.domain.user.dto.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;
    
    @PostMapping("/signup")
    public UserResponse signup(@Valid @RequestBody UserSignupRequest request) {
        return userService.signup(request);
    }
    
    @PostMapping("/auth/normal")
    public UserResponse login(@Valid @RequestBody UserLoginRequest request) {
        return userService.login(request);
    }
}
