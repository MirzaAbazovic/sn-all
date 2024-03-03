/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.10.2011 15:55:31
 */
package de.augustakom.common.tools.net;

/**
 * Binaere Repraesentation einer IP-Adresse des Typs v6.
 */
public class IPToolsBinaryAddressV6 extends IPToolsBinaryAddress {

    private static final int SEGMENT_RADIX = 16;
    private static final int SEGMENT_SIZE = 16;
    private static final int SEGMENT_COUNT = 8;
    static final int MAX_BITS = 128;

    IPToolsBinaryAddressV6() {
        super();
    }

    public static IPToolsBinaryAddressV6 create() {
        return new IPToolsBinaryAddressV6();
    }

    @Override
    public int getMaximumBits() {
        return MAX_BITS;
    }

    @Override
    int getSegmentSize() {
        return SEGMENT_SIZE;
    }

    @Override
    int getSegmentCount() {
        return SEGMENT_COUNT;
    }

    @Override
    int getSegmentRadix() {
        return SEGMENT_RADIX;
    }

    private String normalizeIPV6Segment(int segmentValue) {
        if (segmentValue == 0) {
            return ZERO_SEGMENT;
        }
        String normalizedSegment = Integer.toHexString(segmentValue);
        return normalizedSegment;
    }

    @Override
    String getStringRepresentationOfSegment(int segmentNumber) {
        return normalizeIPV6Segment(segmentNumber);
    }

}
