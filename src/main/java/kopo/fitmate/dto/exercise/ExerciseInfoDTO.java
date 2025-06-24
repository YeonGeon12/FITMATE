package kopo.fitmate.dto.exercise;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "EXERCISE_INFO")
public class ExerciseInfoDTO {

    @Id
    private String id;

    private String userId;     // 사용자 ID: 이 운동 정보를 생성한 사용자를 식별합니다.
    private int height;        // 키: 사용자의 키를 저장합니다.
    private int weight;        // 체중: 사용자의 체중을 저장합니다.
    private String gender;     // 성별: 사용자의 성별을 저장합니다.
    private String goal;       // 운동 목표: 사용자가 설정한 운동 목표를 저장합니다.

    // private List<ExercisePlanDTO> routineList; // 운동 루틴 리스트: 사용자의 운동 루틴 계획들을 리스트 형태로 가집니다.
    private List<ExercisePlanDTO> routineList;


    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) // 이 필드가 날짜/시간 형식임을 지정합니다.
    private Date createdAt; // 생성일시: 해당 운동 정보가 생성된 시간입니다.

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) // 이 필드가 날짜/시간 형식임을 지정합니다.
    private Date updatedAt; // 수정일시: 해당 운동 정보가 마지막으로 수정된 시간입니다.
}