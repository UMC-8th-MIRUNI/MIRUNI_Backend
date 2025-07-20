package dgu.umc_app.domain.review.controller;

import dgu.umc_app.domain.review.dto.request.ReviewCreateRequest;
import dgu.umc_app.domain.review.dto.response.ReviewCreateResponse;
import dgu.umc_app.domain.review.dto.response.ReviewDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "review", description = "회고 작성/관리 API")
public interface ReviewApi {

    @Operation(summary = "회고 작성 후 저장 API",
            description = "특정 일정에 대한 회고(기분, 성취도, 만족도, 메모 등)를 작성하고 저장합니다.")
    ReviewCreateResponse createReview(@RequestBody @Valid ReviewCreateRequest request);

    @Operation(summary = "회고 단일 조회 API", description = "작성된 회고의 상세 정보를 조회합니다.")
    ReviewDetailResponse getReview(@PathVariable Long reviewId);

}
