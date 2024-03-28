package gaya.pe.kr.mosaicsystem.infra.amqp.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Service;

import java.util.Properties;

public class KafkaDataProducerToLogServer {

    public KafkaDataProducerToLogServer() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String bootstrapServers = "localhost:9092";
                String topicName = "testTopic";

                Properties properties = new Properties();
                properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
                properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
                properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

                KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

                ProducerRecord<String, String> record = new ProducerRecord<>(topicName, "Hello, Kafka!");
                producer.send(record);
                producer.close();
            }
        };

        Thread thread = new Thread(runnable);

        thread.start();

    }

}
