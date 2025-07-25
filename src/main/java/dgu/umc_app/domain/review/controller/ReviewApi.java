package dgu.umc_app.domain.review.controller;

import dgu.umc_app.domain.review.dto.request.ReviewCreateRequest;
import dgu.umc_app.domain.review.dto.response.ReviewCreateResponse;
import dgu.umc_app.domain.review.dto.response.ReviewDetailResponse;
import dgu.umc_app.domain.review.dto.response.ReviewListResponse;
import dgu.umc_app.global.authorize.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "review", description = "회고 작성/관리 API")
public interface ReviewApi {

    @Operation(summary = "회고 작성 후 저장 API",
            description = "특정 일정에 대한 회고(기분, 성취도, 만족도, 메모 등)를 작성하고 저장합니다.")
    ReviewCreateResponse createReview(@RequestBody @Valid ReviewCreateRequest request);

    @Operation(summary = "회고 단일 상세 조회 API", description = "작성된 회고의 상세 정보를 조회합니다.")
    ReviewDetailResponse getReview(@PathVariable Long reviewId);

    @Operation(summary = "회고 전체 목록 조회 API", description = "현재 로그인한 사용자의 회고 목록(날짜, 제목, 메모)을 최신순으로 조회합니다.")
    List<ReviewListResponse> getReviewList(@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails);

}
