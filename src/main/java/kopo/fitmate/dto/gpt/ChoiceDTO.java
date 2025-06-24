package kopo.fitmate.dto.gpt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChoiceDTO {

    // GPT 응답에서 각 choice 항목을 파싱하는 DTO
    private GPTMessageDTO message;
}
