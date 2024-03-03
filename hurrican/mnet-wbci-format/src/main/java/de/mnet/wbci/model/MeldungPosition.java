/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.13
 */
package de.mnet.wbci.model;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wbci.validation.constraints.CheckMeldungscode;
import de.mnet.wbci.validation.groups.V1Meldung;

/**
 * Base class for item entries located in a notification message {@link Meldung}. Base entry holds text and code.
 *
 *
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_WBCI_MELDUNG_POSITION")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = MeldungPosition.POSITION_TYPE_FIELD, discriminatorType = DiscriminatorType.STRING)
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_WBCI_MELDUNG_POSITION_0", allocationSize = 1)
@CheckMeldungscode(groups = V1Meldung.class)
public abstract class MeldungPosition extends WbciEntity {

    private static final long serialVersionUID = -5429152498629450705L;

    public static final String POSITION_TYPE_FIELD = "position_typ";

    /**
     * Code and text
     */
    private MeldungsCode meldungsCode;
    private String meldungsText;

    @Transient
    public abstract MeldungPositionTyp getPositionTyp();

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "MELDUNG_CODE")
    public MeldungsCode getMeldungsCode() {
        return meldungsCode;
    }

    /**
     * @param meldungsCode
     */
    public void setMeldungsCode(MeldungsCode meldungsCode) {
        this.meldungsCode = meldungsCode;
    }

    @NotNull
    @Column(name = "MELDUNG_TEXT")
    @Size(min = 0, max = 255, message = "Der Meldungstext muss zwischen {min} und {max} Zeichen haben.")
    public String getMeldungsText() {
        return meldungsText;
    }

    /**
     * @param meldungsText
     */
    public void setMeldungsText(String meldungsText) {
        this.meldungsText = meldungsText;
    }

    @Override
    public String toString() {
        return "Meldungsposition [meldungscode=" + meldungsCode + ", meldungstext=" + meldungsText + "]";
    }
}
