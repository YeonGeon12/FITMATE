package kopo.fitmate.controller;

import kopo.fitmate.dto.exercise.ExercisePlanDTO;
import kopo.fitmate.service.IExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {

    private final IExerciseService exerciseService; // ✅ 인터페이스로 수정

    /**
     * 운동 수행 날짜 데이터를 FullCalendar에 전달
     * @param userId 사용자 ID
     * @return FullCalendar events 리스트 (날짜별 운동 여부)
     */
    @GetMapping("/data")
    public List<Map<String, Object>> getCalendarData(@RequestParam("userId") String userId) {
        List<ExercisePlanDTO> plans = exerciseService.getExerciseRecordsByUser(userId);

        List<Map<String, Object>> events = new ArrayList<>();

        for (ExercisePlanDTO plan : plans) {
            if (plan.getDate() != null && !plan.getDate().isBlank()) {
                Map<String, Object> event = new HashMap<>();
                event.put("title", "운동함");
                event.put("start", plan.getDate()); // 예: "2025-06-16"
                event.put("color", "#4A90E2");
                events.add(event);
            }
        }

        return events;
    }

    @PostMapping("/complete")
    @ResponseBody
    public Map<String, Object> completeWorkout(@RequestBody Map<String, String> pMap) {
        String userId = pMap.get("userId");

        Map<String, Object> res = new HashMap<>();

        boolean saved = exerciseService.saveTodayExerciseCompletion(userId);
        if (saved) {
            res.put("result", 1);
            res.put("msg", "오늘 운동 완료가 기록되었습니다.");
        } else {
            res.put("result", 0);
            res.put("msg", "이미 오늘 완료된 기록이 존재합니다.");
        }

        return res;
    }
}
