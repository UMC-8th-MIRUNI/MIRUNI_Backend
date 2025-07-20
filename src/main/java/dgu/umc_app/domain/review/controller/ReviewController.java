package dgu.umc_app.domain.review.controller;

import dgu.umc_app.domain.review.dto.request.ReviewCreateRequest;
import dgu.umc_app.domain.review.dto.response.ReviewCreateResponse;
import dgu.umc_app.domain.review.dto.response.ReviewDetailResponse;
import dgu.umc_app.domain.review.service.ReviewCommandService;
import dgu.umc_app.domain.review.service.ReviewQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * ReviewController - 회고 작성 API
 */
@RestController
@RequestMapping("/api/schedule/review")
@RequiredArgsConstructor
public class ReviewController implements ReviewApi{

    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;

    /**
     * 회고 작성 후 저장
     */
    @PostMapping
    public ReviewCreateResponse createReview(@RequestBody @Valid ReviewCreateRequest request) {
        return reviewCommandService.saveReview(request);
    }

    //개별 회고 상세조회
    @GetMapping("/{reviewId}")
    public ReviewDetailResponse getReview(@PathVariable Long reviewId) {
        return reviewQueryService.getReview(reviewId);
    }
}
