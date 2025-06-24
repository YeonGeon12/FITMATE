package kopo.fitmate.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import kopo.fitmate.dto.MailDTO;
import kopo.fitmate.dto.UserInfoDTO;
import kopo.fitmate.mapper.IUserInfoMapper;
import kopo.fitmate.service.IMailService;
import kopo.fitmate.service.IUserInfoService;
import kopo.fitmate.util.CmmUtil;
import kopo.fitmate.util.DateUtil;
import kopo.fitmate.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserInfoService implements IUserInfoService {

    private final IUserInfoMapper userInfoMapper; // 회원관련 SQL 사용을 위해 Mapper 가져오기

    private final IMailService mailService; // 메일 발송을 위한 MailService 자바 객체 가져오기

    private final MongoTemplate mongoTemplate;

    // 회원 아이디 중복체크
    @Override
    public UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception {

        log.info("{}.getUserIdExists Start!", this.getClass().getName());

        // DB 이메일이 존재하는지 SQL 쿼리 실행
        UserInfoDTO rDTO = userInfoMapper.getUserIdExists(pDTO);

        log.info("{}.getUserIdExists End!", this.getClass().getName());

        return rDTO;
    }

    // 이메일 중복 체크
    @Override
    public UserInfoDTO getEmailExists(UserInfoDTO pDTO) throws Exception {

        log.info("{}.emailAuth Start!", this.getClass().getName());

        // DB 이메일이 존재하는지 SQL 쿼리 실행
        UserInfoDTO rDTO = Optional.ofNullable(userInfoMapper.getEmailExists(pDTO))
                .orElseGet(UserInfoDTO::new);

        log.info("rDTO : {}", rDTO);

        // 이메일 주소가 중복되지 않는다면.. 메일 발송
        if (CmmUtil.nvl(rDTO.getExistsYn()).equals("N")) {

            // 6자리 랜덤 숫자 생성하기
            int authNumber = ThreadLocalRandom.current().nextInt(100000, 1000000);

            log.info("authNumber : {}", authNumber);

            // 인증번호 발송 로직
            MailDTO dto = new MailDTO();

            dto.setTitle("이메일 중복 확인 인증번호 발송 메일");
            dto.setContents("인증번호는 " + authNumber + " 입니다.");
            dto.setToMail(EncryptUtil.decAES128CBC(CmmUtil.nvl(pDTO.getEmail())));

            mailService.doSendMail(dto); // 이메일 발송

            dto = null;

            rDTO.setAuthNumber(authNumber); // 인증번호를 결과 값에 넣어두기

        }

        log.info("{}.emailAuth End!", this.getClass().getName());

        return rDTO;
    }

    // 회원 가입 코드
    @Override
    public int insertUserInfo(UserInfoDTO pDTO) throws Exception {

        log.info("{}.insertUserInfo Start!", this.getClass().getName());

        // 회원 가입 성공 : 1, 아이디 중복으로 인한 가입 취소 : 2, 기타 에러 발생 : 0
        int res;

        // 회원가입
        int success = userInfoMapper.insertUserInfo(pDTO);

        // db에 데이터가 등록되었다면...
        if (success > 0) {
            res = 1;

            MailDTO mDTO = new MailDTO();

            // 회원 정보화면에서 입력받은 이메일 변수
            mDTO.setToMail(EncryptUtil.decAES128CBC(CmmUtil.nvl(pDTO.getEmail())));

            mDTO.setTitle("[Fitmate] 회원가입을 축하드립니다! 🎉"); // 제목

            // 메일 내용에 가입자 이름 넣어서 내용 발송
            mDTO.setContents(CmmUtil.nvl(pDTO.getUserName()) + "님의 회원가입을 진심으로 축하드립니다." +
                    " Fitmate와 함께 건강한 삶을 시작해보세요!");

            // 회원 가입이 성공했기 때문에 메일을 발송함
            mailService.doSendMail(mDTO);

        } else {
            res = 0;
        }

        log.info("{}.insertUserInfo End!", this.getClass().getName());

        return res;
    }

    // 로그인 코드
    @Override
    public UserInfoDTO getLogin(UserInfoDTO pDTO) throws Exception {

        log.info("{}.getLogin Start!", this.getClass().getName());

        // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인을 위한 Mapper 호출하기
        UserInfoDTO rDTO = Optional.ofNullable(userInfoMapper.getLogin(pDTO))
                .orElseGet(UserInfoDTO::new);

        if (!CmmUtil.nvl(rDTO.getUserId()).isEmpty()) {

            MailDTO mDTO = new MailDTO();

            // 아이디, 패스워드 일치하는지 체크하는 쿼리에서 이메일 값 받아오기(암호화 된 상태에서 넘어와서 복호화 수행)
            mDTO.setToMail(EncryptUtil.decAES128CBC(CmmUtil.nvl(rDTO.getEmail())));

            mDTO.setContents(DateUtil.getDateTime("yyyy-MM.dd hh:mm:ss") + "에 "
                    + CmmUtil.nvl(rDTO.getUserName()) + "님이 로그인하였습니다.");

            // 회원 가입이 성공했기 때문에 메일이 발송됨
            mailService.doSendMail(mDTO);

        }

        log.info("{}.getLogin End!", this.getClass().getName());

        return rDTO;
    }

    // Controller에서 전달받는 userId 변수 값의 존재여부
    @Override
    public UserInfoDTO searchUserIdOrPasswordProc(UserInfoDTO pDTO) throws Exception {

        log.info("{}.searchUserIdOrPasswordProc Start!", this.getClass().getName());

        UserInfoDTO rDTO = userInfoMapper.getUserId(pDTO);

        log.info("{}.searchUserIdOrPasswordProc End!", this.getClass().getName());

        return rDTO;
    }

    // 비밀번호 재설정 함수 구현
    @Override
    public int newPasswordProc(UserInfoDTO pDTO) throws Exception {

        log.info("{}.newPasswordProc Start!", this.getClass().getName());

        // 비밀번호 재설정
        int success = userInfoMapper.updatePassword(pDTO);

        log.info("{}.newPasswordProc End!", this.getClass().getName());

        return success;
    }

    // 마이 페이지 조회
    @Override
    public UserInfoDTO getUserInfo(UserInfoDTO pDTO) throws Exception {

        return userInfoMapper.getUserInfo(pDTO);

    }

    // 회원 탈퇴
    @Override
    public int deleteUserInfo(UserInfoDTO pDTO) throws Exception {
        // ✅ 입력된 평문 비밀번호를 암호화
        String encPassword = EncryptUtil.encHashSHA256(pDTO.getPassword());
        pDTO.setPassword(encPassword); // 암호화된 비밀번호로 교체

        log.info("탈퇴 전 암호화된 비밀번호: {}", encPassword);

        // ✅ 회원 탈퇴 (RDB)
        int res = userInfoMapper.deleteUserInfo(pDTO);

        // ✅ 회원 탈퇴가 정상적으로 되었을 때만 MongoDB 데이터도 삭제
        if (res > 0) {
            String userId = pDTO.getUserId();  // 반드시 DTO에 userId가 존재해야 함

            // MongoDB 컬렉션에서 관련 데이터 삭제
            mongoTemplate.remove(Query.query(Criteria.where("userId").is(userId)), "EXERCISE_INFO");
            mongoTemplate.remove(Query.query(Criteria.where("userId").is(userId)), "DIET_INFO");
            mongoTemplate.remove(Query.query(Criteria.where("userId").is(userId)), "EXERCISE_PLAN");

            log.info("MongoDB 관련 데이터 삭제 완료: userId = {}", userId);
        } else {
            log.warn("회원 탈퇴 실패 또는 존재하지 않는 사용자");
        }

        return res;
    }
}
