/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.01.2006 11:40:25
 */
package de.augustakom.hurrican.service.billing.impl;

import java.util.*;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.iface.FindDAO;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.common.tools.reports.jasper.AKJasperReportContext;
import de.augustakom.common.tools.reports.jasper.AKJasperReportHelper;
import de.augustakom.hurrican.dao.billing.ArchPrintDAO;
import de.augustakom.hurrican.dao.billing.BLZDAO;
import de.augustakom.hurrican.dao.billing.BillDAO;
import de.augustakom.hurrican.dao.billing.FinanzDAO;
import de.augustakom.hurrican.dao.billing.RInfoDAO;
import de.augustakom.hurrican.model.billing.ArchPrintSet;
import de.augustakom.hurrican.model.billing.BLZ;
import de.augustakom.hurrican.model.billing.Finanz;
import de.augustakom.hurrican.model.billing.RInfo;
import de.augustakom.hurrican.model.billing.view.BillRunView;
import de.augustakom.hurrican.model.billing.view.RInfo2KundeView;
import de.augustakom.hurrican.model.shared.view.RInfoAdresseView;
import de.augustakom.hurrican.model.shared.view.RInfoQuery;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.RechnungsService;
import de.augustakom.hurrican.service.billing.impl.reportdata.PrintStatisticJasperDS;

/**
 * Implementierung von <code>RechnungsService</code>.
 *
 *
 */
@BillingTx
public class RechnungsServiceImpl extends DefaultBillingService implements RechnungsService {

    private static final Logger LOGGER = Logger.getLogger(RechnungsServiceImpl.class);

    private ArchPrintDAO archPrintDAO;
    private FinanzDAO finanzDAO;
    private BLZDAO blzDAO;
    private BillDAO billDAO;

    @Override
    public List<RInfo2KundeView> findRInfo2KundeViews(Long billRunId, String year, String month) throws FindException {
        try {
            return ((RInfoDAO) getDAO()).findRInfo2KundeViews(billRunId, year, month);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public RInfo2KundeView findRInfo2KundeView4BillId(String billId, String year, String month) throws FindException {
        if (StringUtils.isBlank(billId) || StringUtils.isBlank(year) || StringUtils.isBlank(month)) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }

        try {
            return ((RInfoDAO) getDAO()).findRInfo2KundeView4BillId(billId, year, month);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public RInfo findRInfo(Long rinfoNo) throws FindException {
        if (rinfoNo == null) {
            return null;
        }
        try {
            return ((FindDAO) getDAO()).findById(rinfoNo, RInfo.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Date findInvoiceDate(Long billRunId) throws FindException {
        if (billRunId == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            return ((RInfoDAO) getDAO()).findInvoiceDate(billRunId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<ArchPrintSet> findArchPrintSets() throws FindException {
        try {
            return getArchPrintDAO().findAll(ArchPrintSet.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Integer[] sumPagesAndBills(Long printSetNo, String groupName) throws FindException {
        if ((printSetNo == null) || StringUtils.isBlank(groupName)) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }

        try {
            return getArchPrintDAO().sumPagesAndBills(printSetNo, groupName);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public JasperPrint reportPrintStatistic(Long printSetNo, String billCycle) throws AKReportException {
        try {
            AKJasperReportContext ctx = new AKJasperReportContext(
                    "de/augustakom/hurrican/reports/stats/PrintStatistic.jasper", null, new PrintStatisticJasperDS(
                    printSetNo, billCycle)
            );

            AKJasperReportHelper jrh = new AKJasperReportHelper();
            return jrh.createReport(ctx);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException(e.getMessage(), e);
        }
    }

    @Override
    public Finanz findFinanz(Long finanzNo) throws FindException {
        if (finanzNo == null) {
            return null;
        }
        try {
            return getFinanzDAO().findById(finanzNo, Finanz.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public BLZ findBLZ(Long blz) throws FindException {
        if (blz == null) {
            return null;
        }
        try {
            return getBlzDAO().findById(blz, BLZ.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<RInfo> findRInfos4KundeNo(Long kundeNo) throws FindException {
        if (kundeNo == null) {
            return null;
        }
        try {
            List<RInfo> result = ((RInfoDAO) getDAO()).findRInfo4KundeNo(kundeNo);
            return (CollectionTools.isNotEmpty(result)) ? result : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<RInfoAdresseView> findKundeByRInfoQuery(RInfoQuery query) throws FindException {
        if ((query == null) || query.isEmpty()) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            List<RInfoAdresseView> result = ((RInfoDAO) getDAO()).findKundenByRInfoQuery(query);
            if (CollectionTools.isNotEmpty(result)) {
                return result;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
        return null;
    }

    @Override
    public List<BillRunView> findBillRunViews() throws FindException {
        try {
            return getBillDAO().findBillRunViews();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    public ArchPrintDAO getArchPrintDAO() {
        return archPrintDAO;
    }

    public void setArchPrintDAO(ArchPrintDAO archPrintDAO) {
        this.archPrintDAO = archPrintDAO;
    }

    public FinanzDAO getFinanzDAO() {
        return finanzDAO;
    }

    public void setFinanzDAO(FinanzDAO finanzDAO) {
        this.finanzDAO = finanzDAO;
    }

    public BLZDAO getBlzDAO() {
        return blzDAO;
    }

    public void setBlzDAO(BLZDAO blzDAO) {
        this.blzDAO = blzDAO;
    }

    public BillDAO getBillDAO() {
        return billDAO;
    }

    public void setBillDAO(BillDAO billDAO) {
        this.billDAO = billDAO;
    }

}
