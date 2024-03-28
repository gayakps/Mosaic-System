package gaya.pe.kr.mosaicsystem.infra.amqp.rabbit_mq.config;

import gaya.pe.kr.mosaicsystem.infra.factory.YamlPropertySourceFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "network")
@Getter
@Setter
@ToString
@PropertySource(value = "classpath:amqp/application-amqp-rabbitmq.yml", factory = YamlPropertySourceFactory.class)
public class RabbitMqProperties {
    private String host;
    private int port;
    private String username;
    private String password;
}
