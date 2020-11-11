package com.lumendata.service;

import com.lumendata.model.PayloadMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

//@Service
public class ProducerService {

    private static final Logger LOG = LoggerFactory.getLogger(ProducerService.class);
    private KafkaTemplate kafkaTemplate;
    public ProducerService(KafkaTemplate<String,String> kafkaTemplate){
        this.kafkaTemplate=kafkaTemplate;
    }

    @Async
    public void sendMessage(PayloadMapper payloadMapper) {
        LOG.debug(String.format("$$ -> Producing message --> %s", payloadMapper.getPayload()));
        kafkaTemplate.send(payloadMapper.getTopicName(), payloadMapper.getPayload());
    }

}
