package gaya.pe.kr.mosaicsystem.infra.amqp.rabbit_mq.service;

import gaya.pe.kr.mosaicsystem.infra.amqp.rabbit_mq.message.MessageSimilarity;
import gaya.pe.kr.mosaicsystem.infra.amqp.rabbit_mq.message.MessageTotalFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RabbitMqService {

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    /**
     * 1. Queue 로 메세지를 발행
     * 2. Producer 역할 -> Direct Exchange 전략
     **/
    public void sendMessage(String msg) {
        log.info("messagge send: {}", msg);
        this.rabbitTemplate.convertAndSend(exchangeName,routingKey, msg);
    }

    int amount = 0;

    /**
     * 1. Queue 에서 메세지를 구독
     **/
    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveMessage(MessageTotalFrame msg) {
        amount++;
        log.info("(MessageTotalFrame) Received Message : {} ( Amount : {} )", msg, amount);
    }

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveMessage(MessageSimilarity msg) {
        amount++;
        log.info("(MessageSimilarity) Received Message : {} ( Amount : {} )", msg, amount);
    }

}
