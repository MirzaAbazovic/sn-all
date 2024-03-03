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
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EndgeraetAcl;


/**
 * Jasper DS fuer die ACLs eines Endgeraets.
 *
 *
 */
public class EGAclJasperDS extends AbstractCCJasperDS {

    private EGConfig egConfig = null;
    private EndgeraetAcl currentData = null;
    private Iterator<EndgeraetAcl> dataIterator = null;

    /**
     * @param egConfig
     * @throws AKReportException
     */
    public EGAclJasperDS(EGConfig egConfig) throws AKReportException {
        super();
        this.egConfig = egConfig;
        init();
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.reportdata.AbstractCCJasperDS#init()
     */
    protected void init() throws AKReportException {
        if (egConfig != null && CollectionTools.isNotEmpty(egConfig.getEndgeraetAcls())) {
            this.dataIterator = egConfig.getEndgeraetAcls().iterator();
        }
    }

    /**
     * @see net.sf.jasperreports.engine.JRDataSource#next()
     */
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
    protected Object getFieldValue(String field) throws JRException {
        if ("ACL_NAME".equals(field)) {
            return currentData.getName();
        }
        else if ("ACL_ROUTERTYP".equals(field)) {
            return currentData.getRouterTyp();
        }
        return null;
    }

}


