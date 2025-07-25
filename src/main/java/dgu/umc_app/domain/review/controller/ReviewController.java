package dgu.umc_app.domain.review.controller;

import dgu.umc_app.domain.review.dto.request.ReviewCreateRequest;
import dgu.umc_app.domain.review.dto.response.ReviewCreateResponse;
import dgu.umc_app.domain.review.dto.response.ReviewDetailResponse;
import dgu.umc_app.domain.review.dto.response.ReviewListResponse;
import dgu.umc_app.domain.review.service.ReviewCommandService;
import dgu.umc_app.domain.review.service.ReviewQueryService;
import dgu.umc_app.global.authorize.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ReviewController - 회고 작성 API
 */
@RestController
@RequestMapping("/api/schedule/review")
@RequiredArgsConstructor
public class ReviewController implements ReviewApi{

    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;


     //회고 작성 후 저장
    @PostMapping
    public ReviewCreateResponse createReview(@RequestBody @Valid ReviewCreateRequest request) {
        return reviewCommandService.saveReview(request);
    }

    //개별 회고 상세조회
    @GetMapping("/{reviewId}")
    public ReviewDetailResponse getReview(@PathVariable Long reviewId) {
        return reviewQueryService.getReview(reviewId);
    }

    //회고 목록 조회
    @GetMapping
    public List<ReviewListResponse> getReviewList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        return reviewQueryService.getReviewListByUserId(userId);
    }

}
