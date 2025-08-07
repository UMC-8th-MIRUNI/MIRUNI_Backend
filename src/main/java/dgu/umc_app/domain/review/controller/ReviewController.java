package dgu.umc_app.domain.review.controller;

import dgu.umc_app.domain.review.dto.request.ReviewCreateRequest;
import dgu.umc_app.domain.review.dto.request.ReviewUpdateRequest;
import dgu.umc_app.domain.review.dto.response.ReviewCountByDateResponse;
import dgu.umc_app.domain.review.dto.response.ReviewCreateResponse;
import dgu.umc_app.domain.review.dto.response.ReviewDetailResponse;
import dgu.umc_app.domain.review.dto.response.ReviewListResponse;
import dgu.umc_app.domain.review.service.ReviewCommandService;
import dgu.umc_app.domain.review.service.ReviewQueryService;
import dgu.umc_app.global.authorize.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
     public ReviewCreateResponse createReview(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @RequestBody @Valid ReviewCreateRequest request) {
         Long userId = userDetails.getId();
         return reviewCommandService.saveReview(userId, request);
     }

    //회고록 날짜별 갯수 조회
    @GetMapping("/countByDate")
    public List<ReviewCountByDateResponse> getReviewCountByDate(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return reviewQueryService.getReviewCountByDate(userDetails.getUser().getId());
    }

    //개별 회고 상세 조회
    @GetMapping("/{reviewId}")
    public ReviewDetailResponse getReview(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @PathVariable Long reviewId) {
        return reviewQueryService.getReview(userDetails.getId(), reviewId);
    }


    // 특정 날짜의 회고 목록 조회
    @GetMapping("/date")
    public List<ReviewListResponse> getReviewListByDate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return reviewQueryService.getReviewListByUserIdAndDate(userDetails.getId(), date);
    }

    //날짜 검색으로 인한 회고 블럭 조회
    @GetMapping("/search")
    public ReviewCountByDateResponse getReviewSearch(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return reviewQueryService.getReviewSearch(userDetails.getUser().getId(), date);
    }

    //회고 수정
    @PatchMapping("/update/{reviewId}")
    public ReviewDetailResponse updateReview(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable Long reviewId,
                                             @RequestBody @Valid ReviewUpdateRequest request) {
        return reviewCommandService.updateReview(userDetails.getId(), reviewId, request);
    }

    //회고 삭제
    @DeleteMapping("/{reviewId}")
    public Long deleteReview(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long reviewId) {
        return reviewCommandService.deleteReview(userDetails.getId(),reviewId);
    }
}
