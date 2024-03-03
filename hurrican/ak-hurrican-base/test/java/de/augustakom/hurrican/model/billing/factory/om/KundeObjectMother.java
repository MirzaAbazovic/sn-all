/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.12.2014
 */
package de.augustakom.hurrican.model.billing.factory.om;

import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.KundeBuilder;
import de.mnet.test.generator.model.Customer;

public class KundeObjectMother extends AbstractTaifunObjectMother {

    private static final String KUNDENTYP_GEWERBLICH = "GEWERBLICH";
    private static final String KUNDENTYP_PRIVAT = "PRIVAT";

    public static KundeBuilder defaultKunde(boolean privat) {
        Customer randomCustomer = randomCustomer();
        return new KundeBuilder()
                .withResellerKundeNo(Kunde.RESELLER_KUNDE_NO_MNET)
                .withName((privat) ? randomCustomer.getLastName() : randomCustomer.getCompanyName())
                .withVorname((privat) ? randomCustomer.getFirstName() : null)
                .withEmail(randomCustomer.getEmail())
                .withKundeTyp((privat) ? KUNDENTYP_PRIVAT : KUNDENTYP_GEWERBLICH);
    }

}

