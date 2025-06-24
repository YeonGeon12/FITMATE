package kopo.fitmate.mapper;

import kopo.fitmate.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserInfoMapper {

    // 회원 가입 전 아이디 중복 체크하기(DB 조회하기)
    UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception;

    // 회원 가입 전 이메일 중복 체크하기
    UserInfoDTO getEmailExists(UserInfoDTO pDTO) throws Exception;

    // 회원 가입하기(회원 정보 등록하기)
    int insertUserInfo(UserInfoDTO pDTO) throws Exception;

    // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기
    UserInfoDTO getLogin(UserInfoDTO pDTO) throws Exception;

    // 아이디, 비밀번호 찾기
    UserInfoDTO getUserId(UserInfoDTO pDTO) throws Exception;

    // 비밀번호 재설정
    int updatePassword(UserInfoDTO pDTO) throws Exception;

    // 마이페이지 조회
    UserInfoDTO getUserInfo(UserInfoDTO pDTO) throws Exception;

    // 회원 탈퇴
    int deleteUserInfo(UserInfoDTO pDTO) throws Exception;

}
