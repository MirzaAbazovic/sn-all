/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.08.2015
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import java.time.*;
import java.time.format.*;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

//import java.util.*;
//import org.joda.time.DateTime;
//import org.joda.time.LocalDate;
//import org.testng.Assert;

/**
 * Created by rubezhanskyymy on 10.08.2015.
 */
public class DummyTest {

    private static final Logger LOGGER = Logger.getLogger(DummyTest.class);

    //    @Test
    //    public void testLocalDate() throws Exception {
    //        final Date nullDate = (Date) null;
    //        final LocalDate localDateJoda = new LocalDate(nullDate);
    //        Assert.assertNotNull(localDateJoda);
    //
    //        //final java.time.LocalDate localDate = java.time.LocalDate.from(nullDate.toInstant());
    //        final Date workaround = Optional.ofNullable(nullDate).orElse(new Date());
    //        Assert.assertNotNull(workaround);
    //
    //        final java.time.LocalDate localDate = workaround.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    //        Assert.assertNotNull(localDate);
    //
    //        Assert.assertEquals(localDate.toString(), localDateJoda.toString());
    //    }
    //
    //    @Test(expectedExceptions = IllegalArgumentException.class)
    //    public void testLocalDate_FromDateFields() throws Exception {
    //        final Date nullDate = (Date) null;
    //        final LocalDate localDateJoda = LocalDate.fromDateFields(nullDate);
    //        Assert.assertNotNull(localDateJoda);
    //    }
    //
    //    @Test
    //    public void testDateTime() throws Exception {
    //        final Date nullDate = (Date) null;
    //        final DateTime dateTimeJoda = new DateTime(nullDate);
    //        Assert.assertNotNull(dateTimeJoda);
    //
    //        //final java.time.LocalDate localDate = java.time.LocalDate.from(nullDate.toInstant());
    //        final Date workaround = Optional.ofNullable(nullDate).orElse(new Date());
    //        Assert.assertNotNull(workaround);
    //
    //        final LocalDateTime LocalDateTime = LocalDateTime.from(workaround.toInstant().atZone(ZoneId.systemDefault()));
    //        Assert.assertNotNull(LocalDateTime);
    //
    //        Assert.assertEquals(LocalDateTime.getDayOfYear(), dateTimeJoda.getDayOfYear());
    //    }

    @Test
    public void testOffsetDateTime() throws Exception {
        LOGGER.info("OffsetDate -> " + OffsetDateTime.now().toString());
        LOGGER.info("ZonedDateTime -> " + ZonedDateTime.now().toString());
        LOGGER.info("ZonedDateTime from offset -> " + ZonedDateTime.from(OffsetDateTime.now()));
        LOGGER.info("ZonedDateTime from offset -> " + ZonedDateTime.now().withFixedOffsetZone());

        LOGGER.info("ZonedDateTime pattern check -->" + ZonedDateTime.now().withFixedOffsetZone().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXXXX")));
        LOGGER.info("ZonedDateTime pattern check -->" + ZonedDateTime.now().withFixedOffsetZone().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ")));

    }
}