package kopo.fitmate.dto.diet;

import lombok.*; // Lombok 라이브러리에서 제공하는 유용한 어노테이션들을 한 번에 import 합니다.
import org.springframework.data.annotation.Id; // MongoDB 문서의 고유 ID를 나타내는 필드임을 지정하기 위해 import 합니다.
import org.springframework.data.mongodb.core.mapping.Document; // 이 클래스가 MongoDB의 어떤 컬렉션에 매핑될지 지정하기 위해 import 합니다.
import org.springframework.format.annotation.DateTimeFormat; // 날짜/시간 형식을 지정하기 위해 import 합니다.

import java.util.Date; // 날짜 및 시간 정보를 다루기 위해 import 합니다.
import java.util.List; // 여러 개의 식단 계획(DietPlanDTO)을 리스트 형태로 다루기 위해 import 합니다.

/*
 * DietInfoDTO 클래스는 식단 정보 데이터를 담는 객체입니다.
 * 이 객체는 MongoDB의 "DIET_INFO"라는 컬렉션에 저장될 문서(Document)의 구조를 정의합니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "DIET_INFO")
public class DietInfoDTO {

    /*
     * @Id: 이 필드가 MongoDB 문서의 고유 식별자(Primary Key)임을 나타냅니다.
     * MongoDB는 자동으로 ObjectId를 생성하며, Spring Data MongoDB가 이를 String 형태로 매핑합니다.
     */
    @Id
    private String id; // MongoDB ObjectId 문자열 형태: 각 식단 정보 문서의 고유 ID입니다.

    private String userId;        // 사용자 ID: 이 식단 정보를 생성한 사용자를 식별합니다.
    private String dietType;      // 식단 유형 (고단백, 균형식 등): 해당 식단의 종류를 나타냅니다.
    private Float calories;       // 총 예상 칼로리: 이 식단 정보의 전체 예상 칼로리입니다.

    // private List<DietPlanDTO> mealPlanList; // 식단 리스트 (요일별 구성): 이 식단 정보에 포함된 세부 식단 계획들을 리스트 형태로 가집니다.
    // (예: 월요일 아침, 점심, 저녁 식단 등)
    private List<DietPlanDTO> mealPlanList;


    /*
     * @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME):
     * 이 필드가 날짜/시간 정보를 담고 있음을 나타내며, ISO 8601 형식(예: 2023-10-26T10:30:00)으로
     * 데이터를 파싱하거나 포맷팅하도록 지시합니다.
     * MongoDB에 날짜 타입으로 저장됩니다.
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createdAt; // 생성일시: 해당 식단 정보가 생성된 시간입니다.

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date updatedAt; // 수정일시: 해당 식단 정보가 마지막으로 수정된 시간입니다.
}