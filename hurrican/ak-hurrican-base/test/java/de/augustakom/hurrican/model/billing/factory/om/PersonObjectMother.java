/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.12.2014
 */
package de.augustakom.hurrican.model.billing.factory.om;

import de.augustakom.hurrican.model.billing.PersonBuilder;
import de.mnet.test.generator.model.Customer;

public class PersonObjectMother extends AbstractTaifunObjectMother {

    public static PersonBuilder defaultPerson() {
        Customer randomCustomer = randomCustomer();
        return new PersonBuilder()
                .withName(randomCustomer.getLastName())
                .withVorname(randomCustomer.getFirstName())
                .withGeschlecht("M")
                .withLanguage("german");
    }

}

