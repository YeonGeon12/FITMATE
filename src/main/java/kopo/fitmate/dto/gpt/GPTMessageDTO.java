package kopo.fitmate.dto.gpt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GPTMessageDTO {

    // 응답 메시지 내부 구조입니다. message.role, message.content에 접근합니다.
    private String role;
    private String content;
}
