package kopo.fitmate.service;

import kopo.fitmate.dto.exercise.ExerciseDTO;
import kopo.fitmate.dto.exercise.ExerciseInfoDTO;
import kopo.fitmate.dto.exercise.ExercisePlanDTO;

import java.util.List;

/**
 * 운동 추천 서비스 인터페이스
 * - 사용자 입력 정보를 기반으로 GPT에게 운동 루틴을 요청
 * - 결과는 ExercisePlanDTO 리스트 형태로 반환
 */
public interface IExerciseService {

    /**
     * GPT API에 사용자 정보를 전달하여 일주일 운동 루틴을 추천받음
     *
     * @param pDTO 사용자 입력 정보 DTO
     * @return 요일별 운동 루틴 리스트
     * @throws Exception GPT 호출 또는 파싱 실패 시 예외 발생
     */
    List<ExercisePlanDTO> getExerciseRecommendation(ExerciseDTO pDTO) throws Exception;

    /**
     * 사용자 신체 정보(키, 체중)를 업데이트한다.
     * 해당 정보는 MongoDB의 EXERCISE_INFO 컬렉션에 저장된다.
     *
     * @param pDTO 사용자 신체 정보 DTO (userId, height, weight 포함)
     * @throws Exception 예외 처리
     */
    void updateBodyInfo(ExerciseInfoDTO pDTO) throws Exception;

    /**
     * 사용자 신체 정보 조회 (MongoDB)
     * @param userId 사용자 ID
     * @return 해당 사용자의 신체 정보 (없으면 null)
     */
    ExerciseInfoDTO getBodyInfo(String userId) throws Exception;

    /**
     * 사용자의 운동 루틴 기록을 MongoDB에서 조회한다.
     * 이 메서드는 캘린더 기능에 사용되며, 저장된 루틴 중 날짜 정보가 있는 항목들을 가져온다.
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 운동 루틴 리스트
     */
    List<ExercisePlanDTO> getExerciseRecordsByUser(String userId);

    /**
     * 오늘 운동을 완료했는지 기록하고, 이미 있다면 저장하지 않는다.
     * @param userId 사용자 ID
     * @return true: 저장됨 / false: 이미 기록 존재
     */
    boolean saveTodayExerciseCompletion(String userId);


}