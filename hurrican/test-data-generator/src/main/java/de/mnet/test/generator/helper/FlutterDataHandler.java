/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.01.2015
 */
package de.mnet.test.generator.helper;

import java.math.*;
import java.util.*;
import org.fluttercode.datafactory.impl.DataFactory;

/**
 *
 */
public final class FlutterDataHandler {


    private static FlutterDataHandler instance;
    private final DataFactory dataFactory;

    private FlutterDataHandler(DataFactory dataFactory) {
        this.dataFactory = dataFactory;
    }

    public static FlutterDataHandler getInstance() {
        if (instance == null) {
            instance = new FlutterDataHandler(new DataFactory());
        }
        return instance;
    }


    public SortedMap<BigInteger, String> generateCompanies() {
        final int max = 1000;
        SortedMap<BigInteger, String> result = new TreeMap<>();
        for (int i = 0; i < max; i++) {
            result.put(BigInteger.valueOf(i), dataFactory.getBusinessName());
        }
        return result;
    }
}
