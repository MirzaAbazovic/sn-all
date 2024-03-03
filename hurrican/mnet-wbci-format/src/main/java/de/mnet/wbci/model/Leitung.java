/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.13
 */
package de.mnet.wbci.model;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 *
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity(name = "de.mnet.wbci.model.Leitung")
@Table(name = "T_WBCI_LEITUNG")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_WBCI_LEITUNG_0", allocationSize = 1)
public class Leitung extends WbciEntity {

    private static final long serialVersionUID = -4710276644848180023L;

    private String vertragsnummer;
    private String lineId;

    @Size(min = 1, max = 10)
    @Column(name = "VERTRAGSNUMMER")
    public String getVertragsnummer() {
        return vertragsnummer;
    }

    public void setVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
    }

    @Size(min = 7, max = 21)
    @Column(name = "LINE_ID")
    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    @Override
    public String toString() {
        return "Leitung [lineId=" + getLineId() + ", vertragsnummer=" + getVertragsnummer() + "]";
    }
}
