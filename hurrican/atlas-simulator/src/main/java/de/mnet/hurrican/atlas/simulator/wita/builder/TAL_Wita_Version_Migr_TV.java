package de.mnet.hurrican.atlas.simulator.wita.builder;

import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderSimulatorHelper;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderXpathExpressions;
import de.mnet.wita.message.MeldungsType;

@Component("TAL_Wita_Version_Migr_TV")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class TAL_Wita_Version_Migr_TV extends AbstractWitaTest {

    @Override
    public void configure() {
        final String currentWitaVersion = "v7";

        receiveOrderREQ("PV")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(getServiceVersion()), "requestedCustomerDate");
        waitForIoArchiveRequestEntry();

        sendNotification("QEB");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB);

        final String futureWitaVersion = "v7";    // change to V8/V9 if next WITA version is integrated
        receiveNotification("V7_TV");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.TV);

        // dynamic calculation of new delivery date based on order date
        // if the requested order-date is on the weekend, then the next week day is chosen
        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                // create new test variable for dynamic delivery date not on weekend
                context.setVariable("deliveryDate", WitaLineOrderSimulatorHelper.calculateDeliveryDate(context.getVariable("requestedCustomerDate")));
            }
        });

        sendNotification("V7_ABM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABM);

        sendNotification("V7_ERLM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ERLM);

        sendNotification("V7_ENTM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ENTM);
    }

}
