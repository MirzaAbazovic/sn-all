package de.mnet.hurrican.atlas.simulator.wita.builder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderXpathExpressions;

/**
 *
 */
@Component("NEU_QEB_TV_ABM_ERLM_TV_ABBM")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class NEU_QEB_TV_ABM_ERLM_TV_ABBM extends AbstractWitaTest {

    @Override
    public void configure() {
        receiveOrderREQ("NEU");
        waitForIoArchiveRequestEntry();

        sendNotification("QEB");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB);

        waitForMnetWitaRequestByExtOrder(TerminVerschiebung.class);
        receiveNotification("TV")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_CHANGE_DATE.getXpath(getServiceVersion()), "requestedCustomerDate");

        waitForIoArchiveEntryByExtOrderNo(MeldungsType.TV);

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        sendNotification("ABM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABM);

        sendNotification("ERLM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ERLM);
    }

}
