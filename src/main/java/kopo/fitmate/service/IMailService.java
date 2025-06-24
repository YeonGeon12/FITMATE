package kopo.fitmate.service;

import kopo.fitmate.dto.MailDTO;

public interface IMailService {

    // 메일 발송
    int doSendMail(MailDTO pDTO);

}
