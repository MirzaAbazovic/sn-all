package de.mnet.hurrican.atlas.simulator.wita.builder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.wita.message.MeldungsType;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderXpathExpressions;

@Component("NEU_QEB_ENTM")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class NEU_QEB_ENTM extends AbstractWitaTest {

    @Override
    public void configure() {
        receiveOrderREQ()
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(getServiceVersion()), "productIdentifier")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(getServiceVersion()), "requestedCustomerDate");
        waitForIoArchiveRequestEntry();

        sendNotification("QEB");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB);

        sendNotification("ENTM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ENTM);
    }

}
