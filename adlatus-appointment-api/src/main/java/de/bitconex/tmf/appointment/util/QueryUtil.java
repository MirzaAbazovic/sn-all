package de.bitconex.tmf.appointment.util;

import org.springframework.data.mongodb.core.query.Query;

public class QueryUtil {
    public static <T> Query createQueryWithIncludedFields(String fields, Class<T> entityClass) {
        Query query = new Query();

        if (fields != null && !fields.isEmpty()) {
            String[] includedFields = fields.split(",");
            for (String field : includedFields) {
                query.fields().include(field.trim());
            }
        }

        return query;
    }
}
