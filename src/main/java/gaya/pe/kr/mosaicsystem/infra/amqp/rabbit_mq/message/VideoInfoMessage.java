package gaya.pe.kr.mosaicsystem.infra.amqp.rabbit_mq.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import gaya.pe.kr.mosaicsystem.infra.amqp.rabbit_mq.message.top.AbstractMosaicProcessorMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Getter
public class VideoInfoMessage extends AbstractMosaicProcessorMessage {

    @JsonProperty("data")
    String data;

}
