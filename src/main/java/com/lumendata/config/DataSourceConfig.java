package com.lumendata.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSourceH2(){
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        builder.setType(EmbeddedDatabaseType.H2);
        return builder.build();
    }

    @Bean
    @Qualifier(value = "dataSource")
    public DataSource dataSource(){
        final  DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(oracle.jdbc.driver.OracleDriver.class.getName());
        dataSource.setUrl("jdbc:oracle:thin:@qrac-scan.qa.cu.edu:6800/mdmstg");
        dataSource.setUsername("rajavarapus");
        dataSource.setPassword("Srini_10293847");
        return dataSource;
    }
}
