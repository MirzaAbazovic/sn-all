/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.12.2011 08:47:20
 */
package de.augustakom.hurrican.model.cc.housing;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.OnDelete;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 * Modell stellt eine Zuordnung eines bestimmten Transponders zu einer Transponder-Gruppe dar.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_TRANSPONDER")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_TRANSPONDER_0", allocationSize = 1)
public class Transponder extends AbstractCCIDModel {

    public static final String TRANSPONDER_ID = "transponderId";
    private Long transponderId;
    public static final String CUSTOMER_FIRST_NAME = "customerFirstName";
    private String customerFirstName;
    public static final String CUSTOMER_LAST_NAME = "customerLastName";
    private String customerLastName;
    private TransponderGroup transponderGroup;

    @Column(name = "TRANSPONDER_ID")
    @NotNull
    public Long getTransponderId() {
        return transponderId;
    }

    public void setTransponderId(Long transponderId) {
        this.transponderId = transponderId;
    }

    @Column(name = "CUST_FIRSTNAME")
    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    @Column(name = "CUST_LASTNAME")
    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    @ManyToOne
    @JoinColumn(name = "TRANSPONDER_GROUP_ID", updatable = false)
    @OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    public TransponderGroup getTransponderGroup() {
        return transponderGroup;
    }

    public void setTransponderGroup(TransponderGroup transponderGroup) {
        this.transponderGroup = transponderGroup;
    }

}
