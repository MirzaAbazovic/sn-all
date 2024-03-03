/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.09.2011 10:37:20
 */
package de.mnet.hurrican.webservice.monline.endpoint;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.hurricanweb.netid.customer.CustomerOrderCombination;

/**
 * Testklass von {@link CustomerOrderCombinationConverter}.
 *
 *
 * @since 16.09.2011
 */
@Test(groups = BaseTest.UNIT)
public class CustomerOrderCombinationConverterTest {

    private CustomerOrderCombinationConverter sut;

    /**
     * Testet die Methode {@link CustomerOrderCombinationConverter#convert(java.util.Set)}. Versuche ein leeres Set an
     * Kundennummer+Auftragsnummer zu konvertieren. Das Ergebnis sollte die Zahl Null ergeben.
     */
    @Test
    public void convert_EmptySet() {
        sut = new CustomerOrderCombinationConverter();
        CustomerOrderCombination[] result = sut.setSource(new HashSet<CustomerOrderCombinationWrapper>()).convert();
        Assert.assertEquals(result.length, 0);
    }

    /**
     * Testet die Methode {@link CustomerOrderCombinationConverter#convert(java.util.Set)}. Versuche eine Null Referenze
     * als Set an Kundennummer+Auftragsnummer zu konvertieren. Das Ergebnis sollte eine {@link NullPointerException}
     * sein.
     */
    @Test(expectedExceptions = NullPointerException.class)
    public void convert_NullSet() {
        sut = new CustomerOrderCombinationConverter();
        sut.setSource(null).convert();
    }

    /**
     * Testet die Methode {@link CustomerOrderCombinationConverter#convert()}. Versuche ein Set mit einem Eintrag an
     * Kundennummer+Auftragsnummer zu konvertieren. Das Ergebnis sollte die gleichen Werte besitzen, wie der
     * Ursprungseintrag.
     */
    @Test
    public void convert_OneEntry() {
        sut = new CustomerOrderCombinationConverter();
        final long customerNo = 1200;
        final long billingNo = 4920;
        Set<CustomerOrderCombinationWrapper> source = createSetWithOneEntry(customerNo, billingNo);
        CustomerOrderCombination[] result = sut.setSource(source).convert();

        Assert.assertEquals(result.length, 1);
        Assert.assertEquals(result[0].getCustomerNo(), customerNo);
        Assert.assertEquals(result[0].getOrderNo(), billingNo);
    }

    /**
     * Testet die Methode {@link CustomerOrderCombinationConverter#convert()}. Mehrere gleiche Eintraege sollten nur
     * einen konvertierten Eintrag erzeugen.
     */
    @Test
    public void convert_MultipleDuplicateEntries() {
        sut = new CustomerOrderCombinationConverter();
        final long customerNo = 2000;
        final long billingNo = 3000;
        Set<CustomerOrderCombinationWrapper> source = createSetWithOneEntry(customerNo, billingNo);
        source.add(CustomerOrderCombinationWrapper.create(customerNo, billingNo));
        source.add(CustomerOrderCombinationWrapper.create(customerNo, billingNo));
        source.add(CustomerOrderCombinationWrapper.create(customerNo, billingNo));
        source.add(CustomerOrderCombinationWrapper.create(customerNo, billingNo));
        source.add(CustomerOrderCombinationWrapper.create(customerNo, billingNo));
        source.add(CustomerOrderCombinationWrapper.create(customerNo, billingNo));
        CustomerOrderCombination[] result = sut.setSource(source).convert();

        Assert.assertEquals(result.length, 1);
    }

    /**
     * Testet die Methode {@link CustomerOrderCombinationConverter#convert()}. Mehrere verschiedene Kombinationen
     * sollten nach der Konvertierung auch genausoviele Eintraege produzieren.
     */
    @Test
    public void convert_MultipleDifferentEntries() {
        sut = new CustomerOrderCombinationConverter();
        final long firstCustomerNo = 2000;
        final long firstBillingNo = 3000;
        final long secondCustomerNo = 1000;
        final long secondBillingNo = 500;
        Set<CustomerOrderCombinationWrapper> source = createSetWithOneEntry(firstCustomerNo, firstBillingNo);
        source.add(CustomerOrderCombinationWrapper.create(firstCustomerNo, secondBillingNo));
        source.add(CustomerOrderCombinationWrapper.create(secondCustomerNo, firstBillingNo));
        source.add(CustomerOrderCombinationWrapper.create(secondCustomerNo, secondBillingNo));
        CustomerOrderCombination[] result = sut.setSource(source).convert();

        Assert.assertTrue(result.length == source.size());
        Assert.assertEquals(result.length, 4);
    }

    /**
     * erzeugt ein {@link HashSet} aus {@link CustomerOrderCombinationWrapper}, das mit einer Instanz aus den
     * angegebenen Parametern befuellt ist.
     *
     * @param customerNo
     * @param orderNo
     * @return
     */
    private Set<CustomerOrderCombinationWrapper> createSetWithOneEntry(long customerNo, long orderNo) {
        Set<CustomerOrderCombinationWrapper> result = new HashSet<CustomerOrderCombinationWrapper>();
        result.add(CustomerOrderCombinationWrapper.create(customerNo, orderNo));
        return result;
    }

} // end

