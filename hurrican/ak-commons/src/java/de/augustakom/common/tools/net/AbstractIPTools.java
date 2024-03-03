/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.2011 14:26:36
 */
package de.augustakom.common.tools.net;

import java.math.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.Pair;

/**
 * Basis Klasse für die Validierung und div. IP Operation
 */
public abstract class AbstractIPTools {

    /**
     * Zerlegt eine Adresse in ihre Bestandteile (IP-Adresse, Prefix-Angabe). <br>
     *
     * @return Pair mit den Bestandteilen der IP-Adresse: <br> FIRST im Pair: Array mit [0]=IP-Adresse;
     * [1]=Prefix-Angabe <br> SECOND im Pair: die Prefix-Angabe <br>
     */
    protected abstract Pair<String[], String[]> splitAddress(String address);

    /**
     * Normalisiert eine valide Addresse bzw. valide relative Addresse (IPV6).
     */
    public abstract String normalizeValidAddress(String validAddress);

    /**
     * liefert eine {@link IPToolsBinaryAddress} dessen konkrete Implementierung in den implementierenden Subklassen
     * gesetzt wird.
     */
    abstract IPToolsBinaryAddress createBinaryAddress();

    /**
     * liefert die Anzahl der maximal setzbaren bits fuer eine IP-Adresse. Die konkrete Zahl muss implementiert werden.
     */
    abstract int getMaximumBits();

    /**
     * liefert das Zeichen fuer die Trennung von Segmenten einer IP-Adresse je nach Typ.
     */
    abstract char getSegmentDelimiter();

    /**
     * liefert die Größe einer Netzmaske oder -1, falls die Addresse invalide ist
     */
    public int netmaskSize(String address) {
        if (validateAddress(address)) {
            Pair<String[], String[]> parts = splitAddress(address);
            if (parts == null) {return -1;}
            String[] addressParts = parts.getFirst();
            return ((addressParts.length > 1) ? Integer.parseInt(addressParts[1]) : 0);
        }
        else {
            return -1;
        }
    }

    abstract boolean validateAddress(String address);

    /**
     * liefert eine Netzmaske zu einer gegebenen IP-Adresse. Beispiel: 192.168.0.0/16 -> 255.255.0.0
     */
    public String getNetmask(String address) {
        int prefixLength = getPrefixLength4Address(address);
        String netMask = getNetmask(prefixLength);
        return netMask;
    }

    /**
     * Ermittelt die Prefix Länge für eine Adresse (192.168.1.1/24)
     *
     * @return Präfix Länge, max wenn Adresse keine Präfix Länge angegeben hat (192.168.1.1 -> 32)
     */
    public int getPrefixLength4Address(String address) {
        boolean validationResult = validateAddress(address);
        if (validationResult) {
            return getPrefixLength4ValidAddress(address);
        }
        return -1;
    }

    public abstract int getPrefixLength4ValidAddress(String address);

    public String getAddressWithoutPrefix(String addressWithPrefix) {
        String result = null;
        Pair<String[], String[]> parts = splitAddress(addressWithPrefix);
        if (parts == null) {return result;}
        String[] addressParts = parts.getFirst();
        if (addressParts.length > 0) {
            result = addressParts[0];
        }
        return result;
    }


    /**
     * Ermittelt die Präfix Länge für eine Netzmaske
     */
    public int getPrefixLength4Netmask(String netmask) {
        boolean validationResult = validateAddressWithoutPrefix(netmask);
        if (validationResult) {
            Pair<String[], String[]> parts = splitAddress(netmask);
            if (parts == null) {return -1;}
            String[] segmentParts = parts.getSecond();
            IPToolsBinaryAddress binaryNetmask = createBinaryAddress();
            binaryNetmask.setSegments(segmentParts);
            return checkPrefixMask4Netmask(binaryNetmask);
        }
        return -1;
    }

    abstract boolean validateAddressWithoutPrefix(String address);

    /**
     * Normalisiert die Präfix Länge
     */
    protected String normalizePrefixLength(String prefixLength, char prefixLengthDelimiter) {
        if (StringUtils.isBlank(prefixLength)) {return null;}
        StringBuilder normalizedPrefix = new StringBuilder();
        normalizedPrefix.append(prefixLengthDelimiter);
        normalizedPrefix.append(Integer.valueOf(prefixLength).toString());
        return normalizedPrefix.toString();
    }

    /**
     * Berechnet die Präfix Länge für eine Anzahl IP Adressen
     */
    protected int getPrefixLength4IPSize(BigInteger ipSize) {
        for (int power = 0; power <= getMaximumBits(); power++) {
            BigInteger base = BigInteger.valueOf(2);
            BigInteger size = base.pow(power);
            if (size.compareTo(ipSize) >= 0) {
                return power;
            }
        }
        return -1;
    }

    /**
     * Berechnet die Anzahl IP Adressen für eine Netzmaske
     */
    protected BigInteger getIPSize4Netmask(String netmask) {
        int prefixLength = getPrefixLength4Netmask(netmask);
        if ((prefixLength >= 0) && (prefixLength <= getMaximumBits())) {
            BigInteger base = BigInteger.valueOf(2);
            BigInteger ipSize = base.pow(getMaximumBits() - prefixLength);
            return ipSize;
        }
        return BigInteger.valueOf(-1);
    }

    /**
     * Erstellt eine Netzmaske für eine Präfix Länge
     */
    protected String getNetmask(int prefixLength) {
        IPToolsBinaryAddress binaryAddress = createBinaryAddress();
        if (!binaryAddress.checkValidPrefixLength(prefixLength)) {return null;}
        binaryAddress.setPrefixLength(prefixLength);
        String[] segments = binaryAddress.getSegmentsAsString();
        StringBuilder netmask = new StringBuilder();
        for (String segment : segments) {
            if (netmask.length() > 0) {
                netmask.append(getSegmentDelimiter());
            }
            netmask.append(segment);
        }
        return netmask.toString();
    }

    /**
     * Prüft die Präfix Bits einer Netzmaske
     *
     * @return Präfix Länge, im Fehlerfall -1
     */
    protected int checkPrefixMask4Netmask(IPToolsBinaryAddress binaryNetmask) {
        int prefixLength = 0;
        boolean prefixClosed = false;
        for (int index = 0; index < binaryNetmask.getArrayLength(); index++) {
            if (!prefixClosed) {
                if (binaryNetmask.get(index) == IPToolsBinaryAddress.off) {
                    prefixClosed = true;
                }
                else {
                    prefixLength++;
                }
            }
            else if ((binaryNetmask.get(index) == IPToolsBinaryAddress.on)) {
                return -1; // fehlerhaftes Präfix 11101...
            }
        }
        return prefixLength;
    }

} // end
