package de.mnet.hurrican.atlas.simulator.wita.builder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.wita.message.MeldungsType;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderXpathExpressions;

@Component("NEU_QEB_ABM_TAM_TAM_ABBM")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class NEU_QEB_ABM_TAM_TAM_ABBM extends AbstractWitaTest {

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

        // sleepling for a while before sending the second TAM to avoid failing of a WorkflowState-Check in the Hurrican-AccTest
        sleep(2000);

        sendNotification("TAM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.TAM, 2L);

        sendNotification("ABBM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABBM);
    }

}
