package gaya.pe.kr.mosaicsystem.infra.amqp.rabbit_mq.message.top;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gaya.pe.kr.mosaicsystem.infra.amqp.rabbit_mq.message.MessageSimilarity;
import gaya.pe.kr.mosaicsystem.infra.amqp.rabbit_mq.message.UploadState;
import gaya.pe.kr.mosaicsystem.infra.amqp.rabbit_mq.message.VideoInfoMessage;
import gaya.pe.kr.mosaicsystem.infra.amqp.rabbit_mq.message.type.MessageType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MessageSimilarity.class, name = "SIMILARITY_INFO"),
        @JsonSubTypes.Type(value = UploadState.class, name = "SUCCESS_UPLOAD_MESSAGE"),
        @JsonSubTypes.Type(value = VideoInfoMessage.class, name = "VIDEO_INFO")
})
@NoArgsConstructor
@Getter
@ToString
public abstract class AbstractMosaicProcessorMessage {

    @JsonProperty("date")
    String date;

    @JsonProperty("type")
    MessageType messageType;


}
