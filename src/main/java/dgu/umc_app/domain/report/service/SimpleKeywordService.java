package dgu.umc_app.domain.report.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleKeywordService {

    private final RestTemplate restTemplate;
    private final ObjectMapper om = new ObjectMapper();

    @Value("${perplexity.api-url}")
    private String apiUrl;

    @Value("${perplexity.api-key}")
    private String apiKey;

    private static final int SINGLE_CALL_MAX = 14_000;
    private static final int CHUNK_MIN = 8_000;
    private static final int CHUNK_MAX = 12_000;

    /**
     * 아주 간단한 결과: "생각보다 쉬웠다", "계획대로", "집중" 같은 표현들만 리스트로
     * @param texts    같은 달 회고 본문 목록
     * @param topN     최종 항목 수
     */
    public List<String> extractTopTerms(List<String> texts, int topN) {
        if (texts == null || texts.isEmpty()) return List.of();

        String joined = String.join("\n", texts);
        // 1) 짧으면 단일 호출
        if (joined.length() <= SINGLE_CALL_MAX) {
            List<String> once = askTopTerms(joined, topN);
            return trimTo(once, topN);
        }

        // 2) 길면 청크: 8~12k 문자 사이로 자르기
        List<String> chunks = chunkByCharBudget(joined, CHUNK_MIN, CHUNK_MAX);

        // 청크별 상위 몇 개만 요청
        int chunkTopK = Math.max(5, Math.min(8, topN)); // 5~8 권장

        Map<String, Integer> freq = new HashMap<>();
        for (int i = 0; i < chunks.size(); i++) {
            List<String> part = askTopTerms(chunks.get(i), chunkTopK);
            for (String raw : part) {
                String norm = normalize(raw);
                if (norm.isBlank()) continue;
                freq.merge(norm, 1, Integer::sum);
            }
        }

        // 3) 빈도 상위 topN 반환
        return freq.entrySet().stream()
                .sorted(Map.Entry.<String,Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry::getKey))
                .limit(topN)
                .map(Map.Entry::getKey)
                .toList();
    }

    /** Perplexity에 "JSON 배열만" 요청해서 간단 리스트 받기 */
    private List<String> askTopTerms(String text, int topN) {
        String system = "유효한 JSON 배열만 출력하세요. 설명/서문/마크다운 금지.";
        String user = """
        다음 한국어 텍스트에서 '자주 쓰인 단어 또는 짧은 구절'만 추려 JSON 배열로만 출력해 주세요.
        - 최대 %d개
        - 너무 일반적인 기능어(그리고, 그래서 등) 제외
        - 가능하면 원문 표기 유지 (형태/조사 생략 가능)
        - 예시는 금지, 결과만 배열로

        텍스트:
        %s
        """.formatted(topN, sampleLong(text, SINGLE_CALL_MAX));

        Map<String, Object> body = Map.of(
                "model", "sonar-pro",
                "messages", List.of(
                        Map.of("role","system","content", system),
                        Map.of("role","user","content", user)
                ),
                "max_tokens", 400,
                "temperature", 0.0,
                "stream", false
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey.trim().replaceFirst("(?i)^Bearer\\s+",""));

        try {
            ResponseEntity<String> res = restTemplate.postForEntity(apiUrl, new HttpEntity<>(body, headers), String.class);
            String responseBody = res.getBody();
            if (responseBody == null) return List.of();

            JsonNode root = om.readTree(responseBody);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            content = content.replaceAll("^```json\\s*", "").replaceAll("\\s*```\\s*$", "");

            List<String> items = om.readValue(content, new TypeReference<List<String>>() {});
            return trimTo(items, topN);
        } catch (Exception e) {
            log.warn("[SimpleKeyword] Perplexity 실패: {}", e.getMessage());
            return naiveTopTokens(text, topN); // 초간단 폴백
        }
    }

    /** 아주 긴 텍스트는 앞/중간/뒤 샘플링해서 길이 제한 내로 잘라 보내기 */
    private String sampleLong(String s, int max) {
        if (s.length() <= max) return s;
        int p = max / 3;
        String head = s.substring(0, p * 2);
        String tail = s.substring(s.length() - p);
        return head + "\n...\n" + tail;
    }

    /** 8~12k 근처로 문장 경계(마침표/줄바꿈) 쪽에서 자르기 */
    private List<String> chunkByCharBudget(String s, int min, int max) {
        List<String> out = new ArrayList<>();
        int start = 0;
        while (start < s.length()) {
            int end = Math.min(start + max, s.length());
            if (end < s.length()) {
                // 경계 찾기(문장/줄바꿈)
                int lastDot = s.lastIndexOf('.', end);
                int lastNL  = s.lastIndexOf('\n', end);
                int cut = Math.max(Math.max(lastDot, lastNL), start + min);
                end = Math.max(cut, start + min);
            }
            out.add(s.substring(start, end));
            start = end;
        }
        return out;
    }

    private static final Pattern MULTI_SPACE = Pattern.compile("\\s+");
    private String normalize(String s) {
        if (s == null) return "";
        String t = s.trim();
        t = MULTI_SPACE.matcher(t).replaceAll(" ");
        // 대소문자 영향 줄이기(한국어엔 영향 적음)
        return t.toLowerCase();
    }

    private List<String> trimTo(List<String> items, int n) {
        if (items == null) return List.of();
        return items.stream()
                .map(this::normalize)
                .filter(x -> !x.isBlank())
                .distinct()
                .limit(n)
                .toList();
    }

    /** 폴백: 공백/구두점 기준 분리 → 한글/영문/숫자 2자+ → 상위 N */
    private List<String> naiveTopTokens(String text, int n) {
        Map<String, Integer> freq = new HashMap<>();
        for (String raw : text.split("\\s+")) {
            String t = raw.replaceAll("[^가-힣A-Za-z0-9]", "");
            if (t.length() < 2) continue;
            freq.merge(t, 1, Integer::sum);
        }
        return freq.entrySet().stream()
                .sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
                .limit(n)
                .map(Map.Entry::getKey)
                .toList();
    }
}
