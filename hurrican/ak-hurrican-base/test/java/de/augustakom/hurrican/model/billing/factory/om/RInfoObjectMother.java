/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.12.2014
 */
package de.augustakom.hurrican.model.billing.factory.om;

import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.RInfoBuilder;


public class RInfoObjectMother extends AbstractTaifunObjectMother {

    private Long rinfoNo = null;
    private String description = null;
    private Long kundeNo = null;
    private Long adresseNo = null;
    private String extDebitorId = null;

    public static RInfoBuilder createDefault() {
        return new RInfoBuilder();
    }

    public static RInfoBuilder withKunde(Kunde kunde, Adresse address) {
        return createDefault()
                .withKundeNo(kunde.getKundeNo())
                .withAdresseNo(address.getAdresseNo());
    }


}
