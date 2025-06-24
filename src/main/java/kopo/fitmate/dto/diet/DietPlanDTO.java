package kopo.fitmate.dto.diet;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietPlanDTO {

    private String day;         // 월요일

    private String mealTime;    // 아침, 점심, 저녁

    private String menu;        // 예: "현미밥 + 두부조림"

    private int calories;       // 450
}

