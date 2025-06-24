package kopo.fitmate.service.impl;

import kopo.fitmate.dto.diet.DietInfoDTO;
import kopo.fitmate.dto.exercise.ExerciseInfoDTO;
import kopo.fitmate.service.IHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HistoryService implements IHistoryService {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<ExerciseInfoDTO> getExerciseHistory(String userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, ExerciseInfoDTO.class, "EXERCISE_INFO");
    }

    @Override
    public List<DietInfoDTO> getDietHistory(String userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, DietInfoDTO.class, "DIET_INFO");
    }

    @Override
    public Optional<?> getRecordById(String id, String type) {
        Query query = new Query(Criteria.where("_id").is(id));
        if ("exercise".equalsIgnoreCase(type)) {
            ExerciseInfoDTO result = mongoTemplate.findOne(query, ExerciseInfoDTO.class, "EXERCISE_INFO");
            return Optional.ofNullable(result);
        } else if ("diet".equalsIgnoreCase(type)) {
            DietInfoDTO result = mongoTemplate.findOne(query, DietInfoDTO.class, "DIET_INFO");
            return Optional.ofNullable(result);
        }
        return Optional.empty();
    }

    @Override
    public void deleteRecordById(String id, String type) {
        Query query = new Query(Criteria.where("_id").is(id));
        if ("exercise".equalsIgnoreCase(type)) {
            mongoTemplate.remove(query, ExerciseInfoDTO.class, "EXERCISE_INFO");
        } else if ("diet".equalsIgnoreCase(type)) {
            mongoTemplate.remove(query, DietInfoDTO.class, "DIET_INFO");
        }
    }
}
