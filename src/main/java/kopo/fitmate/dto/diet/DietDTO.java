package kopo.fitmate.dto.diet;

import lombok.*;

/**
 * GPT API에 전달할 식단 추천 요청용 DTO (간소화 버전)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietDTO {

    private String userId;  // ✅ 저장용으로 필요함

    private String dietType; // 식단 유형 (예: calorie_control, balanced, high_protein, low_carb)

}
