package de.mnet.test.generator.helper;

import java.io.*;
import java.math.*;
import java.nio.charset.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date: 24.05.13
 */
public class DataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataHandler.class);

    private static final Random random = new Random();

    /**
     * Returns a random {@link BigInteger} number between min and max.
     *
     * @param minIn min value
     * @param maxIn max value
     * @return random BigIntger
     */
    public static BigInteger randomBigInt(BigInteger minIn, BigInteger maxIn) {
        BigInteger min = minIn;
        BigInteger max = maxIn;
        if (max.compareTo(min) < 0) {
            BigInteger tmp = min;
            min = max;
            max = tmp;
        }
        else if (max.compareTo(min) == 0) {
            return min;
        }
        max = max.add(BigInteger.ONE);
        BigInteger range = max.subtract(min);
        int length = range.bitLength();
        BigInteger result = new BigInteger(length, random);
        while (result.compareTo(range) >= 0) {
            result = new BigInteger(length, random);
        }
        result = result.add(min);
        return result;
    }

    /**
     * Returns a random {@link int} number between min and max.
     *
     * @param minIn min value
     * @param maxIn max value
     * @return random int
     */
    public static int randomInt(int minIn, int maxIn) {
        int min = minIn;
        int max = maxIn;
        if (max < min) {
            int tmp = min;
            min = max;
            max = tmp;
        }
        else if (max == min) {
            return min;
        }
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * returns a random long value
     */
    private static long randomLong(long minIn, long maxIn) {
        long min = minIn;
        long max = maxIn;
        if (max < min) {
            long tmp = min;
            min = max;
            max = tmp;
        }
        else if (max == min) {
            return min;
        }
        return min + (long) (Math.random() * (max - min + 1));
    }

    /**
     * Returns a random value of a enum
     */
    public static <E> E randomEnum(E[] elements) {
        return elements[random.nextInt(elements.length)];
    }

    /**
     * Returns a random Date between a min and max Date
     *
     * @param minDate as a {@link java.util.Calendar} object
     * @param maxDate as a {@link java.util.Calendar} object
     */
    public static Date randomDate(Date minDate, Date maxDate) {
        long randomLong = randomLong(minDate.getTime(), maxDate.getTime());
        return new Date(randomLong);
    }

    public enum DataFile {
        ADDRESS("/_address.csv"),
        EMAIL("/_email_provider.csv"),
        FIRST_NAMES("/_first_names.csv"),
        LAST_NAMES("/_last_names.csv");

        private String resourceName;

        DataFile(String resourceName) {
            this.resourceName = resourceName;
        }

        public String getResourceName() {
            return resourceName;
        }

        public Reader getReader() {
            LOGGER.info(String.format("Loading CSV resource: '%s'", resourceName));
            return new InputStreamReader(CSVObjectHandler.class.getResourceAsStream(resourceName), Charset.forName("UTF-8"));
        }
    }
}
