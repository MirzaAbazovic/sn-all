package de.bitconex.tmf.appointment.configuration;


import de.bitconex.tmf.appointment.util.MongoOffsetDateTimeReaderUtil;
import de.bitconex.tmf.appointment.util.MongoOffsetDateTimeWriterUtil;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Arrays;


@Configuration
@EnableMongoRepositories(basePackages = "de.bitconex.tmf.appointment.repository")
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Arrays.asList(
                new MongoOffsetDateTimeWriterUtil(),
                new MongoOffsetDateTimeReaderUtil()
        ));
    }
}