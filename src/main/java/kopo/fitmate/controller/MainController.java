package kopo.fitmate.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping(value = "/")
public class MainController {

    @GetMapping(value = "/index")
    public String index(HttpSession session, Model model) {

        String userId = (String) session.getAttribute("SS_USER_ID");

        if (userId != null) {
            model.addAttribute("SS_USER_ID", userId); // ✅ 사용자 ID 전달
            log.info("✅ 로그인된 사용자 진입 - userId: {}", userId);
            return "indexLogin";
        }

        return "index";
    }
}
