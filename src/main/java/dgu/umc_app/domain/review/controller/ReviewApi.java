package dgu.umc_app.domain.review.controller;

import dgu.umc_app.domain.review.dto.request.ReviewCreateRequest;
import dgu.umc_app.domain.review.dto.request.ReviewUpdateRequest;
import dgu.umc_app.domain.review.dto.response.ReviewCountByDateResponse;
import dgu.umc_app.domain.review.dto.response.ReviewCreateResponse;
import dgu.umc_app.domain.review.dto.response.ReviewDetailResponse;
import dgu.umc_app.domain.review.dto.response.ReviewListResponse;
import dgu.umc_app.domain.review.service.ReviewCommandService;
import dgu.umc_app.global.authorize.CustomUserDetails;
import dgu.umc_app.global.authorize.LoginUser;
import dgu.umc_app.global.exception.CustomErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회고 작성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": {
                            "id": 1,
                            "aiPlanId": 2,
                            "planId": 5,
                            "title": "프로젝트 기획서 작성",
                            "description": "프로젝트 초기 기획서 작성 및 검토",
                            "mood": "GOOD",
                            "achievement": 85,
                            "memo": "예상보다 빨리 완료했고, 팀원들의 피드백도 좋았다.",
                            "createdAt": "2025-08-13T14:30:00"
                        }
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "유효성 검증 실패",
                                            value = """
                    {
                        "status": 400,
                        "errorCode": "COMMON_002",
                        "message": "입력값 검증에 실패했습니다."
                    }
                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                    {
                        "status": 401,
                        "errorCode": "COMMON_003",
                        "message": "인증이 필요합니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "일정 없음",
                                            value = """
                    {
                        "status": 404,
                        "errorCode": "PLAN404_1",
                        "message": "해당 일정이 존재하지 않습니다."
                    }
                    """
                                    ),
                                    @ExampleObject(
                                            name = "AI 일정 없음",
                                            value = """
                    {
                        "status": 404,
                        "errorCode": "REVIEW404_1",
                        "message": "해당 AI 계획이 존재하지 않습니다."
                    }
                    """
                                    ),
                                    @ExampleObject(
                                            name = "사용자 없음",
                                            value = """
                    {
                        "status": 404,
                        "errorCode": "USER404_1",
                        "message": "해당 사용자가 존재하지 않습니다."
                    }
                    """
                                    )
                            }
                    )
            )
    })
    ReviewCreateResponse createReview(@LoginUser Long userId, @RequestBody @Valid ReviewCreateRequest request);

    @Operation(summary = "날짜별 회고 목록 갯수 조회 API",
            description = "날짜에 작성된 회고의 갯수 목록을 날짜순으로 반환합니다.")
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "날짜별 회고 갯수 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": [
                            {
                                "date": "2025-08-13",
                                "count": 3
                            },
                            {
                                "date": "2025-08-12",
                                "count": 1
                            },
                            {
                                "date": "2025-08-10",
                                "count": 2
                            }
                        ]
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                    {
                        "status": 401,
                        "errorCode": "COMMON_003",
                        "message": "인증이 필요합니다."
                    }
                    """
                            )
                    )
            )
    })
    List<ReviewCountByDateResponse> getReviewCountByDate(@LoginUser Long userId);

    @Operation(summary = "단일 회고 상세 조회 API", description = "작성된 회고의 상세 정보를 조회합니다.")
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회고 상세 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": {
                            "id": 1,
                            "aiPlanId": 2,
                            "planId": 5,
                            "mood": "GOOD",
                            "title": "프로젝트 기획서 작성",
                            "description": "프로젝트 초기 기획서 작성 및 검토",
                            "achievement": 85,
                            "memo": "예상보다 빨리 완료했고, 팀원들의 피드백도 좋았다. 다음에는 더 구체적인 일정 계획을 세워야겠다.",
                            "createdAt": "2025-08-13 14:30:00"
                        }
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                    {
                        "status": 401,
                        "errorCode": "COMMON_003",
                        "message": "인증이 필요합니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "회고를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "회고 없음",
                                    value = """
                    {
                        "status": 404,
                        "errorCode": "REVIEW404_3",
                        "message": "해당 회고가 존재하지 않습니다."
                    }
                    """
                            )
                    )
            )
    })
    ReviewDetailResponse getReview(@LoginUser Long userId, @PathVariable Long reviewId);

    @Operation(summary = "특정 날짜의 회고 목록 조회 API", description = "현재 로그인한 사용자의 특정 날짜 회고 목록(날짜, 제목, 부제목)을 최신순으로 조회합니다.")
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "특정 날짜 회고 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": [
                            {
                                "reviewId": 1,
                                "title": "프로젝트 기획서 작성",
                                "description": "프로젝트 초기 기획서 작성 및 검토",
                                "createdAt": "2025-08-13 14:30:00"
                            },
                            {
                                "reviewId": 2,
                                "title": "팀 미팅 참석",
                                "description": "주간 팀 미팅 및 업무 공유",
                                "createdAt": "2025-08-13 10:15:00"
                            },
                            {
                                "reviewId": 3,
                                "title": "코드 리뷰",
                                "description": "신규 기능 코드 리뷰 및 피드백",
                                "createdAt": "2025-08-13 09:00:00"
                            }
                        ]
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "날짜 형식 오류",
                                    value = """
                    {
                        "status": 400,
                        "errorCode": "COMMON_002",
                        "message": "입력값 검증에 실패했습니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                    {
                        "status": 401,
                        "errorCode": "COMMON_003",
                        "message": "인증이 필요합니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "사용자 없음",
                                            value = """
                    {
                        "status": 404,
                        "errorCode": "USER404_1",
                        "message": "해당 사용자가 존재하지 않습니다."
                    }
                    """
                                    ),
                                    @ExampleObject(
                                            name = "해당 날짜 회고 없음",
                                            value = """
                    {
                        "status": 404,
                        "errorCode": "REVIEW404_4",
                        "message": "해당 날짜에 작성된 회고가 없습니다."
                    }
                    """
                                    )
                            }
                    )
            )
    })
    List<ReviewListResponse> getReviewListByDate(
            @LoginUser Long userId,
            @Parameter(description = "조회할 날짜", example = "2025-08-13", required = true)
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    );

    @Operation(summary = "특정 회고 날짜 검색 API", description = "검색된 날짜에 작성된 회고의 갯수를 조회합니다.")
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "특정 날짜 회고 갯수 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": {
                            "date": "2025-08-13",
                            "count": 3
                        }
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "날짜 형식 오류",
                                    value = """
                    {
                        "status": 400,
                        "errorCode": "COMMON_002",
                        "message": "입력값 검증에 실패했습니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                    {
                        "status": 401,
                        "errorCode": "COMMON_003",
                        "message": "인증이 필요합니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "해당 날짜에 회고가 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "해당 날짜 회고 없음",
                                    value = """
                    {
                        "status": 404,
                        "errorCode": "REVIEW404_4",
                        "message": "해당 날짜에 작성된 회고가 없습니다."
                    }
                    """
                            )
                    )
            )
    })
    ReviewCountByDateResponse getReviewSearch(
            @LoginUser Long userId,
            @Parameter(description = "검색할 날짜", example = "2025-08-13", required = true)
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    );

    @Operation(summary = "회고 수정 API", description = "회고의 내용을 수정합니다.(기분, 성취도, 메모)")
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회고 수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": {
                            "id": 1,
                            "aiPlanId": 2,
                            "planId": 5,
                            "mood": "EXCELLENT",
                            "title": "프로젝트 기획서 작성",
                            "description": "프로젝트 초기 기획서 작성 및 검토",
                            "achievement": 95,
                            "memo": "수정된 회고 메모입니다. 목표보다 훨씬 잘 완료했고, 추가적인 개선사항도 발견했습니다.",
                            "createdAt": "2025-08-13 14:30:00"
                        }
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "유효성 검증 실패",
                                    value = """
                    {
                        "status": 400,
                        "errorCode": "COMMON_002",
                        "message": "입력값 검증에 실패했습니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                    {
                        "status": 401,
                        "errorCode": "COMMON_003",
                        "message": "인증이 필요합니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "회고를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "회고 없음",
                                    value = """
                    {
                        "status": 404,
                        "errorCode": "REVIEW404_3",
                        "message": "해당 회고가 존재하지 않습니다."
                    }
                    """
                            )
                    )
            )
    })
    public ReviewDetailResponse updateReview(@LoginUser Long userId,
                                             @Parameter(description = "수정할 회고 ID", example = "1", required = true)
                                             @PathVariable Long reviewId,
                                             @RequestBody @Valid ReviewUpdateRequest request);


    @Operation(summary = "회고 삭제", description = "특정 회고를 삭제합니다.")
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회고 삭제 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": 1
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                    {
                        "status": 401,
                        "errorCode": "COMMON_003",
                        "message": "인증이 필요합니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "회고를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "회고 없음",
                                    value = """
                    {
                        "status": 404,
                        "errorCode": "REVIEW404_3",
                        "message": "해당 회고가 존재하지 않습니다."
                    }
                    """
                            )
                    )
            )
    })
    public Long deleteReview(@LoginUser Long userId,
                             @Parameter(description = "삭제할 회고 ID", example = "1", required = true)
                             @PathVariable Long reviewId);
}
