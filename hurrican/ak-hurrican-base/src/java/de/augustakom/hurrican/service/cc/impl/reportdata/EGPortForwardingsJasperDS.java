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
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.PortForwarding;


/**
 * Jasper DS fuer die Port-Forwardings eines Endgeraets.
 *
 *
 */
public class EGPortForwardingsJasperDS extends AbstractCCJasperDS {

    private EGConfig egConfig = null;
    private PortForwarding currentData = null;
    private Iterator<PortForwarding> dataIterator = null;

    /**
     * @param egConfig
     * @throws AKReportException
     */
    public EGPortForwardingsJasperDS(EGConfig egConfig) throws AKReportException {
        super();
        this.egConfig = egConfig;
        init();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init()
     */
    @Override
    protected void init() throws AKReportException {
        if ((egConfig != null) && CollectionTools.isNotEmpty(egConfig.getPortForwardings())) {
            List<PortForwarding> portForwardings = new ArrayList<PortForwarding>();
            for (PortForwarding portForwarding : egConfig.getPortForwardings()) {
                if (BooleanTools.nullToFalse(portForwarding.getActive())) {
                    portForwardings.add(portForwarding);
                }
            }

            this.dataIterator = portForwardings.iterator();
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
        if ("PROTOKOLL".equals(field)) {
            return currentData.getTransportProtocol();
        }
        else if ("SOURCE_IP".equals(field)) {
            return (currentData.getSourceIpAddressRef() != null) ?
                    currentData.getSourceIpAddressRef().getAddress() : null;
        }
        else if ("SOURCE_PORT".equals(field)) {
            return currentData.getSourcePort();
        }
        else if ("DEST_IP".equals(field)) {
            return (currentData.getDestIpAddressRef() != null) ?
                    currentData.getDestIpAddressRef().getAddress() : null;
        }
        else if ("DEST_PORT".equals(field)) {
            return currentData.getDestPort();
        }
        return null;
    }

}


