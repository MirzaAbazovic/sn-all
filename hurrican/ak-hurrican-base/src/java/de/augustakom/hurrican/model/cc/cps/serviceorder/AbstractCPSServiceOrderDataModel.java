/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.04.2009 14:48:38
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import java.io.*;


/**
 * Abstrakte Klasse fuer die (Java) Modell-Abbildung der ServiceOrder-Data Struktur fuer CPS-Provisionierungen. <br>
 * Ueber die Java-Modelle wird die vorgesehene XML-Struktur fuer die Uebergabe an den CPS abgebildet. Die Generierung
 * des XMLs aus den Java-Modellen erfolgt durch einen Parser wie z.B. XStream. <br>
 *
 *
 */
public abstract class AbstractCPSServiceOrderDataModel implements Serializable {

    /**
     * Datums-Format fuer CPS ServiceOrder-Data, um "Jahr-Monat-Tag Stunde:Minute" darzustellen (yyyy-MM-dd HH:mm).
     */
    protected static final String DEFAULT_CPS_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    protected static final String DEFAULT_CPS_DATE_TIME_FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";

    private static final long serialVersionUID = -6104540319693782494L;
}


