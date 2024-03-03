package de.mnet.hurrican.atlas.simulator.wita.builder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.wita.message.MeldungsType;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderXpathExpressions;

/**
 * Testfall dient zum Test, dass der über WBCI abgestimmte Wechseltermin in der Erzeugung, in der ABM und in der TV
 * überprüft werden. In diesem Fall wird keine TV ausgelöst, da eine im Test ein Exception geworfen wird. Siehe
 * de.mnet.wita.acceptance.WitaWbciConformanceTest.
 */
@Component("TAL_Wita_Wbci_Conform_04")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class TAL_Wita_Wbci_Conform_04 extends AbstractWitaTest {
    @Override
    public void configure() {

        receiveOrderREQ("NEU")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(getServiceVersion()),
                        "requestedCustomerDate");
        waitForIoArchiveRequestEntry();

        sendNotification("QEB");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB);

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        sendNotification("ABM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABM);
    }

}
