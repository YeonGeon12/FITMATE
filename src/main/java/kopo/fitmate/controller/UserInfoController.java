package kopo.fitmate.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.fitmate.dto.MsgDTO;
import kopo.fitmate.dto.UserInfoDTO;
import kopo.fitmate.dto.exercise.ExerciseInfoDTO;
import kopo.fitmate.service.IExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import kopo.fitmate.service.IUserInfoService;
import kopo.fitmate.util.CmmUtil;
import kopo.fitmate.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/user")
@RequiredArgsConstructor
@Controller
public class UserInfoController {

    private final IUserInfoService userInfoService; // 서비스 호출
    private final IExerciseService exerciseService; // 서비스 호출

    // 회원가입 화면으로 이동
    @GetMapping(value = "userRegForm")
    public String userRegForm() {

        log.info("{}.user/userRegForm", this.getClass().getName());

        return "user/userRegForm";
    }

    // 회원가입 전 아이디 중복체크
    @ResponseBody
    @PostMapping(value = "getUserIdExists")
    public UserInfoDTO getUserIdExists(HttpServletRequest request) throws Exception {

        log.info("{}.getUserIdExists Start!", this.getClass().getName());

        String userId = CmmUtil.nvl(request.getParameter("userId"));

        log.info("userId:{}", userId);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);

        // 중복 조회
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getUserIdExists(pDTO))
                .orElseGet(UserInfoDTO::new);

        log.info("{}.getUserIdExists End!", this.getClass().getName());

        return rDTO;
    }

    // 회원 가입 전 이메일 중복 체크
    @ResponseBody
    @PostMapping(value = "getEmailExists")
    public UserInfoDTO getEmailExists(HttpServletRequest request) throws Exception {

        log.info("{}.getEmailExists Start!", this.getClass().getName());

        String email = CmmUtil.nvl(request.getParameter("email")); // 회원 아이디

        log.info("email : {}", email);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setEmail(EncryptUtil.encAES128CBC(email));

        // 입력된 이메일이 중복된 이메일인지 조회
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getEmailExists(pDTO))
                .orElseGet(UserInfoDTO::new);

        log.info("{}.getEmailExists End!", this.getClass().getName());

        return rDTO;
    }

    // 회원가입 로직
    @ResponseBody
    @PostMapping(value = "insertUserInfo")
    public MsgDTO insertUserInfo(HttpServletRequest request) throws Exception {

        log.info("{}.insertUserInfo Start!", this.getClass().getName());

        int res = 0; // 회원 가입 결과
        String msg = ""; // 결과에 대한 메세지를 전달할 변수
        MsgDTO dto; // 결과 메세지의 구조

        UserInfoDTO pDTO;  // 웹에서 받는 정보를 저장할 변수

        try {
            //웹에서 받는 정보 임시로 변수에 임시 저장
            String userId = CmmUtil.nvl(request.getParameter("userId")); // 아이디
            String userName = CmmUtil.nvl(request.getParameter("userName")); // 이름
            String password = CmmUtil.nvl(request.getParameter("password")); // 비밀번호
            String email = CmmUtil.nvl(request.getParameter("email")); // 이메일

            // 개발을 잘하는 사람은 로그를 잘 찍어서 값이 넘어오는 지 확인하는 사람! -교수님
            log.info("userId : " + userId);
            log.info("userName : " + userName);
            log.info("password : " + password);
            log.info("email : " + email);

            // 웹에서 받는 정보를 저장할 변수를 메모리에 올리기
            pDTO = new UserInfoDTO();

            pDTO.setUserId(userId);
            pDTO.setUserName(userName);
            // 비밀번호는 절대로 복호화되지 않도록 해시 알고리즘으로 암호화 함
            pDTO.setPassword(EncryptUtil.encHashSHA256(password));
            // 민감 정보인 이메일은 AES128-CBC로 암호화함
            pDTO.setEmail(EncryptUtil.encAES128CBC(email));

            // 회원가입
             res = userInfoService.insertUserInfo(pDTO);

             log.info("회원가입 결과(res) : {}", res);

             if (res == 1) {
                 msg = "회원가입되었습니다.";

             } else if (res == 2) {
                 msg = "이미 가입된 아이디입니다.";

             } else {
                 msg = "오류로 인하여 회원가입에 실패하였습니다.";

             }

        } catch (Exception e) {
            // 저장이 실패하면 사용자에게 보여줄 메세지
            msg = "실패하였습니다. : " + e;
            log.info(e.toString());

        } finally {
            // 결과 메시지 전달하기
            dto = new MsgDTO();
            dto.setResult(res);
            dto.setMsg(msg);

            log.info("{}.insertUserInfo End!", this.getClass().getName());
        }

        return dto;
    }

    // 로그인 화면으로 이동하기
    @GetMapping(value = "login")
    public String login() {
        log.info("{}.login Start!", this.getClass().getName());

        return "user/login";
    }

    // 로그인 로직
    @ResponseBody
    @PostMapping(value = "loginProc")
    public MsgDTO loginProc(HttpServletRequest request, HttpSession session) throws Exception  {

        log.info("{}.loginProc Start!", this.getClass().getName());

        int res = 0; // 로그인 처리 결과를 저장할 변수
        String msg = ""; // 로그인 결과에 대한 메시지를 전달할 변수
        MsgDTO dto; // 결과 메시지 구종

        // 웹에서 받는 정보를 저장할 변수
        UserInfoDTO pDTO;

        try {

            String userId = CmmUtil.nvl(request.getParameter("userId")); // 아이디
            String password = CmmUtil.nvl(request.getParameter("password")); // 비밀번호

            log.info("userId : {} / password : {}", userId, password);

            // 웹에서 받는 정보를 저장할 변수를 메모리에 올리기
            pDTO = new UserInfoDTO();

            pDTO.setUserId(userId);

            // 비밀번호는 절대 복호화 안되게 해시 알고리즘으로 암호화
            pDTO.setPassword(EncryptUtil.encHashSHA256(password));

            // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인을 위해 userInfoService 호출
            UserInfoDTO rDTO = userInfoService.getLogin(pDTO);

            if (!CmmUtil.nvl(rDTO.getUserId()).isEmpty()) { // 로그인 성공

                res = 1;

                msg = "로그인이 성공했습니다.";

                session.setAttribute("SS_USER_ID", userId);
                session.setAttribute("SS_USER_NAME", CmmUtil.nvl(rDTO.getUserName()));


            } else {
                msg = "아이디와 비밀번호가 올바르지 않습니다.";

            }
        }catch (Exception e) {

            msg = "시스템 문제로 로그인이 실패했습니다.";
            res = 2;
            log.info(e.toString());

        } finally {
            dto = new MsgDTO();
            dto.setResult(res);
            dto.setMsg(msg);

            log.info("{}.loginProc End!", this.getClass().getName());
        }

        return dto;
    }

    @ResponseBody
    @PostMapping(value = "logoutProc")
    public MsgDTO logoutProc(HttpSession session) {
        log.info("{}.logoutProc Start!", this.getClass().getName());

        int res = 0;
        String msg = "";
        MsgDTO dto;

        session.invalidate();  // ✅ 세션 무효화
        res = 1;
        msg = "로그아웃되었습니다.";

        dto = new MsgDTO();     // ✅ 응답 객체 구성
        dto.setResult(res);
        dto.setMsg(msg);

        return dto;
    }

    // 아이디 찾기 화면
    @GetMapping(value = "findId")
    public String searchUserId() {
        log.info("{}.searchUserId Start!", this.getClass().getName());

        log.info("{}.searchUserId End!", this.getClass().getName());

        return "user/findId";
    }

    // 아이디 찾기 로직
    @PostMapping(value = "findIdProc")
    public String findIdProc(HttpServletRequest request, ModelMap model) throws Exception {

        log.info("{}.findIdProc Start!", this.getClass().getName());

        String userName = CmmUtil.nvl(request.getParameter("userName"));
        String email = CmmUtil.nvl(request.getParameter("email"));

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserName(userName);
        pDTO.setEmail(EncryptUtil.encAES128CBC(email));

        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.searchUserIdOrPasswordProc(pDTO))
                .orElseGet(UserInfoDTO::new);

        if (CmmUtil.nvl(rDTO.getUserId()).length() > 0) {
            model.addAttribute("userId", CmmUtil.nvl(rDTO.getUserId())); // 아이디만 전달
        } else {
            model.addAttribute("userId", "알 수 없음"); // 실패 시 메시지 처리
        }

        log.info("{}.findIdProc End!", this.getClass().getName());

        return "user/findIdSuccess"; // templates/user/findIdSuccess.html
    }




    // 비밀번호 찾기 화면
    @GetMapping(value = "findPw")
    public String findPw(HttpSession session) {

        log.info("{}.findPw Start!", this.getClass().getName());

        // 강제 URL 입력 같은 경우를 대비해 세션 삭제
        // 비밀번호 재생성하는 화면은 보안을 위해 NEW_PASSWORD 세션 삭제
        session.setAttribute("NEW_PASSWORD", "");
        session.removeAttribute("NEW_PASSWORD");

        log.info("{}.findPw End!", this.getClass().getName());

        return "user/findPw";
    }

    // 비밀번호 찾기 로직 수행
    @PostMapping(value = "findPwProc")
    public String findPwProc(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {

        log.info("{}.findPwProc Start/end!", this.getClass().getName());

        String userId = CmmUtil.nvl(request.getParameter("userId"));   // 아이디
        String userName = CmmUtil.nvl(request.getParameter("userName")); // 이름
        String email = CmmUtil.nvl(request.getParameter("email"));     // 이메일

        log.info("입력값 userId: {}, userName: {}, email: {}", userId, userName, email);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);
        pDTO.setUserName(userName);
        pDTO.setEmail(EncryptUtil.encAES128CBC(email));

        // 사용자 존재 여부 확인
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.searchUserIdOrPasswordProc(pDTO))
                .orElseGet(UserInfoDTO::new);

        if (CmmUtil.nvl(rDTO.getUserId()).length() > 0) {
            log.info("회원 정보 일치: 비밀번호 재설정 페이지로 이동");
            session.setAttribute("NEW_PASSWORD", rDTO.getUserId()); // 세션에 아이디 저장
            log.info("{}.findPwProc End!", this.getClass().getName());
            return "user/newPassword";
        } else {
            log.warn("입력된 정보와 일치하는 회원이 없습니다.");
            model.addAttribute("msg", "입력한 정보로 가입된 회원이 없습니다.");
            log.info("{}.findPwProc End!", this.getClass().getName());
            return "user/findPw";
        }
    }


    // 비밀번호 재설정 로직
    @PostMapping(value = "newPasswordProc")
    public String newPasswordProc(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {

        log.info("{}.newPasswordProc Start!", this.getClass().getName());

        String msg; // 웹에 보여줄 메세지

        // 정상적인 접근인지 체크
        String newPassword = CmmUtil.nvl((String) session.getAttribute("NEW_PASSWORD"));

        if (!newPassword.isEmpty()) {

            String password = CmmUtil.nvl(request.getParameter("password")); // 신규 비밀번호

            log.info("password : {}", password);

            UserInfoDTO pDTO = new UserInfoDTO();
            pDTO.setUserId(newPassword);
            pDTO.setPassword(EncryptUtil.encHashSHA256(password));

            userInfoService.newPasswordProc(pDTO);

            // 비밀번호 재생성하는 화면은 보안을 위해 생성한 NEW_PASSWORD 세션 삭제
            session.setAttribute("NEW_PASSWORD", "");
            session.removeAttribute("NEW_PASSWORD");

            msg = "비밀번호가 재설정되었습니다.";

        } else {
            msg = "비정상 접근입니다.";
        }
        model.addAttribute("msg", msg);

        log.info("{}.newPasswordProc End!", this.getClass().getName());

        return "user/newPasswordSuccess";
    }

    @GetMapping("/myPage")
    public String getMyPage(HttpSession session, Model model) throws Exception {
        String userId = (String) session.getAttribute("SS_USER_ID");

        log.info("▶ [myPage] 사용자 ID: {}", userId);

        if (userId == null) {
            return "redirect:/login";
        }

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);

        UserInfoDTO rDTO = userInfoService.getUserInfo(pDTO);

        model.addAttribute("user", rDTO); // user.userId, user.userName 등으로 Thymeleaf에 전달

        return "user/myPage"; // → templates/user/myPage.html
    }

    //
    @PostMapping("/updateBodyInfo")
    public String updateBodyInfo(@ModelAttribute ExerciseInfoDTO pDTO,
                                 HttpSession session,
                                 Model model) throws Exception {

        String userId = (String) session.getAttribute("SS_USER_ID");

        log.info("▶ 신체 정보 수정 요청 시작");
        log.info("세션에서 가져온 사용자 ID: {}", userId);

        if (userId == null) {
            log.warn("⛔ 세션에 사용자 ID 없음 - 로그인 페이지로 이동");
            return "redirect:/login";
        }

        pDTO.setUserId(userId);
        log.info("수정할 신체 정보 - height: {}, weight: {}", pDTO.getHeight(), pDTO.getWeight());

        // DB에 업데이트
        exerciseService.updateBodyInfo(pDTO);
        log.info("✅ MongoDB 신체 정보 업데이트 완료");

        // 수정 후 다시 조회하여 화면에 표시
        ExerciseInfoDTO updatedDTO = exerciseService.getBodyInfo(userId);
        model.addAttribute("body", updatedDTO); // <- 여기 추가 필수

        model.addAttribute("message", "신체 정보가 저장되었습니다.");

        return "user/bodyUpdate"; // 그대로 유지
    }

    /**
     * 신체 정보 수정 화면 진입
     */
    @GetMapping("/bodyUpdate")
    public String bodyUpdatePage(HttpSession session, Model model) throws Exception {
        // ✅ 세션에서 사용자 ID 추출
        String userId = (String) session.getAttribute("SS_USER_ID");

        log.info("▶ [GET /user/bodyUpdate] 신체 정보 수정 화면 요청");
        log.info("세션 사용자 ID: {}", userId);

        if (userId == null) {
            log.warn("⛔ 로그인되지 않은 사용자 요청 → 로그인 페이지로 리다이렉트");
            return "redirect:/login";
        }

        // ✅ 사용자 신체 정보 조회
        ExerciseInfoDTO pDTO = exerciseService.getBodyInfo(userId);

        if (pDTO != null) {
            log.info("불러온 신체 정보: height={}, weight={}", pDTO.getHeight(), pDTO.getWeight());
            model.addAttribute("body", pDTO); // ✅ 템플릿에서 사용할 객체 전달
        } else {
            log.warn("⚠️ 신체 정보가 DB에 존재하지 않음 → 기본 빈 객체 전달");
            model.addAttribute("body", new ExerciseInfoDTO()); // ⚠️ 널 방지용 기본값
        }

        // ✅ message 속성은 POST 저장 후 redirect 시에만 사용되므로, GET 요청에서는 포함하지 않음
        return "user/bodyUpdate"; // → templates/user/bodyUpdate.html 로 이동
    }

    /**
     * 회원탈퇴 비밀번호 확인 화면 진입
     */
    @GetMapping("/deleteCheck")
    public String deleteCheckPage(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("SS_USER_ID");

        log.info("▶ [GET /user/deleteCheck] 회원탈퇴 비밀번호 확인 페이지 진입");
        log.info("세션 사용자 ID: {}", userId);

        if (userId == null) {
            log.warn("⛔ 로그인되지 않은 사용자 요청 → 로그인 페이지로 리다이렉트");
            return "redirect:/login";
        }

        return "user/deleteCheck"; // templates/user/deleteCheck.html
    }

    /**
     * 회원 탈퇴 처리
     */
    @PostMapping("/deleteProc")
    public String deleteUser(HttpSession session,
                             @RequestParam("password") String password,
                             RedirectAttributes redirectAttributes) throws Exception {

        String userId = (String) session.getAttribute("SS_USER_ID");

        log.info("▶ [POST /user/deleteProc] 회원 탈퇴 요청 수신");
        log.info("세션 사용자 ID: {}", userId);

        if (userId == null) {
            log.warn("⛔ 로그인되지 않은 사용자 → 로그인 페이지로 리다이렉트");
            return "redirect:/login";
        }

        // DTO 구성
        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);
        pDTO.setPassword(password);

        log.info("🧾 탈퇴 요청 정보: userId={}, password=****", pDTO.getUserId());

        // 서비스에서 탈퇴 처리 (비밀번호 검증 포함)
        int res = userInfoService.deleteUserInfo(pDTO);

        if (res > 0) {
            log.info("✅ 회원 탈퇴 성공 → 세션 무효화 및 메인 페이지로 이동");
            session.invalidate(); // 세션 종료
            redirectAttributes.addFlashAttribute("message", "회원 탈퇴가 완료되었습니다.");
            return "redirect:/index";
        } else {
            log.warn("❌ 회원 탈퇴 실패 → 비밀번호 불일치 또는 존재하지 않는 사용자");
            redirectAttributes.addFlashAttribute("message", "비밀번호가 일치하지 않습니다.");
            return "redirect:/user/deleteCheck";
        }
    }
}
