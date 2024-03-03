package de.mnet.hurrican.atlas.simulator.wita.builder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.wita.message.MeldungsType;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderVariableNames;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderXpathExpressions;

/**
 * Testfall dient zum Test, dass eine WITA NEU ueber die automatische Prozessierung einer WBCI AKM-TR
 * erstellt wird.
 */
@Component("TAL_Wita_Wbci_Conform_07")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class TAL_Wita_Wbci_Conform_07 extends AbstractWitaTest {

    @Override
    public void configure() {
        receiveOrderREQ("NEU")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(getServiceVersion()), "requestedCustomerDate")
                .extractFromPayload(WitaLineOrderXpathExpressions.PRE_AGREEMENT_ID.getXpath(getServiceVersion()), WitaLineOrderVariableNames.PRE_AGREEMENT_ID);
        waitForIoArchiveRequestEntry();

        sendNotification("QEB");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB);
    }

}
