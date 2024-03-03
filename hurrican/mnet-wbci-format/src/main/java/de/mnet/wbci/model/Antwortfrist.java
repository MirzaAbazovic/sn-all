/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.14
 */
package de.mnet.wbci.model;

import javax.persistence.*;

/**
 * Enthaelt die Antwortfrist je nach Request-Status.
 *
 *
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity(name = "de.mnet.wbci.model.Antwortfrist")
@Table(name = "T_WBCI_ANTWORTFRIST")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_WBCI_ANTWORTFRIST_0", allocationSize = 1)
public class Antwortfrist extends WbciEntity {

    private static final long serialVersionUID = -1987516313480745155L;

    public static final String STATUS = "requestStatus";
    public static final String TYP = "typ";

    private RequestTyp typ;
    private WbciRequestStatus requestStatus;
    private Long fristInStunden;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYP", length = 25, columnDefinition = "varchar2(25)")
    public RequestTyp getTyp() {
        return typ;
    }

    public void setTyp(RequestTyp typ) {
        this.typ = typ;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 25, columnDefinition = "varchar2(25)")
    public WbciRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(WbciRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    @Column(name = "FRIST_IN_STUNDEN")
    public Long getFristInStunden() {
        return fristInStunden;
    }

    public void setFristInStunden(Long fristInStunden) {
        this.fristInStunden = fristInStunden;
    }

    @Transient
    public Long getFristInTagen() {
        return fristInStunden != null ? (long) Math.ceil(fristInStunden.doubleValue() / 24) : null;
    }

    @Override
    public String toString() {
        return "Antwortfrist{" +
                "typ=" + typ +
                ", requestStatus=" + requestStatus +
                ", fristInStunden=" + fristInStunden +
                '}';
    }

}
