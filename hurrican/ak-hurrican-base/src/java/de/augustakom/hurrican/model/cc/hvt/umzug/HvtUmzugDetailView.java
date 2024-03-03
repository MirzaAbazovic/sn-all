/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.05.2015 08:21
 */
package de.augustakom.hurrican.model.cc.hvt.umzug;

import java.util.*;
import javax.validation.constraints.*;

import de.augustakom.common.model.AbstractObservable;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.mnet.common.tools.DateConverterUtils;

/**
 *
 */
public class HvtUmzugDetailView extends AbstractObservable implements CCAuftragModel, Comparable {

    private Long id;
    private Long auftragId;
    private Long auftragNoOrig;
    private String produkt;
    private String auftragStatus;
    private Date witaBereitstellungAm;
    private String lbz;
    private String uevt;
    private String uevtNeu;
    private String endstellenTyp;
    private Long rangierIdNeu;
    private Long rangierAddIdNeu;
    private Boolean additionalOrder;
    private Boolean rangNeuErzeugt;
    private Boolean cpsAllowed;
    private String cpsStatus;
    private Long cpsTxId;
    private String vbz;
    private HvtUmzug hvtUmzug;
    private Boolean manualCc;

    public HvtUmzugDetailView() {
    }

    public HvtUmzugDetailView withHvtUmzugDetail(@NotNull final HvtUmzug hvtUmzug, @NotNull final HvtUmzugDetail detail) {
        withHvtUmzug(hvtUmzug);
        withId(detail.getId());
        withAuftragId(detail.getAuftragId());
        withAuftragNoOrig(detail.getAuftragNoOrig());
        withWitaBereitstellungAm((detail.getWitaBereitstellungAm() != null
                ? DateConverterUtils.asDate(detail.getWitaBereitstellungAm()) : null));
        withLbz(detail.getLbz());
        withUevt(detail.getUevtStiftAlt());
        withUevtNeu(detail.getUevtStiftNeu());
        withEndstellenTyp(detail.getEndstellenTyp());
        withRangierIdNeu(detail.getRangierIdNeu());
        withRangierAddIdNeu(detail.getRangierAddIdNeu());
        withAdditionalOrder(detail.getAdditionalOrder() != null ? detail.getAdditionalOrder() : Boolean.FALSE);
        withRangNeuErzeugt(detail.getRangNeuErzeugt() != null ? detail.getRangNeuErzeugt() : Boolean.FALSE);
        withCpsTxId(detail.getCpsTxId());
        withManualCc(detail.getManualCc());
        return this;
    }

    public HvtUmzug getHvtUmzug() {
        return hvtUmzug;
    }

    public HvtUmzugDetailView withHvtUmzug(HvtUmzug hvtUmzug) {
        this.hvtUmzug = hvtUmzug;
        return this;
    }

    public Long getId() {
        return id;
    }

    private HvtUmzugDetailView withId(Long id) {
        this.id = id;
        return this;
    }

    public Long getAuftragId() {
        return auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    private HvtUmzugDetailView withAuftragId(Long auftragId) {
        this.auftragId = auftragId;
        return this;
    }

    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    public HvtUmzugDetailView withAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
        return this;
    }

    public String getProdukt() {
        return produkt;
    }

    public HvtUmzugDetailView withProdukt(String produkt) {
        this.produkt = produkt;
        return this;
    }

    public String getAuftragStatus() {
        return auftragStatus;
    }

    public HvtUmzugDetailView withAuftragStatus(String auftragStatus) {
        this.auftragStatus = auftragStatus;
        return this;
    }

    public Date getWitaBereitstellungAm() {
        return witaBereitstellungAm == null ? null : new Date(witaBereitstellungAm.getTime());
    }

    private HvtUmzugDetailView withWitaBereitstellungAm(Date withWitaBereitstellungAm) {
        this.witaBereitstellungAm = withWitaBereitstellungAm;
        return this;
    }

    public String getLbz() {
        return lbz;
    }

    private HvtUmzugDetailView withLbz(String lbz) {
        this.lbz = lbz;
        return this;
    }

    public String getUevt() {
        return uevt;
    }

    private HvtUmzugDetailView withUevt(String uevt) {
        this.uevt = uevt;
        return this;
    }

    public String getUevtNeu() {
        return uevtNeu;
    }

    private HvtUmzugDetailView withUevtNeu(String uevtNeu) {
        this.uevtNeu = uevtNeu;
        return this;
    }

    public String getEndstellenTyp() {
        return endstellenTyp;
    }

    private HvtUmzugDetailView withEndstellenTyp(String endstellenTyp) {
        this.endstellenTyp = endstellenTyp;
        return this;
    }

    public Long getRangierIdNeu() {
        return rangierIdNeu;
    }

    private HvtUmzugDetailView withRangierIdNeu(Long rangierIdNeu) {
        this.rangierIdNeu = rangierIdNeu;
        return this;
    }

    public Long getRangierAddIdNeu() {
        return rangierAddIdNeu;
    }

    private HvtUmzugDetailView withRangierAddIdNeu(Long rangierAddIdNeu) {
        this.rangierAddIdNeu = rangierAddIdNeu;
        return this;
    }

    public Boolean getAdditionalOrder() {
        return additionalOrder;
    }

    private HvtUmzugDetailView withRangNeuErzeugt(Boolean rangNeuErzeugt) {
        this.rangNeuErzeugt = rangNeuErzeugt;
        return this;
    }

    public Boolean getRangNeuErzeugt() {
        return rangNeuErzeugt;
    }

    private HvtUmzugDetailView withAdditionalOrder(Boolean additionalOrder) {
        this.additionalOrder = additionalOrder;
        return this;
    }

    public Boolean getCpsAllowed() {
        return cpsAllowed;
    }

    public HvtUmzugDetailView withCpsAllowed(Boolean cpsAllowed) {
        this.cpsAllowed = cpsAllowed;
        return this;
    }

    public String getCpsStatus() {
        return cpsStatus;
    }

    public HvtUmzugDetailView withCpsStatus(String cpsStatus) {
        this.cpsStatus = cpsStatus;
        return this;
    }

    public Long getCpsTxId() {
        return cpsTxId;
    }

    public HvtUmzugDetailView withCpsTxId(Long cpsTxId) {
        this.cpsTxId = cpsTxId;
        return this;
    }

    public String getVbz() {
        return vbz;
    }

    public HvtUmzugDetailView withVbz(String vbz) {
        this.vbz = vbz;
        return this;
    }

    public Boolean getManualCc() {
        return manualCc;
    }

    public HvtUmzugDetailView withManualCc(Boolean manualCc) {
        this.manualCc = manualCc;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(auftragId, auftragNoOrig, produkt, auftragStatus, witaBereitstellungAm, lbz, uevt, uevtNeu,
                additionalOrder, rangierIdNeu, rangierAddIdNeu, rangNeuErzeugt, cpsAllowed, cpsStatus, cpsTxId,
                hvtUmzug, vbz, manualCc);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null || getClass() != obj.getClass()) {return false;}
        final HvtUmzugDetailView other = (HvtUmzugDetailView) obj;
        return Objects.equals(this.auftragId, other.auftragId)
                && Objects.equals(this.auftragNoOrig, other.auftragNoOrig)
                && Objects.equals(this.produkt, other.produkt)
                && Objects.equals(this.auftragStatus, other.auftragStatus)
                && Objects.equals(this.witaBereitstellungAm, other.witaBereitstellungAm)
                && Objects.equals(this.lbz, other.lbz)
                && Objects.equals(this.uevt, other.uevt)
                && Objects.equals(this.uevtNeu, other.uevtNeu)
                && Objects.equals(this.additionalOrder, other.additionalOrder)
                && Objects.equals(this.rangierIdNeu, other.rangierIdNeu)
                && Objects.equals(this.rangierAddIdNeu, other.rangierAddIdNeu)
                && Objects.equals(this.rangNeuErzeugt, other.rangNeuErzeugt)
                && Objects.equals(this.cpsAllowed, other.cpsAllowed)
                && Objects.equals(this.cpsStatus, other.cpsStatus)
                && Objects.equals(this.cpsTxId, other.cpsTxId)
                && Objects.equals(this.hvtUmzug, other.hvtUmzug)
                && Objects.equals(this.vbz, other.vbz)
                && Objects.equals(this.manualCc, other.manualCc);
    }

    /**
     * Sorting auf {@link #auftragNoOrig}, if not present, element will be at the end!
     */
    @Override
    public int compareTo(Object o) {
        if (o instanceof HvtUmzugDetailView) {
            HvtUmzugDetailView ov = (HvtUmzugDetailView) o;
            return Long.compare(this.getAuftragNoOrig() != null ? this.getAuftragNoOrig() : Long.MAX_VALUE,
                    ov.getAuftragNoOrig() != null ? ov.getAuftragNoOrig() : Long.MAX_VALUE);
        }
        return Integer.compare(this.hashCode(), o.hashCode());
    }

}
