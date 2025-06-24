package kopo.fitmate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.fitmate.dto.exercise.ExerciseDTO;
import kopo.fitmate.dto.exercise.ExerciseInfoDTO;
import kopo.fitmate.dto.exercise.ExercisePlanDTO;
import kopo.fitmate.service.IExerciseService;
import kopo.fitmate.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseService implements IExerciseService {

    private final OpenAiService openAiService;
    private final ExerciseInfoService exerciseInfoService;
    private final ObjectMapper objectMapper;
    private final MongoTemplate mongoTemplate; // ✅ 여기에 추가해 주세요

    @Override
    public List<ExercisePlanDTO> getExerciseRecommendation(ExerciseDTO dto) throws Exception {
        log.info("운동 추천 요청 수신: {}", dto);

        // 1. GPT 프롬프트 생성
        String prompt = String.format("""
        다음은 사용자의 운동 정보입니다:
        - 성별: %s
        - 키: %.1fcm
        - 몸무게: %.1fkg
        - 운동 수준: %s
        - 목표: %s
        월요일부터 일요일까지 하루에 1개 루틴씩 운동 루틴을 구성하고,
        각 요일마다 아래와 같은 JSON 형식으로 구성해줘:
        {
            "day": "월요일",
            "routineTitle": "전신 루틴",
            "routineDetail": ["운동1", "운동2"],
            "estimatedTime": "30분"
        }
        전체를 JSON 배열로 반환해줘.
        """,
                dto.getGender(),
                dto.getHeight(),
                dto.getWeight(),
                dto.getLevel(),
                String.join(", ", dto.getGoal())
        );

        // 2. GPT API 호출
        String gptContent = openAiService.callGptAPI(prompt);

        // 3. 응답 파싱
        List<ExercisePlanDTO> resultList = objectMapper.readValue(
                gptContent,
                new TypeReference<>() {}
        );

        // ✅ 저장은 하지 않음 (JS 저장 버튼에서 별도로 처리)
        return resultList;
    }

    // 신체 정보 업데이트
    @Override
    public void updateBodyInfo(ExerciseInfoDTO pDTO) throws Exception {

        log.info("▶ [updateBodyInfo] 신체 정보 업데이트 시작");
        log.info("받은 사용자 ID: {}", pDTO.getUserId());
        log.info("수정할 키: {} cm / 몸무게: {} kg", pDTO.getHeight(), pDTO.getWeight());

        // MongoDB 쿼리 생성: userId 기준으로 업데이트
        Query query = new Query(Criteria.where("userId").is(pDTO.getUserId()));
        Update update = new Update()
                .set("height", pDTO.getHeight())
                .set("weight", pDTO.getWeight())
                .set("updatedAt", new Date());

        // MongoDB 업데이트 실행
        mongoTemplate.updateFirst(query, update, "EXERCISE_INFO");

        log.info("✅ [updateBodyInfo] MongoDB 업데이트 완료");
    }

    /**
     * 사용자 신체 정보 조회
     */
    @Override
    public ExerciseInfoDTO getBodyInfo(String userId) throws Exception {
        log.info("📥 getBodyInfo() 호출 - userId: {}", userId);

        Query query = new Query(Criteria.where("userId").is(userId));
        ExerciseInfoDTO result = mongoTemplate.findOne(query, ExerciseInfoDTO.class, "EXERCISE_INFO");

        if (result != null) {
            log.info("✅ 사용자 신체 정보 조회 성공 - height: {}, weight: {}", result.getHeight(), result.getWeight());
        } else {
            log.warn("⚠️ 해당 사용자의 신체 정보가 존재하지 않음 - userId: {}", userId);
        }

        return result;
    }

    // 운동 한날 안한 날 구분하는 로직
    @Override
    public List<ExercisePlanDTO> getExerciseRecordsByUser(String userId) {
        log.info("📅 [getExerciseRecordsByUser] 운동 루틴 조회 시작 - userId: {}", userId);

        // MongoDB 쿼리 생성: userId 기준으로 검색
        Query query = new Query(Criteria.where("userId").is(userId));
        List<ExercisePlanDTO> result = mongoTemplate.find(query, ExercisePlanDTO.class, "EXERCISE_PLAN");

        log.info("📦 [getExerciseRecordsByUser] 조회 결과: {}건", result.size());
        return result;
    }


    @Override
    public boolean saveTodayExerciseCompletion(String userId) {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        Query query = new Query(Criteria.where("userId").is(userId).and("date").is(today));
        boolean exists = mongoTemplate.exists(query, ExercisePlanDTO.class, "EXERCISE_PLAN");

        if (exists) {
            log.warn("⚠️ 이미 기록 존재: userId={}, date={}", userId, today);
            return false;
        }

        ExercisePlanDTO dto = new ExercisePlanDTO();
        dto.setUserId(userId);
        dto.setDate(today);
        dto.setRoutineTitle("오늘 운동 완료");
        dto.setRoutineDetail(List.of("사용자 확인에 의한 완료"));
        dto.setEstimatedTime("기록 없음");

        mongoTemplate.insert(dto, "EXERCISE_PLAN");
        log.info("✅ 저장 완료: userId={}, date={}", userId, today);
        return true;
    }
}
