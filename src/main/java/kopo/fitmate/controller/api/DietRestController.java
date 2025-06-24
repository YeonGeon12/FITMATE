package kopo.fitmate.controller.api;

import kopo.fitmate.dto.diet.DietInfoDTO;
import kopo.fitmate.service.IDietInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/history/diet")
@RequiredArgsConstructor
public class DietRestController {

    private final IDietInfoService dietInfoService;

    /**
     * 식단 루틴 저장 API
     * 클라이언트에서 DietInfoDTO 전송 시 MongoDB에 저장
     */
    @PostMapping("/save")
    public ResponseEntity<String> saveDiet(@RequestBody DietInfoDTO dto) {
        log.info("✅ 식단 루틴 저장 요청 수신: {}", dto);

        try {
            // 📌 createdAt, updatedAt 자동 설정
            Date now = new Date();
            dto.setCreatedAt(now);
            dto.setUpdatedAt(now);

            dietInfoService.saveDiet(dto);
            log.info("✅ 식단 루틴 저장 완료");
            return ResponseEntity.ok("식단 루틴 저장 성공");
        } catch (Exception e) {
            log.error("❌ 식단 루틴 저장 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("식단 루틴 저장 실패: " + e.getMessage());
        }
    }
}
