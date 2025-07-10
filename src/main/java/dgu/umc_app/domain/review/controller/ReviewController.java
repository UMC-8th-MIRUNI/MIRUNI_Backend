package dgu.umc_app.domain.review.controller;

import dgu.umc_app.domain.review.dto.ReviewCreateRequest;
import dgu.umc_app.domain.review.dto.ReviewCreateResponse;
import dgu.umc_app.domain.review.service.ReviewCommandService;
import dgu.umc_app.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ReviewController - 회고 작성 API
 */
@RestController
@RequestMapping("/api/schedule/review")
@RequiredArgsConstructor
public class ReviewController implements ReviewApi{

    private final ReviewCommandService reviewCommandService;

    /**
     * 회고 작성 후 저장
     */
    @PostMapping
    public ReviewCreateResponse createReview(@RequestBody @Valid ReviewCreateRequest request) {
        return reviewCommandService.saveReview(request);
    }

}
