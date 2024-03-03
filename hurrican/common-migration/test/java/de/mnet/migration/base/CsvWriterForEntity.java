/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.11.2010 18:08:25
 */
package de.mnet.migration.base;

import static org.testng.Assert.*;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

public class CsvWriterForEntity<T> {

    private static final Logger LOGGER = Logger.getLogger(CsvWriterForEntity.class);

    private final String[] header;
    private final CellProcessor[] cellProcessors;
    private final CsvBeanWriter csvWriter;


    /**
     * Creates an instance of {@link CsvWriterForEntity} which writes to the given {@code fileName}. The {@code header}
     * will be written as first row to the CSV file. Due to not supplied {@link CellProcessor}s for every column will be
     * used a {@link ConvertNullTo} {@link CellProcessor} with {@code ""} (= empty string) as argument.
     *
     * @param fileName file in which the generated CSV content will be written (if already exists it will be deleted
     *                 before)
     * @param header   an array containing the headers in the desired order
     * @throws IOException - if there is a problem with the supplied {@code fileName}
     */
    public CsvWriterForEntity(String fileName, String[] header) throws IOException {
        this(fileName, header, null);
    }

    /**
     * Creates an instance of {@link CsvWriterForEntity} which writes to the given {@code fileName}. The {@code header}
     * will be written as first row to the CSV file. If the given {@code cellProcessors == null}, a {@link
     * ConvertNullTo} {@link CellProcessor} with {@code ""} (= empty string) as argument will be used to process every
     * column.
     *
     * @param fileName       file in which the generated CSV content will be written (if already exists it will be
     *                       deleted before)
     * @param header         an array containing the headers in the desired order
     * @param cellProcessors the supplied {@link CellProcessor}s, one for every column (= same column size as header)
     * @throws IOException - if there is a problem with the supplied {@code fileName}
     */
    public CsvWriterForEntity(String fileName, String[] header, CellProcessor[] cellProcessors) throws IOException {
        cellProcessors = getOrCreateCellProcessors(header, cellProcessors);
        OutputStreamWriter fileWriter = getAndCheckFileWriter(fileName);

        this.header = header;
        this.cellProcessors = cellProcessors;
        this.csvWriter = new CsvBeanWriter(fileWriter, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);

        csvWriter.writeHeader(header);
    }


    private CellProcessor[] getOrCreateCellProcessors(String[] header, CellProcessor[] cellProcessors) {
        if (cellProcessors == null) {
            cellProcessors = new CellProcessor[header.length];
            for (int i = 0; i < cellProcessors.length; i++) {
                cellProcessors[i] = new ConvertNullTo("");
            }
        }
        return cellProcessors;
    }


    private OutputStreamWriter getAndCheckFileWriter(String fileName) throws UnsupportedEncodingException, FileNotFoundException {
        File resultFile = new File(fileName);
        if (resultFile.exists()) {
            boolean result = resultFile.delete();
            assertTrue(result, "ResultFile could not be deleted");
        }
        LOGGER.info("****** CSV file: " + resultFile.getAbsolutePath());

        return new OutputStreamWriter(new FileOutputStream(resultFile), "ISO-8859-1"); // to see umlauts in Excel
    }


    /**
     * Closes the writer and its output stream
     */
    public void closeWriter() throws IOException {
        csvWriter.close(); // fileWriter.close() is done by csvWriter
    }


    public void processEntities(List<T> entities) throws IOException {
        for (T entity : entities) {
            processEntity(entity);
        }
    }


    public void processEntity(T entity) throws IOException {
        csvWriter.write(entity, header, cellProcessors);
    }
}
