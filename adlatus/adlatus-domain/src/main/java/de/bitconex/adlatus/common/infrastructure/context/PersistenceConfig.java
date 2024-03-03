package de.bitconex.adlatus.common.infrastructure.context;

import de.bitconex.adlatus.wholebuy.provision.persistence.ResourceOrderBaseRepository;
import de.bitconex.adlatus.common.persistence.TmfOrderInboxRepository;
import de.bitconex.adlatus.common.persistence.WitaProductInboxRepository;
import de.bitconex.adlatus.common.persistence.WitaProductOutboxRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;

@Configuration
@EnableTransactionManagement
@EnableMongoRepositories(basePackageClasses = {
    ResourceOrderBaseRepository.class,
    TmfOrderInboxRepository.class,
    WitaProductInboxRepository.class,
    WitaProductOutboxRepository.class
})
public class PersistenceConfig {
    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Arrays.asList(
            new OffsetDateTimeReadConverter(),
            new OffsetDateTimeWriteConverter()
        ));
    }

    static class OffsetDateTimeWriteConverter implements Converter<OffsetDateTime, Date> {
        @Override
        public Date convert(OffsetDateTime source) {
            return source == null ? null : Date.from(source.toInstant().atZone(ZoneOffset.UTC).toInstant());
        }
    }

    static class OffsetDateTimeReadConverter implements Converter<Date, OffsetDateTime> {

        @Override
        public OffsetDateTime convert(Date source) {
            return source == null ? null : source.toInstant().atOffset(ZoneOffset.UTC);
        }
    }
}
