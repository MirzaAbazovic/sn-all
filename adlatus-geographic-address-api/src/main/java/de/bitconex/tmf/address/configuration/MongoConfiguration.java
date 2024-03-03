package de.bitconex.tmf.address.configuration;

import de.bitconex.tmf.address.util.MongoOffsetDateTimeReaderUtil;
import de.bitconex.tmf.address.util.MongoOffsetDateTimeWriterUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Arrays;

@Configuration
public class MongoConfiguration {
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Arrays.asList(
                new MongoOffsetDateTimeWriterUtil(),
                new MongoOffsetDateTimeReaderUtil()
        ));
    }
}