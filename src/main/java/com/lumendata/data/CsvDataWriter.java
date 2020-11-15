package com.lumendata.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumendata.model.Constituent;
import com.lumendata.model.ConstituentRecord;
import com.lumendata.model.PayloadMapper;
import com.lumendata.service.ProducerService;
import com.opencsv.CSVWriter;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CsvDataWriter {

    private CSVWriter csvWriter;
    public CsvDataWriter(String fileName) throws IOException {
        FileWriter outputFile = new FileWriter(new File(fileName));
        csvWriter=new CSVWriter(outputFile);
        String[] header = { "partyUid", "generatedIds"};
        csvWriter.writeNext(header);
    }

    public void writeToCsvFile(Map<String, List<String>> partyUids,boolean flush){
        partyUids.forEach((partyUid,generatedIds)->{
            String[] data=new String[2];
            data[0]=partyUid;
            if(null!=generatedIds) {
                data[1] = generatedIds.toString();
            }
            csvWriter.writeNext(data);
        });
        try {
            if(flush) {
                csvWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
