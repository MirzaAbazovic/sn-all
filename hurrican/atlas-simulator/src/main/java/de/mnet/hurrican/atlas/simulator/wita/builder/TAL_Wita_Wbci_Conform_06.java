package de.mnet.hurrican.atlas.simulator.wita.builder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderVariableNames;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderXpathExpressions;

/**
 * Testfall dient zum Test, dass eine WITA TV ueber die automatische Prozessierung einer WBCI ERLM-TV erstellt wird.
 */
@Component("TAL_Wita_Wbci_Conform_06")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class TAL_Wita_Wbci_Conform_06 extends AbstractWitaTest {
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

        // eingehende TV pruefen!
        waitForMnetWitaRequestByExtOrder(TerminVerschiebung.class);
        receiveNotification("TV")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_CHANGE_DATE.getXpath(getServiceVersion()), "tvRequestedCustomerDate");

        waitForIoArchiveEntryByExtOrderNo(MeldungsType.TV);

        sendNotification("ABM_2");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABM, 2L);
    }

}
