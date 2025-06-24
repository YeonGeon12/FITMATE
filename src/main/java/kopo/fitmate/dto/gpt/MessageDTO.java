package kopo.fitmate.dto.gpt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDTO {

    // 대화 메시지 형식을 정의
    private String role;    // "user" 또는 "assistant"
    private String content; // 대화 내용
}
