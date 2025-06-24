package kopo.fitmate.dto.exercise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * GPT API에 전달할 운동 추천 요청용 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDTO {

    private String userId;     // ✅ 추가됨

    private double height;        // 키

    private double weight;        // 체중

    private String gender;        // 성별

    private String level;         // 숙련도

    private List<String> goal;    // 운동 목적 (복수 선택)

}