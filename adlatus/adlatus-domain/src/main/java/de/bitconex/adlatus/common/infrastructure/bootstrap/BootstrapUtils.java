package de.bitconex.adlatus.common.infrastructure.bootstrap;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

public class BootstrapUtils {

    private static final String X_FORWARD_HEADER = "X-Forwarded-For";

    public static String extractIpAddress(HttpServletRequest request) {
        String forwardHeader = request.getHeader(X_FORWARD_HEADER);
        String result = "";

        if (StringUtils.isNotEmpty(forwardHeader)) {
            String ipAddress = StringUtils.deleteWhitespace(forwardHeader);
            if (ipAddress.contains(",")) {
                result = ipAddress.substring(0, ipAddress.indexOf(','));
            } else {
                result = ipAddress;
            }
        } else {
            result = request.getRemoteAddr();
        }
        return result;
    }
}
