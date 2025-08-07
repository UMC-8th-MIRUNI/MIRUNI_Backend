package dgu.umc_app.domain.user.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import dgu.umc_app.domain.user.service.UserCommandService;
import dgu.umc_app.domain.user.service.UserQueryService;
import dgu.umc_app.domain.user.dto.request.KakaoSignUpRequest;
import dgu.umc_app.domain.user.dto.request.GoogleSignUpRequest;
import dgu.umc_app.domain.user.dto.request.UserSignupRequest;
import dgu.umc_app.domain.user.dto.request.UserLoginRequest;
import dgu.umc_app.domain.user.dto.request.GoogleLoginRequest;
import dgu.umc_app.domain.user.dto.request.KakaoLoginRequest;
import dgu.umc_app.domain.user.dto.request.ReissueTokenRequest;
import dgu.umc_app.domain.user.dto.response.UserResponse;
import dgu.umc_app.domain.user.dto.response.AuthLoginResponse;
import dgu.umc_app.domain.user.dto.response.ReissueTokenResponse;
import dgu.umc_app.global.authorize.LoginUser;
import dgu.umc_app.global.authorize.TokenService;
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
    private final TokenService tokenService;
    
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

    @PostMapping("/auth/google")
    public AuthLoginResponse googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        return userCommandService.loginWithGoogle(request);
    }

    @PostMapping("/auth/kakao")
    public AuthLoginResponse kakaoLogin(@Valid @RequestBody KakaoLoginRequest request) {
        return userCommandService.loginWithKakao(request);
    }

    @PatchMapping("/auth/google/complete")
    public UserResponse googleSignUp(@Valid @RequestBody GoogleSignUpRequest request, @LoginUser Long userId) {
        return userCommandService.googleSignUp(request, userId);
    }

    @PatchMapping("/auth/kakao/complete")
    public UserResponse kakaoSignUp(@Valid @RequestBody KakaoSignUpRequest request, @LoginUser Long userId) {
        return userCommandService.kakaoSignUp(request, userId);
    }

    @PostMapping("/auth/logout")
    public void logout() {
        userCommandService.logout();
    }

    @PostMapping("/auth/reissue")
    public ReissueTokenResponse reissueToken(@Valid @RequestBody ReissueTokenRequest request) {
        return tokenService.reissueToken(request.refreshToken());
    }
}
