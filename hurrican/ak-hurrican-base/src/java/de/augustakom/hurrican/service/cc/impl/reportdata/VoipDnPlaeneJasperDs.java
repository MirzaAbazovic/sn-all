/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.01.14
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.model.cc.VoipDnPlanView;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.service.cc.VoIPService;

public class VoipDnPlaeneJasperDs extends AbstractCCJasperDS {
    private static final Logger LOGGER = Logger.getLogger(VoipDnPlaeneJasperDs.class);

    private final Long auftragId;
    private Iterator<VoipDnPlanView> planIterator = Collections.emptyIterator();
    private VoipDnPlanView currentPlan = null;

    public VoipDnPlaeneJasperDs(final Long auftragId) {
        super();
        this.auftragId = auftragId;
        init();
    }

    @Override
    public void init() {
        List<VoipDnPlanView> plaene = new ArrayList<>();

        try {
            final VoIPService voipService = getCCService(VoIPService.class);
            for (final AuftragVoipDNView dnView : voipService.findVoIPDNView(auftragId)) {
                plaene.addAll(dnView.getValidVoipDnPlanViews());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        planIterator = plaene.iterator();
    }

    @Override
    public boolean next() throws JRException {
        final boolean hasNext = planIterator.hasNext();
        if (hasNext) {
            currentPlan = planIterator.next();
        }
        return hasNext;
    }

    @Override
    protected Object getFieldValue(final String field) throws JRException {
        if (currentPlan == null) {
            return null;
        }
        switch (field) {
            case "GUELTIG_AB":
                return currentPlan.getGueltigAb();
            case "VOIP_DN_PLAN":
                return currentPlan;
            case "HAUPTRUFNR":
                return currentPlan.getSipHauptrufnummer();
            case "SIP_LOGIN":
                return currentPlan.getSipLogin();
            default:
                return null;
        }
    }
}
