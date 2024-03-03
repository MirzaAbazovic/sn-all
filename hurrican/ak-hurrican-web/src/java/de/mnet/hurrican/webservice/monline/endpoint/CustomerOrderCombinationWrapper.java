/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.09.2011 10:15:03
 */
package de.mnet.hurrican.webservice.monline.endpoint;

/**
 * Repraesentiert eine Kombination aus Kundennummer und Auftragsnummer.
 *
 *
 * @since 16.09.2011
 */
public final class CustomerOrderCombinationWrapper {

    private Long customerNo;
    private Long billingNo;

    private CustomerOrderCombinationWrapper() {
    }

    /**
     * @return Returns the customerNo.
     */
    protected Long getCustomerNo() {
        return customerNo;
    }

    /**
     * @param customerNo The customerNo to set.
     */
    protected void setCustomerNo(Long customerNo) {
        this.customerNo = customerNo;
    }

    /**
     * @return Returns the billingNo.
     */
    protected Long getBillingNo() {
        return billingNo;
    }

    /**
     * @param billingNo The billingNo to set.
     */
    protected void setBillingNo(Long billingNo) {
        this.billingNo = billingNo;
    }

    /**
     * erstellt eine neue instance von {@link CustomerOrderCombinationWrapper}.
     *
     * @param customerNo
     * @param billingNo
     * @return
     */
    static CustomerOrderCombinationWrapper create(Long customerNo, Long billingNo) {
        CustomerOrderCombinationWrapper instance = new CustomerOrderCombinationWrapper();
        instance.setCustomerNo(customerNo);
        instance.setBillingNo(billingNo);
        return instance;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((billingNo == null) ? 0 : billingNo.hashCode());
        result = (prime * result) + ((customerNo == null) ? 0 : customerNo.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CustomerOrderCombinationWrapper)) {
            return false;
        }
        CustomerOrderCombinationWrapper other = (CustomerOrderCombinationWrapper) obj;
        if (billingNo == null) {
            if (other.billingNo != null) {
                return false;
            }
        }
        else if (!billingNo.equals(other.billingNo)) {
            return false;
        }
        if (customerNo == null) {
            if (other.customerNo != null) {
                return false;
            }
        }
        else if (!customerNo.equals(other.customerNo)) {
            return false;
        }
        return true;
    }

} // end
