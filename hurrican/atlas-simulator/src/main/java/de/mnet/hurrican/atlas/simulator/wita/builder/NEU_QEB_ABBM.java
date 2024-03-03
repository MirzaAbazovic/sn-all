package de.mnet.hurrican.atlas.simulator.wita.builder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.wita.message.MeldungsType;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderVariableNames;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderXpathExpressions;

/**
 *
 */
@Component("NEU_QEB_ABBM")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class NEU_QEB_ABBM extends AbstractWitaTest {

    @Override
    public void configure() {

        receiveOrderREQ()
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(getServiceVersion()), WitaLineOrderVariableNames.REQUESTED_CUSTOMER_DATE)
                .extractFromPayload(WitaLineOrderXpathExpressions.CUSTOMER_ID.getXpath(getServiceVersion()), WitaLineOrderVariableNames.CUSTOMER_ID)
                .extractFromPayload(WitaLineOrderXpathExpressions.THIRD_PARTY_SALESMAN_CUSTOMER_ID.getXpath(getServiceVersion()), WitaLineOrderVariableNames.THIRD_PARTY_SALESMAN_CUSTOMER_ID);
        waitForIoArchiveRequestEntry();

        sendNotification("QEB");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB);

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        sendNotification("ABBM");
    }
}
