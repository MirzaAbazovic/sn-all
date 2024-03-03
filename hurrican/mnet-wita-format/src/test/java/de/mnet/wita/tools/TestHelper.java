package de.mnet.wita.tools;

import java.lang.reflect.*;

import de.mnet.wita.WitaCdmVersion;

/**
 *
 */
public class TestHelper {

    /**
     * Extracts the WITA CDM Version form the name of the provided method. It assumes that the name ends with a valid wita
     * version. E.g. it could look like 'xxxxxxxV4' or 'xxxV7'.
     */
    public static WitaCdmVersion extractWitaVersion(Method method) {
        String methodName = method.getName();
        int index = methodName.lastIndexOf("V");
        String errorMessage = "Invalid test method name. The name of the test method should end with " +
                "the Wita CDM version, which has to be tested, e.g. xxxV4 or xxxV7.";
        if (index != -1) {
            String version = methodName.substring(index, methodName.length());
            try {
                return WitaCdmVersion.valueOf(version);
            }
            catch (IllegalArgumentException iae) {
                throw new RuntimeException(errorMessage, iae);
            }
        }
        throw new RuntimeException(errorMessage);
    }

}
