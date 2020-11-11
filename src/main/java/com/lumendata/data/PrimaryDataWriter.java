package com.lumendata.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumendata.model.Constituent;
import com.lumendata.model.ConstituentRecord;
import com.lumendata.model.PayloadMapper;
import com.lumendata.service.ProducerService;
import org.springframework.util.CollectionUtils;

public class PrimaryDataWriter {

    ProducerService producerService;
    ObjectMapper objectMapper=new ObjectMapper();
    public PrimaryDataWriter(ProducerService producerService){
       this.producerService=producerService;
    }

    public void writeData(Constituent constituent){
        if(!CollectionUtils.isEmpty(constituent.getSource())){
            constituent.getSource().forEach(source -> {
                ConstituentRecord constituentRecord=new ConstituentRecord();
                constituentRecord.setPrimaryData(constituent.getPrimaryData());
                constituentRecord.setAddresses(constituent.getAddresses());
                constituentRecord.setEmails(constituent.getEmails());
                constituentRecord.setAffiliations(constituent.getAffiliations());
                constituentRecord.setNames(constituent.getNames());
                constituentRecord.setIdentifications(constituent.getIdentifications());
                constituentRecord.setSource(source);
                PayloadMapper payloadMapper=new PayloadMapper();
                payloadMapper.setTopicName("");
                try {
                    payloadMapper.setPayload(objectMapper.writeValueAsString(constituentRecord));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
              //  producerService.sendMessage(payloadMapper);
            });
        }
    }
}
