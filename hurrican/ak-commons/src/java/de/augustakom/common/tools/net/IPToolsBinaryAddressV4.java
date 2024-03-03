/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.10.2011 14:27:50
 */
package de.augustakom.common.tools.net;

/**
 * Binaere Repraesentation einer IP-Adresse des Typs v4.
 */
public class IPToolsBinaryAddressV4 extends IPToolsBinaryAddress {

    private static final int SEGMENT_RADIX = 10;
    private static final int SEGMENT_SIZE = 8;
    private static final int SEGMENT_COUNT = 4;
    static final int MAX_BITS = 32;

    IPToolsBinaryAddressV4() {
        super();
    }

    public static IPToolsBinaryAddressV4 create() {
        return new IPToolsBinaryAddressV4();
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

    @Override
    String getStringRepresentationOfSegment(int segmentNumber) {
        return Integer.toString(segmentNumber);
    }


}
