/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.05.2009 08:38:36
 */
package de.augustakom.hurrican.model.cc.cps;


/**
 * Interface zur Definition von Konstanten aus dem CPS.
 *
 *
 */
public interface ICPSConstants {

    /**
     * Wert definiert eine Success-Meldung ueber das ServiceResponse-Objekt vom CPS (Parameter SOResult).
     */
    public static final int SERVICE_RESPONSE_SORESULT_CODE_SUCCESS = 0;

    /**
     * Wert definiert, dass ein mit 'createSubscriber' uebermittelter Auftrag im CPS bereits existent ist. (Somit keine
     * Ausfuehrung innerhalb des CPS!)
     */
    public static final int SERVICE_RESPONSE_SORESULT_SUB_EXIST = 401;

}


