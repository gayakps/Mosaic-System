package gaya.pe.kr.mosaicsystem.infra.amqp.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Properties;

public class KafkaDataConsumerFromPythonServer {

    public KafkaDataConsumerFromPythonServer() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String bootstrapServers = "localhost:9092";
                String groupId = "test-group";
                String topicName = "testTopic";

                Properties properties = new Properties();
                properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
                properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
                properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
                properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

                KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

                consumer.subscribe(Collections.singletonList(topicName));

                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(100);
                    for (ConsumerRecord<String, String> record : records) {
                        System.out.printf("Received message: (%s, %s, %d, %d)\n", record.key(), record.value(), record.partition(), record.offset());
                    }
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

}
