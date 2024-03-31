package gaya.pe.kr.mosaicsystem.infra.amqp.rabbit_mq.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import gaya.pe.kr.mosaicsystem.infra.amqp.rabbit_mq.message.top.AbstractMosaicProcessorMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SuccessUploadMessage extends AbstractMosaicProcessorMessage {

    @JsonProperty("frame")
    String frame;

    @JsonProperty("length")
    String length;

    @JsonProperty("msg")
    String msg;



}
