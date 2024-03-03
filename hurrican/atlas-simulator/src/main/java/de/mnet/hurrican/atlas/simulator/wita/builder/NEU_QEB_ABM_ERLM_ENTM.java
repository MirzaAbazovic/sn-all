package de.mnet.hurrican.atlas.simulator.wita.builder;

import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderVariableNames;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderXpathExpressions;
import de.mnet.wita.message.MeldungsType;

/**
 *
 */
@Component("NEU_QEB_ABM_ERLM_ENTM")
@Scope("prototype")
@SuppressWarnings("squid:S00101")
public class NEU_QEB_ABM_ERLM_ENTM extends AbstractWitaTest {
    private static final String PRODUCT_IDENTIFIER_1 = "TAL; CuDA 4 Draht hbr (HVt)";
    private static final String PRODUCT_IDENTIFIER_2 = "TAL; CuDA 2 Draht hbr (HVt)";
    private static final String PRODUCT_IDENTIFIER_3 = "TAL; CuDA 2 Draht hbr (KVz)";

    @Override
    public void configure() {

        receiveOrderREQ()
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(getServiceVersion()), WitaLineOrderVariableNames.PRODUCT_IDENTIFIER)
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(getServiceVersion()), WitaLineOrderVariableNames.REQUESTED_CUSTOMER_DATE);
        waitForIoArchiveRequestEntry();

        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                String identifier = context.getVariable(WitaLineOrderVariableNames.PRODUCT_IDENTIFIER);
                if (PRODUCT_IDENTIFIER_1.equalsIgnoreCase(identifier)) {
                    context.setVariable(WitaLineOrderVariableNames.ABM_TEMPLATE, "1");
                }
                else if (PRODUCT_IDENTIFIER_2.equalsIgnoreCase(identifier) || PRODUCT_IDENTIFIER_3.equalsIgnoreCase(identifier)) {
                    context.setVariable(WitaLineOrderVariableNames.ABM_TEMPLATE, "2");
                }
                else {
                    throw new CitrusRuntimeException(String.format("Product Identifier '%s' not supported", identifier));
                }
            }
        });

        sendNotification("QEB");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.QEB);

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        conditional(
                sendNotification("ABM")
        ).when("${" + WitaLineOrderVariableNames.ABM_TEMPLATE + "} = 1");

        conditional(
                sendNotification("ABM_2")
        ).when("${" + WitaLineOrderVariableNames.ABM_TEMPLATE + "} = 2");

        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ABM);

        sendNotification("ERLM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ERLM);

        sendNotification("ENTM");
        waitForIoArchiveEntryByExtOrderNo(MeldungsType.ENTM);
    }

}
