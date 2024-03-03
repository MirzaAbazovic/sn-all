/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.06.2004 14:12:24
 */
package de.augustakom.hurrican.service;

import java.util.*;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import de.augustakom.hurrican.service.billing.IBillingService;
import de.augustakom.hurrican.service.cc.ICCService;
import de.augustakom.hurrican.service.exmodules.sap.ISAPService;
import de.augustakom.hurrican.service.internet.IInternetService;


/**
 * Abstrakter TestCase fuer alle TestCases der Hurrican-Services. Nur noch ein Dummy vorhanden.
 *
 *
 * @deprecated
 */
@Deprecated
public abstract class AbstractHurricanServiceTestCase extends TestCase {

    public AbstractHurricanServiceTestCase() {
        super();
    }

    public AbstractHurricanServiceTestCase(String arg0) {
        super(arg0);
    }

    /**
     * Gibt die Session-ID des Authentication-Systems zurueck.
     *
     * @return Session-ID
     */
    protected Long getSessionId() {
        return 42L;
    }

    /**
     * Sucht nach einem Billing-Service mit dem angegebenen Namen und Typ und gibt diesen zurueck.
     *
     * @param type Typ des gesuchten Services
     * @return der angeforderte Service
     */
    protected <T extends IBillingService> T getBillingService(Class<T> type) {
        return null;
    }

    /**
     * Sucht nach einem CC-Service mit dem angegebenen Namen und Typ und gibt diesen zurueck.
     *
     * @param type Typ des gesuchten Services
     * @return der angeforderte Service
     */
    protected <T extends ICCService> T getCCService(Class<T> type) {
        return null;
    }

    /**
     * Sucht nach einem Internet-Service des angegebenen Typs.
     *
     * @param type Typ des gesuchten Services - gleichzeitig der Name
     * @return
     */
    protected <T extends IInternetService> T getInternetService(Class<T> type) {
        return null;
    }

    /**
     * Sucht nach einem SAP-Service des angegebenen Typs.
     *
     * @param type Typ des gesuchten Services - gleichzeitig der Name
     * @return
     */
    protected <T extends ISAPService> T getSAPService(Class<T> type) {
        return null;
    }

    /**
     * Annahme, dass die Collection nicht <code>null</code> ist und zumindest einen Eintrag enthaelt.
     *
     * @param msg
     * @param coll
     */
    public void assertNotEmpty(String msg, Collection<?> coll) {
        if (coll == null) {
            throw new AssertionFailedError(msg);
        }

        if (coll.isEmpty()) {
            throw new AssertionFailedError(msg);
        }
    }

    /**
     * Annahme, dass die Collection <code>null</code> ist oder keinen Eintrag enthaelt.
     *
     * @param msg
     * @param coll
     */
    public void assertEmpty(String msg, Collection<?> coll) {
        if (coll != null) {
            if (!coll.isEmpty()) {
                throw new AssertionFailedError(msg);
            }
        }
    }

}
