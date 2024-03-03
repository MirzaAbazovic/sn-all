package de.mnet.hurrican.atlas.simulator.wita.builder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderXpathExpressions;
import de.mnet.wita.message.MeldungsType;

@Component("TAL_Wita_Version_Migr_LAE")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class TAL_Wita_Version_Migr_LAE extends AbstractWitaTest {

    @Override
    public void configure() {
        final String futureWitaVersion = "v7";

        receiveOrderREQ("LAE")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(getServiceVersion()), "requestedCustomerDate");
        waitForIoArchiveRequestEntry();

        sendNotification("QEB");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB);

        sendNotification("V7_ABM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABM);

        sendNotification("V7_ERLM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ERLM);

        sendNotification("V7_ENTM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ENTM);
    }

}
