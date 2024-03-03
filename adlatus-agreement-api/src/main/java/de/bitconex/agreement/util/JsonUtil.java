package de.bitconex.agreement.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.Locale;
import java.util.TimeZone;

@UtilityClass
public class JsonUtil {

    public static ObjectMapper createObjectMapper() {
        return Jackson2ObjectMapperBuilder.json()
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .featuresToEnable(
                SerializationFeature.INDENT_OUTPUT
            )
            .featuresToDisable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS
            )
            .dateFormat(new StdDateFormat())
            .locale(Locale.getDefault())
            .timeZone(TimeZone.getDefault())
            .build().registerModule(new JavaTimeModule());
    }
}
