package com.lumendata.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumendata.model.Constituent;
import com.lumendata.model.ConstituentRecord;
import com.lumendata.model.InputRecord;
import com.lumendata.model.PayloadMapper;
import com.lumendata.service.ProducerService;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class PrimaryDataWriter {

    ProducerService producerService;
    ObjectMapper objectMapper=new ObjectMapper();
    public PrimaryDataWriter(ProducerService producerService){
       this.producerService=producerService;
    }

    public Map<String, List<String>> writeData(ConstituentRecord constituentRecord){
        Map<String, List<String>> guids=new HashMap<>();
        String parentGuid=constituentRecord.getGuid();
        if(!CollectionUtils.isEmpty(constituentRecord.getSource())){
            InputRecord inputRecord=new InputRecord();
            List<Constituent> constituents=new ArrayList<>( constituentRecord.getSource().size());
            constituentRecord.getSource().forEach(source -> {
                Constituent constituent=new Constituent();
                if(null==constituentRecord.getGuid()){
                    constituent.setGuid(UUID.randomUUID().toString());
                    guids.get(parentGuid).add(constituent.getGuid());
                }else{
                    constituent.setGuid(constituentRecord.getGuid());
                    guids.put(constituentRecord.getGuid(),new ArrayList<>());
                    constituentRecord.setGuid(null);
                }
                constituent.setPrimaryData(constituentRecord.getPrimaryData());
                constituent.setAddresses(constituentRecord.getAddresses());
                constituent.setEmails(constituentRecord.getEmails());
                constituent.setAffiliations(constituentRecord.getAffiliations());
                constituent.setNames(constituentRecord.getNames());
                constituent.setIdentifications(constituentRecord.getIdentifications());
                constituent.setSource(source);
                constituents.add(constituent);
            });
            inputRecord.setCustomers(constituents);
            inputRecord.setGuids(guids);
            PayloadMapper payloadMapper=new PayloadMapper();
            payloadMapper.setTopicName("address-verification-topic");
            try {
                payloadMapper.setPayload(objectMapper.writeValueAsString(inputRecord));
                producerService.sendMessage(payloadMapper);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return guids;
    }
}
