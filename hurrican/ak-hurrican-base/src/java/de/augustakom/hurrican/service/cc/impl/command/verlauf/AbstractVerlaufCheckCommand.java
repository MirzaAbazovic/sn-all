/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.03.2007 16:00:59
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.service.base.AKServiceCommandChain;
import de.augustakom.common.service.iface.IServiceCommandChainAware;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand;


/**
 * Abstrakte Klasse fuer alle Verlauf-Checks. <br> Die Verlauf-Checks sollen Ueberpruefungen vornehmen, bevor ein
 * Bauauftrag erstellt wird. <br> <br> Die Implementierungen ueberpruefen entsprechende Auftragsdaten und geben das
 * Ergebnis der Pruefung ueber ein Objekt vom Typ <code>ServiceCommandResult</code> zurueck.
 *
 *
 */
public abstract class AbstractVerlaufCheckCommand extends AbstractServiceCommand implements IServiceCommandChainAware {

    private static final Logger LOGGER = Logger.getLogger(AbstractVerlaufCheckCommand.class);

    /**
     * Key fuer die Uebergabe der Session-ID des Users
     */
    public static final String KEY_SESSION_ID = "user.session.id";
    /**
     * Key fuer die Uebergabe der ID des Auftrags, zu dem der Check durchgefuehrt werden soll.
     */
    public static final String KEY_AUFTRAG_ID = "auftrag.id";
    /**
     * Key fuer die Uebergabe der ID des Anlasses, zu dem der Check durchgefuehrt werden soll.
     */
    public static final String KEY_ANLASS_ID = "anlass.id";
    /**
     * Key fuer die Uebergabe des Produkt-Modells des Auftrags.
     */
    public static final String KEY_PRODUKT = "produkt";
    /**
     * Key fuer die Uebergabe des Billing-Auftrag Modells.
     */
    public static final String KEY_BILLING_AUFTRAG = "billing.auftrag";
    /**
     * Key fuer die Uebergabe des Realisierungsdatums vom Bauauftrag.
     */
    public static final String KEY_REAL_DATE = "real.date";

    /**
     * Key fuer den Zugriff auf das Command-Result 'Portierungsart' fuer eine Rufnummer.
     */
    public static final String RESULT_KEY_PORTIERUNGSART = "portierungsart";
    /**
     * Key fuer den Zugriff auf das Command-Result 'in-short-term' (kurzfristige Realisierung).
     */
    public static final String RESULT_KEY_IN_SHORT_TERM = "short.term";

    private AKServiceCommandChain serviceCommandChain = null;

    /**
     * Gibt die Rufnummern des Billing-Auftrags zurueck. <br> Sollten die Rufnummern noch nicht im Command-Context
     * vorhanden sein, werden sie neu geladen, ansonsten werden sie aus diesem geholt. <br> Ist kein Billing-Auftrag
     * vorhanden, wird <code>null</code> zurueck gegeben. <br> <br> Die Rufnummern-Suche ist auf HistLast=1
     * eingeschraenkt!
     *
     * @return Liste mit den Rufnummern des Auftrags (oder null)
     *
     */
    protected List<Rufnummer> getRufnummern() {
        try {
            @SuppressWarnings("unchecked")
            List<Rufnummer> result = (List<Rufnummer>) getContextParameter("order.dn");
            if (CollectionTools.isEmpty(result) && getBillingAuftrag() != null) {
                RufnummerService rs = getBillingService(RufnummerService.class);
                result = rs.findByParam(Rufnummer.STRATEGY_FIND_BY_AUFTRAG_NO_ORIG,
                        new Object[] { getBillingAuftrag().getAuftragNoOrig(), Boolean.TRUE, Boolean.TRUE });

                setContextParameter("order.dn", result);
            }

            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void setServiceCommandChain(AKServiceCommandChain serviceCommandChain) {
        this.serviceCommandChain = serviceCommandChain;
    }

    public AKServiceCommandChain getServiceCommandChain() {
        return serviceCommandChain;
    }

    @Override
    public Object getContextParameter(Object key) {
        return getServiceCommandChain().getCommandContextParameter(key);
    }

    @Override
    public void setContextParameter(Object key, Object value) {
        getServiceCommandChain().setCommandContextParameter(key, value);
    }

    /**
     * Gibt die Auftrags-ID zurueck.
     */
    protected Long getAuftragId() {
        return (Long) getPreparedValue(KEY_AUFTRAG_ID);
    }

    /**
     * Gibt die Anlass-ID zurueck.
     */
    protected Long getAnlassId() {
        return (Long) getPreparedValue(KEY_ANLASS_ID);
    }

    /**
     * Gibt die Session-ID zurueck.
     */
    protected Long getSessionId() {
        return (Long) getPreparedValue(KEY_SESSION_ID);
    }

    /**
     * Gibt das Produkt-Modell zurueck.
     */
    protected Produkt getProdukt() {
        return (Produkt) getPreparedValue(KEY_PRODUKT);
    }

    /**
     * Gibt das BillingAuftrags-Modell zurueck.
     */
    protected BAuftrag getBillingAuftrag() {
        return (BAuftrag) getPreparedValue(KEY_BILLING_AUFTRAG);
    }

    /**
     * Gibt das Realisierungsdatum fuer den Bauauftrag zurueck.
     */
    protected Date getRealDate() {
        return (Date) getPreparedValue(KEY_REAL_DATE);
    }
}


