package gaya.pe.kr.mosaicsystem.infra.amqp.rabbit_mq.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import gaya.pe.kr.mosaicsystem.infra.amqp.rabbit_mq.message.top.AbstractMosaicProcessorMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
public class MessageSimilarity extends AbstractMosaicProcessorMessage {

    @JsonProperty("frame")
    String frame;
    @JsonProperty("similarity")
    String similarity;
    @JsonProperty("data")
    String data;

}
