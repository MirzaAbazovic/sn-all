package de.mnet.test.generator.helper;

import java.io.*;
import java.math.*;
import java.util.*;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

/**
 * provides helper functions to read or write Maps from a CSV file
 *
 * Date: 24.05.13
 */
public class CSVObjectHandler {

    /**
     * generate a Map of Objects from a CSV file and returns it.
     *
     * @param csvReader the reader, which is used for accessing and reading the CSV file
     * @throws java.io.IOException
     */
    public static SortedMap<BigInteger, Map<String, String>> generateMapFromCSV(Reader csvReader) throws IOException {
        SortedMap<BigInteger, Map<String, String>> data = new TreeMap<>();
        BigInteger i = BigInteger.ZERO;
        ICsvMapReader reader = null;

        try {
            //config the reader
            reader = new CsvMapReader(csvReader, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);

            //read out the headers
            String[] header = reader.getHeader(true);

            //create Objects for map
            Map<String, String> value;
            while ((value = reader.read(header)) != null) {
                data.put(i, value);
                i = i.add(BigInteger.ONE);
            }
        }
        finally {
            if (reader != null) {
                reader.close();
            }
        }
        return data;
    }

}
