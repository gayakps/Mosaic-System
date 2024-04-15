package gaya.pe.kr.mosaicsystem.infra.amqp.rabbit_mq.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import gaya.pe.kr.mosaicsystem.infra.amqp.rabbit_mq.message.top.AbstractMosaicProcessorMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.net.URL;

@ToString
@NoArgsConstructor
@Getter
public class ResultFileURL extends AbstractMosaicProcessorMessage {

    @JsonProperty("url")
    URL resultFileURL;

}
