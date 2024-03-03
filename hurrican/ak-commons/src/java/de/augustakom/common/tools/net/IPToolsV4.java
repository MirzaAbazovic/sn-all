/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.2011 15:33:32
 */
package de.augustakom.common.tools.net;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.Pair;

/**
 * IP V4 Tools Implementierung
 */
public final class IPToolsV4 extends AbstractIPTools {

    private IPToolsV4() {
    }

    private static final IPToolsV4 instance = new IPToolsV4();

    public static IPToolsV4 instance() {
        return instance;
    }

    private static final Logger LOGGER = Logger.getLogger(IPToolsV4.class);

    private static final char IPV4_SEGMENT_DELIMITER = '.';
    public static final char IPV4_PREFIX_LENGTH_DELIMITER = '/';
    private static final String IPV4_SEGMENT_DELIMITER_REGEXP = "\\.";
    private static final String IPV4_PREFIX_LENGTH_DELIMITER_REGEXP = "/";

    @Override
    public Pair<String[], String[]> splitAddress(String address) {
        if (StringUtils.isBlank(address)) {return null;}
        String[] addressParts = address.split(IPV4_PREFIX_LENGTH_DELIMITER_REGEXP);
        if ((addressParts == null) || (addressParts.length < 1)) {return null;}
        String[] segmentParts = addressParts[0].split(IPV4_SEGMENT_DELIMITER_REGEXP);
        return new Pair<String[], String[]>(addressParts, segmentParts);
    }

    /**
     * Normalisiert eine Addresse (Addresse bzw. relative Addresse mit Präfix)
     *
     * @param validAddress gültige und expandierte Adresse!
     */
    private String normalizeAddress(String validAddress) {
        Pair<String[], String[]> parts = splitAddress(validAddress);
        if (parts == null) {return null;}
        String[] addressParts = parts.getFirst();
        String[] segmentParts = parts.getSecond();
        IPToolsBinaryAddress binaryAddress = createBinaryAddress();
        binaryAddress.setSegments(segmentParts);
        String[] normalizedSegments = binaryAddress.getSegmentsAsString();
        StringBuilder normalizedAddress = new StringBuilder();
        for (String normalizedSegment : normalizedSegments) {
            if (normalizedAddress.length() > 0) {
                normalizedAddress.append(IPV4_SEGMENT_DELIMITER);
            }
            normalizedAddress.append(normalizedSegment);
        }
        if (addressParts.length > 1) {
            normalizedAddress.append(normalizePrefixLength(addressParts[1], IPV4_PREFIX_LENGTH_DELIMITER));
        }
        return normalizedAddress.toString();
    }

    /**
     * Validiert eine Netzmaske (z.B. 255.255.255.0 -> 24)
     *
     * @return OK == true, nicht OK == false
     */
    public boolean validateNetmask(String netmask) {
        if (getPrefixLength4Netmask(netmask) == -1) {
            return false;
        }
        return true;
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

    @Override
    IPToolsBinaryAddressV4 createBinaryAddress() {
        return IPToolsBinaryAddressV4.create();
    }

    @Override
    public int getMaximumBits() {
        return IPToolsBinaryAddressV4.MAX_BITS;
    }

    @Override
    char getSegmentDelimiter() {
        return IPV4_SEGMENT_DELIMITER;
    }

    @Override
    boolean validateAddress(String address) {
        return IPValidationTool.validateIPV4(address).isValid();
    }

    @Override
    public int getPrefixLength4ValidAddress(String validAddress) {
        Pair<String[], String[]> parts = IPToolsV4.instance().splitAddress(validAddress);
        if (parts == null) {
            return -1;
        }
        String[] addressParts = parts.getFirst();
        if (addressParts.length > 1) {
            int prefixLength = Integer.parseInt(addressParts[1]);
            if ((prefixLength >= 0) && (prefixLength <= IPToolsV4.instance().getMaximumBits())) {
                return prefixLength;
            }
            else {
                return -1;
            }
        }
        return IPToolsV4.instance().getMaximumBits();
    }

    @Override
    boolean validateAddressWithoutPrefix(String address) {
        return IPValidationTool.validateIPV4WithoutPrefix(address).isValid();
    }

    /**
     * Erzeugt eine Praefix-Adresse aus einer validen Nicht-Praefix-Adresse und einer validen Netzmaske
     *
     * @param addressWithoutPrefix eine valide IP Addresse ohne Praefix
     * @param netmask              eine valide Netzmaske
     * @return die angegebene IP-Adresse ergaenzt um das Praefix, das der angegebenen Netzmaske entspricht, bzw. null
     * falls die Netzmaske oder die IP-Adresse nicht valide ist
     */
    public String netmaskToAddressWithPrefix(String addressWithoutPrefix, String netmask) {
        if (validateAddressWithoutPrefix(addressWithoutPrefix)) {
            final int prefixLength = getPrefixLength4Netmask(netmask);
            if (prefixLength >= 0) {
                return new StringBuilder(addressWithoutPrefix).append(IPV4_PREFIX_LENGTH_DELIMITER)
                        .append(prefixLength).toString();
            }
        }
        return null;
    }
}
