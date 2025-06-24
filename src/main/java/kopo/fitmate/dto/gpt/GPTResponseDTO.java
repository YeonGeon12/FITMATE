package kopo.fitmate.dto.gpt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // ✅ 정의되지 않은 필드 무시

public class GPTResponseDTO {

    // GPT 응답 본체 choices 배열만 필요하다면 이 구조로 충분
    private List<ChoiceDTO> choices;
}
