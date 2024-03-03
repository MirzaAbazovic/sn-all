package de.mnet.hurrican.atlas.simulator.wita.builder;

import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.wita.message.MeldungsType;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderSimulatorHelper;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderXpathExpressions;

@Component("TAL_Providerwechsel_PV3_05")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class TAL_Providerwechsel_PV3_05 extends AbstractWitaTest {

    @Override
    public void configure() {
        receiveOrderREQ()
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(getServiceVersion()), "requestedCustomerDate");
        waitForIoArchiveRequestEntry();

        sendNotification("QEB");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB);

        // dynamic calculation of new delivery date based on order date
        // if the requested order-date is on the weekend, then the next week day is chosen
        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                // create new test variable for dynamic delivery date not on weekend
                context.setVariable("deliveryDate", WitaLineOrderSimulatorHelper.calculateDeliveryDate(context.getVariable("requestedCustomerDate")));
            }
        });

        sendNotification("ABBM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABBM);
    }

}
