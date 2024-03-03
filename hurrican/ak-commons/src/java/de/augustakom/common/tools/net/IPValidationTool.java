/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.01.2012 08:35:08
 */
package de.augustakom.common.tools.net;

import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;

/**
 * Hilfsklasse fuer die IP-Validierung.
 */
public final class IPValidationTool {

    // @formatter:off
    private static final String IP_V4_EXTNETMASK_REGEXP = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)/0*[0-9]+";
    private static final String IP_V4_PREFIXLENGTH_MISSING_REGEXP = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)/";
    private static final String IP_V4_REGEXP = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

    private static final String IP_V6_EXTNETMASK_REGEXP = "(?:[A-Fa-f0-9]{1,4}:){7}[A-Fa-f0-9]{1,4}/0*[0-9]+";
    private static final String IP_V6_PREFIXLENGTH_MISSING_REGEXP = "(?:[A-Fa-f0-9]{1,4}:){7}[A-Fa-f0-9]{1,4}/";
    private static final String IP_V6_REGEXP = "(?:[A-Fa-f0-9]{1,4}:){7}[A-Fa-f0-9]{1,4}";
    private static final int IPV6_RELATIVE_MAX_PREFIX_LENGTH = 128;
    private static final int IPV6_EUI64_PREFIX_LENGTH = 64;
    // @formatter:on

    private IPValidationTool() {
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String msg;

        public static final ValidationResult VALID = new ValidationResult(true, "");

        public static class ValidationFailedResult extends ValidationResult {
            public ValidationFailedResult(String msg) {
                super(false, msg);
            }
        }

        private ValidationResult(boolean valid, String msg) {
            this.valid = valid;
            this.msg = msg;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMsg() {
            return msg;
        }
    }


    /**
     * Prueft ob es sich um ein gueltiges IPv4-Netz handelt
     */
    public static ValidationResult validateIPV4Prefix(String prefix) {
        ValidationResult validRes = validateAddressV4(prefix, Boolean.TRUE, 0);
        if (validRes.isValid()) {
            int prefixLength = IPToolsV4.instance().getPrefixLength4ValidAddress(prefix);
            validRes = checkPrefixBoundsV4(prefix, prefixLength);
        }
        return validRes;
    }

    /**
     * Prueft ob es sich um eine gueltige IPv4 ohne Praefix handelt
     */
    public static ValidationResult validateIPV4WithoutPrefix(String address) {
        return validateAddressV4(address, Boolean.FALSE, 1);
    }

    /**
     * Prueft ob es sich um eine gueltige IPv6 ohne Praefix handelt
     */
    public static ValidationResult validateIPV6WithoutPrefix(String address) {
        return validateAddressV6(address, Boolean.FALSE, 1);
    }

    /**
     * Prueft ob es sich um eine gueltige IPv6 in Praefixschreibweise handelt
     */
    public static ValidationResult validateIPV6WithPrefix(String address) {
        return validateAddressV6(address, Boolean.TRUE, 1);
    }

    /**
     * Prueft ob es sich um eine gueltige IPv4 ohne Prefix handelt
     */
    public static ValidationResult validateIPV4(String address) {
        return validateAddressV4(address, null, 1);
    }

    /**
     * Prueft ob es sich um eine gueltige IPv4 in Praefixschreibweise handelt
     */
    public static ValidationResult validateIPV4WithPrefix(String address) {
        return validateAddressV4(address, Boolean.TRUE, 1);
    }

    /**
     * Prueft ob es sich um eine gueltige IPv6 handelt
     */
    public static ValidationResult validateIPV6(String address) {
        return validateAddressV6(address, null, 1);
    }

    /**
     * Prueft ob es sich um ein gueltiges IPv6-Netz handelt
     */
    public static ValidationResult validateIPV6Prefix(String prefix) {
        ValidationResult validRes = validateAddressV6(prefix, Boolean.TRUE, 0);
        if (validRes.isValid()) {
            int prefixLength = IPToolsV6.instance().getPrefixLength4ValidAddress(prefix);
            validRes = checkPrefixBoundsV6(prefix, prefixLength);
        }
        return validRes;
    }

    /**
     * Prueft ob es sich um eine gueltige IPv6-EUI64 handelt
     */
    public static ValidationResult validateIPV6EUI64(String address) {
        ValidationResult validRes = validateAddressV6(address, Boolean.TRUE, 1);
        if (validRes.isValid()) {
            if (!NumberTools.equal(IPToolsV6.instance().getPrefixLength4ValidAddress(address),
                    IPV6_EUI64_PREFIX_LENGTH)) {
                return new ValidationResult.ValidationFailedResult(String.format(
                        "Die Präfixlänge einer EUI64 Adresse muss %d sein!", IPV6_EUI64_PREFIX_LENGTH));
            }
            validRes = checkPrefixBoundsV6(address, IPV6_EUI64_PREFIX_LENGTH);
        }
        return validRes;
    }

    /**
     * Prueft ob es sich um eine gueltige, relative IPv6 handelt, d.h. die Addresse darf sich nicht mit dem Praefix
     * ueberschneiden
     */
    public static ValidationResult validateIPV6Relative(String validPrefix, String address) {
        if (StringUtils.isBlank(validPrefix)) {
            return new ValidationResult.ValidationFailedResult(
                    "Präfix ist nicht gesetzt!");
        }
        ValidationResult validationResult = validateAddressV6(address, Boolean.TRUE, 1);

        if (validationResult.isValid()) {
            int prefixLengthOfPrefix = IPToolsV6.instance().getPrefixLength4ValidAddress(validPrefix);
            int prefixLengthOfAdr = IPToolsV6.instance().getPrefixLength4ValidAddress(address);
            if (prefixLengthOfPrefix != -1) {
                if (prefixLengthOfAdr > IPV6_RELATIVE_MAX_PREFIX_LENGTH) {
                    return new ValidationResult.ValidationFailedResult(String.format(
                            "Präfixlänge ist größer als der max. erlaubte Wert (%s)!", IPV6_RELATIVE_MAX_PREFIX_LENGTH));
                }
                if (prefixLengthOfPrefix > prefixLengthOfAdr) {
                    return new ValidationResult.ValidationFailedResult(
                            "Präfixlänge ist kleiner als die Präfixlänge des Netzes(Präfix)!");
                }
                ValidationResult relativePrefixBitCheck = checkRelativePrefixBitsV6(prefixLengthOfPrefix, address);
                if (relativePrefixBitCheck != ValidationResult.VALID) {
                    return relativePrefixBitCheck;
                }
            }
        }

        return validationResult;
    }

    /**
     * Prueft ob es sich um eine gueltige, relative IPv6-EUI64 handelt, d.h. die Addresse darf sich nicht mit dem
     * Praefix ueberschneiden und die Praefixlaenge muss 64-bit betragen
     */
    public static ValidationResult validateIPV6EUI64Relativ(String validPrefix, String address) {
        if (StringUtils.isBlank(validPrefix)) {
            return new ValidationResult.ValidationFailedResult(
                    "Präfix ist nicht gesetzt!");
        }

        ValidationResult validationResult = validateAddressV6(address, Boolean.TRUE, 1);
        if (validationResult.isValid()) {
            final int prefixLengthOfPrefix = IPToolsV6.instance().getPrefixLength4ValidAddress(validPrefix);
            int prefixLengthOfAdr = IPToolsV6.instance().getPrefixLength4ValidAddress(address);
            if (NumberTools.isGreater(prefixLengthOfPrefix, IPV6_EUI64_PREFIX_LENGTH)) {
                return new ValidationResult.ValidationFailedResult(String.format(
                        "Präfixlänge des Netzes(Präfix) mus <= %d sein!", IPV6_EUI64_PREFIX_LENGTH));
            }
            if (!NumberTools.equal(prefixLengthOfAdr, IPV6_EUI64_PREFIX_LENGTH)) {
                return new ValidationResult.ValidationFailedResult(String.format(
                        "Die Präfixlänge einer EUI64 Adresse muss %d sein!", IPV6_EUI64_PREFIX_LENGTH));
            }
            validationResult = checkRelativePrefixBitsV6(prefixLengthOfPrefix, address);
            if (validationResult.isValid()) {
                validationResult = checkPrefixBoundsV6(address, prefixLengthOfAdr);
            }
        }
        return validationResult;
    }

    static ValidationResult validateAddressV4(String address, Boolean withPrefixLength, int prefixLengthLowLimit) {
        ValidationResult checkSyntaxResult = checkSyntaxV4(address, withPrefixLength);
        if (checkSyntaxResult != ValidationResult.VALID) {
            return checkSyntaxResult;
        }
        if ((withPrefixLength == null) || withPrefixLength) {
            int prefixLength = IPToolsV4.instance().getPrefixLength4ValidAddress(address);
            if (prefixLength < prefixLengthLowLimit) {
                return new ValidationResult.ValidationFailedResult("Ungültige Präfixlänge!");
            }
        }
        return ValidationResult.VALID;
    }

    /**
     * Prüft die Syntax einer V4 Adresse bzw. einer Netmaske
     *
     * @param withPrefixLength == null -> mit und ohne prefix testen; == false -> ohne prefix testen ; == true -> mit
     *                         prefix testen
     */
    private static ValidationResult checkSyntaxV4(String address, Boolean withPrefixLength) {
        ValidationResult validRes = new ValidationResult.ValidationFailedResult("Ungültige IP-Adresse!");
        if (!StringUtils.isBlank(address)) {
            if (((withPrefixLength == null) || !withPrefixLength) && address.matches(IP_V4_REGEXP)) {
                validRes = ValidationResult.VALID;
            }
            if (((withPrefixLength == null) || withPrefixLength)
                    && address.matches(IP_V4_EXTNETMASK_REGEXP)) {
                validRes = ValidationResult.VALID;
            }
            if ((validRes != ValidationResult.VALID)) {
                if (((withPrefixLength == null) || withPrefixLength)
                        && address.matches(IP_V4_PREFIXLENGTH_MISSING_REGEXP)) {
                    validRes = new ValidationResult.ValidationFailedResult("Präfixwert bitte angeben!");
                }
                else if (((withPrefixLength != null) && withPrefixLength) && address.matches(IP_V4_REGEXP)) {
                    validRes = new ValidationResult.ValidationFailedResult("Präfixlänge fehlt!");
                }
            }
        }
        return validRes;
    }

    static ValidationResult validateAddressV6(String address, Boolean withPrefixLength, int minPrefixLength) {
        return validateAddressV6(address, withPrefixLength, minPrefixLength, null);
    }

    /**
     * Validiert eine V6-Adresse oder Netzmaske
     *
     * @param withPrefixLength == null -> mit und ohne prefix testen; == false -> ohne prefix testen ; == true -> mit
     *                         prefix testen
     * @param fixedPrefLength  != null -> check if prefix length matches exactly this value
     */
    static ValidationResult validateAddressV6(String address, Boolean withPrefixLength, int minPrefixLength,
            Integer fixedPrefLength) {
        ValidationResult result = new ValidationResult.ValidationFailedResult("Ungültige IP-Adresse!");
        if (!StringUtils.isBlank(address)) {
            String expandedAddress = IPToolsV6.instance().expandAddress(address);
            result = checkSyntaxV6(expandedAddress, withPrefixLength);
            if (result.isValid() && (withPrefixLength == null || withPrefixLength)) {
                final int prefixLength = IPToolsV6.instance().getPrefixLength4ValidAddress(expandedAddress);
                if ((prefixLength < minPrefixLength)
                        || ((fixedPrefLength != null) && !fixedPrefLength.equals(prefixLength))) {
                    result = new ValidationResult.ValidationFailedResult("Ungültige Präfixlänge!");
                }
            }
        }
        return result;
    }

    private static ValidationResult checkSyntaxV6(String expandedAddress,
            Boolean withPrefixLength) {
        ValidationResult validRes = new ValidationResult.ValidationFailedResult("Ungültige IP-Adresse!");
        if (expandedAddress != null) {
            if (((withPrefixLength == null) || !withPrefixLength)
                    && expandedAddress.matches(IP_V6_REGEXP)) {
                validRes = ValidationResult.VALID;
            }
            if (((withPrefixLength == null) || withPrefixLength)
                    && expandedAddress.matches(IP_V6_EXTNETMASK_REGEXP)) {
                validRes = ValidationResult.VALID;
            }
            if ((validRes != ValidationResult.VALID)) {
                if (((withPrefixLength == null) || withPrefixLength)
                        && expandedAddress.matches(IP_V6_PREFIXLENGTH_MISSING_REGEXP)) {
                    validRes = new ValidationResult.ValidationFailedResult("Präfixwert bitte angeben!");
                }
                else if (((withPrefixLength != null) && withPrefixLength)
                        && expandedAddress.matches(IP_V6_REGEXP)) {
                    validRes = new ValidationResult.ValidationFailedResult("Präfixlänge fehlt!");
                }
            }
        }
        return validRes;
    }

    private static ValidationResult checkPrefixBoundsV4(String prefix, int prefixLength) {
        return checkPrefixBoundsGeneric(prefix, prefixLength, IPToolsV4.instance());
    }

    /**
     * Prüft ob Bits des Präfixes ausserhalb der Präfix Länge (z.B. '.../48') sind
     *
     * @return true, keine ungültigen Bits; false, mindestens ein ungültiges Bit entdeckt
     */
    private static ValidationResult checkPrefixBoundsV6(String validPrefix, int prefixLength) {
        return checkPrefixBoundsGeneric(validPrefix, prefixLength, IPToolsV6.instance());
    }

    private static ValidationResult checkPrefixBoundsGeneric(String validPrefix, int prefixLength,
            AbstractIPTools ipTools) {
        Pair<String[], String[]> parts = ipTools.splitAddress(validPrefix);
        if (parts == null) {
            return new ValidationResult.ValidationFailedResult("Ungültige IP-Adresse!");
        }
        String[] segmentParts = parts.getSecond();
        IPToolsBinaryAddress binaryAddress = ipTools.createBinaryAddress();
        binaryAddress.setSegments(segmentParts);
        for (int index = prefixLength; index < ipTools.getMaximumBits(); index++) {
            if (binaryAddress.get(index) != IPToolsBinaryAddress.off) {
                return new ValidationResult.ValidationFailedResult("Adressbits ausserhalb der Präfixlänge!");
            }
        }
        return ValidationResult.VALID;
    }

    /**
     * Prüft, ob sich die Bits einer relativen Addresse mit denen des Präfixes überschneiden
     */
    private static ValidationResult checkRelativePrefixBitsV6(int prefixLength, String validRelativeAddress) {
        Pair<String[], String[]> parts = IPToolsV6.instance().splitAddress(validRelativeAddress);
        if (parts == null) {
            return new ValidationResult.ValidationFailedResult("Ungültige IP-Adresse!");
        }
        String[] segmentParts = parts.getSecond();
        IPToolsBinaryAddress binaryAddress = IPToolsV6.instance().createBinaryAddress();
        binaryAddress.setSegments(segmentParts);
        return (binaryAddress.checkPrefixBitsNotSet(prefixLength)) ? ValidationResult.VALID
                : new ValidationResult.ValidationFailedResult("Es dürfen nur Bits ausserhalb des Präfixes gesetzt werden!");
    }
}
