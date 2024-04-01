package gaya.pe.kr.mosaicsystem.infra.amqp.rabbit_mq.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import gaya.pe.kr.mosaicsystem.infra.amqp.rabbit_mq.message.top.AbstractMosaicProcessorMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class UploadState extends AbstractMosaicProcessorMessage {

    @JsonProperty("frame")
    int nowFrame;

    @JsonProperty("length")
    int elapseTimeSec;

    @JsonProperty("total_frame")
    int totalFrames;

    @JsonProperty("status")
    boolean status;

    @JsonProperty("msg")
    String msg;



}
