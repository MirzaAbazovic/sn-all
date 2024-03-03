/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.2011 14:54:47
 */
package de.augustakom.common.tools.net;

import de.augustakom.common.tools.lang.StringTools;

/**
 * Eine IP Adresse in seiner binären Repräsentation
 *
 * @since Release 10
 */
public abstract class IPToolsBinaryAddress {

    public static final String ZERO_SEGMENT = "0";
    public static final char LEADING_ZERO = '0';
    public static final byte invalid = -1;
    public static final char cOff = '0';
    public static final char cOn = '1';
    public static final byte off = 0;
    public static final byte on = 1;

    private byte[] bitArray; // least significant bit == index 0; most significant bit == max index

    /**
     * liefert die Anzahl der maximal setzbaren Bits fuer eine IP-Adresse. Die konkrete Zahl muss implementiert werden.
     *
     * @return
     */
    public abstract int getMaximumBits();

    /**
     * liefert die Groesse der Segmente fuer eine IP-Adresse.
     *
     * @return
     */
    abstract int getSegmentSize();

    /**
     * liefert die Anzahl an Segmenten fuer eine IP-Adresse.
     *
     * @return
     */
    abstract int getSegmentCount();

    /**
     * liefert das Zahlensystem auf welchem die Segmente der IP-Adresse basieren. Bsp: IPv4 = 10, IPv6 = 16
     *
     * @return
     */
    abstract int getSegmentRadix();

    /**
     * liefert einen String aus der Zahlenrepraesentation eines Segments. Je nach Implementierung faellt dieser anders
     * aus.
     *
     * @param segmentNumber
     * @return
     */
    abstract String getStringRepresentationOfSegment(int segmentNumber);

    /**
     * @return Returns the bitArray.
     */
    protected byte[] getBitArray() {
        if (bitArray == null) {
            bitArray = new byte[getMaximumBits()];
        }
        return bitArray;
    }

    protected IPToolsBinaryAddress() {
        for (int i = 0; i < getBitArray().length; i++) {
            bitArray[i] = off;
        }
    }

    protected IPToolsBinaryAddress(IPToolsBinaryAddress source) {
        bitArray = new byte[source.bitArray.length];
        System.arraycopy(source.bitArray, 0, bitArray, 0, bitArray.length);
    }

    private boolean isIndexInBounds(int index) {
        if ((index < 0) || (index >= bitArray.length)) {return true;}
        return false;
    }

    public int getArrayLength() {
        return getBitArray().length;
    }

    public byte get(int index) {
        if (isIndexInBounds(index)) {return invalid;}
        return bitArray[index];
    }

    public void set(int index, byte bitValue) {
        if (!isIndexInBounds(index) || ((bitValue != on) && (bitValue != off))) {
            throw new IllegalArgumentException("Der Index oder der Bitwert sind ungültig!");
        }
        bitArray[index] = bitValue;
    }

    public void orOperation(IPToolsBinaryAddress operand) {
        if (bitArray.length != operand.bitArray.length) {
            throw new IllegalArgumentException("Die Länge der Bit Arrays stimmen nicht überein!");
        }
        for (int i = 0; i < bitArray.length; i++) {
            if ((bitArray[i] == on) || (operand.bitArray[i] == on)) {
                bitArray[i] = on;
            }
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof IPToolsBinaryAddress) {
            IPToolsBinaryAddress operand = (IPToolsBinaryAddress) object;
            if (bitArray.length != operand.bitArray.length) {return false;}
            for (int i = 0; i < bitArray.length; i++) {
                if ((bitArray[i] != operand.bitArray[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    boolean equalTo(IPToolsBinaryAddress operand, int indexFrom, int indexTo) {
        if ((bitArray.length != operand.bitArray.length) || (indexFrom > indexTo)
                || (indexTo >= bitArray.length)) {
            throw new IllegalArgumentException("Die Indizes oder die Länge der Bit Arrays stimmen nicht überein!");
        }
        for (int i = indexFrom; i <= indexTo; i++) {
            if ((bitArray[i] != operand.bitArray[i])) {
                return false;
            }
        }
        return true;
    }

    public boolean prefixEquals(IPToolsBinaryAddress operand, int prefixLengthOfPrefix) {
        if (!checkValidPrefixLength(prefixLengthOfPrefix)) {
            throw new IllegalArgumentException("Die Präfix Länge ist fehlerhaft!");
        }
        int indexTo = prefixLengthOfPrefix - 1;
        int indexFrom = 0;
        return equalTo(operand, indexFrom, indexTo);
    }

    public boolean checkPrefixBitsNotSet(int prefixLengthOfPrefix) {
        if (!checkValidPrefixLength(prefixLengthOfPrefix)) {
            throw new IllegalArgumentException("Die Präfix Länge ist fehlerhaft!");
        }
        int indexTo = prefixLengthOfPrefix - 1;
        int indexFrom = 0;
        for (int i = indexFrom; i <= indexTo; i++) {
            if ((bitArray[i] != off)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Implementierung wie FindBugs Vorschlag. Falls diese Klasse in eine HashMap/HashTable eingefügt werden soll, muss
     * die Implementierung überarbeitet werden!
     */
    @Override
    public int hashCode() {
        assert false : "hashCode not designed";
        return 42; // any arbitrary constant will do
    }


    protected boolean checkValidPrefixLength(int prefixLength) {
        boolean result = true;
        if ((prefixLength < 0) || (prefixLength > getMaximumBits())) {
            result = false;
        }
        return result;
    }

    public void setPrefixLength(int prefixLength) {
        int index = 0;
        for (; prefixLength > 0; prefixLength--, index++) {
            bitArray[index] = on;
        }
    }

    protected int[] getSegments() {
        int index = 0;
        int[] segments = new int[getSegmentCount()];
        for (int segmentIndex = 0; segmentIndex < getSegmentCount(); segmentIndex++) {
            for (int segmentBitIndex = 0; segmentBitIndex < getSegmentSize(); segmentBitIndex++, index++) {
                segments[segmentIndex] = segments[segmentIndex] << 1;
                segments[segmentIndex] |= (bitArray[index] == on) ? 1 : 0;
            }
        }
        return segments;
    }

    public String[] getSegmentsAsString() {
        int[] segments = getSegments();
        String[] segmentsAsString = new String[segments.length];
        for (int i = 0; i < segments.length; i++) {
            segmentsAsString[i] = getStringRepresentationOfSegment(segments[i]);
        }
        return segmentsAsString;
    }

    public void setSegments(String[] segments) {
        int segmentSize = getSegmentSize();
        int segmentRadix = getSegmentRadix();
        int segmentCount = getSegmentCount();
        if ((segments == null) || (segments.length != segmentCount)) {
            throw new IllegalArgumentException("Die Anzahl der Segmente ist nicht korrekt!");
        }
        int index = 0;
        for (int segmentIndex = 0; segmentIndex < segmentCount; segmentIndex++) {
            Integer segmentAsInteger = Integer.valueOf(segments[segmentIndex], segmentRadix);
            String segmentAsString = Integer.toBinaryString(segmentAsInteger.intValue());
            segmentAsString = StringTools.fillToSize(segmentAsString, segmentSize, LEADING_ZERO, true);
            for (int segmentBitIndex = 0; segmentBitIndex < segmentSize; segmentBitIndex++, index++) {
                char bit = cOff;
                if (segmentAsString.length() > segmentBitIndex) {
                    bit = segmentAsString.charAt(segmentBitIndex);
                }
                bitArray[index] = (bit == cOn) ? on : off;
            }
        }
    }

} // end
