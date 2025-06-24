package kopo.fitmate.controller;

import jakarta.servlet.http.HttpSession;
import kopo.fitmate.dto.diet.DietDTO;
import kopo.fitmate.dto.diet.DietInfoDTO;
import kopo.fitmate.dto.diet.DietPlanDTO;
import kopo.fitmate.service.IDietService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/diet")
public class DietController {

    private final IDietService dietService;

    // ✅ 식단 추천 입력 폼 진입
    @GetMapping("/dietInfoForm")
    public String dietInfoForm() {
        log.info("-------- 식단 추천 입력 폼 진입 --------");
        return "diet/dietInfoForm";
    }

    // ✅ 식단 추천 요청 처리
    // ✅ 식단 추천 요청 처리
    @PostMapping("/dietPlan")
    public String getDietPlan(@ModelAttribute DietDTO pDTO,
                              Model model,
                              HttpSession session) throws Exception {

        log.info("-------- 식단 추천 요청 시작: {} --------", pDTO);

        // ✅ GPT 호출 결과 DietInfoDTO 반환 → 내부 식단 리스트 추출
        DietInfoDTO result = dietService.getDietRecommendation(pDTO);
        List<DietPlanDTO> planList = result.getMealPlanList();

        // ✅ 요일 기준으로 그룹화 (월~일 순서 유지)
        Map<String, List<DietPlanDTO>> groupedByDay = planList.stream()
                .collect(Collectors.groupingBy(DietPlanDTO::getDay, LinkedHashMap::new, Collectors.toList()));

        model.addAttribute("groupedByDay", groupedByDay); // ✅ 있어야 함

        // 사용자 ID → Thymeleaf 템플릿에 전달
        String userId = (String) session.getAttribute("SS_USER_ID");
        if (userId != null) {
            model.addAttribute("SS_USER_ID", userId);
        }

        // 식단 유형도 전달 (HTML 내 메타태그나 저장 시 사용)
        model.addAttribute("dietType", pDTO.getDietType());

        log.info("-------- 식단 추천 완료 --------");
        return "diet/dietPlan";
    }
}
