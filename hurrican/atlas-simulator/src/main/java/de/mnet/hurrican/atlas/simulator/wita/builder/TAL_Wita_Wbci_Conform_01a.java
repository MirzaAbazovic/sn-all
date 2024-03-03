package de.mnet.hurrican.atlas.simulator.wita.builder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderXpathExpressions;

@Component("TAL_Wita_Wbci_Conform_01a")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class TAL_Wita_Wbci_Conform_01a extends AbstractWitaTest {

    @Override
    public void configure() {
        receiveOrderREQ("VBL")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(getServiceVersion()), "requestedCustomerDate");
        waitForIoArchiveRequestEntry();
    }

}
