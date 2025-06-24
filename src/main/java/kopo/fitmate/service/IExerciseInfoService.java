package kopo.fitmate.service;

import kopo.fitmate.dto.exercise.ExerciseInfoDTO; // 운동 정보 데이터(ExerciseInfoDTO)를 사용하기 위해 import 합니다.

import java.util.List; // 여러 개의 운동 정보를 반환할 때 List 타입을 사용하기 위해 import 합니다.

/**
 * 운동 정보와 관련된 비즈니스 로직을 정의하는 인터페이스.
 */
public interface IExerciseInfoService {

    // 새로운 운동 정보 저장
    void saveExercise(ExerciseInfoDTO dto);

    // 사용자 ID로 모든 운동 정보 조회
    List<ExerciseInfoDTO> getExercisesByUserId(String userId);

    // 운동 ID로 상세 정보 조회
    ExerciseInfoDTO getExerciseById(String id);

    // 운동 ID로 해당 기록 삭제
    void deleteExerciseById(String id);
}
