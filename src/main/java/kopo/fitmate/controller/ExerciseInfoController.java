package kopo.fitmate.controller;

import kopo.fitmate.dto.exercise.ExerciseInfoDTO;
import kopo.fitmate.service.IExerciseInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercise")
@RequiredArgsConstructor
public class ExerciseInfoController {

    private final IExerciseInfoService exerciseInfoService; // 운동 정보 관련 비즈니스 로직을 처리할 서비스 객체입니다.

    // ✅ 특정 사용자 ID로 운동 루틴 전체 조회
    @GetMapping("/list/{userId}") // HTTP GET 요청을 "/api/exercise/list/{userId}" 경로로 매핑합니다.
    public List<ExerciseInfoDTO> getExercisesByUser(@PathVariable String userId) { // URL 경로에서 사용자 ID를 추출합니다.
        return exerciseInfoService.getExercisesByUserId(userId); // 서비스 계층을 통해 해당 사용자의 모든 운동 루틴을 조회합니다.
    }

    // ✅ 운동 루틴 상세 조회
    @GetMapping("/{id}") // HTTP GET 요청을 "/api/exercise/{id}" 경로로 매핑합니다.
    public ExerciseInfoDTO getExerciseById(@PathVariable String id) { // URL 경로에서 운동 루틴의 고유 ID를 추출합니다.
        return exerciseInfoService.getExerciseById(id); // 서비스 계층을 통해 해당 ID의 운동 루틴 상세 정보를 조회합니다.
    }

    // ✅ 운동 루틴 삭제
    @DeleteMapping("/{id}") // HTTP DELETE 요청을 "/api/exercise/{id}" 경로로 매핑합니다.
    public String deleteExercise(@PathVariable String id) { // URL 경로에서 삭제할 운동 루틴의 고유 ID를 추출합니다.
        exerciseInfoService.deleteExerciseById(id); // 서비스 계층을 통해 해당 ID의 운동 루틴을 삭제합니다.
        return "삭제 완료"; // 삭제 완료 메시지를 반환합니다.
    }
}