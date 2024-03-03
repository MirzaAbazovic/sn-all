package de.mnet.test.generator.model;

import java.io.*;
import java.util.*;

/**
 *
 */
public class Customer implements Serializable {

    private static final long serialVersionUID = -6570754897178709616L;
    private String firstName;
    private String lastName;
    private String email;
    private String companyName;
    private List<Address> addresses;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {

        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", companyName='" + companyName + '\'' +
                ", addresses=" + addresses +
                '}';
    }

    /**
     * returns the first primary {@link Address} object of a customer
     */
    public Address getPrimaryAddress() {
        //serach for primary addresses
        for (Address a : addresses) {
            if (a.isPrimary()) {
                return a;
            }
        }
        //if no primary address is set return the first address
        if (!addresses.isEmpty()) {
            return addresses.get(0);
        }
        return null;
    }
}
