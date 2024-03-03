package de.mnet.hurrican.atlas.simulator.wita.builder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderXpathExpressions;
import de.mnet.wita.message.MeldungsType;

@Component("TAL_Wita_Version_Migr_AkmPV")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class TAL_Wita_Version_Migr_AkmPV extends AbstractWitaTest {

    @Override
    public void configure() {

        receiveOrderREQ("NEU")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(getServiceVersion()), "requestedCustomerDate");

        final String currentWitaVersion = "v7";
        waitForIoArchiveRequestEntry();

        sendNotification("QEB");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB);

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        sendNotification("ABM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABM);

        sendNotification("ERLM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ERLM);

        sendNotification("ENTM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ENTM);

        variables().add("plannedAquisitionDate", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 12)");
        variables().add("replyDeadline", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 10)");

        sendNotification("AKM-PV");
        final String futureWitaVersion = "v7";   // change to V8/V9 if next WITA version is integrated
        waitForIoArchiveEntryByContractId(MeldungsType.AKM_PV);

        sendNotification("ABBM-PV");
        waitForIoArchiveEntryByContractId(MeldungsType.ABBM_PV);
    }

}
