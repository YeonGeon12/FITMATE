package kopo.fitmate.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UserInfoDTO {

    // 아이디
    private String userId;
    // 유저 이름
    private String userName;
    // 비밀번호
    private String password;
    // 이메일
    private String email;
    // 등록자 아이디
    private String regId;
    // 등록 시간
    private String regDt;
    // 수정자 아이디
    private String chgId;
    // 수정 시간
    private String chgDt;
    // 중복가입 방지
    private String existsYn;
    // 이메일 중복체크를 위한 인증번호(6자리)
    private int authNumber;
}
