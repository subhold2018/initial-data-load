package com.lumendata.config;

import com.lumendata.data.PartyIdMapper;
import com.lumendata.listeners.JobCompletionNotificationListener;
import com.lumendata.model.PartyUidData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@Slf4j
public class ConstituentJobConfig {
    private static final String JOB_NAME="constituentRecordProcessorJob";

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    private static final String PARTY_UID="SELECT PARTY_UID as guidId from SIEBEL.S_PARTY where party_uid <>row_id and party_type_cd ='Person'";

    @Autowired
    @Qualifier(value = "dataSource")
    DataSource dataSource;

    @Bean
    public Job constituentRecordProcessorJob(
            Step preProcessingStep,
            JobCompletionNotificationListener jobCompletionListener)
    {
        return jobBuilderFactory.get(JOB_NAME)
                .listener(jobCompletionListener)
                .start(preProcessingStep)
                .build();
    }

    @Bean
    public Step preProcessingStep() {
        return stepBuilderFactory.get("preProcessingStep").<PartyUidData,PartyUidData> chunk(100)
                .reader(getReader()).processor(processData())
                .writer(writeData()).build();
    }

    public ItemWriter<? super PartyUidData> writeData() {
        return new ItemWriter<PartyUidData>() {
            @Override
            public void write(List<? extends PartyUidData> items) throws Exception {
                items.forEach(partyId->{
                    log.info("writer-PartyId={}",partyId.getGuidId());
                });

            }
        };
    }

    @Bean
    public ItemProcessor<? super PartyUidData,? extends PartyUidData> processData() {
        return new ItemProcessor<PartyUidData, PartyUidData>() {
            @Override
            public PartyUidData process(PartyUidData item) throws Exception {
                log.info("process-PartyId={}",item.getGuidId());
                return item;
            }
        };
    }

    @Bean
    public ItemReader<? extends PartyUidData> getReader() {
        JdbcCursorItemReader<PartyUidData> reader = new JdbcCursorItemReader<PartyUidData>();
        reader.setDataSource(dataSource);
        reader.setSql(PARTY_UID);
        reader.setRowMapper(new PartyIdMapper());
        return reader;
    }
}
