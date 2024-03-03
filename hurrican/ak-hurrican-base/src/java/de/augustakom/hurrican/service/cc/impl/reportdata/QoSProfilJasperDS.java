/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.11.2013
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.service.cc.QoSProfilService;

/**
 * Jasper-DataSource, um die QoS-Profil-Parameter zu einem Auftrag zu ermitteln.
 */
public class QoSProfilJasperDS extends AbstractCCJasperDS {
    private static final Logger LOGGER = Logger.getLogger(QoSProfilJasperDS.class);

    private Long auftragId = null;
    private Date validDate = null;

    private Iterator<QoSProfilService.QosProfileWithValidFromAndDownstream> dataIterator;
    private QoSProfilService.QosProfileWithValidFromAndDownstream currentData;

    public QoSProfilJasperDS(Long auftragId) {
        super();
        this.auftragId = auftragId;
        this.validDate = new Date();
        init();
    }

    public QoSProfilJasperDS(Long auftragId, Date validDate) {
        super();
        this.auftragId = auftragId;
        this.validDate = (validDate != null) ? validDate : new Date();
        init();
    }

    @Override
    protected void init() {
        try {
            final QoSProfilService.QosProfileWithValidFromAndDownstream qosProfilAndDownStream =
                    getCCService(QoSProfilService.class).getQoSProfilDownStreamAndValidDate4Auftrag(auftragId,
                            validDate);
            if (qosProfilAndDownStream != null) {
                dataIterator = Arrays.asList(qosProfilAndDownStream).iterator();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public boolean next() throws JRException {
        boolean hasNext = false;

        if (dataIterator != null) {
            hasNext = dataIterator.hasNext();
            if (hasNext) {
                currentData = dataIterator.next();
            }
        }
        return hasNext;
    }

    @Override
    protected Object getFieldValue(String field) throws JRException {
        if (currentData == null) {
            return null;
        }
        switch (field) {
            case "QOS_PROFIL":
                return (currentData.qosProfile != null) ? currentData.qosProfile.getStrValue() : null;
            case "QOS_PRIO":
                return (currentData.qosProfile != null) ? currentData.qosProfile.getLongValue() : null;
            case "QOS_DOWNSTREAM":
                return currentData.downstream;
            case "AKTIV_AB":
                return currentData.validFrom;
            default:
                return null;
        }
    }
}
