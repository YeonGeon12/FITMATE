package kopo.fitmate.service;

import kopo.fitmate.dto.UserInfoDTO;

public interface IUserInfoService {

    // 아이디 중복 체크
    UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception;

    // 이메일 주소 중복 체크 및 인증 값
    UserInfoDTO getEmailExists(UserInfoDTO pDTO) throws Exception;

    // 회원 가입하기
    int insertUserInfo(UserInfoDTO pDTO) throws Exception;

    // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기
    UserInfoDTO getLogin(UserInfoDTO pDTO) throws Exception;

    // 아이디, 비밀번호 찾기에 활용
    UserInfoDTO searchUserIdOrPasswordProc(UserInfoDTO pDTO) throws Exception;

    // 비밀번호 재설정
    int newPasswordProc(UserInfoDTO pDTO) throws Exception;

    // 마이페이지 조회
    UserInfoDTO getUserInfo(UserInfoDTO pDTO) throws Exception;

    // 회원 탈퇴
    int deleteUserInfo(UserInfoDTO pDTO) throws Exception;

}
