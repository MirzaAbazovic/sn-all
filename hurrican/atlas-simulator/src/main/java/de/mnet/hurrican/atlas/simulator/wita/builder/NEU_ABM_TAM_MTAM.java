package de.mnet.hurrican.atlas.simulator.wita.builder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.wita.message.MeldungsType;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderXpathExpressions;

@Component("NEU_ABM_TAM_MTAM")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class NEU_ABM_TAM_MTAM extends AbstractWitaTest {

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

        sleep(5000L); // need to sleep some time before sending next TAM so hurrican test assertions can process first TAM

        sendNotification("MTAM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.TAM, 2L);
    }

}
