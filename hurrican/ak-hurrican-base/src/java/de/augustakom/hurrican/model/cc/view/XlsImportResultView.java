/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.04.2012 09:26:50
 */
package de.augustakom.hurrican.model.cc.view;

import java.io.*;
import java.util.*;
import java.util.Map.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;

import de.augustakom.common.tools.messages.AKMessage;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.poi.XlsPoiTool;

/**
 * Container für Fehlermeldungen und Warnungen bei einen XML Import.
 */
public class XlsImportResultView implements Serializable {

    private static final long serialVersionUID = 1L;

    private SortedMap<Integer, List<String>> rowWarnings = new TreeMap<>();
    private SortedMap<Integer, String> rowFailures = new TreeMap<Integer, String>();
    private SortedMap<Integer, String> rowDisplayNames = new TreeMap<Integer, String>();
    private SortedMap<Integer, String> rowMessages = new TreeMap<Integer, String>();

    private int successCount;
    private int ignoredCount;
    private String sheetName;
    private byte[] xlsDataOut;
    private int[] rowDisplayNameColumnIndexes;

    /**
     * Der Index einer identifizierenden Spalte für die Import Zeile (z.B. HVTName).
     *
     * @param sheetName                   der Name des importierten XLS Sheets
     * @param rowDisplayNameColumnIndexes
     */
    public XlsImportResultView(String sheetName, int[] rowDisplayNameColumnIndexes) {
        this.sheetName = sheetName;
        this.rowDisplayNameColumnIndexes = Arrays.copyOf(rowDisplayNameColumnIndexes,
                rowDisplayNameColumnIndexes.length);
    }

    /**
     * @return Anzahl der erfolgreich verarbeiteten Zeilen (inkl. der Zeilen mit Warnungen)
     */
    public int getSuccessCount() {
        return successCount;
    }

    /**
     * @return Anzahl der (erfolgreich) verarbeiteten Zeilen mit Warnung(en)
     */
    public int getWarningCount() {
        return rowWarnings.size();
    }

    /**
     * @return Anzahl der Zeilen mit Fehler (import fehlgeschlagen)
     */
    public int getFailedCount() {
        return rowFailures.size();
    }

    /**
     * Setzt die Fehlermeldung für eine Zeile. Es wird davon ausgegangen das die entsprechende Zeile nicht importiert
     * wurde.
     *
     * @param rowIndex
     * @param row
     * @param message
     */
    public void setFailure4Row(int rowIndex, Row row, String message) {
        setRowDisplayName(rowIndex, row);
        rowWarnings.remove(rowIndex);
        rowFailures.put(rowIndex, message);
    }

    /**
     * @param rowIndex
     * @param row
     * @param singleRowResult
     */
    public void addSingleRowResult(int rowIndex, Row row, SingleRowResult singleRowResult) {
        if (singleRowResult.isIgnored()) {
            ignoredCount++;
        }
        else {
            successCount++;
        }

        if (singleRowResult.rowWarnings.isEmpty() && StringUtils.isBlank(singleRowResult.rowMessage)) {
            return;
        }
        setRowDisplayName(rowIndex, row);
        if (!singleRowResult.rowWarnings.isEmpty()) {
            rowWarnings.put(rowIndex, singleRowResult.rowWarnings);
        }
        if (StringUtils.isNotBlank(singleRowResult.rowMessage)) {
            rowMessages.put(rowIndex, singleRowResult.rowMessage);
        }
    }

    private void setRowDisplayName(Integer rowIndex, Row row) {
        if (rowDisplayNames.containsKey(rowIndex)) {
            return;
        }
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < rowDisplayNameColumnIndexes.length; i++) {
            if (i > 0) {
                str.append(", ");
            }
            str.append(XlsPoiTool.getContentAsString(row, rowDisplayNameColumnIndexes[i]));
        }
        rowDisplayNames.put(rowIndex, str.toString());
    }

    /**
     * @return
     */
    public SortedMap<Integer, String> getRowDisplayNames() {
        return rowDisplayNames;
    }

    /**
     * @param rowIndex
     * @return
     */
    public String getRowDisplayName(Integer rowIndex) {
        return rowDisplayNames.get(rowIndex);
    }

    /**
     * @return
     */
    public SortedMap<Integer, String> getRowFailures() {
        return rowFailures;
    }

    /**
     * @return
     */
    public SortedMap<Integer, List<String>> getRowWarnings() {
        return rowWarnings;
    }

    /**
     * @return
     */
    public SortedMap<Integer, String> getRowMessages() {
        return rowMessages;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("XLS Sheet: ").append(sheetName).append("\n");
        str.append("Importierte Zeilen: ").append(getSuccessCount()).append("\n");
        for (Entry<Integer, String> entry : getRowMessages().entrySet()) {
            str.append("\tZeile ").append(entry.getKey()).append(" (").append(getRowDisplayName(entry.getKey()))
                    .append(") : ").append(entry.getValue()).append("\n");
        }
        str.append(" (davon mit Warnung: ").append(getWarningCount()).append(")\n");
        for (Entry<Integer, List<String>> entry : getRowWarnings().entrySet()) {
            str.append("\tZeile ").append(entry.getKey()).append(" (").append(getRowDisplayName(entry.getKey()))
                    .append(") : ").append(entry.getValue()).append("\n");
        }
        str.append("NICHT importierte Zeilen (Fehler): ").append(getFailedCount()).append("\n");
        for (Entry<Integer, String> entry : getRowFailures().entrySet()) {
            str.append("\tZeile ").append(entry.getKey()).append(" (").append(getRowDisplayName(entry.getKey()))
                    .append(") : ").append(entry.getValue()).append("\n");
        }
        if (ignoredCount > 0) {
            str.append("Ignorierte Zeilen: ").append(ignoredCount).append("\n");
        }
        return str.toString();
    }

    public byte[] getXlsDataOut() {
        return xlsDataOut;
    }

    public void setXlsDataOut(byte[] xlsDataOut) {
        this.xlsDataOut = xlsDataOut;
    }

    /**
     * Import Ergebnis einer einzelnen Zeile.
     */
    public static class SingleRowResult implements Serializable {
        private static final long serialVersionUID = 1L;

        private boolean ignored;
        private List<String> rowWarnings = new ArrayList<>();
        String rowMessage;

        public void setIgnored(boolean ignored) {
            this.ignored = ignored;
        }

        public boolean isIgnored() {
            return ignored;
        }

        public void addWarning(String warning) {
            rowWarnings.add(warning);
        }

        public void addWarnings(AKWarnings warnings) {
            if ((warnings != null) && warnings.isNotEmpty()) {
                for (AKMessage message : warnings.getAKMessages()) {
                    rowWarnings.add(message.getMessage());
                }
            }
        }

        public int getWarningCount() {
            return rowWarnings.size();
        }

        public void setRowMessage(String msg) {
            rowMessage = msg;
        }
    }
}
