package dgu.umc_app.domain.plan.controller;

import dgu.umc_app.domain.plan.dto.request.PlanCreateRequest;
import dgu.umc_app.domain.plan.dto.request.PlanDelayRequest;
import dgu.umc_app.domain.plan.dto.request.PlanSplitRequest;
import dgu.umc_app.domain.plan.dto.request.PlanUpdateRequest;
import dgu.umc_app.domain.plan.dto.response.*;
import dgu.umc_app.global.authorize.CustomUserDetails;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name="plan", description = "일정 등록/쪼개기/조회/관리 API")
public interface PlanApi {
    @Operation(
            summary = "일정 등록 API",
            description = "사용자가 새로운 일정을 등록합니다. 제목, 설명, 마감일 등을 포함합니다.")
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일정 등록 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": {
                            "planId": 1,
                            "title": "프로젝트 최종 발표 준비",
                            "deadline": "2025-09-30T09:00:00",
                            "scheduledStart": "2025-09-20T00:00:00",
                            "isDone": false
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
                                    ),
                                    @ExampleObject(
                                            name = "날짜 범위 오류",
                                            value = """
                    {
                        "status": 400,
                        "errorCode": "PLAN400_1",
                        "message": "시작일은 종료일보다 이전이어야 합니다."
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
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "사용자 없음",
                                    value = """
                    {
                        "status": 404,
                        "errorCode": "USER404_1",
                        "message": "해당 사용자가 존재하지 않습니다."
                    }
                    """
                            )
                    )
            )
    })
    PlanCreateResponse createPlan(
            @RequestBody @Valid PlanCreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "월 단위 일정 조회 API",
            description = "사용자가 특정 연도와 월에 등록한 일반/AI 일정을 모두 조회합니다. 쿼리 파라미터 year, month 사용."
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "월 단위 일정 조회 성공",
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
                                "date": "2025-07-13",
                                "hasPlans": true,
                                "hasAiPlans": false
                            },
                            {
                                "date": "2025-07-15",
                                "hasPlans": false,
                                "hasAiPlans": true
                            },
                            {
                                "date": "2025-07-20",
                                "hasPlans": true,
                                "hasAiPlans": true
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
                                    name = "파라미터 검증 실패",
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
            )
    })
    List<CalendarMonthResponse> getSchedulesByMonth(
            @Parameter(description = "조회할 연도", example = "2025", required = true) @RequestParam int year,
            @Parameter(description = "조회할 월", example = "7", required = true) @RequestParam int month,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(summary = "미룬 일정 조회 API", description = "수행날짜가 지났지만 완료되지 않은 일정을 조회합니다.")
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "미룬 일정 조회 성공",
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
                                "planId": 1,
                                "title": "프로젝트 기획서 작성",
                                "priority": "HIGH",
                                "deadline": "2025-08-10T18:00:00",
                                "scheduledStart": "2025-08-08T09:00:00",
                                "daysDelayed": 3
                            },
                            {
                                "planId": 2,
                                "title": "팀 미팅 자료 준비",
                                "priority": "MEDIUM",
                                "deadline": "2025-08-12T14:00:00",
                                "scheduledStart": "2025-08-11T10:00:00",
                                "daysDelayed": 1
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
    @GetMapping("/delayed")
    List<DelayedPlanResponse> getDelayedPlans(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "일자별 일정 조회 API",
            description = "특정 날짜의 일반 일정 및 AI 쪼개기 일정을 함께 조회합니다."
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일자별 일정 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": {
                            "date": "2025-07-13",
                            "planResponses": [
                                {
                                    "planId": 1,
                                    "title": "프로젝트 최종 발표 준비",
                                    "startTime": "09:00",
                                    "endTime": "12:00",
                                    "status": "NOT_STARTED"
                                }
                            ],
                            "aiPlanResponses": [
                                {
                                    "aiPlanId": 1,
                                    "planId": 1,
                                    "description": "발표 자료 PPT 작성",
                                    "startTime": "14:00",
                                    "endTime": "16:00",
                                    "status": "IN_PROGRESS"
                                }
                            ]
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
            )
    })
    CalendarDayWrapperResponse getSchedulesByDay(
            @Parameter(description = "조회할 날짜", example = "2025-07-13", required = true) @RequestParam LocalDate day,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "AI 일정 쪼개기 API",
            description = "AI 프롬프트로 하위(세부) 일정을 분할합니다."
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "AI 일정 쪼개기 성공",
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
                                "id": 1,
                                "description": "프로젝트 기획 및 요구사항 분석",
                                "expectedDuration": 120,
                                "scheduledStart": "2025-09-20T09:00:00",
                                "scheduledEnd": "2025-09-20T11:00:00"
                            },
                            {
                                "id": 2,
                                "description": "UI/UX 디자인 설계",
                                "expectedDuration": 180,
                                "scheduledStart": "2025-09-21T14:00:00",
                                "scheduledEnd": "2025-09-21T17:00:00"
                            },
                            {
                                "id": 3,
                                "description": "발표 자료 준비 및 리허설",
                                "expectedDuration": 90,
                                "scheduledStart": "2025-09-29T15:00:00",
                                "scheduledEnd": "2025-09-29T16:30:00"
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
                                    ),
                                    @ExampleObject(
                                            name = "AI 일정 필드 오류",
                                            value = """
                    {
                        "status": 400,
                        "errorCode": "AIPLAN_400_1",
                        "message": "신규 AI 일정 필수값이 누락/형식 오류입니다."
                    }
                    """
                                    ),
                                    @ExampleObject(
                                            name = "시간 범위 오류",
                                            value = """
                    {
                        "status": 400,
                        "errorCode": "AIPLAN_400_2",
                        "message": "시작/종료 시간이 올바르지 않습니다."
                    }
                    """
                                    ),
                                    @ExampleObject(
                                            name = "마감기한 오류",
                                            value = """
                    {
                        "status": 400,
                        "errorCode": "AIPLAN_400_3",
                        "message": "세부 일정이 마감기한 이후입니다."
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
            ),
            @ApiResponse(responseCode = "500", description = "AI 서비스 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "AI 응답 없음",
                                            value = """
                    {
                        "status": 500,
                        "errorCode": "AI_001",
                        "message": "AI 일정 분할 응답이 비어 있습니다."
                    }
                    """
                                    ),
                                    @ExampleObject(
                                            name = "AI 요청 실패",
                                            value = """
                    {
                        "status": 500,
                        "errorCode": "AI_002",
                        "message": "AI 일정 분할 요청에 실패했습니다."
                    }
                    """
                                    )
                            }
                    )
            )
    })
    List<PlanSplitResponse> splitPlan(
            @Parameter(description = "쪼갤 일정 ID", example = "1", required = true) @PathVariable Long planId,
            @RequestBody @Valid PlanSplitRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(summary = "안 한 일정 조회 API", description = "미루지도 않고 수행하지도 않은 일정을 조회합니다.")
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "안 한 일정 조회 성공",
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
                                "planId": 3,
                                "title": "코드 리뷰 진행",
                                "priority": "MEDIUM",
                                "deadline": "2025-08-15T17:00:00",
                                "scheduledStart": "2025-08-14T10:00:00"
                            },
                            {
                                "planId": 4,
                                "title": "문서 작성",
                                "priority": "LOW",
                                "deadline": "2025-08-20T12:00:00",
                                "scheduledStart": "2025-08-16T14:00:00"
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
    @GetMapping("/unfinished")
    List<UnstartedPlanResponse> getUnfinishedPlans(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(summary = "일정 미루기 API", description = "일정을 수행하다가 수행예정 날짜와 소요시간을 설정해 미룹니다.")
    @PatchMapping("/{planId}/delay")
    PlanDelayResponse delayPlan(
            @PathVariable Long planId,
            @RequestBody @Valid PlanDelayRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(summary = "일반/AI 일정 수정 API", description = "일정의 세부 정보들을 수정합니다.")
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일정 수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": {
                            "title": "수정된 프로젝트 최종 발표 준비",
                            "deadline": "2025-09-30T09:00:00",
                            "taskRange": "전체 프로젝트 완성도 향상",
                            "priority": "HIGH",
                            "plans": [
                                {
                                    "id": 1,
                                    "date": "2025-09-20",
                                    "description": "수정된 프로젝트 기획 및 요구사항 분석",
                                    "expectedDuration": 150,
                                    "startTime": "09:00",
                                    "endTime": "11:30"
                                }
                            ]
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
                                    ),
                                    @ExampleObject(
                                            name = "날짜 범위 오류",
                                            value = """
                    {
                        "status": 400,
                        "errorCode": "PLAN400_1",
                        "message": "시작일은 종료일보다 이전이어야 합니다."
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
            @ApiResponse(responseCode = "404", description = "일정을 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "일정 없음",
                                    value = """
                    {
                        "status": 404,
                        "errorCode": "PLAN404_1",
                        "message": "해당 일정이 존재하지 않습니다."
                    }
                    """
                            )
                    )
            )
    })
    @PatchMapping("/{planId}")
    PlanDetailResponse updatePlan(
            @Parameter(description = "수정할 일정 ID", example = "1", required = true) @PathVariable Long planId,
            @RequestBody @Valid PlanUpdateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );


    @Operation(
            summary = "일정별 세부 조회 API",
            description = "일정별 세부정보를 조회합니다."
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일정 세부 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": {
                            "title": "프로젝트 최종 발표 준비",
                            "deadline": "2025-09-30T09:00:00",
                            "taskRange": "전체 프로젝트 완성도 향상",
                            "priority": "HIGH",
                            "plans": [
                                {
                                    "id": 1,
                                    "date": "2025-09-20",
                                    "description": "프로젝트 기획 및 요구사항 분석",
                                    "expectedDuration": 120,
                                    "startTime": "09:00",
                                    "endTime": "11:00"
                                },
                                {
                                    "id": 2,
                                    "date": "2025-09-21",
                                    "description": "UI/UX 디자인 설계",
                                    "expectedDuration": 180,
                                    "startTime": "14:00",
                                    "endTime": "17:00"
                                }
                            ]
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
            @ApiResponse(responseCode = "404", description = "일정을 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "일정 없음",
                                    value = """
                    {
                        "status": 404,
                        "errorCode": "PLAN404_1",
                        "message": "해당 일정이 존재하지 않습니다."
                    }
                    """
                            )
                    )
            )
    })
    PlanDetailResponse getPlanDetail(
            @Parameter(description = "조회할 일정 ID", example = "1", required = true) @PathVariable Long planId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

}
