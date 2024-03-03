/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.12.2014
 */
package de.augustakom.hurrican.model.billing.factory.om;

import java.time.*;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.mnet.common.tools.DateConverterUtils;


public class RufnummerObjectMother extends AbstractTaifunObjectMother {
    private static final Logger LOGGER = Logger.getLogger(RufnummerObjectMother.class);

    private TNB actCarrier = TNB.MNET;
    private String onkz;

    public RufnummerBuilder blockDn(Long rangeFrom, Long size) {
        String rangeTo = String.format("%s", rangeFrom + size - 1);
        String rangeFromAsString = StringTools.fillToSize(rangeFrom.toString(), rangeTo.length(), '0', true);
        final String onKz = StringUtils.isNotEmpty(this.onkz) ? this.onkz : getRandomOnKz();
        String dnBase = getDnBase(onKz, 12 - rangeTo.length());
        return createDefault()
                .withOnKz(onKz)
                .withDnBase(dnBase)
                .withDnSize(size)
                .withDirectDial("0")
                .withRangeFrom(rangeFromAsString)
                .withRangeTo(rangeTo);
    }

    public RufnummerBuilder blockDn(String onkz, String dnBase, Long rangeFrom, Long size) {
        RufnummerBuilder builder = blockDn(rangeFrom, size);
        if (onkz != null) {
            builder.withOnKz(onkz);
        }
        if (dnBase != null) {
            builder.withDnBase(dnBase);
        }
        return builder;
    }

    public List<RufnummerBuilder> blockDns(String onkz, String dnBase, Long... sizes) {
        assert sizes != null && sizes.length > 0;
        List<RufnummerBuilder> builders = new ArrayList<>(sizes.length);
        if (sizes.length > 1 && onkz == null) {
            onkz = StringUtils.isNotEmpty(this.onkz) ? this.onkz : getRandomOnKz();
        }
        if (sizes.length > 1 && dnBase == null) {
            dnBase = getDnBase(onkz, 9);
        }
        Long rangeFrom = 0L;
        for (Long size : sizes) {
            builders.add(blockDn(onkz, dnBase, rangeFrom, size));
            rangeFrom += size;
        }
        return builders;
    }

    public List<RufnummerBuilder> singleDns(int dnCount) {
        List<RufnummerBuilder> builders = new ArrayList<>();
        for (int i=0; i< dnCount; i++) {
            builders.add(createDefault());
        }
        return builders;
    }

    public RufnummerBuilder createDefault() {
        final String onKz = StringUtils.isNotEmpty(this.onkz) ? this.onkz : getRandomOnKz();
        final String dnBase = getDnBase(onKz, 12);
        final RufnummerBuilder builder = new RufnummerBuilder()
                .setPersist(false)
                .withOnKz(onKz)
                .withDnBase(dnBase)
                .withNonBillable(Boolean.FALSE)
                .withRealDate(DateConverterUtils.asDate(LocalDateTime.now().minusDays(14)))
                .withActCarrier(actCarrier.carrierName, actCarrier.tnbKennung)
                .withLastCarrier("DTAG", "D001");

        return builder;
    }

    public RufnummerObjectMother withActCarrier(TNB actCarrier) {
        if (actCarrier != null) {
            this.actCarrier = actCarrier;
        }
        return this;
    }

    private static String getDnBase(String onKz, int maxLength) {
        return StringUtils.substring(String.format("%s", randomInt(900, 999999999)), 0, maxLength - onKz.length());
    }

    private static String getRandomOnKz() {
        return String.format("0%s%s", randomInt(2,5), randomInt(0, 999));
    }

    public RufnummerObjectMother withOnkz(String onkz) {
        this.onkz = onkz;
        return this;
    }

}
