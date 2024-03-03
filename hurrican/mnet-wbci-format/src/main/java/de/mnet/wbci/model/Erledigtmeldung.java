/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.model;

import java.time.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import de.mnet.wbci.validation.constraints.CheckErlmTvTermin;
import de.mnet.wbci.validation.groups.V1MeldungStorno;
import de.mnet.wbci.validation.groups.V1MeldungTv;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("ERLM")
@CheckErlmTvTermin(groups = V1MeldungTv.class)
public class Erledigtmeldung extends Meldung<MeldungPositionErledigtmeldung> implements AenderungsIdAware, StornoIdAware {
    private static final long serialVersionUID = -1443623226122062111L;

    private LocalDate wechseltermin;
    private String stornoIdRef;
    private String aenderungsIdRef;

    public Erledigtmeldung() {
        super(MeldungTyp.ERLM);
    }

    @NotNull(groups = V1MeldungTv.class)
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    public LocalDate getWechseltermin() {
        return wechseltermin;
    }

    public void setWechseltermin(LocalDate wechseltermin) {
        this.wechseltermin = wechseltermin;
    }

    @NotEmpty(groups = V1MeldungStorno.class)
    @Column(name = "STORNO_ID_REF")
    public String getStornoIdRef() {
        return stornoIdRef;
    }

    public void setStornoIdRef(String stornoIdRef) {
        this.stornoIdRef = stornoIdRef;
    }


    @NotEmpty(groups = V1MeldungTv.class)
    @Column(name = "AENDERUNG_ID_REF")
    public String getAenderungsIdRef() {
        return aenderungsIdRef;
    }

    public void setAenderungsIdRef(String aenderungsIdRef) {
        this.aenderungsIdRef = aenderungsIdRef;
    }

    @Override
    public String toString() {
        return "Erledigtmeldung [wechselTermin=" + getWechseltermin() +
                ", aenderungsIdRef=" + getAenderungsIdRef() + ", stornoIdRef=" + getStornoIdRef() +
                ", toString=" + super.toString() + "]";
    }
}
