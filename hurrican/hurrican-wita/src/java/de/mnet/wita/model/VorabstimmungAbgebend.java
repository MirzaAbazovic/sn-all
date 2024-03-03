/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.03.2012 09:48:58
 */
package de.mnet.wita.model;

import java.time.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.Carrier;

/**
 * Modell zur Abbildung der Provider-Daten zu einer Carrierbestellung. (Notwendig fuer WITA Abgebenden
 * Providerwechsel.)
 */
@Entity
@Table(name = "T_VORABSTIMMUNG_ABGEBEND")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_VORABST_ABGD_0", allocationSize = 1)
public class VorabstimmungAbgebend extends AbstractCCIDModel {

    private static final long serialVersionUID = -6218791033524970624L;

    public static final Boolean RUECKMELDUNG_POSITIVE = true;
    public static final Boolean RUECKMELDUNG_NEGATIVE = false;

    private Long auftragId;
    private String endstelleTyp;

    private Carrier carrier;
    private LocalDate abgestimmterProdiverwechselTermin;
    private Boolean rueckmeldung;
    private String bemerkung;
    private String vorabstimmungsIDFax;

    @Column(name = "VORABSTIMMUNG_FAX")
    public String getVorabstimmungsIDFax() {
        return vorabstimmungsIDFax;
    }

    public void setVorabstimmungsIDFax(String vorabstimmungsIDFax) {
        this.vorabstimmungsIDFax = vorabstimmungsIDFax;
    }

    @NotNull
    @Column(name = "AUFTRAG_ID")
    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @NotNull
    @Column(name = "ES_TYP", length = 1)
    public String getEndstelleTyp() {
        return endstelleTyp;
    }

    public void setEndstelleTyp(String endstelleTyp) {
        this.endstelleTyp = endstelleTyp;
    }

    @NotNull
    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.PERSIST })
    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    @NotNull
    @Column(name = "ABGESTIMMTER_PV_TERMIN")
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    public LocalDate getAbgestimmterProdiverwechsel() {
        return abgestimmterProdiverwechselTermin;
    }

    public void setAbgestimmterProdiverwechsel(LocalDate abgestimmterProdiverwechsel) {
        this.abgestimmterProdiverwechselTermin = abgestimmterProdiverwechsel;
    }

    @Column(name = "BEMERKUNG")
    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    @NotNull
    @Column(name = "RUECKMELDUNG")
    public Boolean getRueckmeldung() {
        return rueckmeldung;
    }

    public void setRueckmeldung(Boolean rueckmeldung) {
        this.rueckmeldung = rueckmeldung;
    }
}
