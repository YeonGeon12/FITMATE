package kopo.fitmate.dto.exercise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;
/**
 * GPT API로부터 받은 운동 추천 결과 응답용 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExercisePlanDTO {

    private String day;                  // 요일 (예: 월요일)

    private String routineTitle;         // 루틴명 (예: 전신 워밍업 루틴)

    private List<String> routineDetail;  // 주요 운동 구성 (여러 줄로 구성)

    private String estimatedTime;        // 예상 소요 시간 (예: 25분)

    private String date;                // 운동 기간 체크
    private String userId;

}
