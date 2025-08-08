package dgu.umc_app.domain.report.controller;

import dgu.umc_app.domain.report.dto.response.StoragePageResponse;
import dgu.umc_app.global.authorize.CustomUserDetails;
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

}