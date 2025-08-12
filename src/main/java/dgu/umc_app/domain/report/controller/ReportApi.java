package dgu.umc_app.domain.report.controller;

import dgu.umc_app.domain.report.dto.response.ReportResponse;
import dgu.umc_app.domain.report.dto.response.StoragePageResponse;
import dgu.umc_app.global.authorize.CustomUserDetails;
import dgu.umc_app.global.authorize.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "report", description = "월말 리포트 및 보관함 API")
public interface ReportApi {

    @Operation(summary = "보관함페이지 정보 조회 API",
            description = "사용자의 피넛 수, 일정 달성률, 저번달 리포트 오픈 여부 등을 반환합니다.")
    StoragePageResponse getStoragePage(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @RequestParam int year,
                                  @RequestParam int month);

    @Operation(summary = "이번달 리포트 오픈 API",
            description = "리포트 오픈 조건을 만족할 경우 땅콩 갯수 30개를 차감하고 리포트를 오픈합니다.")
    public ReportResponse openThisMonth(@LoginUser Long userId,
                                        @PathVariable int year,
                                        @PathVariable int month);

    @Operation(summary = "이번달 리포트 조회 API",
            description = "이번달 리포트를 오픈했다는 가정 하에 오픈 기준으로 정보를 갱신하여 리포트 정보를 반환합니다.")
    public ReportResponse getReport(@LoginUser Long userId,
                                    @PathVariable int year,
                                    @PathVariable int month);

    @Operation(summary = "저번달 리포트 조회 API",
            description = "저번달에 리포트를 오픈했을 경우 저번달 리포트 정보를 반환합니다.")
    public ReportResponse getLastMonth(@LoginUser Long userId);
}