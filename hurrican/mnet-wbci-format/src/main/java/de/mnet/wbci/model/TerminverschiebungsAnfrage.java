/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.model;

import java.time.*;
import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

import de.mnet.wbci.validation.constraints.CheckKundenwunschtermin;
import de.mnet.wbci.validation.constraints.CheckKundenwunschterminIgnoringNextDay;
import de.mnet.wbci.validation.constraints.CheckKundenwunschterminNotInRange;
import de.mnet.wbci.validation.constraints.CheckTvTerminNotBroughtForward;
import de.mnet.wbci.validation.groups.V1RequestTv;
import de.mnet.wbci.validation.groups.V1RequestTvVaKueMrn;
import de.mnet.wbci.validation.groups.V1RequestTvVaKueMrnWarn;
import de.mnet.wbci.validation.groups.V1RequestTvVaKueOrn;
import de.mnet.wbci.validation.groups.V1RequestTvVaKueOrnWarn;
import de.mnet.wbci.validation.groups.V1RequestTvVaRrnp;

/**
 * Request-Objekt fuer eine WBCI Terminverschiebung (TV). <br> Eine WBCI-Terminverschiebung darf nur auf eine mit
 * RUEM-VA bestaetigte {@link VorabstimmungsAnfrage} ausgeloest werden. Die TV bezieht sich den urspruenglichen Request
 * und auch dessen Geschaeftsfall.
 *
 * @param <GF>
 */
@Entity
@DiscriminatorValue(RequestTyp.TV_NAME)
@CheckTvTerminNotBroughtForward(groups = { V1RequestTv.class })
public class TerminverschiebungsAnfrage<GF extends WbciGeschaeftsfall> extends WbciRequest<GF> {

    private static final long serialVersionUID = -62913653493695704L;

    private String vorabstimmungsIdRef;
    private String aenderungsId;
    private LocalDate tvTermin;
    private PersonOderFirma endkunde;

    @Override
    @Transient
    public RequestTyp getTyp() {
        return RequestTyp.TV;
    }

    /**
     * <b>Achtung:</b> Feld wird nur fuer eingehende Terminverschiebungen verwendet, um die Referenz auf die
     * Vorabstimmung zu definieren, auf die sich die TV bezieht.
     */
    @Transient
    public String getVorabstimmungsIdRef() {
        return vorabstimmungsIdRef;
    }

    public void setVorabstimmungsIdRef(String vorabstimmungsIdRef) {
        this.vorabstimmungsIdRef = vorabstimmungsIdRef;
    }

    @NotNull
    @Column(name = WbciRequest.COL_NAME_AENDERUNGS_ID)
    public String getAenderungsId() {
        return aenderungsId;
    }

    public void setAenderungsId(String aenderungsId) {
        this.aenderungsId = aenderungsId;
    }

    @NotNull(groups = { V1RequestTv.class }, message = "darf nicht leer sein")
    @Column(name = "TV_TERMIN")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @CheckKundenwunschterminIgnoringNextDay(groups = { V1RequestTvVaRrnp.class }, minimumLeadTime = 1)
    @CheckKundenwunschtermin(groups = { V1RequestTvVaKueMrn.class, V1RequestTvVaKueOrn.class }, minimumLeadTime = 5)
    @CheckKundenwunschterminNotInRange(groups = { V1RequestTvVaKueMrnWarn.class, V1RequestTvVaKueOrnWarn.class }, from = 5, to = 7)
    public LocalDate getTvTermin() {
        return tvTermin;
    }

    public void setTvTermin(LocalDate tvTermin) {
        this.tvTermin = tvTermin;
    }

    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, targetEntity = PersonOderFirma.class)
    @JoinColumn(name = WbciRequest.COL_NAME_ENDKUNDE_ID, nullable = true)
    @Valid
    /***
     * According to WBCI-FAQs No. 9, the field should only be set in the FAX process.
     */
    @Null(groups = V1RequestTv.class)
    public PersonOderFirma getEndkunde() {
        return endkunde;
    }

    public void setEndkunde(PersonOderFirma endkunde) {
        this.endkunde = endkunde;
    }

    @Override
    public String toString() {
        return "TerminverschiebungsAnfrage{" +
                ", typ='" + getTyp() + "'" +
                ", vorabstimmungsIdRef='" + vorabstimmungsIdRef + "'" +
                ", aenderungsId='" + aenderungsId + "'" +
                ", tvTermin='" + tvTermin + "'" +
                ", endkunde='" + endkunde + "'" +
                ", toString='" + super.toString() + "'" +
                '}';
    }

}
