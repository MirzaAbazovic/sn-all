/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.12.2014
 */
package de.augustakom.hurrican.model.billing.factory.om;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.mnet.test.generator.CustomerGenerator;
import de.mnet.test.generator.model.Customer;

/**
 * Basis-Klasse fuer sog. ObjectMothers von Taifun-Objekten. <br/> Ueber die ObjectMothers werden die Test-Builder
 * Klassen der jeweiligen Objekte gekapselt und ein Default-Set an Daten generiert.
 */
public abstract class AbstractTaifunObjectMother {

    private static final Random RANDOM = new Random();

    public static String randomString() {
        return UUID.randomUUID().toString();
    }

    public static String randomString(int maxLength) {
        String uuID = UUID.randomUUID().toString();
        return StringUtils.left(uuID, maxLength);
    }

    public static Integer randomInt(int min, int max) {
        return RANDOM.nextInt((max - min) + 1) + min;
    }

    public static Long randomLong(int min) {
        return Long.valueOf(randomInt(min, Integer.MAX_VALUE));
    }

    public static Long randomLong(int min, int max) {
        return Long.valueOf(randomInt(min, max));
    }

    protected static Customer randomCustomer() {
        try {
            Customer customer = CustomerGenerator.generateCustomer();
            //for taifun only use german customers
            if (customer.getPrimaryAddress().getCountry().startsWith("De")) {
                return customer;
            }
            else {
                return randomCustomer();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
