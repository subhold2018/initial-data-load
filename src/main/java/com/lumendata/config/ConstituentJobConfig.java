package com.lumendata.config;

import com.lumendata.data.*;
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
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Value("${sql.email-data}")
    private String emailDataSql;

    @Value("${sql.phone-data}")
    private String phoneDataSql;

    @Value("${sql.address-data}")
    private String addressDataSql;

    @Value("${sql.nid-data}")
    private String identityDataSql;

    @Value("${sql.affiliation-data}")
    private String affiliationDataSql;

    @Value("${sql.name-data}")
    private String nameDataSql;


    @Autowired
    @Qualifier(value = "dataSource")
    DataSource dataSource;
    AtomicInteger count=new AtomicInteger();

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
        return stepBuilderFactory.get("primaryDataProcessingStep").<PartyUidData, Constituent> chunk(5)
                .reader(getReader()).processor(processData())
                .writer(writeData()).build();
    }

    public ItemWriter<? super Constituent> writeData() {
        return new ItemWriter<Constituent>() {
            @Override
            public void write(List<? extends Constituent> items) throws Exception {
                items.forEach(constituent->{
                   Map<String,List<String>> partyUids= primaryDataWriter()
                           .writeData(constituent);
                   csvDataWriter().writeToCsvFile(partyUids,true);
                   log.info("Record-count"+(count));
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
                constituent.setGuid(item.getGuidId());
                constituent.setPrimaryData(primaryDataProcessor().readPrimaryData(item));
                constituent.setSource(sourceDataProcessor().readSourceData(item));
                constituent.setEmails(emailDataProcessor().readEmailData(item));
                constituent.setPhones(phoneDataProcessor().readPhoneData(item));
                constituent.setAddresses(addressDataProcessor().readAddressData(item));
                constituent.setIdentifications(identityDataProcessor().reaIdentityData(item));
                constituent.setAffiliations(affiliationDataProcessor().readAffiliationData(item));
                constituent.setNames(nameDataProcessor().readNameData(item));
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
        return new PrimaryDataProcessor(primaryDataSql,connection());
    }
    @Bean
    public SourceDataProcessor sourceDataProcessor(){
        return new SourceDataProcessor(sourceDataSql,dataSource);
    }

    @Bean
    public Connection connection(){
        try {
            return dataSource.getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Bean
    public EmailDataProcessor emailDataProcessor(){
        return new EmailDataProcessor(emailDataSql,dataSource);
    }
    @Bean
    public PhoneDataProcessor phoneDataProcessor(){
        return new PhoneDataProcessor(phoneDataSql,dataSource);
    }
    @Bean
    public PrimaryDataWriter primaryDataWriter(){
        return new PrimaryDataWriter(null);
    }
    @Bean
    public AddressDataProcessor addressDataProcessor(){
        return new AddressDataProcessor(addressDataSql,dataSource);
    }

    @Bean
    public IdentityDataProcessor identityDataProcessor(){
        return new IdentityDataProcessor(identityDataSql,dataSource);
    }

    @Bean
    public CsvDataWriter csvDataWriter(){
        try {
            return new CsvDataWriter("F:\\lumen-data-repo\\initial-data-load\\guids.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    public AffiliationDataProcessor affiliationDataProcessor(){
        return new AffiliationDataProcessor(affiliationDataSql,dataSource);
    }

    @Bean
    public NameDataProcessor nameDataProcessor(){
        return new NameDataProcessor(nameDataSql,dataSource);
    }

  /*  @Bean
    public ProducerService producerService() {
        return new ProducerService(kafkaTemplate);
    }*/

}
