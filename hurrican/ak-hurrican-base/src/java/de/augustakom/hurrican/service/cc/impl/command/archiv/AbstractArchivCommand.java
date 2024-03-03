/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.04.2007 11:29:38
 */
package de.augustakom.hurrican.service.cc.impl.command.archiv;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.RInfo;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.RechnungsService;
import de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand;


/**
 * Abstrakte Implemetierung eines Service-Commands
 *
 *
 */
public abstract class AbstractArchivCommand extends AbstractServiceCommand {

    private static final Logger LOGGER = Logger.getLogger(AbstractArchivCommand.class);

    public static final String KUNDE__NO = "kunde.no.orig";
    public static final String AUFTRAG_ID = "cc.auftrag.id";
    public static final String DEBITOR_NO = "debitor.no";
    public static final String DOK_KLASSE = "dokumenten.klasse";
    public static final String ORDER__NO = "billing.auftrag.no";
    public static final String ARCHIV_PARAMETER_ID = "archiv.parameter.id";

    /**
     * ermittelt die Debitorennummer fuer die uebergebene Billing-Auftrag-NO
     *
     * @param oNo
     * @return Liste mit Inhalt vom Typ LONG
     * @throws ServiceNotFoundException
     */
    protected List<String> getDebitor4Auftrag(Long oNo) throws ServiceNotFoundException {
        try {
            if (oNo != null) {
                BillingAuftragService bAuftragService = getBillingService(BillingAuftragService.class);
                RechnungsService rs = getBillingService(RechnungsService.class);
                String debNo = null;
                BAuftrag bAuftrag = bAuftragService.findAuftrag(oNo);
                if (bAuftrag != null) {
                    RInfo rinfo = rs.findRInfo(bAuftrag.getRechInfoNoOrig());
                    debNo = (rinfo != null) ? rinfo.getExtDebitorId() : null;
                }
                if (StringUtils.isNotBlank(debNo)) {
                    List<String> rInfos = new ArrayList<String>();
                    rInfos.add(0, debNo);
                    return rInfos;
                }
                else {
                    throw new Exception("Es konnte kein Debitor ermittelt werden(keine Billing-Auftrag NO vorhanden!");
                }
            }
            else {
                throw new Exception("Es konnte kein Debitor ermittelt werden(keine Billing-Auftrag NO vorhanden!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceNotFoundException(e);
        }
    }

    /**
     * Gibt die Auftrags-ID zurueck.
     */
    protected Long getAuftragId() {
        return (Long) getPreparedValue(AUFTRAG_ID);
    }

    /**
     * Gibt die KUNDE__NO_ORIG zurueck.
     */
    protected Long getKundeNo() {
        return (Long) getPreparedValue(KUNDE__NO);
    }

    /**
     * Gibt die DEBITOREN_NO zurueck.
     */
    protected Long getDebitorNo() {
        return (Long) getPreparedValue(DEBITOR_NO);
    }

    /**
     * Gibt die Dokumentklasse von ScanView zurueck.
     */
    protected Integer getDokType() {
        return (Integer) getPreparedValue(DOK_KLASSE);
    }


    /**
     * Gibt die ORDER__NO zurück
     */
    protected Long getOrderNo() {
        return (Long) getPreparedValue(ORDER__NO);
    }

    /**
     * Gibt den Suchparameter des Archivierungssystems zurück
     */
    protected Long getParameterId() {
        return (Long) getPreparedValue(ARCHIV_PARAMETER_ID);
    }

    /**
     * Überschreibt die ARCHIV_PARAMETER_ID
     */
    protected void setParameterId(Long wert) {
        prepare(ARCHIV_PARAMETER_ID, wert);
    }
}


