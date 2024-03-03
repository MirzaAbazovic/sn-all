/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.01.14
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;

import de.augustakom.hurrican.model.cc.VoipDn2DnBlockView;
import de.augustakom.hurrican.model.cc.VoipDnPlanView;

public class VoipDnPlanJasperDs extends AbstractCCJasperDS {
    private final VoipDnPlanView plan;
    private Iterator<VoipDn2DnBlockView> blockIterator = Collections.emptyIterator();
    private VoipDn2DnBlockView currentBlock = null;

    public VoipDnPlanJasperDs(final VoipDnPlanView plan) {
        super();
        this.plan = plan;
        init();
    }

    @Override
    public void init() {
        if (plan != null) {
            blockIterator = plan.getSortedVoipDn2DnBlockViews().iterator();
        }
    }

    @Override
    public boolean next() throws JRException {
        final boolean hasNext = blockIterator.hasNext();
        if (hasNext) {
            currentBlock = blockIterator.next();
        }
        return hasNext;
    }

    @Override
    protected Object getFieldValue(final String field) throws JRException {
        if (currentBlock == null) {
            return null;
        }
        switch (field) {
            case "RUFNUMMER":
                return currentBlock.getOnkz() + " " + currentBlock.getDnBase();
            case "BLOCK":
                return currentBlock.toString();
            case "ZENTRALE":
                return currentBlock.getZentrale() ? "ja" : "nein";
            default:
                return null;
        }
    }
}
