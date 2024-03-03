/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.03.2005 14:44:53
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.VoipDnPlanView;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.VoIPService;

/**
 * Jasper-DataSource, um die Rufnummern zu einem Auftrag zu ermitteln.
 *
 *
 */
public class RufnummernJasperDS extends AbstractCCJasperDS {

    private static final Logger LOGGER = Logger.getLogger(RufnummernJasperDS.class);

    private Long auftragNoOrig = null;
    private Long auftragId = null;

    private Iterator<Rufnummer> dataIterator = null;
    private Rufnummer currentData = null;

    private List<AuftragVoipDNView> voipDataList = null;
    private AuftragVoipDNView currentVoipData = null;

    /**
     * Konstruktor mit Angabe der Billing-Auftragsnummer und der Hurrican-Auftragsnummer.
     *
     * @param auftragNoOrig
     * @param auftragId
     */
    public RufnummernJasperDS(Long auftragNoOrig, Long auftragId) {
        super();
        this.auftragNoOrig = auftragNoOrig;
        this.auftragId = auftragId;
        init();
    }

    /* Laedt die Rufnummern-Daten zu einem Auftrag. */
    protected void init() {
        try {
            if (auftragNoOrig != null) {
                RufnummerService rs = getBillingService(RufnummerService.class);
                List<Rufnummer> rufnummern = rs.findByParam(Rufnummer.STRATEGY_FIND_BY_AUFTRAG_NO_ORIG,
                        new Object[] { auftragNoOrig, Boolean.TRUE });

                if (rufnummern != null) {
                    dataIterator = rufnummern.iterator();
                }
            }
            else {
                LOGGER.warn("RufnummernJasperDS: Keine Billing-Auftragsnummer angegeben!");
            }

            if (auftragId != null) {
                VoIPService voipService = getCCService(VoIPService.class);
                voipDataList = voipService.findVoIPDNView(auftragId);
            }
            else {
                LOGGER.warn("RufnummernJasperDS: Keine Hurrican-Auftragsnummer angegeben!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    public boolean next() throws JRException {
        boolean hasNext = false;

        if (dataIterator != null) {
            hasNext = dataIterator.hasNext();
            if (hasNext) {
                currentData = dataIterator.next();
                currentVoipData = getVoipData(currentData);
            }
        }
        return hasNext;
    }

    private AuftragVoipDNView getVoipData(Rufnummer rufnummer) {
        if (rufnummer != null && voipDataList != null) {
            for (AuftragVoipDNView voipData : voipDataList) {
                if (rufnummer.getDnBase().equals(voipData.getDnBase())) {
                    return voipData;
                }
            }
        }
        return null;
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanJasperDS#getFieldValue(java.lang.String)
     */
    @Override
    protected Object getFieldValue(String field) throws JRException {
        if (currentData != null) {
            switch (field) {
                case "ONKZ":
                    return currentData.getOnKz();
                case "DN_BASE":
                    return currentData.getDnBase();
                case "DIRECT_DIAL":
                    return currentData.getDirectDial();
                case "RANGE_FROM":
                    return currentData.getRangeFrom();
                case "RANGE_TO":
                    return currentData.getRangeTo();
                case "IS_MAIN":
                    return currentData.isMainNumber();
                case "PORT_MODE":
                    return currentData.getPortMode();
                case "REAL_DATE":
                    return currentData.getRealDate();
                case "VALID_FROM":
                    return currentData.getGueltigVon();
                case "VALID_TO":
                    return currentData.getGueltigBis();
                case "REMARKS":
                    return currentData.getRemarks();
                default:
                    break;
            }
        }
        if (currentVoipData != null) {
            VoipDnPlanView latestVoipDnPlan = currentVoipData.getLatestVoipDnPlanView();
            switch (field) {
                case "SIP_PASSWORD":
                    return currentVoipData.getSipPassword();
                case "SIP_DOMAIN":
                    return (currentVoipData.getSipDomain() != null)
                            ? currentVoipData.getSipDomain().getStrValue() : null;
                case "SIP_LOGIN":
                    return (latestVoipDnPlan == null) ? null : latestVoipDnPlan.getSipLogin();
                case "HAUPTRUFNR":
                    return (latestVoipDnPlan == null) ? null : latestVoipDnPlan.getSipHauptrufnummer();
                default:
                    break;
            }
        }
        return null;
    }

}
