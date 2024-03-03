package de.augustakom.common.tools.net;

import org.apache.log4j.Logger;


/**
 * IP-Adress-Konverter fuer die Umwandlung von IP-Adressen (v4 und v6) in Binaer-Darstellung. <br><br> Codebase is
 * provided from BSI!
 */
public class IPAddressConverter {

    private static final Logger LOGGER = Logger.getLogger(IPAddressConverter.class);

    private static final String BIT_ON = "1";
    private static final String BIT_OFF = "0";
    private static final String IP_DELIMITER = "/";

    /**
     * Generiert aus der angegebenen IP-Adresse die Binaer-Darstellung. <br> Die Umwandlung in Binaer erfolgt dabei in
     * verkuerzter Schreibweise. Netze / Prefixe werden ebenfalls beruecksichtigt.
     *
     * @param input IP-Adresse in herkoemmlicher Schreibweise. <br> Beispiele: <br> <ul> <li> 192.168.2.1  -->
     *              11000000101010000000001000000001 <li> 192.168.2.1/16  -->  1100000010101000 <li> 12.12.12.12  -->
     *              00001100000011000000110000001100 <li> 0.0.0.0      -->  00000000000000000000000000000000 <li>
     *              255.255.255.255  -->  11111111111111111111111111111111 <li> 192  -->
     *              00000000000000000000000011000000 <li> 256.0.0.0  -->  null </ul>
     * @return IP Adresse in (verkuerzter) Binaer-Darstellung
     */
    public static String parseIPAddress(String input, boolean ignorePrefixLength) {
        /* change IP address to binary string */
        String[] parts = input.split(IP_DELIMITER);
        String iP = parts[0];
        Integer praefix;

        StringBuilder output = new StringBuilder();
        if (IPAddressUtil.isIPv6LiteralAddress(iP)) {
            byte[] ipV6 = IPAddressUtil.textToNumericFormatV6(iP);
            for (byte element : ipV6) {
                output.append(toBinaryString(element));
            }
            praefix = Integer.valueOf(64);
        }
        else if (IPAddressUtil.isIPv4LiteralAddress(iP)) {
            byte[] ipV4 = IPAddressUtil.textToNumericFormatV4(iP);
            for (byte element : ipV4) {
                output.append(toBinaryString(element));
            }
            praefix = Integer.valueOf(32);
        }
        else {
            return null;
        }

        if ((parts.length > 1) && !ignorePrefixLength) {
            try {
                praefix = Integer.valueOf(parts[1]);
            }
            catch (Exception e) {
                LOGGER.info(e.getMessage());
                // do nothing
            }
        }

        return output.substring(0, praefix.intValue());
    }

    /**
     * Konvertiert das byte in eine String-Darstellung. <br> Beispiel: 192 --> 11000000
     *
     * @param n
     * @return
     */
    public static String toBinaryString(byte n) {
        String[] sb = new String[8];
        for (int i = 0; i < sb.length; i++) {
            sb[i] = BIT_OFF;
        }
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb[7 - bit] = BIT_ON;
            }
        }
        StringBuilder result = new StringBuilder();
        for (String element : sb) {
            result.append(element);
        }
        return result.toString();
    }

}
