/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.2011 15:34:04
 */
package de.augustakom.common.tools.net;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.Pair;

/**
 * IP V6 Tools Implementierung
 */
public final class IPToolsV6 extends AbstractIPTools {

    private IPToolsV6() {
    }

    private static final IPToolsV6 instance = new IPToolsV6();

    public static final IPToolsV6 instance() {
        return instance;
    }

    private static final Logger LOGGER = Logger.getLogger(IPToolsV6.class);

    // @formatter:off
    private static final String IP_V6_COMPRESSED_REGEXP = "::(?:/[0-9]*)?";
    private static final String IP_V6_COMPRESSED_LEFT_REGEXP = "::(?:(?:[A-Fa-f0-9]{1,4}:)*[A-Fa-f0-9]{1,4})(?:/[0-9]*)?";
    private static final String IP_V6_COMPRESSED_INNER_REGEXP = "(?:(?:[A-Fa-f0-9]{1,4}:)*[A-Fa-f0-9]{1,4})::(?:(?:[A-Fa-f0-9]{1,4}:)*[A-Fa-f0-9]{1,4})(?:/[0-9]*)?";
    private static final String IP_V6_COMPRESSED_RIGHT_REGEXP = "(?:(?:[A-Fa-f0-9]{1,4}:)*[A-Fa-f0-9]{1,4})::(?:/[0-9]*)?";
    // @formatter:on

    private static final char IPV6_SEGMENT_DELIMITER = ':';
    public static final char IPV6_PREFIX_LENGTH_DELIMITER = '/';
    private static final String IPV6_EXTENDED_SEGMENT_DELIMITER = "::";
    private static final String IPV6_SEGMENT_DELIMITER_REGEXP = ":";
    private static final String IPV6_PREFIX_LENGTH_DELIMITER_REGEXP = "/";

    /**
     * Erzeugt einen String mit Null-Segmenten z.B '0:0:0'
     */
    private String createSegmentsMissing(int segmentsMissing) {
        StringBuilder segments = new StringBuilder();
        for (; segmentsMissing > 0; segmentsMissing--) {
            if (segments.length() > 0) {
                segments.append(IPV6_SEGMENT_DELIMITER);
            }
            segments.append(IPToolsBinaryAddress.ZERO_SEGMENT);
        }
        return segments.toString();
    }

    /**
     * Sucht und ersetzt "::"
     */
    public String expandAddress(String address) {
        int delimitersCount = StringUtils.countMatches(address, new String(new char[] { IPV6_SEGMENT_DELIMITER }));
        int extendedDelimiterCount = StringUtils.countMatches(address, IPV6_EXTENDED_SEGMENT_DELIMITER);
        if (extendedDelimiterCount == 0) {
            return address;
        }
        else if (extendedDelimiterCount > 1) {
            return null;
        }
        int segmentsMissing = 0;
        StringBuilder replacement = new StringBuilder();
        if (address.matches(IP_V6_COMPRESSED_REGEXP)) {
            segmentsMissing = createBinaryAddress().getSegmentCount();
            String formatedSegments = createSegmentsMissing(segmentsMissing);
            replacement.append(formatedSegments);
        }
        else if (address.matches(IP_V6_COMPRESSED_LEFT_REGEXP)) {
            int segmentsCount = delimitersCount - 1; // Anzahl ':' - "::" + 1 (':' links)
            segmentsMissing = createBinaryAddress().getSegmentCount() - segmentsCount;
            String formatedSegments = createSegmentsMissing(segmentsMissing);
            replacement.append(formatedSegments);
            replacement.append(IPV6_SEGMENT_DELIMITER);
        }
        else if (address.matches(IP_V6_COMPRESSED_INNER_REGEXP)) {
            int segmentsCount = delimitersCount; // Anzahl ':' - "::" + 2 (':' links unt rechts)
            segmentsMissing = createBinaryAddress().getSegmentCount() - segmentsCount;
            String formatedSegments = createSegmentsMissing(segmentsMissing);
            replacement.append(IPV6_SEGMENT_DELIMITER);
            replacement.append(formatedSegments);
            replacement.append(IPV6_SEGMENT_DELIMITER);
        }
        else if (address.matches(IP_V6_COMPRESSED_RIGHT_REGEXP)) {
            int segmentsCount = delimitersCount - 1; // Anzahl ':' - "::" + 1 (':' rechts)
            segmentsMissing = createBinaryAddress().getSegmentCount() - segmentsCount;
            String formatedSegments = createSegmentsMissing(segmentsMissing);
            replacement.append(IPV6_SEGMENT_DELIMITER);
            replacement.append(formatedSegments);
        }
        else {
            return null;
        }
        if ((segmentsMissing < 1) || (segmentsMissing > createBinaryAddress().getSegmentCount())) {
            return null;
        }
        return StringUtils.replace(address, IPV6_EXTENDED_SEGMENT_DELIMITER, replacement.toString());
    }

    @Override
    public Pair<String[], String[]> splitAddress(String address) {
        if (StringUtils.isBlank(address)) {
            return null;
        }
        String expandedAddress = expandAddress(address);
        if (expandedAddress == null) {
            return null;
        }
        String[] addressParts = expandedAddress.split(IPV6_PREFIX_LENGTH_DELIMITER_REGEXP);
        if ((addressParts == null) || (addressParts.length < 1)) {
            return null;
        }
        String[] segmentParts = addressParts[0].split(IPV6_SEGMENT_DELIMITER_REGEXP);
        return new Pair<String[], String[]>(addressParts, segmentParts);
    }

    /**
     * Sucht den groessten zusammenhängenden Bereich mit 'Null Segmenten'
     */
    private Pair<Integer, Integer> findRange2Collapse(String[] segmentParts) {
        List<Pair<Integer, Integer>> ranges = new ArrayList<Pair<Integer, Integer>>();
        int start = -1;
        int dimension = 0;
        for (int index = 0; index < segmentParts.length; index++) {
            if (StringUtils.equals(segmentParts[index], IPToolsBinaryAddress.ZERO_SEGMENT)) {
                if (start == -1) {
                    start = index;
                    dimension = 1;
                }
                else {
                    dimension++;
                }
            }
            else {
                if (start > -1) {
                    ranges.add(new Pair<Integer, Integer>(Integer.valueOf(start), Integer.valueOf(dimension)));
                    start = -1;
                    dimension = 0;
                }
            }
        }
        if (start > -1) {
            ranges.add(new Pair<Integer, Integer>(Integer.valueOf(start), Integer.valueOf(dimension)));
        }
        Pair<Integer, Integer> result = null;
        for (Pair<Integer, Integer> range : ranges) {
            if ((result == null) || (result.getSecond() < range.getSecond())) {
                result = range;
            }
        }
        return result;
    }

    /**
     * Ersetzt den größten Bereich mit Null-Segmenten in einer normalisierten Adressse durch "::"
     */
    protected String collapseNormalizedAddress(String normalizedAddress) {
        Pair<String[], String[]> parts = splitAddress(normalizedAddress);
        if (parts == null) {
            return null;
        }
        String[] addressParts = parts.getFirst();
        String[] segmentParts = parts.getSecond();
        Pair<Integer, Integer> range = findRange2Collapse(segmentParts);
        StringBuilder collapsedPrefix = new StringBuilder();
        int start = (range != null) ? range.getFirst().intValue() : -1;
        int dimension = (range != null) ? range.getSecond().intValue() : 0;

        for (int index = 0; index < segmentParts.length; index++) {
            if ((index < start) || (index >= (start + dimension))) {
                if (collapsedPrefix.length() > 0) {
                    collapsedPrefix.append(IPV6_SEGMENT_DELIMITER);
                }
                collapsedPrefix.append(segmentParts[index]);

            }
            else {
                // unterscheidung zwischen nullen bis ende oder nur zwischendurch
                if ((index == start) && ((start + dimension) == segmentParts.length)) {
                    collapsedPrefix.append(IPV6_SEGMENT_DELIMITER).append(IPV6_SEGMENT_DELIMITER);
                    break;
                }
                else if (index == ((start + dimension) - 1)) {
                    collapsedPrefix.append(IPV6_SEGMENT_DELIMITER);
                    continue;
                }
            }
        }
        if (addressParts.length > 1) {
            collapsedPrefix.append(IPV6_PREFIX_LENGTH_DELIMITER);
            collapsedPrefix.append(addressParts[1]);
        }
        return collapsedPrefix.toString();
    }

    /**
     * Normalisiert ein Präfix (Präfix bzw. EUI-64 Adresse)
     *
     * @param validAddress gültiges Präfix bzw. EUI-64 Adresse!
     */
    private String normalizeAddress(String validAddress) {
        Pair<String[], String[]> parts = splitAddress(validAddress);
        if (parts == null) {
            return null;
        }
        String[] addressParts = parts.getFirst();
        String[] segmentParts = parts.getSecond();
        IPToolsBinaryAddress binaryAddress = createBinaryAddress();
        binaryAddress.setSegments(segmentParts);
        String[] normalizedSegments = binaryAddress.getSegmentsAsString();
        StringBuilder normalizedAddress = new StringBuilder();
        for (String normalizedSegment : normalizedSegments) {
            if (normalizedAddress.length() > 0) {
                normalizedAddress.append(IPV6_SEGMENT_DELIMITER);
            }
            normalizedAddress.append(normalizedSegment);
        }
        if (addressParts.length > 1) {
            normalizedAddress.append(normalizePrefixLength(addressParts[1], IPV6_PREFIX_LENGTH_DELIMITER));
        }
        String collapsedAddress = collapseNormalizedAddress(normalizedAddress.toString());
        return collapsedAddress;
    }

    @Override
    public String normalizeValidAddress(String validAddress) {
        try {
            String normalizedAddress = normalizeAddress(validAddress);
            return normalizedAddress;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    public String createAbsoluteAddress(String validPrefix, String validRelativeAddress) {
        // Prefix
        Pair<String[], String[]> prefixParts = splitAddress(validPrefix);
        if (prefixParts == null) {
            return null;
        }
        String[] prefixSegmentParts = prefixParts.getSecond();
        IPToolsBinaryAddress prefixBinaryAddress = createBinaryAddress();
        prefixBinaryAddress.setSegments(prefixSegmentParts);

        // relative Address
        Pair<String[], String[]> relativeParts = splitAddress(validRelativeAddress);
        if (relativeParts == null) {
            return null;
        }
        String[] relativeAddressParts = relativeParts.getFirst();
        String[] relativeSegmentParts = relativeParts.getSecond();
        IPToolsBinaryAddress relativeBinaryAddress = createBinaryAddress();
        relativeBinaryAddress.setSegments(relativeSegmentParts);

        prefixBinaryAddress.orOperation(relativeBinaryAddress);
        String[] normalizedSegments = prefixBinaryAddress.getSegmentsAsString();
        StringBuilder absoluteAddress = new StringBuilder();
        for (String normalizedSegment : normalizedSegments) {
            if (absoluteAddress.length() > 0) {
                absoluteAddress.append(IPV6_SEGMENT_DELIMITER);
            }
            absoluteAddress.append(normalizedSegment);
        }
        if (relativeAddressParts.length > 1) {
            absoluteAddress.append(normalizePrefixLength(relativeAddressParts[1], IPV6_PREFIX_LENGTH_DELIMITER));
        }
        String collapsedAbsoluteAddress = collapseNormalizedAddress(absoluteAddress.toString());
        return collapsedAbsoluteAddress;
    }

    @Override
    public IPToolsBinaryAddressV6 createBinaryAddress() {
        return IPToolsBinaryAddressV6.create();
    }

    @Override
    public int getMaximumBits() {
        return IPToolsBinaryAddressV6.MAX_BITS;
    }

    @Override
    char getSegmentDelimiter() {
        return IPV6_SEGMENT_DELIMITER;
    }

    @Override
    boolean validateAddress(String address) {
        return IPValidationTool.validateIPV6(address).isValid();
    }

    @Override
    public int getPrefixLength4ValidAddress(String validAddress) {
        Pair<String[], String[]> parts = splitAddress(validAddress);
        if (parts == null) {
            return -1;
        }
        String[] addressParts = parts.getFirst();
        if (addressParts.length > 1) {
            int prefixLength = Integer.parseInt(addressParts[1]);
            if ((prefixLength >= 0) && (prefixLength <= getMaximumBits())) {
                return prefixLength;
            }
            else {
                return -1;
            }
        }
        return getMaximumBits();
    }

    @Override
    boolean validateAddressWithoutPrefix(String address) {
        return IPValidationTool.validateIPV6WithoutPrefix(address).isValid();
    }

}
