package de.mnet.test.generator.helper;

import java.io.*;
import java.util.*;
import org.supercsv.io.dozer.CsvDozerBeanReader;
import org.supercsv.io.dozer.ICsvDozerBeanReader;
import org.supercsv.prefs.CsvPreference;


/**
 * A generic CSV reader and writer to handle with POJOs
 *
 * Date: 23.05.13
 */
public class CSVBeanHandler<V> {

    /**
     * Handels with csv-files to read or write a list of POJOs. The headers of the file must be similar to the getter
     * and setter of the class {@link V}
     *
     * @param vClass {@link Class}
     * @param csvReader the reader, which is used for accessing and reading the CSV file
     * @throws IOException
     */
    public List<V> generatePojosFromCSV(Class<V> vClass, Reader csvReader) throws IOException {
        List<V> data = new ArrayList<>();
        ICsvDozerBeanReader beanReader = null;
        try {
            //configure the bean reader
            beanReader = new CsvDozerBeanReader(csvReader, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
            //read out the headers
            String[] header = beanReader.getHeader(true);
            beanReader.configureBeanMapping(vClass, header);

            //create the POJOs
            V value;
            while ((value = beanReader.read(vClass)) != null) {
                data.add(value);
            }

        }
        finally {
            if (beanReader != null) {
                beanReader.close();
            }
        }
        return data;
    }

}
