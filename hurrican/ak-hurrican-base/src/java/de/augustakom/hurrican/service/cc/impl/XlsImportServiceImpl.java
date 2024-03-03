/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.04.2012 16:10:02
 */
package de.augustakom.hurrican.service.cc.impl;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzug;
import de.augustakom.hurrican.model.cc.view.XlsImportResultView;
import de.augustakom.hurrican.model.cc.view.XlsImportResultView.SingleRowResult;
import de.augustakom.hurrican.service.base.exceptions.ImportException;
import de.augustakom.hurrican.service.cc.XlsImportService;
import de.augustakom.hurrican.service.cc.impl.command.AbstractXlsImportCommand;
import de.augustakom.hurrican.service.cc.impl.command.ImportFTTXBaugruppenCommand;
import de.augustakom.hurrican.service.cc.impl.command.ImportFTTXKVZRangierungenCommand;
import de.augustakom.hurrican.service.cc.impl.command.ImportFTTXKvzAdresseRowCommand;
import de.augustakom.hurrican.service.cc.impl.command.ImportFTTXKvzRowCommand;
import de.augustakom.hurrican.service.cc.impl.command.ImportHvtAdresseCommand;
import de.augustakom.hurrican.service.cc.impl.command.ImportHvtUmzugDetailsCommand;
import de.augustakom.hurrican.service.cc.impl.command.ImportKvzAdresseCommand;
import de.augustakom.hurrican.service.cc.impl.command.ImportUevtStifteCommand;
import de.mnet.common.service.locator.ServiceLocator;

/**
 * XlsImportService Impl.
 *
 *
 */
public class XlsImportServiceImpl extends DefaultCCService implements XlsImportService {
    private static final Logger LOGGER = Logger.getLogger(XlsImportServiceImpl.class);

    @Autowired
    private ServiceLocator serviceLocator;

    @Override
    public XlsImportResultView[] importFttxKvz(byte[] xlsData, Long sessionId) throws ImportException {
        IServiceCommand kvzRowCmdBean = serviceLocator.getCmdBean(ImportFTTXKvzRowCommand.class);
        kvzRowCmdBean.prepare(ImportFTTXKvzRowCommand.PARAM_SESSION_ID, sessionId);
        XlsImportResultView importResult[] = new XlsImportResultView[2];
        importResult[0] = executeImport(xlsData, 0, 2, new int[] { 1 }, kvzRowCmdBean, true);

        IServiceCommand kvzAdrRowCmdBean = serviceLocator.getCmdBean(ImportFTTXKvzAdresseRowCommand.class);
        kvzRowCmdBean.prepare(ImportFTTXKvzRowCommand.PARAM_SESSION_ID, sessionId);
        importResult[1] = executeImport(xlsData, 1, 2, new int[] { 0, 1 }, kvzAdrRowCmdBean, true);
        LOGGER.info("importFttxKvz: " + Arrays.toString(importResult));
        return importResult;
    }

    @Override
    public XlsImportResultView[] importUevtStifte(byte[] xlsData, Long sessionId) throws ImportException {
        IServiceCommand cmdBean = serviceLocator.getCmdBean(ImportUevtStifteCommand.class);
        cmdBean.prepare(ImportUevtStifteCommand.PARAM_SESSION_ID, sessionId);

        XlsImportResultView importResult = executeImport(xlsData, 0, 2, new int[] { 0 }, cmdBean, true);
        LOGGER.info("importUevtStifte: " + importResult);
        return new XlsImportResultView[] { importResult };
    }

    private XlsImportResultView executeImport(byte[] xlsData, int sheetNumber, int startRow,
            int[] rowDisplayNameColumnIndexes, IServiceCommand cmdBean,
            boolean transactionPerLine) throws ImportException {
        return executeImport(xlsData, sheetNumber, startRow, rowDisplayNameColumnIndexes,
                cmdBean, transactionPerLine, 0);
    }

    private XlsImportResultView executeImport(byte[] xlsData, int sheetNumber, int startRow,
            int[] rowDisplayNameColumnIndexes, IServiceCommand cmdBean,
            boolean transactionPerLine, int colForIsEmptyCheck) throws ImportException {
        Sheet sheet = loadExcelFile(xlsData, sheetNumber);
        XlsImportResultView importResult = new XlsImportResultView(sheet.getSheetName(),
                rowDisplayNameColumnIndexes);

        int maxRows = sheet.getLastRowNum();
        LOGGER.info(String.format("Import Service: Excel sheet hat insgesamt %s Zeilen.", maxRows));
        for (int rowIndex = startRow; rowIndex <= maxRows; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (XlsPoiTool.isEmpty(row, colForIsEmptyCheck)) {
                // skip empty rows ...
                continue;
            }
            int xlsRowIndex = rowIndex + 1; // XLS Zeilennummer startet mit
            // 1
            try {
                cmdBean.prepare(AbstractXlsImportCommand.PARAM_IMPORT_ROW, row);
                SingleRowResult result = (SingleRowResult) cmdBean.execute();
                importResult.addSingleRowResult(xlsRowIndex, row, result);
            }
            catch (Exception e) {
                LOGGER.error("Fehler in Zeile " + xlsRowIndex, e);
                importResult.setFailure4Row(xlsRowIndex, row, e.getMessage());
                if (!transactionPerLine) {
                    // nicht die Zeile ist transaktional, sondern der gesamte Import
                    throw new ImportException(String.format("Fehler in Zeile %d!%nDetails: %s", xlsRowIndex,
                            e.getMessage()));
                }
            }
            if (rowIndex % 1000 == 0) {
                LOGGER.info(String.format("Import Service: Verarbeitung bis Zeile %s abgeschlossen.", rowIndex));
            }
        }

        /*
         * Das Excel-Input-Sheet kann je Row durch das Command modifiziert werden und hier wird das modifizierte Sheet
         * als importResult zurueckgegeben.
         */
        try {
            byte[] out = XlsPoiTool.writeWorkbook(sheet.getWorkbook());
            importResult.setXlsDataOut(out);
        }
        catch (IOException e) {
            throw new ImportException("Fehler beim Schreiben des Excel-Sheets", e);
        }
        return importResult;
    }

    private Sheet loadExcelFile(byte[] xlsData, int sheetNumber) throws ImportException {
        try {
            return XlsPoiTool.loadExcelFile(xlsData, sheetNumber);
        }
        catch (Exception e) {
            throw new ImportException("Fehler beim Laden der XLS Daten", e);
        }
    }

    @Override
    public XlsImportResultView[] importBaugruppenAndPorts(byte[] xlsData, Long sessionId) throws ImportException {
        IServiceCommand cmdBean = serviceLocator.getCmdBean(ImportFTTXBaugruppenCommand.class);

        XlsImportResultView importResult = executeImport(xlsData, 0, 2, new int[] { 0 }, cmdBean, true);
        LOGGER.info("importBaugruppenAndPorts: " + importResult);
        return new XlsImportResultView[] { importResult };
    }

    @Override
    @CcTxRequiresNew
    public XlsImportResultView[] importFttcKVZRangierungen(byte[] xlsData, Long sessionId) throws ImportException {
        IServiceCommand cmdBean = serviceLocator.getCmdBean(ImportFTTXKVZRangierungenCommand.class);
        cmdBean.prepare(ImportFTTXKVZRangierungenCommand.PARAM_AK_USER, getAKUserBySessionIdSilent(sessionId));
        cmdBean.prepare(ImportFTTXKVZRangierungenCommand.PARAM_SESSION_ID, sessionId);

        XlsImportResultView importResult = executeImport(xlsData, 0, 2, new int[] { 0 }, cmdBean, false);
        LOGGER.info("importKvzRangierungen: " + importResult);
        return new XlsImportResultView[] { importResult };
    }

    @Override
    public XlsImportResultView[] importKvzAdressen(byte[] xlsData) throws ImportException {
        IServiceCommand cmdBean = serviceLocator.getCmdBean(ImportKvzAdresseCommand.class);
        XlsImportResultView importResult = executeImport(xlsData, 0, 1, new int[] { ImportKvzAdresseCommand.COL_ONKZ,
                ImportKvzAdresseCommand.COL_ASB, ImportKvzAdresseCommand.COL_KVZNR }, cmdBean, true);
        LOGGER.info("importKvzAdressen: " + importResult);
        return new XlsImportResultView[] { importResult };
    }

    @Override
    public XlsImportResultView[] importHvtAdressen(byte[] xlsData) throws ImportException {
        IServiceCommand cmdBean = serviceLocator.getCmdBean(ImportHvtAdresseCommand.class);
        XlsImportResultView importResult = executeImport(xlsData, 0, 1,
                new int[] { ImportHvtAdresseCommand.COL_ONKZ, ImportHvtAdresseCommand.COL_ASB }, cmdBean, true);
        LOGGER.info("importHvtAdressen: " + importResult);
        return new XlsImportResultView[] { importResult };
    }

    @Override
    public XlsImportResultView[] importHvtUmzugDetails(HvtUmzug hvtUmzug, byte[] xlsData) throws ImportException {
        final IServiceCommand cmdBean = serviceLocator.getCmdBean(ImportHvtUmzugDetailsCommand.class);
        cmdBean.prepare(ImportHvtUmzugDetailsCommand.PARAM_HVT_UMZUG, hvtUmzug);

        final XlsImportResultView importResult = executeImport(xlsData, 0, 1,
                new int[] { HvtUmzugServiceImpl.COL_CARRIER_ID }, cmdBean, false,
                HvtUmzugServiceImpl.COL_UEVT_STIFT_ALT);
        LOGGER.info("importHvtUmzugDetails: " + importResult);
        return new XlsImportResultView[] { importResult };
    }
}
