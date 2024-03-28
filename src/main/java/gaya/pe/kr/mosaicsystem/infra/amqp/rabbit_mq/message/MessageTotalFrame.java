package gaya.pe.kr.mosaicsystem.infra.amqp.rabbit_mq.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor

public class MessageTotalFrame {

    @JsonProperty("amount")
    String amount;

    @JsonProperty("data")
    String data;

}
