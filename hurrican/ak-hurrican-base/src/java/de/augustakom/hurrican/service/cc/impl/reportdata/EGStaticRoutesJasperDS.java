/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.10.2009 15:05:41
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import java.util.*;
import net.sf.jasperreports.engine.JRException;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.Routing;


/**
 * Jasper DS fuer die ACLs eines Endgeraets.
 *
 *
 */
public class EGStaticRoutesJasperDS extends AbstractCCJasperDS {

    private EG2Auftrag eg2Auftrag = null;
    private Routing currentData = null;
    private Iterator<Routing> dataIterator = null;

    /**
     * @param egConfig
     * @throws AKReportException
     */
    public EGStaticRoutesJasperDS(EG2Auftrag eg2Auftrag) throws AKReportException {
        super();
        this.eg2Auftrag = eg2Auftrag;
        init();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init()
     */
    @Override
    protected void init() throws AKReportException {
        if ((eg2Auftrag != null) && CollectionTools.isNotEmpty(eg2Auftrag.getRoutings())) {
            this.dataIterator = eg2Auftrag.getRoutings().iterator();
        }
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
    @Override
    public boolean next() throws JRException {
        boolean hasNext = false;

        if (this.dataIterator != null) {
            hasNext = this.dataIterator.hasNext();
            if (hasNext) {
                this.currentData = this.dataIterator.next();
            }
        }
        return hasNext;
    }

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanJasperDS#getFieldValue(java.lang.String)
     */
    @Override
    protected Object getFieldValue(String field) throws JRException {
        if ("DEST_IP".equals(field)) {
            return currentData.getDestinationAdressRef().getAddress();
        }
        else if ("NEXT_HOP".equals(field)) {
            return currentData.getNextHop();
        }
        return null;
    }

}


