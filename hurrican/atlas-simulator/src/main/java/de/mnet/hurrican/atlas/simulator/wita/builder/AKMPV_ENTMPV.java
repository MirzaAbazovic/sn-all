package de.mnet.hurrican.atlas.simulator.wita.builder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.wita.message.MeldungsType;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderXpathExpressions;

@Component("AKMPV_ENTMPV")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class AKMPV_ENTMPV extends AbstractWitaTest {

    @Override
    public void configure() {
        receiveOrderREQ("NEU")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(getServiceVersion()), "requestedCustomerDate");
        waitForIoArchiveRequestEntry();

        sendNotification("QEB");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB);

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        sendNotification("ABM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABM);

        waitForCbVorgangClosed();

        sendNotification("ERLM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ERLM);

        sendNotification("ENTM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ENTM);

        variables().add("plannedAquisitionDate", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 7)");
        variables().add("replyDeadline", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 5)");

        sendNotification("AKM-PV");
        waitForIoArchiveEntryByContractId(MeldungsType.AKM_PV);

        // sleepling for a while before sending the EntmPV to avoid failing of a WorkflowState-Check in the Hurrican-AccTest
        sleep(3000);

        sendNotification("ENTM-PV");
        waitForIoArchiveEntryByContractId(MeldungsType.ENTM_PV);
    }

}
