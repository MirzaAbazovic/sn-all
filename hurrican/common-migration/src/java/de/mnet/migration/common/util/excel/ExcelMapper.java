/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2010 13:20:39
 */
package de.mnet.migration.common.util.excel;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.core.io.Resource;

import de.mnet.migration.common.util.ColumnName;
import de.mnet.migration.common.util.Pair;
import de.mnet.migration.common.util.ReflectionUtil;


/**
 * Liest Excel-Dateien aus und mappt die Zeilen via Ueberschriften auf die gegebene Entity.
 *
 *
 */
public class ExcelMapper {
    private static final Logger LOGGER = Logger.getLogger(ExcelMapper.class);

    public static class Config {
        public Integer sheetNumber = 0;
        public Integer captionRow = 0;
        public ColumnNameExtractor columnNameExtractor = new ColumnNameExtractor();

        public Config setSheetNumber(Integer sheetNumber) {
            this.sheetNumber = sheetNumber;
            return this;
        }

        public Config setCaptionRow(Integer captionRow) {
            this.captionRow = captionRow;
            return this;
        }

        public Config setColumnNameExtractor(ColumnNameExtractor columnNameExtractor) {
            this.columnNameExtractor = columnNameExtractor;
            return this;
        }
    }


    public static class FieldMapping {
        public Field field;
        public Integer column;
        public CellMapper cellMapper;

        public FieldMapping(Field field, Integer column, CellMapper cellMapper) {
            this.field = field;
            this.column = column;
            this.cellMapper = cellMapper;
        }
    }


    public static <T> List<T> map(Resource excelFile, Class<T> entityClass) {
        return map(excelFile, entityClass, new Config());
    }


    /**
     * Liest Excel-Dateien aus und mappt die Zeilen via Ueberschriften auf die gegebene Entity. Falls {@code
     * config.captionRow == null}, benutze erste Zeile.
     */
    public static <T> List<T> map(Resource excelFile, Class<T> entityClass, Config config) {
        HSSFSheet workbookSheet = getWorkbookSheet(excelFile, config.sheetNumber);
        Integer captionRow = config.captionRow == null ? Integer.valueOf(workbookSheet.getFirstRowNum()) : config.captionRow;
        List<FieldMapping> fieldMapping = createFieldMapping(workbookSheet,
                captionRow, config.columnNameExtractor, entityClass);
        return mapSheet(workbookSheet, captionRow, fieldMapping, entityClass);
    }


    private static HSSFSheet getWorkbookSheet(Resource excelFile, Integer sheetNumber) {
        HSSFWorkbook workbook;
        try {
            workbook = new HSSFWorkbook(excelFile.getInputStream());
            HSSFSheet sheet = workbook.getSheetAt(sheetNumber);
            return sheet;
        }
        catch (IOException e) {
            LOGGER.error("getWorkbookSheet() - could not open file");
            throw new RuntimeException("getWorkbookSheet() - could not open file", e);
        }
    }


    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "DE_MIGHT_IGNORE",
            justification = "Exceptions here are anticipated and can be ignored")
    private static <T> List<FieldMapping> createFieldMapping(HSSFSheet sheet, Integer captionRow,
            ColumnNameExtractor columnNameExtractor, Class<T> entityClass) {
        Map<String, Integer> columns = columnNameExtractor.getColumns(sheet, captionRow);
        CellMapper defaultMapper = new CellMapper();

        List<FieldMapping> mapping = new ArrayList<FieldMapping>();
        for (Field field : ReflectionUtil.filterStaticAndFinal(ReflectionUtil.getAllFields(entityClass, entityClass))) {
            String columnName = field.getName();
            ColumnName annotation = field.getAnnotation(ColumnName.class);
            if (annotation != null) {
                columnName = annotation.value();
            }
            CellMapper mapper = defaultMapper;
            Mapper mapperAnnotation = field.getAnnotation(Mapper.class);
            if (mapperAnnotation != null) {
                try {
                    mapper = mapperAnnotation.value().newInstance();
                }
                catch (Exception e) { // NOPMD just use default
                }
            }
            Integer column = -1;
            Set<String> possibleNames = new HashSet<String>();
            for (String string : ReflectionUtil.createPossibleNames(columnName)) {
                possibleNames.add(string.toLowerCase());
            }
            for (String possibleName : possibleNames) {
                column = columns.get(possibleName);
                if (column != null) {
                    break;
                }
            }
            if (Integer.valueOf(-1).equals(column) || (column == null)) {
                throw new RuntimeException("Could not match field '" + columnName +
                        "' of class " + entityClass.getName() + " to an existing column");
            }
            mapping.add(new FieldMapping(field, column, mapper));
        }
        return mapping;
    }


    private static <T> List<T> mapSheet(HSSFSheet sheet, Integer captionRow,
            List<FieldMapping> fieldMapping, Class<T> entityClass) {
        List<T> resultList = new ArrayList<T>();
        for (int idx = captionRow + 1; idx <= sheet.getLastRowNum(); ++idx) {
            HSSFRow currentRow = sheet.getRow(idx);
            if (currentRow != null) {
                boolean anyNonNull = false;
                T result;
                try {
                    result = entityClass.newInstance();
                }
                catch (Exception e) {
                    throw new RuntimeException("Exception while trying to create new Instance of class " + entityClass.getName());
                }
                for (FieldMapping mapping : fieldMapping) {
                    Pair<Object, Boolean> mapResult = null;
                    try {
                        mapResult = mapping.cellMapper.map(currentRow, mapping.field, mapping.column);
                        anyNonNull = anyNonNull || mapResult.getSecond().booleanValue();
                        mapping.field.setAccessible(true);
                        mapping.field.set(result, mapResult.getFirst());
                    }
                    catch (Exception e) {
                        Object value = (mapResult == null ? null : mapResult.getFirst());
                        throw new RuntimeException("Exception while trying to set field " + mapping.field.getName() +
                                " of class " + entityClass.getName() + " (type: " + mapping.field.getType() + ") to " +
                                (value == null ? "null" : value.toString()) + " (type: " + (value == null ? "-" : value.getClass()) + ")", e);
                    }
                }
                if (anyNonNull) {
                    resultList.add(result);
                }
            }
        }
        return resultList;
    }


    /**
     * Falls mal gemergte Zellen genutzt werden sollen...
     */
    public static class Region {
        private int top;
        private int bottom;
        private int left;
        private int right;

        public Region(int top, int bottom, int left, int right) {
            this.top = top;
            this.bottom = bottom;
            this.left = left;
            this.right = right;
        }

        public boolean isInside(int row, int column) {
            return (top <= row) && (bottom >= row) && (left <= column) && (right >= column);
        }

        public int top() {
            return top;
        }

        public int left() {
            return left;
        }
    }
}
