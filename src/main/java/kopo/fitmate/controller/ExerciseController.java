package kopo.fitmate.controller;

import jakarta.servlet.http.HttpSession;
import kopo.fitmate.dto.exercise.ExerciseDTO;
import kopo.fitmate.dto.exercise.ExercisePlanDTO;
import kopo.fitmate.service.IExerciseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/exercise")
public class ExerciseController {

    private final IExerciseService exerciseService;

    // ✅ 운동 추천 입력 폼 진입
    @GetMapping("/exerciseInfoForm")
    public String exerciseInfoForm() {
        log.info("-------- 운동 추천 입력 폼 페이지 진입 --------");
        return "exercise/exerciseInfoForm"; // → templates/exercise/exerciseInfoForm.html
    }

    @PostMapping("/exercisePlan")
    public String generateExercisePlan(@ModelAttribute ExerciseDTO pDTO,
                                       Model model,
                                       HttpSession session) throws Exception {

        // ✅ 세션 사용자 ID
        String userId = (String) session.getAttribute("SS_USER_ID");
        if (userId != null) {
            pDTO.setUserId(userId);
            model.addAttribute("SS_USER_ID", userId);
        } else {
            log.warn("⚠️ 세션에 사용자 ID 없음");
        }

        log.info("운동 추천 요청 시작: {}", pDTO);

        // ✅ GPT로 추천 요청
        List<ExercisePlanDTO> resultList = exerciseService.getExerciseRecommendation(pDTO);
        model.addAttribute("planList", resultList);

        // ✅ 사용자 입력 정보도 템플릿으로 전달
        model.addAttribute("height", pDTO.getHeight());
        model.addAttribute("weight", pDTO.getWeight());
        model.addAttribute("gender", pDTO.getGender());
        model.addAttribute("goal", pDTO.getGoal());

        log.info("-------- 운동 추천 완료 --------");
        return "exercise/exercisePlan";
    }
}
