package kopo.fitmate.service.impl;

import kopo.fitmate.dto.diet.DietInfoDTO;
import kopo.fitmate.service.IDietInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DietInfoService implements IDietInfoService {

    private final MongoTemplate mongoTemplate;

    @Override
    public void saveDiet(DietInfoDTO dto) {
        Date now = new Date();
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        mongoTemplate.insert(dto, "DIET_INFO"); // MongoDB에 저장
    }

    @Override
    public List<DietInfoDTO> getDietsByUserId(String userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, DietInfoDTO.class, "DIET_INFO");
    }

    //
    @Override
    public DietInfoDTO getDietById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, DietInfoDTO.class, "DIET_INFO");
    }

    // 식단 삭제
    @Override
    public void deleteDietById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, DietInfoDTO.class, "DIET_INFO");
    }
}
