/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.2009 14:11:10
 */
package de.augustakom.hurrican.model.billing;

import java.util.*;


/**
 *
 */
@SuppressWarnings("unused")
public class RufnummerBuilder extends BillingEntityBuilder<RufnummerBuilder, Rufnummer> {

    private Long dnNo = null;
    private Long dnNoOrig = null;
    private Long auftragNoOrig = null;
    private String onKz = null;

    private String dnBase = null;
    private String directDial = null;
    private String rangeFrom = null;
    private String rangeTo = null;
    private Long dnSize = null;
    private boolean mainNumber = false;
    private Long blockNoOrig = null;
    private Long oeNoOrig = null;
    private String portMode = null;
    private Date realDate = null;
    private Date wishDate = null;
    private String remarks = null;
    private String state = null;
    private String futureCarrier = null;
    private String lastCarrier = null;
    private String actCarrier = null;
    private DNTNB futureCarrierDnTnb;
    private String futureCarrierPortKennung;
    private DNTNB lastCarrierDnTnb;
    private String lastCarrierPortKennung;
    private DNTNB actCarrierDnTnb;
    private String actCarrierPortKennung;
    private Date portierungVon = null;
    private Date portierungBis = null;
    private Boolean histLast = null;
    private String histStatus = null;
    private Integer histCnt = null;
    private Date gueltigVon = null;
    private Date gueltigBis = null;
    private Boolean nonBillable = null;

    @Override
    protected void beforeBuild() {
        if (actCarrier != null) {
            this.actCarrierDnTnb = new DNTNBBuilder()
                    .withTnb(actCarrier).withPortKennung(actCarrierPortKennung)
                    .setPersist(getPersist()).build();
        }

        if (lastCarrier != null) {
            this.lastCarrierDnTnb = new DNTNBBuilder()
                    .withTnb(lastCarrier).withPortKennung(lastCarrierPortKennung)
                    .setPersist(getPersist()).build();
        }

        if (futureCarrier != null) {
            this.futureCarrierDnTnb = new DNTNBBuilder()
                    .withTnb(futureCarrier).withPortKennung(futureCarrierPortKennung)
                    .setPersist(getPersist()).build();
        }
        
        super.beforeBuild();
    }

    public RufnummerBuilder withDialnumber(String onkz, String dnBase, String directDial, String rangeFrom, String rangeTo) {
        this.onKz = onkz;
        this.dnBase = dnBase;
        this.directDial = directDial;
        this.rangeFrom = rangeFrom;
        this.rangeTo = rangeTo;
        return this;
    }

    public RufnummerBuilder withLastCarrier(String lastCarrier, String lastCarrierPortKennung) {
        this.lastCarrier = lastCarrier;
        this.lastCarrierPortKennung = lastCarrierPortKennung;
        return this;
    }

    public RufnummerBuilder withRangeFrom(String rangeFrom) {
        this.rangeFrom = rangeFrom;
        return this;
    }

    public RufnummerBuilder withRangeTo(String rangeTo) {
        this.rangeTo = rangeTo;
        return this;
    }

    public RufnummerBuilder withDnNo(Long dnNo) {
        this.dnNo = dnNo;
        return this;
    }

    public RufnummerBuilder withRandomDnNo() {
        this.dnNo = randomLong(10000);
        return this;
    }

    public RufnummerBuilder withRandomDnNoOrig() {
        this.dnNoOrig = randomLong(50000000);
        return this;
    }

    public RufnummerBuilder withDnNoOrig(Long dnNoOrig) {
        this.dnNoOrig = dnNoOrig;
        return this;
    }

    public RufnummerBuilder withAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
        return this;
    }

    public RufnummerBuilder withOnKz(String onKz) {
        this.onKz = onKz;
        return this;
    }

    public RufnummerBuilder withDnBase(String dnBase) {
        this.dnBase = dnBase;
        return this;
    }

    public RufnummerBuilder withActCarrier(String actCarrier, String actCarrierPortKennung) {
        this.actCarrier = actCarrier;
        this.actCarrierPortKennung = actCarrierPortKennung;
        return this;
    }

    public RufnummerBuilder withRemarks(String remarks) {
        this.remarks = remarks;
        return this;
    }

    public RufnummerBuilder withHistCnt(Integer histCnt) {
        this.histCnt = histCnt;
        return this;
    }

    public RufnummerBuilder withHistLast(Boolean histLast) {
        this.histLast = histLast;
        return this;
    }

    public RufnummerBuilder withHistStatus(String histStatus) {
        this.histStatus = histStatus;
        return this;
    }

    public RufnummerBuilder withOeNoOrig(Long oeNoOrig) {
        this.oeNoOrig = oeNoOrig;
        return this;
    }

    public RufnummerBuilder withGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
        return this;
    }

    public RufnummerBuilder withGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
        return this;
    }

    public RufnummerBuilder withDirectDial(String directDial) {
        this.directDial = directDial;
        return this;
    }

    public RufnummerBuilder withFutureCarrier(String futureCarrier, String futureCarrierPortKennung) {
        this.futureCarrier = futureCarrier;
        this.futureCarrierPortKennung = futureCarrierPortKennung;
        return this;
    }

    public RufnummerBuilder withRealDate(Date realDate) {
        this.realDate = realDate;
        return this;
    }

    public RufnummerBuilder withPortMode(String portMode) {
        this.portMode = portMode;
        return this;
    }

    public RufnummerBuilder withMainNumber(boolean mainNumber) {
        this.mainNumber = mainNumber;
        return this;
    }

    public RufnummerBuilder withNonBillable(Boolean nonBillable) {
        this.nonBillable = nonBillable;
        return this;
    }

    public RufnummerBuilder withDnSize(Long dnSize) {
        this.dnSize = dnSize;
        return this;
    }
}
