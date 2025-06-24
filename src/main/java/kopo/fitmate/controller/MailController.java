package kopo.fitmate.controller;

import jakarta.servlet.http.HttpServletRequest;
import kopo.fitmate.dto.MailDTO;
import kopo.fitmate.dto.MsgDTO;
import kopo.fitmate.service.IMailService;
import kopo.fitmate.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping(value = "/mail")
@RequiredArgsConstructor
@RestController
public class MailController {

    private final IMailService mailService; // 메일 발송을 위한 서비스 객체를 사용하기


    @GetMapping(value = "mailForm")
    public String mailForm() {

        log.info("mailForm");
        return "mail/mailForm";
    }

    /**
     * 메일 발송하기
     */
    @ResponseBody
    @PostMapping(value = "sendMail")
    public MsgDTO sendMail(HttpServletRequest request) {

        // 로그 찍기
        log.info("{}.sendMail Start!", this.getClass().getName());

        String msg; // 발송 결과 메시지

        // 웹 URL로부터 전달 받는 값들
        String toMail = CmmUtil.nvl(request.getParameter("toMail"));
        String title = CmmUtil.nvl(request.getParameter("title"));
        String contents = CmmUtil.nvl(request.getParameter("contents"));

        log.info("toMail: {}, title: {}, contents: {}", toMail, title, contents);

        // 메일 발송할 정보 넣기 위한 DTO 객체 생성하기
        MailDTO pDTO = new MailDTO();

        // 웹에서 받은 값을 DTO에 넣기
        pDTO.setToMail(toMail);
        pDTO.setTitle(title);
        pDTO.setContents(contents);

        // 메일 발송하기
        int res = mailService.doSendMail(pDTO);

        if (res == 1) {
            msg = "메일 발송하였습니다.";

        } else {
            msg = "메일 발송 실패하였습니다.";
        }

        log.info(msg);

        // 결과 메시지 전달하기
        MsgDTO dto = new MsgDTO();
        dto.setMsg(msg);

        log.info("{}.sendMail End!", this.getClass().getName());

        return dto;
    }
}