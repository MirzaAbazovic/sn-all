package de.bitconex.tmf.resource_catalog.utility;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;

import java.time.OffsetDateTime;
import java.util.Date;

// Since MongoDB does not support OffsetDateTime natively, we need a workaround by converting it to two fields, dateTime and offset.
public class MongoOffsetDateTimeWriter implements Converter<OffsetDateTime, Document> {

    public static final String DATE_FIELD = "dateTime";
    public static final String OFFSET_FIELD = "offset";

    @Override
    public Document convert(final OffsetDateTime offsetDateTime) {
        final Document document = new Document();
        document.put(DATE_FIELD, Date.from(offsetDateTime.toInstant()));
        document.put(OFFSET_FIELD, offsetDateTime.getOffset().toString());
        return document;
    }

}
