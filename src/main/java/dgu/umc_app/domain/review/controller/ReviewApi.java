package dgu.umc_app.domain.review.controller;

import dgu.umc_app.domain.review.dto.request.ReviewCreateRequest;
import dgu.umc_app.domain.review.dto.response.ReviewCountByDateResponse;
import dgu.umc_app.domain.review.dto.response.ReviewCreateResponse;
import dgu.umc_app.domain.review.dto.response.ReviewDetailResponse;
import dgu.umc_app.domain.review.dto.response.ReviewListResponse;
import dgu.umc_app.global.authorize.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "review", description = "회고 작성/관리 API")
public interface ReviewApi {

    @Operation(summary = "회고 작성 후 저장 API",
            description = "특정 일정에 대한 회고(기분, 성취도, 만족도, 메모 등)를 작성하고 저장합니다.")
    ReviewCreateResponse createReview(@RequestBody @Valid ReviewCreateRequest request);

    @Operation(summary = "날짜별 회고 목록 갯수 조회 API",
            description = "날짜에 작성된 회고의 갯수 목록을 날짜순으로 반환합니다.")
    List<ReviewCountByDateResponse> getReviewCountByDate(@AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "단일 회고 상세 조회 API", description = "작성된 회고의 상세 정보를 조회합니다.")
    ReviewDetailResponse getReview(@PathVariable Long reviewId);

    @Operation(summary = "특정 날짜의 회고 목록 조회 API", description = "현재 로그인한 사용자의 특정 날짜 회고 목록(날짜, 제목, 부제목)을 최신순으로 조회합니다.")
    List<ReviewListResponse> getReviewListByDate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    );

}
