/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.09.2011 10:13:12
 */
package de.mnet.hurrican.webservice.monline.endpoint;

import java.util.*;

import de.mnet.hurricanweb.netid.customer.CustomerOrderCombination;
import de.mnet.hurricanweb.netid.customer.impl.CustomerOrderCombinationImpl;

/**
 * Konvertiert ein {@link Set} von {@link CustomerOrderCombinationWrapper} in das fuer den Webservice verarbeitbare
 * Format: Ein Array von {@link CustomerOrderCombination}.
 *
 *
 * @since 16.09.2011
 */
class CustomerOrderCombinationConverter {

    private Set<CustomerOrderCombinationWrapper> source;

    /**
     * @return Returns the source.
     */
    protected Set<CustomerOrderCombinationWrapper> getSource() {
        return source;
    }

    /**
     * @param source The source to set.
     */
    CustomerOrderCombinationConverter setSource(Set<CustomerOrderCombinationWrapper> source) {
        this.source = source;
        return this;
    }

    /**
     * @param customerOrderCombination
     * @return
     */
    CustomerOrderCombination[] convert() {
        CustomerOrderCombination[] result = new CustomerOrderCombinationImpl[getSource().size()];
        int i = 0;
        for (CustomerOrderCombinationWrapper internalCombination : getSource()) {
            final CustomerOrderCombination combination = CustomerOrderCombination.Factory.newInstance();
            combination.setCustomerNo(internalCombination.getCustomerNo());
            combination.setOrderNo(internalCombination.getBillingNo());
            result[i] = combination;
            i++;
        }
        return result;
    }

} // end
