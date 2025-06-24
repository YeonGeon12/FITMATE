package kopo.fitmate.controller;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;
import kopo.fitmate.dto.diet.DietInfoDTO;
import kopo.fitmate.dto.diet.DietPlanDTO;
import kopo.fitmate.dto.exercise.ExerciseInfoDTO;
import kopo.fitmate.service.IHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/history")
public class HistoryController {

    private final IHistoryService historyService;

    // ✅ 통합 기록 리스트 화면 진입
    @GetMapping("/recordList")
    public String showRecordListPage(HttpSession session, ModelMap model) {
        String userId = (String) session.getAttribute("SS_USER_ID");
        if (userId != null) {
            model.addAttribute("SS_USER_ID", userId);
        }
        return "history/recordList";
    }

    // ✅ 상세보기 페이지 진입 (운동/식단에 따라 템플릿 분기)
    @GetMapping("/recordDetail")
    public String getRecordDetail(@RequestParam("id") String id,
                                  @RequestParam("type") String type,
                                  ModelMap model) {

        Optional<?> optionalRecord = historyService.getRecordById(id, type);

        if (optionalRecord.isEmpty()) {
            return "redirect:/history/recordList";
        }

        if ("diet".equals(type)) {
            DietInfoDTO dto = (DietInfoDTO) optionalRecord.get();

            List<DietPlanDTO> planList = Optional.ofNullable(dto.getMealPlanList())
                    .orElse(Collections.emptyList());

            log.info("✅ 식단 planList 수: {}", planList.size());

            Map<String, List<DietPlanDTO>> grouped = planList.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(
                            DietPlanDTO::getDay,
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));

            log.info("✅ groupedMealPlan 요일 목록: {}", grouped.keySet());

            model.addAttribute("record", dto);
            model.addAttribute("groupedMealPlan", grouped);
            return "history/recordDietDetail";

        } else if ("exercise".equals(type)) {
            model.addAttribute("record", (ExerciseInfoDTO) optionalRecord.get());
            return "history/recordExerciseDetail";

        } else {
            return "redirect:/history/recordList";
        }
    }
}

