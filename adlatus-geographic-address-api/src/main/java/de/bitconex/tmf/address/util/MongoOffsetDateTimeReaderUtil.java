package de.bitconex.tmf.address.util;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class MongoOffsetDateTimeReaderUtil implements Converter<Document, OffsetDateTime> {

    @Override
    public OffsetDateTime convert(final Document document) {
        final Date dateTime = document.getDate(MongoOffsetDateTimeWriterUtil.DATE_FIELD);
        final ZoneOffset offset = ZoneOffset.of(document.getString(MongoOffsetDateTimeWriterUtil.OFFSET_FIELD));
        return OffsetDateTime.ofInstant(dateTime.toInstant(), offset);
    }
}