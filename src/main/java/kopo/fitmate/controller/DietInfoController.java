package kopo.fitmate.controller;

import kopo.fitmate.dto.diet.DietInfoDTO;
import kopo.fitmate.service.IDietInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diet")
@RequiredArgsConstructor
public class DietInfoController {

    private final IDietInfoService dietInfoService;

    // ✅ 사용자 ID 기준 식단 목록 조회
    @GetMapping("/list/{userId}")
    public List<DietInfoDTO> getDietsByUser(@PathVariable String userId) {
        return dietInfoService.getDietsByUserId(userId);
    }

    // ✅ 식단 상세 조회
    @GetMapping("/{id}")
    public DietInfoDTO getDietById(@PathVariable String id) {
        return dietInfoService.getDietById(id);
    }

    // ✅ 식단 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDiet(@PathVariable String id) {
        dietInfoService.deleteDietById(id);
        return ResponseEntity.ok("삭제 완료");
    }
}
