/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.14
 */
package de.mnet.hurrican.atlas.simulator.wita.builder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.Storno;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderVariableNames;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderXpathExpressions;

/**
 * Testfall dient zum Test, dass ein WITA Storno ueber die automatische Prozessierung einer WBCI STR-AUFH ERLM erstellt
 * wird.
 */
@Component("TAL_Wita_Wbci_Conform_08")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class TAL_Wita_Wbci_Conform_08 extends AbstractWitaTest {
    @Override
    public void configure() {

        receiveOrderREQ("NEU")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(getServiceVersion()), "requestedCustomerDate")
                .extractFromPayload(WitaLineOrderXpathExpressions.PRE_AGREEMENT_ID.getXpath(getServiceVersion()), WitaLineOrderVariableNames.PRE_AGREEMENT_ID);
        waitForIoArchiveRequestEntry();

        sendNotification("QEB");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB);

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        sendNotification("ABM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABM);

        // eingehenden Storno pruefen
        waitForMnetWitaRequestByExtOrder(Storno.class);
        receiveNotification("STORNO");

        waitForIoArchiveEntryByExtOrderNo(MeldungsType.STORNO);

        sendNotification("ERLM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ERLM);

        sendNotification("ENTM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ENTM);
    }

}
