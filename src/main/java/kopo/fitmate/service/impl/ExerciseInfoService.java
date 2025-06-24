package kopo.fitmate.service.impl;

import com.mongodb.client.result.DeleteResult;
import kopo.fitmate.dto.exercise.ExerciseInfoDTO;
import kopo.fitmate.service.IExerciseInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseInfoService implements IExerciseInfoService {

    private final MongoTemplate mongoTemplate;

    @Override
    public void saveExercise(ExerciseInfoDTO dto) {
        Date now = new Date();
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        mongoTemplate.insert(dto, "EXERCISE_INFO"); // MongoDB에 저장
    }

    @Override
    public List<ExerciseInfoDTO> getExercisesByUserId(String userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, ExerciseInfoDTO.class, "EXERCISE_INFO");
    }

    @Override
    public ExerciseInfoDTO getExerciseById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, ExerciseInfoDTO.class, "EXERCISE_INFO");
    }

    // 운동 루틴 삭제
    @Override
    public void deleteExerciseById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, ExerciseInfoDTO.class, "EXERCISE_INFO");
    }
}
