package com.lumendata.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

//@Configuration
public class KafkaConfig {

    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String servers;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }
    @Bean
    public KafkaTemplate<String, String> operationKafkaTemplate() {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<String, String>(producerConfigs()));
        kafkaTemplate.setProducerListener(new ProducerListener<String, String>() {

            @Override
            public void onSuccess(ProducerRecord<String, String> record, RecordMetadata recordMetadata) {
                System.out.println("### Callback :: " + recordMetadata.topic() + " ; partition = "
                        + recordMetadata.partition()  +" with offset= " + recordMetadata.offset()
                        + " ; Timestamp : " + recordMetadata.timestamp() + " ; Message Size = " + recordMetadata.serializedValueSize());
            }

            @Override
            public void onError(ProducerRecord<String, String> producerRecord, Exception exception) {
                System.out.println("### Topic = " + producerRecord.topic() + " ; Message = " + producerRecord.value());
                exception.printStackTrace();
            }
        });
        return kafkaTemplate;
    }
}
