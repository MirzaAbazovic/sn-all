package de.mnet.hurrican.atlas.simulator.wita.builder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.wita.message.MeldungsType;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderXpathExpressions;

@Component("NEU_ABM_TAM_TV_TAM")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class NEU_ABM_TAM_TV_TAM extends AbstractWitaTest {

    @Override
    public void configure() {

        receiveOrderREQ()
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(getServiceVersion()), "requestedCustomerDate");

        waitForIoArchiveRequestEntry();

        sendNotification("QEB");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB);

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        sendNotification("ABM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABM);

        sendNotification("TAM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.TAM);

        // TV
        receiveNotification()
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_CHANGE_DATE.getXpath(getServiceVersion()), "requestedCustomerDate");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.TV);

        sendNotification("TAM");
    }
}
