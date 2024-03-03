package de.bitconex.tmf.party.configuration;

import de.bitconex.tmf.party.utility.MongoOffsetDateTimeReader;
import de.bitconex.tmf.party.utility.MongoOffsetDateTimeWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Arrays;

@Configuration
public class MongoConfig {
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        // Add converters to support saving and reading OffsetDateTime
        return new MongoCustomConversions(Arrays.asList(
                new MongoOffsetDateTimeWriter(),
                new MongoOffsetDateTimeReader()
        ));
    }
}
