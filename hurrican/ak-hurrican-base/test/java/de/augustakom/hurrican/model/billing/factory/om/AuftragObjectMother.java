/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.12.2014
 */
package de.augustakom.hurrican.model.billing.factory.om;

import java.time.*;

import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.BAuftragBuilder;
import de.augustakom.hurrican.model.billing.BillingConstants;
import de.augustakom.hurrican.model.billing.Kunde;
import de.mnet.common.tools.DateConverterUtils;


public class AuftragObjectMother extends AbstractTaifunObjectMother {

    public static BAuftragBuilder createDefault() {
        BAuftragBuilder builder = new BAuftragBuilder()
                .withAtyp("NEU")
                .withAstatus(5)
                .withHistStatus(BillingConstants.HIST_STATUS_AKT)
                .withHistCnt(0)
                .withHistLast(Boolean.TRUE)
                .withGueltigVon(DateConverterUtils.asDate(LocalDateTime.now().minusDays(100)))
                ;

        return builder;
    }

    public static BAuftragBuilder withKunde(Kunde kunde, Adresse address) {
        return createDefault()
                .withKundeNo(kunde.getKundeNo())
                .withApAddressNo(address.getAdresseNo());
    }

}
