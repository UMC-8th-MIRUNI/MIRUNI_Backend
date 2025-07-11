package dgu.umc_app.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dgu.umc_app.domain.user.service.UserService;
import dgu.umc_app.domain.user.dto.request.UserSignupRequest;
import dgu.umc_app.domain.user.dto.request.UserLoginRequest;
import dgu.umc_app.domain.user.dto.response.UserSignupResponse;
import dgu.umc_app.domain.user.dto.response.UserLoginResponse;
import dgu.umc_app.global.response.ApiResponse;
import dgu.umc_app.global.common.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;
    
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserSignupResponse>> signup(@Valid @RequestBody UserSignupRequest request) {
        UserSignupResponse response = userService.signup(request);
        return SuccessResponse.created(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponse>> login(@Valid @RequestBody UserLoginRequest request) {
        UserLoginResponse response = userService.login(request);
        return SuccessResponse.ok(response);
    }
}
