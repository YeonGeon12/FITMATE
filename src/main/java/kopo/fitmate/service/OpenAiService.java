package kopo.fitmate.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.fitmate.dto.gpt.GPTRequestDTO;
import kopo.fitmate.dto.gpt.GPTResponseDTO;
import kopo.fitmate.dto.gpt.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * OpenAI GPT API 호출 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // ✅ 알 수 없는 필드 무시

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    /**
     * GPT에게 프롬프트를 보내고 응답 텍스트를 반환합니다.
     *
     * @param prompt GPT에게 보낼 사용자 질문/요청
     * @return GPT 응답 메시지 (message.content)
     * @throws Exception JSON 파싱 또는 통신 오류 발생 시
     */
    public String callGptAPI(String prompt) throws Exception {

        // ✅ 1. GPT 요청 메시지 구성
        GPTRequestDTO request = new GPTRequestDTO(
                "gpt-4o",
                List.of(new MessageDTO("user", prompt)),
                0.7
        );

        // ✅ 2. 요청 헤더 구성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // ✅ 3. 요청 본문 직렬화
        String jsonBody = objectMapper.writeValueAsString(request);
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        // ✅ 4. API 호출
        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                String.class
        );

        HttpStatusCode statusCode = response.getStatusCode();
        String responseBody = response.getBody();
        MediaType contentType = response.getHeaders().getContentType();

        // ✅ 5. 예외 처리 및 응답 유효성 검사
        if (!statusCode.is2xxSuccessful()) {
            log.error("❌ GPT 응답 실패: {}, 본문: {}", statusCode, response.getBody());
            throw new IllegalStateException("GPT 응답 실패");
        }

        if (responseBody == null || responseBody.trim().isEmpty()) {
            log.error("❌ GPT 응답 본문이 비어 있음");
            throw new IllegalStateException("GPT 응답이 비어 있습니다.");
        }

        if (contentType != null && !MediaType.APPLICATION_JSON.includes(contentType)) {
            log.warn("⚠️ GPT 응답 Content-Type이 JSON이 아님: {}", contentType);
        }

        log.debug("📦 GPT 응답 원문:\n{}", responseBody);

        // ✅ 6. GPT 응답에서 마크다운 블록(```json ... ```) 제거
        if (responseBody.trim().startsWith("```")) {
            responseBody = responseBody
                    .replaceAll("(?s)^```(?:json)?\\s*", "")  // 시작 백틱 제거
                    .replaceAll("\\s*```$", "")               // 종료 백틱 제거
                    .trim();
            log.debug("✅ 백틱 제거 후 응답:\n{}", responseBody);
        }

        // ✅ 7. 응답 파싱
        // ✅ 7. GPT 응답 파싱
        GPTResponseDTO gptResponse;
        try {
            gptResponse = objectMapper.readValue(responseBody, GPTResponseDTO.class);
        } catch (Exception e) {
            log.error("❌ GPT 응답 JSON 파싱 실패. 원본 응답:\n{}", responseBody);
            throw new RuntimeException("GPT 응답 파싱 중 오류 발생", e);
        }

// ✅ 8. message.content 추출 후 백틱 제거
        String content = gptResponse.getChoices().get(0).getMessage().getContent();
        log.debug("📥 GPT 응답 content 원문:\n{}", content);

// 백틱 제거 (```json ... ```)
        String cleanedContent = content
                .replaceAll("(?s)^```(?:json)?\\s*", "")
                .replaceAll("\\s*```$", "")
                .trim();

        log.debug("✅ 백틱 제거 후 content:\n{}", cleanedContent);
        log.info("✅ GPT 응답 수신 완료");

        return cleanedContent;
    }

    public String getChatCompletion(String prompt) throws Exception {
        return callGptAPI(prompt); // 내부 호출 메서드가 이미 존재한다고 가정
    }
}
