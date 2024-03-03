package de.bitconex.tmf.resource_catalog.utility;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

// Since MongoDB does not support OffsetDateTime natively, we need a workaround by converting it to two fields, dateTime and offset.
public class MongoOffsetDateTimeReader implements Converter<Document, OffsetDateTime> {

    @Override
    public OffsetDateTime convert(final Document document) {
        final Date dateTime = document.getDate(MongoOffsetDateTimeWriter.DATE_FIELD);
        final ZoneOffset offset = ZoneOffset.of(document.getString(MongoOffsetDateTimeWriter.OFFSET_FIELD));
        return OffsetDateTime.ofInstant(dateTime.toInstant(), offset);
    }

}