package de.bitconex.tmf.resource_catalog.configuration;

import de.bitconex.tmf.resource_catalog.utility.MongoOffsetDateTimeReader;
import de.bitconex.tmf.resource_catalog.utility.MongoOffsetDateTimeWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Arrays;

@Configuration
public class MongoConfiguration {
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        // Add converters to support saving and reading OffsetDateTime
        return new MongoCustomConversions(Arrays.asList(
                new MongoOffsetDateTimeWriter(),
                new MongoOffsetDateTimeReader()
        ));
    }
}
