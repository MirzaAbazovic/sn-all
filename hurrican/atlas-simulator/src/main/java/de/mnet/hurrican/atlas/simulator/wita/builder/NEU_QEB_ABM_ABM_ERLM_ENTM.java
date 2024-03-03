package de.mnet.hurrican.atlas.simulator.wita.builder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.wita.message.MeldungsType;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderXpathExpressions;

@Component("NEU_QEB_ABM_ABM_ERLM_ENTM")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class NEU_QEB_ABM_ABM_ERLM_ENTM extends AbstractWitaTest {

    @Override
    public void configure() {
        receiveOrderREQ()
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(getServiceVersion()), "productIdentifier")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(getServiceVersion()), "requestedCustomerDate");
        waitForIoArchiveRequestEntry();

        sendNotification("QEB");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB);

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        variables().add("bindingDeliveryDate", "atlas:changeDate('yyyy-MM-dd','${requestedCustomerDate}','+4d')");
        sendNotification("ABM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABM);

        variables().add("bindingDeliveryDate", "atlas:changeDate('yyyy-MM-dd','${requestedCustomerDate}','+5d')");
        sendNotification("ABM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABM, 2L);

        sendNotification("ERLM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ERLM);

        sendNotification("ENTM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ENTM);
    }

}
