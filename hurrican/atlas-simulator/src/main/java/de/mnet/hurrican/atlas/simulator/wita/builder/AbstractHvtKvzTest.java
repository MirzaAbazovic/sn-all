/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.03.14
 */
package de.mnet.hurrican.atlas.simulator.wita.builder;

import com.consol.citrus.dsl.definition.ReceiveMessageActionDefinition;

import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderVariableNames;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderXpathExpressions;

/**
 *
 */
public class AbstractHvtKvzTest extends AbstractWitaTest {

    @Override
    protected ReceiveMessageActionDefinition receiveOrderREQ(boolean enableDatabaseCheck) {
        return super.receiveOrderREQ(enableDatabaseCheck)
                .extractFromPayload(WitaLineOrderXpathExpressions.IS_BEREITSTELLUNG.getXpath(getServiceVersion()), WitaLineOrderVariableNames.IS_BEREITSTELLUNG)
                .extractFromPayload(WitaLineOrderXpathExpressions.IS_KUENDIGUNG.getXpath(getServiceVersion()), WitaLineOrderVariableNames.IS_KUENDIGUNG)
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(getServiceVersion()), WitaLineOrderVariableNames.REQUESTED_CUSTOMER_DATE)
                .extractFromPayload(WitaLineOrderXpathExpressions.CUSTOMER_ID.getXpath(getServiceVersion()), WitaLineOrderVariableNames.CUSTOMER_ID)
                .extractFromPayload(WitaLineOrderXpathExpressions.THIRD_PARTY_SALESMAN_CUSTOMER_ID.getXpath(getServiceVersion()), WitaLineOrderVariableNames.THIRD_PARTY_SALESMAN_CUSTOMER_ID)
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(getServiceVersion()), WitaLineOrderVariableNames.PRODUCT_IDENTIFIER);
    }

}
