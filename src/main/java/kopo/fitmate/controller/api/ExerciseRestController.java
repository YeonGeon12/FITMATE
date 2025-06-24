package kopo.fitmate.controller.api;

import kopo.fitmate.dto.exercise.ExerciseInfoDTO;
import kopo.fitmate.service.IExerciseInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api/exercise")
@RequiredArgsConstructor
public class ExerciseRestController {

    private final IExerciseInfoService exerciseInfoService;

    /**
     * 운동 루틴 저장 API
     * 클라이언트에서 ExerciseInfoDTO (userId, 키, 몸무게, 성별, 목표, 루틴 등) 전송 시 MongoDB에 저장
     */
    @PostMapping("/save")
    public ResponseEntity<String> saveExercise(@RequestBody ExerciseInfoDTO dto) {
        log.info("✅ 운동 루틴 저장 요청 수신: {}", dto);

        try {
            // 📌 createdAt, updatedAt 자동 설정
            Date now = new Date();
            dto.setCreatedAt(now);
            dto.setUpdatedAt(now);

            exerciseInfoService.saveExercise(dto);
            log.info("✅ 운동 루틴 저장 완료");
            return ResponseEntity.ok("운동 루틴 저장 성공");
        } catch (Exception e) {
            log.error("❌ 운동 루틴 저장 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("운동 루틴 저장 실패: " + e.getMessage());
        }
    }
}
