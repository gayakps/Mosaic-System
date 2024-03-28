package gaya.pe.kr.mosaicsystem.infra.amqp.rabbit_mq.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor

public class MessageSimilarity {

    @JsonProperty("frame")
    String frame;
    @JsonProperty("similarity")
    String similarity;
    @JsonProperty("data")
    String data;




}
