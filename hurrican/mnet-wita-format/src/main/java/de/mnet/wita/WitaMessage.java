/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2011 16:10:53
 */
package de.mnet.wita;

import java.io.*;

/**
 * Marker-Interface fuer alle (Haupt-)Objekte, die als WITA Message verschickt und empfangen werden koennen.
 */
public interface WitaMessage extends Serializable {
    // marker interface

    WitaCdmVersion getCdmVersion();

    void setCdmVersion(WitaCdmVersion version);
}
