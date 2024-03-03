package de.bitconex.tmf.resource_catalog.configuration;

import de.bitconex.tmf.resource_catalog.model.JobStateType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class EnumConverterConfiguration {

    @Bean(name = "de.bitconex.tmf.resource_catalog.configuration.EnumConverterConfiguration.jobStateTypeConverter")
    Converter<String, JobStateType> jobStateTypeConverter() {
        return new Converter<String, JobStateType>() {
            @Override
            public JobStateType convert(String source) {
                return JobStateType.fromValue(source);
            }
        };
    }

}
