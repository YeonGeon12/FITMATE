package kopo.fitmate.dto.gpt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
// GPT 호출 시 model, messages, temperature를 구성하는 본체입니다.
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GPTRequestDTO {

    private String model;

    private List<MessageDTO> messages;

    private double temperature;
}
