/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.07.2011 13:47:55
 */
package de.mnet.wita.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import com.google.common.base.CharMatcher;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.WBCIVorabstimmungFax;
import de.augustakom.hurrican.validation.cc.LbzValid;
import de.mnet.wbci.model.ProduktGruppe;

/**
 * Modell zur Abbildung der Provider-Daten zu einer Carrierbestellung. (Notwendig fuer WITA Anbieterwechsel.)
 */
@Entity
@Table(name = "T_VORABSTIMMUNG")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_VORABSTIMMUNG_0", allocationSize = 1)
public class Vorabstimmung extends AbstractCCIDModel {

    private static final long serialVersionUID = 6711227400528300968L;

    private Long auftragId;
    private String endstelleTyp;

    private Carrier carrier;
    private CCAddress previousLocationAddress;
    /**
     * Produktgruppe zu der gewechselt wird
     */
    private ProduktGruppe produktGruppe;
    private String providerVtrNr;
    private String providerLbz;
    private String bestandssucheOnkz;
    private String bestandssucheDn;
    private String bestandssucheDirectDial;

    private WBCIVorabstimmungFax wbciVorabstimmungFax;

    @ManyToOne
    @JoinColumn(name = "VORABSTIMMUNG_ID")
    public WBCIVorabstimmungFax getWbciVorabstimmungFax() {
        return wbciVorabstimmungFax;
    }

    public void setWbciVorabstimmungFax(WBCIVorabstimmungFax wbciVorabstimmungFax) {
        this.wbciVorabstimmungFax = wbciVorabstimmungFax;
    }

    @Transient
    public boolean isBestandsSucheSet() {
        return StringUtils.isNotBlank(bestandssucheOnkz) && StringUtils.isNotBlank(bestandssucheDn);
    }

    @Transient
    public boolean isCarrierDtag() {
        return ((getCarrier() != null) && NumberTools.isIn(getCarrier().getId(), new Number[] { Carrier.ID_DTAG,
                Carrier.ID_TELEKOM_DEUTSCHLAND })) ? true : false;
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
    @Column(name = "AUFTRAG_ID")
    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    @NotNull
    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.PERSIST })
    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "PREVIOUSLOCATIONADDRESS_ID", nullable = true)
    public CCAddress getPreviousLocationAddress() {
        return previousLocationAddress;
    }

    public void setPreviousLocationAddress(CCAddress previousLocationAddress) {
        this.previousLocationAddress = previousLocationAddress;
    }

    @NotNull(message = "Bitte wählen sie eine Produkt-Gruppe aus.")
    @Enumerated(EnumType.STRING)
    @Column(name = "PRODUKT_GRUPPE", length = 10, columnDefinition = "varchar2(10)")
    public ProduktGruppe getProduktGruppe() {
        return produktGruppe;
    }

    public void setProduktGruppe(ProduktGruppe produktGruppe) {
        this.produktGruppe = produktGruppe;
    }

    @Size(max = 10)
    @Column(name = "PROVIDER_VTRNR")
    public String getProviderVtrNr() {
        return providerVtrNr;
    }

    public void setProviderVtrNr(String providerVtrNr) {
        this.providerVtrNr = providerVtrNr;
    }

    @Size(max = 35)
    @Column(name = "PROVIDER_LBZ")
    @LbzValid
    public String getProviderLbz() {
        return providerLbz;
    }

    public void setProviderLbz(String providerLbz) {
        if (StringUtils.isBlank(providerLbz)) {
            this.providerLbz = null;
        }
        else {
            this.providerLbz = providerLbz;
        }
    }

    @Size(max = 5)
    @Column(name = "BESTANDSSUCHE_ONKZ")
    public String getBestandssucheOnkz() {
        return bestandssucheOnkz;
    }

    public void setBestandssucheOnkz(String bestandssucheOnkz) {
        this.bestandssucheOnkz = bestandssucheOnkz;
    }

    @Transient
    public String getBestandssucheOnkzWithoutLeadingZeros() {
        return CharMatcher.is('0').trimLeadingFrom(bestandssucheOnkz);
    }

    @Size(max = 14)
    @Column(name = "BESTANDSSUCHE_DN")
    public String getBestandssucheDn() {
        return bestandssucheDn;
    }

    public void setBestandssucheDn(String bestandssucheDn) {
        this.bestandssucheDn = bestandssucheDn;
    }

    @Size(max = 6)
    @Column(name = "BESTANDSSUCHE_DIRECTDIAL")
    public String getBestandssucheDirectDial() {
        return bestandssucheDirectDial;
    }

    public void setBestandssucheDirectDial(String bestandssucheDirectDial) {
        this.bestandssucheDirectDial = bestandssucheDirectDial;
    }

}
