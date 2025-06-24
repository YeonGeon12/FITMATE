package kopo.fitmate.service;

import kopo.fitmate.dto.diet.DietDTO;
import kopo.fitmate.dto.diet.DietInfoDTO;
import kopo.fitmate.dto.diet.DietPlanDTO;

import java.util.List;

/**
 * 식단 추천 서비스 인터페이스
 * - 사용자 입력 정보를 기반으로 GPT에게 식단 루틴을 요청
 * - 결과는 DietPlanDTO 리스트 형태로 반환
 */
public interface IDietService {

    /**
     * 사용자의 dietType을 기반으로 GPT에게 일주일 식단 추천을 요청하고 결과를 반환한다.
     *
     * @param pDTO 사용자 입력 DTO (식단 유형)
     * @return 일주일간 아침/점심/저녁 추천 식단 목록
     * @throws Exception GPT API 호출 또는 파싱 중 예외 발생 시
     */
    DietInfoDTO getDietRecommendation(DietDTO pDTO) throws Exception;
}
