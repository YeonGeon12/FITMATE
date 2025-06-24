package kopo.fitmate.controller.api;

import kopo.fitmate.dto.diet.DietInfoDTO;
import kopo.fitmate.dto.exercise.ExerciseInfoDTO;
import kopo.fitmate.service.IHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/history")
public class HistoryRestController {

    private final IHistoryService historyService;

    // ✅ 사용자 운동 루틴 목록 조회
    @GetMapping("/exercise/{userId}")
    public ResponseEntity<List<ExerciseInfoDTO>> getExerciseHistory(@PathVariable String userId) {
        return ResponseEntity.ok(historyService.getExerciseHistory(userId));
    }

    // ✅ 사용자 식단 목록 조회
    @GetMapping("/diet/{userId}")
    public ResponseEntity<List<DietInfoDTO>> getDietHistory(@PathVariable String userId) {
        return ResponseEntity.ok(historyService.getDietHistory(userId));
    }

    // ✅ 통합 상세 조회: /api/history/detail/{id}?type=diet|exercise
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getDetail(@PathVariable String id, @RequestParam String type) {
        return historyService.getRecordById(id, type)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ 통합 삭제 요청: /api/history/{id}?type=diet|exercise
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id, @RequestParam String type) {
        historyService.deleteRecordById(id, type);
        return ResponseEntity.ok("삭제 완료");
    }
}
