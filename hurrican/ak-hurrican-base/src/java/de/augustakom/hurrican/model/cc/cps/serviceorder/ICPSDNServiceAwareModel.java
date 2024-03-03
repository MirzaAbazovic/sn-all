/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2009 11:16:23
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import java.util.*;


/**
 * Interface fuer CPS-Datenmodelle, die eine Kenntnis ueber Rufnummernleistungen besitzen.
 *
 *
 */
public interface ICPSDNServiceAwareModel {

    /**
     * Uebergibt dem Modell eine Liste mit Rufnummernleistungen.
     *
     * @param dnServices the dnServices to set
     */
    public void setDnServices(List<CPSDNServiceData> dnServices);

}


