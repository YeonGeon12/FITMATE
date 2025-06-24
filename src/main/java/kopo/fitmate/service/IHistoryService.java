package kopo.fitmate.service;

import kopo.fitmate.dto.diet.DietInfoDTO;
import kopo.fitmate.dto.exercise.ExerciseInfoDTO;

import java.util.List;
import java.util.Optional;

/**
 * 기록 조회 기능을 위한 통합 서비스 인터페이스
 */
public interface IHistoryService {

    // 사용자 ID별 운동/식단 기록 리스트
    List<ExerciseInfoDTO> getExerciseHistory(String userId);
    List<DietInfoDTO> getDietHistory(String userId);

    // 통합 조회 (상세)
    Optional<?> getRecordById(String id, String type);

    // 통합 삭제
    void deleteRecordById(String id, String type);
}
