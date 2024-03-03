/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2010 13:20:39
 */
package de.mnet.migration.common.util.csv;

import java.io.*;
import java.lang.reflect.*;
import java.math.*;
import java.nio.charset.*;
import java.text.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseBigDecimal;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvReflectionException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import de.mnet.migration.common.util.ColumnName;
import de.mnet.migration.common.util.ReflectionUtil;


/**
 * Liest Csv-Dateien aus und mappt die Zeilen via Spaltenindizes auf die gegebene Entity.
 * <p/>
 * Mapping kann entweder ueber Spaltennamen (1. Zeile im .csv-File) via {@code @ColumnName()} oder ueber Spaltennummern
 * via {@code @ColumnIndex()} erfolgen.
 * <p/>
 * Achtung: Der CsvMapper kann nur auf String-Felder mappen, falls keine Header-Zeile in der CSV-Datei ist.
 */
public class CsvMapper {
    private static final Logger LOGGER = Logger.getLogger(CsvMapper.class);

    /**
     * Liest Csv-Dateien aus und mappt die Zeilen via Spaltenindizes auf die gegebene Entity.
     */
    public static <T> List<T> map(Resource csvFile, Class<T> entityClass, CsvPreference preferences, boolean hasHeader) {
        return map(csvFile, entityClass, preferences, hasHeader, Charset.defaultCharset());
    }

    /**
     * Liest Csv-Dateien aus und mappt die Zeilen via Spaltenindizes auf die gegebene Entity.
     */
    public static <T> List<T> map(Resource csvFile, Class<T> entityClass,
            CsvPreference preferences, boolean hasHeader, Charset charset) {
        CsvBeanReader csvReader = null;
        try {
            csvReader = new CsvBeanReader(new InputStreamReader(csvFile.getInputStream(), charset), preferences);
            Map<String, Integer> header = extractHeader(hasHeader, csvReader);
            return mapSheet(csvReader, createFieldMapping(header, entityClass), entityClass, header);
        }
        catch (FileNotFoundException e) {
            LOGGER.error("could not find file '" + csvFile.getFilename() + "'");
            throw new RuntimeException("could not find file '" + csvFile.getFilename() + "'", e);
        }
        catch (IOException e) {
            LOGGER.error("could not open file '" + csvFile.getFilename() + "'");
            throw new RuntimeException("could not open file '" + csvFile.getFilename() + "'", e);
        }
        finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                }
                catch (IOException e) {
                    LOGGER.warn("map() - exception closing csv reader", e);
                }
            }
        }
    }

    static Map<String, Integer> extractHeader(boolean hasHeader, CsvBeanReader csvReader) throws IOException {
        Map<String, Integer> header = new HashMap<String, Integer>();
        if (hasHeader) {
            String[] headerArr = csvReader.getHeader(true);
            LOGGER.debug("CSV header used for mapping: " + Arrays.toString(headerArr));
            for (int i = 0; i < headerArr.length; i++) {
                if (headerArr[i] != null) {
                    header.put(headerArr[i].toLowerCase(), Integer.valueOf(i));
                }
            }
        }
        return header;
    }

    static <T> List<FieldMapping> createFieldMapping(Map<String, Integer> header, Class<T> entityClass) {
        List<FieldMapping> mapping = new ArrayList<FieldMapping>();
        for (Field field : ReflectionUtil.filterStaticAndFinal(ReflectionUtil.getAllFields(entityClass, entityClass))) {
            Integer columnIndex = null;

            String name = field.getName().toLowerCase();
            ColumnName nameAnnotation = field.getAnnotation(ColumnName.class);
            if (nameAnnotation != null) {
                name = nameAnnotation.value();
            }
            columnIndex = header.get(name.toLowerCase());

            ColumnIndex indexAnnotation = field.getAnnotation(ColumnIndex.class);
            if (indexAnnotation != null) {
                columnIndex = indexAnnotation.value();
                if (columnIndex.intValue() < 0) {
                    throw new IndexOutOfBoundsException("Column index of field '" + field.getName() + "' in class "
                            + entityClass.getName() + " must be greater or equal to zero.");
                }
            }

            if (columnIndex == null) {
                throw new RuntimeException("createFieldMapping() - Could not map field '" + field.getName() + "'"
                        + " of class " + entityClass.getName());
            }
            if (columnIndex.intValue() >= 0) {
                mapping.add(new FieldMapping(field, columnIndex));
            }
        }
        return mapping;
    }

    static <T> List<T> mapSheet(CsvBeanReader reader, List<FieldMapping> fieldMapping,
            Class<T> entityClass, Map<String, Integer> header) {
        List<T> resultList = new ArrayList<T>();

        Collections.sort(fieldMapping, new Comparator<FieldMapping>() {
            @Override
            public int compare(FieldMapping o1, FieldMapping o2) {
                return o1.columnIndex.compareTo(o2.columnIndex);
            }
        });

        List<String> nameMapping = new ArrayList<String>();
        int lastIndex = 0;
        for (FieldMapping mapping : fieldMapping) {
            while (lastIndex < mapping.columnIndex) {
                nameMapping.add(null);
                lastIndex++;
            }
            nameMapping.add(mapping.field.getName());
            lastIndex++;
        }

        String[] nameMappings = nameMapping.toArray(new String[nameMapping.size()]);

        try {
            T result;
            if ((header == null) || header.entrySet().isEmpty()) {
                // without CellProcessors
                while ((result = reader.read(entityClass, nameMappings)) != null) {
                    resultList.add(result);
                }
            }
            else {
                // with CellProcessors
                CellProcessor[] cellProcessors = createCellProcessors(fieldMapping, header);
                while ((result = reader.read(entityClass, nameMappings, cellProcessors)) != null) {
                    resultList.add(result);
                }
            }

            return resultList;
        }
        catch (SuperCsvReflectionException e) {
            LOGGER.error("mapSheet() - exception during reflective mapping of '" + entityClass.getName() + "'");
            throw new RuntimeException("mapSheet() - exception during reflective mapping of '" + entityClass.getName() + "'", e);
        }
        catch (IOException e) {
            LOGGER.error("mapSheet() - file i/o exception during mapping of '" + entityClass.getName() + "'");
            throw new RuntimeException("mapSheet() - file i/o exception during mapping of '" + entityClass.getName() + "'", e);
        }
    }

    private static CellProcessor[] createCellProcessors(List<FieldMapping> fieldMapping, Map<String, Integer> header) {
        CellProcessor[] cellProcessors = new CellProcessor[header.size()];

        for (FieldMapping mapping : fieldMapping) {
            Class<?> type = mapping.field.getType();
            Integer columnIndex = mapping.columnIndex;
            if (type.equals(Long.class)) {
                cellProcessors[columnIndex] = new Optional(new ParseLong());
            }
            else if (type.equals(Integer.class)) {
                cellProcessors[columnIndex] = new Optional(new ParseInt());
            }
            else if (type.equals(Boolean.class)) {
                cellProcessors[columnIndex] = new Optional(new ParseBool());
            }
            else if (type.equals(BigDecimal.class)) {
                cellProcessors[columnIndex] = new Optional(new ParseBigDecimal(DecimalFormatSymbols.getInstance(Locale.GERMAN)));
            }
            else if (Enum.class.isAssignableFrom(type)) {
                cellProcessors[columnIndex] = new ParseEnum(mapping.field);
            }
        }

        return cellProcessors;
    }

    static class FieldMapping {
        public Field field;
        public Integer columnIndex;

        public FieldMapping(Field field, Integer columnIndex) {
            this.field = field;
            this.columnIndex = columnIndex;
        }
    }

}
