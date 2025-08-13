package dgu.umc_app.domain.plan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dgu.umc_app.domain.plan.dto.response.PlanSplitResponse;
import dgu.umc_app.domain.plan.entity.*;
import dgu.umc_app.domain.plan.exception.AiPlanErrorCode;
import dgu.umc_app.domain.plan.repository.AiPlanRepository;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import dgu.umc_app.global.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiSplitService {
    private final RestTemplate restTemplate;
    private final AiPlanRepository aiPlanRepository;
    private final ObjectMapper objectMapper;
    private final PlanRepository planRepository;

    @Value("${perplexity.api-url}")
    private String apiUrl;

    @Value("${perplexity.api-key}")
    private String apiKey;

    @Transactional
    public List<PlanSplitResponse> requestSplitResponseOnly(
            String title,
            LocalDateTime deadline,
            LocalDateTime scheduledStart,
            Priority priority,
            PlanType planType,
            String taskRange,
            String detailRequest,
            Plan savedPlan
    ) {
        String prompt = buildPrompt(title, deadline, scheduledStart, priority, planType, taskRange, detailRequest);
        String responseBody = sendRequestToAi(prompt);
        return parseResponse(responseBody);
    }

    // 1. 프롬프트 생성
    private String buildPrompt(String title, LocalDateTime deadline, LocalDateTime scheduledStart,
                               Priority priority, PlanType planType, String taskRange, String detailRequest) {
        return String.format("""
            아래 일정을 참고하여 최소 2개에서 최대 10개 단계로 나눠줘. 단계갯수는 너가 생각해서 정해줘.
            **응답은 반드시 아래 JSON 형식의 배열로 작성해줘.**
            각 단계는 아래 예시와 같은 형식으로 작성하되, 내용은 내가 같은 질문을 해도 매번 새롭게 생성해줘:
            
            [
              {
                "stepOrder": 1,
                "scheduledDate": "2025-08-02",
                "description": "전체 기획안 작성",
                "expectedDuration": 60,
                "startTime": "09:00",
                "endTime": "10:00"
              },
              {
                "stepOrder": 2,
                "scheduledDate": "2025-08-03",
                "description": "UI 시안 작업",
                "expectedDuration": 90,
                "startTime": "10:00",
                "endTime": "11:30"
              }
            ]
            
            **필드는 반드시 모두 포함하고, 순서도 유지해줘.**
            
            - stepOrder: 실행 순서 (숫자)
            - scheduledDate: yyyy-MM-dd 형식의 실행 날짜
            - description: 일정 내용
            - expectedDuration: 예상 소요 시간 (분 단위)
            - startTime: HH:mm 형식의 시작 시간
            - endTime: HH:mm 형식의 종료 시간
            
            ---
            참고할 정보:
            - 제목: %s
            - 마감기한: %s
            - 수행시작일자: %s
            - 우선순위: %s
            - 작업유형: %s
            - 일정범위: %s
            - 추가 요청: %s
            """,
                title, deadline, scheduledStart, priority.name(), planType, taskRange, detailRequest
        );
    }

    // 2. AI 요청 전송
    private String sendRequestToAi(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> userMessage = Map.of(
                "role", "user",
                "content", prompt
        );

        Map<String, Object> systemMessage = Map.of(
                "role", "system",
                "content", "정확하고 간결하게 대답해줘."
        );

        Map<String, Object> requestBody = Map.of(
                "model", "sonar-pro",
                "messages", List.of(systemMessage, userMessage),
                "max_tokens", 1200,
                "temperature", 0.0
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
            String responseBody = response.getBody();
            log.info("Perplexity 응답: {}", responseBody);

            if (responseBody == null) {
                throw new BaseException(AiPlanErrorCode.AI_EMPTY_RESPONSE);
            }
            return responseBody;

        } catch (Exception e) {
            log.error("AI 분할 실패: {}", e.getMessage());
            throw new BaseException(AiPlanErrorCode.AI_REQUEST_FAILED);
        }
    }

    // 3. 응답 파싱
    private List<PlanSplitResponse> parseResponse(String responseBody) {
        try{
            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("choices").get(0).path("message").path("content").asText();

            return objectMapper
                    .readerForListOf(PlanSplitResponse.class)
                    .readValue(content);

        }catch (Exception e) {
            log.error("AI 응답 파싱 실패: {}", e.getMessage());
            throw new BaseException(AiPlanErrorCode.AI_REQUEST_FAILED);
        }
    }
}
