package de.mnet.test.generator;

import java.io.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mnet.test.generator.helper.CSVBeanHandler;
import de.mnet.test.generator.helper.DataHandler;
import de.mnet.test.generator.helper.DataHandler.DataFile;
import de.mnet.test.generator.model.Address;


/**
 * generate random addresses from the prefilled csv files
 */
public final class AddressGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AddressGenerator.class);
    private static AddressGenerator instance;
    private final List<Address> preLoadedAddresses;

    private AddressGenerator(List<Address> preLoadedAddresses) {
        this.preLoadedAddresses = preLoadedAddresses;
    }

    public static AddressGenerator getInstance() throws IOException {
        if (instance == null) {
            instance = new AddressGenerator(readCSVAddresses());
        }
        return instance;
    }

    /**
     * Returns a List of {@link Address}-Objekts from the preloaded csv file
     *
     * @throws IOException Problem beim Lesen der CSV-Datei mit den Adressen
     */
    private static List<Address> readCSVAddresses() throws IOException{
        CSVBeanHandler<Address> addressCSVHandler = new CSVBeanHandler<>();
        List<Address> addresses = addressCSVHandler.generatePojosFromCSV(Address.class, DataFile.ADDRESS.getReader());
        //filter wrong parsed addresses
        List<Address> result = new ArrayList<>();
        int wrongAddresses = 0;
        for (Address a : addresses) {
            String zip = a.getZip().trim();
            if (zip.matches("^\\d{1,5}$")) {
                result.add(a);
            }
            else {
                wrongAddresses++;
            }
        }

        if (wrongAddresses > 0) {
            LOGGER.warn("{} not valid addresses found!", wrongAddresses);
        }
        return result;
    }

    /**
     * Returns a random {@link Address}-Objekts from the preloaded csv file
     */
    public Address getRandomAddress() {
        return preLoadedAddresses.get(DataHandler.randomInt(0, preLoadedAddresses.size() - 1));
    }

}
