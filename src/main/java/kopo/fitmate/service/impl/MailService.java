package kopo.fitmate.service.impl;

import jakarta.mail.internet.MimeMessage;
import kopo.fitmate.dto.MailDTO;
import kopo.fitmate.service.IMailService;
import kopo.fitmate.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService implements IMailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Override
    public int doSendMail(MailDTO pDTO) {

        //  로그 찍기
        log.info("{}.doSendMail Start!", this.getClass().getName());

        // 메일 발송 성공 여부
        int res = 1;

        // 전달 받은 DTO로부터 데이터 가져오기
        if (pDTO == null) {
            pDTO = new MailDTO();
        }

        String toMail = CmmUtil.nvl(pDTO.getToMail()); // 받는 사람
        String title = CmmUtil.nvl(pDTO.getTitle()); // 메일 제목
        String contents = CmmUtil.nvl(pDTO.getContents()); // 메일 제목

        log.info("toMail: {}, title: {}, contents: {}", toMail, title, contents);

        // 메일 발송 구조(파일 첨부 가능)
        MimeMessage message = mailSender.createMimeMessage();

        // 메일 발송 메시지 구조를 쉽게 생성하게 도와주는 객체
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, "UTF-8");

        try {

            messageHelper.setTo(toMail);
            messageHelper.setFrom(fromMail);
            messageHelper.setSubject(title);
            messageHelper.setText(contents);

            mailSender.send(message);

        } catch (Exception e) {
            res = 0;
            log.info("[ERROR] doSendMail : {}", e);
        }

        log.info("{}.doSendMail End!", this.getClass().getName());

        return res;
    }
}
