/*

 * Copyright (c) 2016 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 11.03.2016

 */

package de.mnet.hurrican.startup;

public final class HurricanWebUserSession {

    private HurricanWebUserSession() {
    }

    private static Long sessionId;

    public static synchronized void setSessionId(final Long sessionId) {
        HurricanWebUserSession.sessionId = sessionId;
    }

    public static synchronized Long getSessionId() {
        return HurricanWebUserSession.sessionId;
    }
}
