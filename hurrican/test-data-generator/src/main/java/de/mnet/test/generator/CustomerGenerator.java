package de.mnet.test.generator;

import java.io.*;
import java.math.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mnet.test.generator.helper.CSVObjectHandler;
import de.mnet.test.generator.helper.DataHandler;
import de.mnet.test.generator.helper.FlutterDataHandler;
import de.mnet.test.generator.model.Address;
import de.mnet.test.generator.model.Customer;

public final class CustomerGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerGenerator.class);
    private static CustomerGenerator instance;
    private final SortedMap<BigInteger, Map<String, String>> firstNames, lastNames, emailProv;
    private final SortedMap<BigInteger, String> companies;

    //porivate Consturctor for Singelton
    private CustomerGenerator(SortedMap<BigInteger, Map<String, String>> firstNames,
            SortedMap<BigInteger, Map<String, String>> lastNames,
            SortedMap<BigInteger, Map<String, String>> emailProv,
            SortedMap<BigInteger, String> companies) {
        this.firstNames = firstNames;
        this.lastNames = lastNames;
        this.emailProv = emailProv;
        this.companies = companies;
    }

    //initialize the singelton
    public static CustomerGenerator getInstance() throws IOException {
        if (instance == null) {
            //fill phone Prefixes
            instance = new CustomerGenerator(
                    CSVObjectHandler.generateMapFromCSV(DataHandler.DataFile.FIRST_NAMES.getReader()),
                    CSVObjectHandler.generateMapFromCSV(DataHandler.DataFile.LAST_NAMES.getReader()),
                    CSVObjectHandler.generateMapFromCSV(DataHandler.DataFile.EMAIL.getReader()),
                    FlutterDataHandler.getInstance().generateCompanies()
            );

        }
        return instance;
    }


    /**
     * @return the same result as {@link CustomerGenerator#generateCustomer(long)} with value 1.
     * @throws IOException
     */
    public static Customer generateCustomer() throws IOException {
        List<Customer> list = generateCustomer(1);
        return list == null || list.isEmpty() ? null : list.iterator().next();
    }

    /**
     * Generates the random data for the application, from different CSV-Files, which are placed under /resources and
     * put the random data into it.
     *
     * @param count the number of random customers
     * @throws IOException
     */
    public static List<Customer> generateCustomer(long count) throws IOException {
        LOGGER.debug("------------- Start to generate random data --------");

        List<Customer> result = new ArrayList<>();
        for (long i = 0; i < count; i++) {

            //Variables to control the min, max of the different attributes
            int numOfAddresses = DataHandler.randomInt(1, 3);

            //get a prefilled, random customer
            Customer newCustomer = getInstance().getRandomPreFilledCustomer();

            //add two addresses for each customer
            for (int a = 0; a < numOfAddresses; a++) {
                Address newAddress = AddressGenerator.getInstance().getRandomAddress();
                if (a == 0) {
                    newAddress.setPrimary(true);
                }
                else {
                    newAddress.setPrimary(false);
                }
                newCustomer.getAddresses().add(newAddress);
            }
            result.add(newCustomer);
        }
        LOGGER.debug("------------- {} random customers generated", count);
        return result;
    }

    /**
     * Germany Get a random prefilled {@link Customer}-POJO with the following filled attributes: - Id - Name (first
     * name + last name) - Email - Phone
     *
     * @return {@link Customer}
     */
    public Customer getRandomPreFilledCustomer() {
        Customer customer = new Customer();

        //get random values
        String fName = firstNames.get(DataHandler.randomBigInt(firstNames.firstKey(), firstNames.lastKey())).get("firstname");
        String lName = lastNames.get(DataHandler.randomBigInt(lastNames.firstKey(), lastNames.lastKey())).get("lastname");
        String emailProvider = emailProv.get(DataHandler.randomBigInt(emailProv.firstKey(), emailProv.lastKey())).get("e-mail-provider");
        String company = companies.get(DataHandler.randomBigInt(companies.firstKey(), companies.lastKey()));
        validateNotEmptyString(fName, lName, emailProvider, company);

        //set Values
        customer.setFirstName(fName);
        customer.setLastName(lName);
        customer.setEmail(fName.toLowerCase() + "." + lName.toLowerCase() + emailProvider);
        customer.setCompanyName(company);

        //empty preloaded lists
        customer.setAddresses(new ArrayList<Address>());
        return customer;
    }

    private void validateNotEmptyString(String... value) {
        if (value == null || value.length == 0) {
            throw new RuntimeException("the validated string have to be not NULL or empty!");
        }
    }

}
