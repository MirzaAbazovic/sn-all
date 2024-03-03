/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.11.2010 18:08:25
 */
package de.mnet.migration.base;

import static org.testng.Assert.*;

import java.io.*;
import java.sql.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowCountCallbackHandler;

public class SqlResultDumpRowCallbackHandler extends RowCountCallbackHandler {
    static final Logger LOGGER = Logger.getLogger(SqlResultDumpRowCallbackHandler.class);
    private File resultFile;
    private OutputStreamWriter fileWriter;

    public SqlResultDumpRowCallbackHandler(String fileName) throws IOException {
        resultFile = new File(fileName);
        if (resultFile.exists()) {
            boolean result = resultFile.delete();
            assertTrue(result, "ResultFile could not be deleted");
        }
        LOGGER.info("****** Dump file: " + resultFile.getAbsolutePath());
        fileWriter = new OutputStreamWriter(new FileOutputStream(resultFile), "ISO-8859-1");
    }

    public void closeFile() throws IOException {
        fileWriter.close();
    }

    @Override
    protected void processRow(ResultSet rs, int rowNum) throws SQLException {
        if (rowNum == 0) {
            printColumnNames();
        }
        printColumn(rs, getColumnCount());
    }

    private void printColumn(ResultSet rs, int columnCount) throws SQLException {
        String[] array = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            Object object = rs.getObject(i + 1);
            if (object != null) {
                array[i] = object.toString();
            }
        }
        printArrayToFile(array);
    }

    private void printColumnNames() {
        String[] array = getColumnNames();
        // make sure Excel does not try to import file as SLYK - this happens if first column in first line is 'ID'
        if ((array != null) && "ID".equals(array[0])) {
            array[0] = " ID";
        }
        printArrayToFile(array);
    }

    private void printArrayToFile(String[] array) {
        try {
            fileWriter.write(printCsvArray(array));
        }
        catch (IOException e) {
            throw new RuntimeException("IO exception trying to write result file " + resultFile.getAbsolutePath(), e);
        }
    }

    private String printCsvArray(String[] array) {
        if (array == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int pos = 0; pos < array.length - 1; pos++) {
            sb.append(createEscapedString(array[pos]) + ";");
        }
        if (array.length > 0) {
            sb.append(createEscapedString(array[array.length - 1]));
        }
        sb.append(System.getProperty("line.separator"));
        return sb.toString();
    }

    private String createEscapedString(String string) {
        if (StringUtils.isEmpty(string)) {
            return "";
        }
        boolean quoted = string.contains("\"");
        boolean semicolon = string.contains(";");
        if (quoted || semicolon) {
            return "\"" + string.replaceAll("\"", "\"\"") + "\"";
        }
        return string;
    }
}
