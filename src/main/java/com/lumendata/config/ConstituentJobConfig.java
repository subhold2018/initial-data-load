package com.lumendata.config;

import com.lumendata.data.PartyIdMapper;
import com.lumendata.data.PrimaryDataProcessor;
import com.lumendata.data.PrimaryDataWriter;
import com.lumendata.data.SourceDataProcessor;
import com.lumendata.listeners.JobCompletionNotificationListener;
import com.lumendata.model.Constituent;
import com.lumendata.model.PartyUidData;
import com.lumendata.model.PrimaryData;
import com.lumendata.service.ProducerService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

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

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${sql.read-partyUid}")
    private String partyUidSql;

    @Value("${sql.primary-data}")
    private String primaryDataSql;

    @Value("${sql.source-data}")
    private String sourceDataSql;

    @Autowired
    @Qualifier(value = "dataSource")
    DataSource dataSource;

    @Bean
    public Job constituentRecordProcessorJob(
            Step primaryDataProcessingStep,
            JobCompletionNotificationListener jobCompletionListener)
    {
        return jobBuilderFactory.get(JOB_NAME)
                .listener(jobCompletionListener)
                .start(primaryDataProcessingStep)
                .build();
    }

    @Bean
    public Step primaryDataProcessingStep() {
        return stepBuilderFactory.get("primaryDataProcessingStep").<PartyUidData, Constituent> chunk(1)
                .reader(getReader()).processor(processData())
                .writer(writeData()).build();
    }

    public ItemWriter<? super Constituent> writeData() {
        return new ItemWriter<Constituent>() {
            @Override
            public void write(List<? extends Constituent> items) throws Exception {
                items.forEach(constituent->{
                    primaryDataWriter().writeData(constituent);
                });
            }
        };
    }

    @Bean
    public ItemProcessor<PartyUidData, Constituent> processData() {
        return new ItemProcessor<PartyUidData, Constituent>() {
            @Override
            public Constituent process(PartyUidData item) throws Exception {
                log.info("process-PartyId={}",item.getGuidId());
                Constituent constituent=new Constituent();
                constituent.setPrimaryData(primaryDataProcessor().readPrimaryData(item));
                constituent.setSource(sourceDataProcessor().readSourceData(item));
                return constituent;
            }
        };
    }

    @Bean
    public ItemReader<? extends PartyUidData> getReader() {
        JdbcCursorItemReader<PartyUidData> reader = new JdbcCursorItemReader<PartyUidData>();
        reader.setDataSource(dataSource);
        reader.setSql(partyUidSql);
        reader.setRowMapper(new PartyIdMapper());
        return reader;
    }
    @Bean
    public PrimaryDataProcessor primaryDataProcessor(){
        return new PrimaryDataProcessor(primaryDataSql,dataSource);
    }
    @Bean
    public SourceDataProcessor sourceDataProcessor(){
        return new SourceDataProcessor(sourceDataSql,dataSource);
    }
    @Bean
    public PrimaryDataWriter primaryDataWriter(){
        return new PrimaryDataWriter(producerService());
    }

    @Bean
    public ProducerService producerService() {
        return new ProducerService(kafkaTemplate);
    }

}
