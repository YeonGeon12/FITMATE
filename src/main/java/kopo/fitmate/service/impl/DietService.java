package kopo.fitmate.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.fitmate.dto.diet.DietDTO;
import kopo.fitmate.dto.diet.DietInfoDTO;
import kopo.fitmate.dto.diet.DietPlanDTO;
import kopo.fitmate.service.IDietService;
import kopo.fitmate.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DietService implements IDietService {

    private final OpenAiService openAiService;

    /**
     * ✅ GPT 식단 추천 요청 처리 (DB 저장 제외)
     */
    @Override
    public DietInfoDTO getDietRecommendation(DietDTO pDTO) {
        log.info("✅ 식단 추천 요청 DTO: {}", pDTO);

        // 1. 프롬프트 생성
        String prompt = buildPrompt(pDTO);
        String response;

        try {
            // 2. GPT API 호출
            response = openAiService.getChatCompletion(prompt);
        } catch (Exception e) {
            log.error("❌ GPT API 호출 실패: {}", e.getMessage(), e);
            return new DietInfoDTO();  // 빈 객체 반환 (에러 대응)
        }

        // 3. GPT 응답 파싱 → DTO 반환
        return parseDietResponse(pDTO, response);
    }

    /**
     * 📌 1. 프롬프트 생성
     */
    private String buildPrompt(DietDTO pDTO) {
        return String.format("""
            사용자의 식단 유형은 다음과 같습니다:
            - 식단 유형: %s

            위 조건을 반영하여 요일별 아침/점심/저녁 식사로 구성된 일주일 식단을 JSON 형식으로 반환해 주세요.

            JSON 예시 형식:
            {
              "mealPlanList": [
                { "day": "월요일", "mealTime": "아침", "menu": "현미밥, 닭가슴살", "calories": 500 },
                { "day": "월요일", "mealTime": "점심", "menu": "불고기덮밥", "calories": 700 }
              ]
            }
            전체 JSON 배열 형식으로 응답해 주세요.
        """, pDTO.getDietType());
    }

    /**
     * 📌 3. GPT 응답 파싱 및 DTO 구성
     */
    private DietInfoDTO parseDietResponse(DietDTO pDTO, String response) {
        log.info("✅ GPT 응답: {}", response);

        ObjectMapper mapper = new ObjectMapper();
        DietInfoDTO dietInfo = new DietInfoDTO();

        try {
            // 3-1. JSON 파싱
            JsonNode root = mapper.readTree(response);

            // 3-2. 일주일 식단 구성
            List<DietPlanDTO> mealPlanList = new ArrayList<>();
            for (JsonNode node : root.path("mealPlanList")) {
                DietPlanDTO plan = new DietPlanDTO();
                plan.setDay(node.path("day").asText());
                plan.setMealTime(node.path("mealTime").asText(null)); // optional
                plan.setMenu(node.path("menu").asText());
                plan.setCalories(node.path("calories").asInt(0)); // optional
                mealPlanList.add(plan);
            }

            // 4. 최종 DTO 구성
            dietInfo.setUserId(pDTO.getUserId());
            dietInfo.setDietType(pDTO.getDietType());
            dietInfo.setMealPlanList(mealPlanList);

            Date now = new Date();
            dietInfo.setCreatedAt(now);
            dietInfo.setUpdatedAt(now);

        } catch (Exception e) {
            log.error("❌ GPT 응답 파싱 실패: {}", e.getMessage(), e);
        }

        return dietInfo;
    }
}
