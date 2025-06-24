package kopo.fitmate.service;

import kopo.fitmate.dto.diet.DietInfoDTO;

import java.util.List;

/**
 * 식단 정보와 관련된 비즈니스 로직을 정의하는 인터페이스.
 */
public interface IDietInfoService {

    // MongoDB에 새로운 식단 정보 저장
    void saveDiet(DietInfoDTO dto);

    // 특정 사용자 ID로 식단 정보 전체 조회
    List<DietInfoDTO> getDietsByUserId(String userId);

    // 특정 식단 ID로 상세 조회
    DietInfoDTO getDietById(String id);

    // 특정 식단 ID로 식단 삭제
    void deleteDietById(String id);
}
