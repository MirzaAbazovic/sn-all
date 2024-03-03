/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2011 13:59:50
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.CBVorgangAutomationError;
import de.augustakom.hurrican.model.cc.tal.CBVorgangSubOrder;
import de.augustakom.hurrican.model.cc.tal.TalRealisierungsZeitfenster;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

import de.mnet.common.tools.DateConverterUtils;

@SuppressWarnings("unused")
public class AbstractCBVorgangBuilder<BUILDER extends AbstractCBVorgangBuilder<BUILDER, ENTITY>, ENTITY extends CBVorgang>
        extends AbstractCCIDModelBuilder<BUILDER, ENTITY> {

    protected Boolean klaerfall = false;
    protected String klaerfallBemerkung;
    private CarrierbestellungBuilder cbBuilder = null;
    private Long cbId = null;
    private AuftragBuilder auftragBuilder = null;
    private Long auftragId = null;
    private Long carrierId = Carrier.ID_DTAG;
    private Long typ = CBVorgang.TYP_NEU;
    private CBUsecaseBuilder usecaseBuilder = null;
    private Long status = CBVorgang.STATUS_SUBMITTED;
    private String bezeichnungMnet = null;
    private Date vorgabeMnet = DateTools.plusWorkDays(5);
    private String montagehinweis = null;
    private String carrierRefNr = null;
    private Boolean returnOk = null;
    private String returnLBZ = null;
    private String returnVTRNR = null;
    private String returnAQS = null;
    private String returnLL = null;
    private Boolean returnKundeVorOrt = null;
    private Date returnRealDate = null;
    private String returnBemerkung = null;
    private Date submittedAt = new Date();
    private Date answeredAt = null;
    private AKUser bearbeiter = new AKUser(176L, "test", "test", null, null, null, null, null, null, true, null);
    private Long userId = bearbeiter.getId();
    private String carrierBearbeiter = null;
    private String carrierKennungAbs = null;
    private Long exmId = null;
    private Long exmRetFehlertyp = null;
    private String statusBemerkung = null;
    private String returnMaxBruttoBitrate;
    private Set<CBVorgangSubOrder> subOrders;
    private Date wiedervorlageAm = null;
    private Boolean anbieterwechselTkg46 = Boolean.FALSE;
    private Boolean automation = Boolean.FALSE;
    private Set<CBVorgangAutomationError> automationErrors;
    private Long gfTypInternRefId;
    private TalRealisierungsZeitfenster talRealisierungsZeitfenster;

    public BUILDER withCbBuilder(CarrierbestellungBuilder cbBuilder) {
        this.cbBuilder = cbBuilder;
        return getActualClass();
    }

    public BUILDER withCbId(Long cbId) {
        this.cbId = cbId;
        return getActualClass();
    }

    public BUILDER withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return getActualClass();
    }

    public BUILDER withAuftragId(Long auftragId) {
        this.auftragId = auftragId;
        return getActualClass();
    }

    protected BUILDER getActualClass() {
        @SuppressWarnings("unchecked")
        BUILDER actualClass = (BUILDER) this;
        return actualClass;
    }

    public AuftragBuilder getAuftragBuilder() {
        return auftragBuilder;
    }

    public BUILDER withCarrierId(Long carrierId) {
        this.carrierId = carrierId;
        return getActualClass();
    }

    public BUILDER withTyp(Long typ) {
        this.typ = typ;
        return getActualClass();
    }

    public BUILDER withUsecaseBuilder(CBUsecaseBuilder usecaseBuilder) {
        this.usecaseBuilder = usecaseBuilder;
        return getActualClass();
    }

    public BUILDER withStatus(Long status) {
        this.status = status;
        return getActualClass();
    }

    public BUILDER withBezeichnungMnet(String bezeichnungMnet) {
        this.bezeichnungMnet = bezeichnungMnet;
        return getActualClass();
    }

    public BUILDER withVorgabeMnet(Date vorgabeMnet) {
        this.vorgabeMnet = vorgabeMnet;
        return getActualClass();
    }

    public BUILDER withMontagehinweis(String montagehinweis) {
        this.montagehinweis = montagehinweis;
        return getActualClass();
    }

    public BUILDER withCarrierRefNr(String carrierRefNr) {
        this.carrierRefNr = carrierRefNr;
        return getActualClass();
    }

    public BUILDER withReturnOk(Boolean returnOk) {
        this.returnOk = returnOk;
        return getActualClass();
    }

    public BUILDER withReturnLBZ(String returnLBZ) {
        this.returnLBZ = returnLBZ;
        return getActualClass();
    }

    public BUILDER withReturnVTRNR(String returnVTRNR) {
        this.returnVTRNR = returnVTRNR;
        return getActualClass();
    }

    public BUILDER withReturnAQS(String returnAQS) {
        this.returnAQS = returnAQS;
        return getActualClass();
    }

    public BUILDER withReturnLL(String returnLL) {
        this.returnLL = returnLL;
        return getActualClass();
    }

    public BUILDER withReturnKundeVorOrt(Boolean returnKundeVorOrt) {
        this.returnKundeVorOrt = returnKundeVorOrt;
        return getActualClass();
    }

    public BUILDER withReturnRealDate(Date returnRealDate) {
        this.returnRealDate = returnRealDate;
        return getActualClass();
    }

    public BUILDER withReturnBemerkung(String returnBemerkung) {
        this.returnBemerkung = returnBemerkung;
        return getActualClass();
    }

    public BUILDER withSubmittedAt(Date submittedAt) {
        this.submittedAt = submittedAt;
        return getActualClass();
    }

    public BUILDER withAnsweredAt(Date answeredAt) {
        this.answeredAt = answeredAt;
        return getActualClass();
    }

    public BUILDER withBearbeiter(AKUser bearbeiter) {
        this.bearbeiter = bearbeiter;
        this.userId = bearbeiter.getId();
        return getActualClass();
    }

    public BUILDER withCarrierBearbeiter(String carrierBearbeiter) {
        this.carrierBearbeiter = carrierBearbeiter;
        return getActualClass();
    }

    public BUILDER withCarrierKennungAbs(String carrierKennungAbs) {
        this.carrierKennungAbs = carrierKennungAbs;
        return getActualClass();
    }

    public BUILDER withExmId(Long exmId) {
        this.exmId = exmId;
        return getActualClass();
    }

    public BUILDER withRandomExmId() {
        this.exmId = randomLong(999999);
        return getActualClass();
    }

    public BUILDER withExmRetFehlertyp(Long exmRetFehlertyp) {
        this.exmRetFehlertyp = exmRetFehlertyp;
        return getActualClass();
    }

    public BUILDER withStatusBemerkung(String statusBemerkung) {
        this.statusBemerkung = statusBemerkung;
        return getActualClass();
    }

    public BUILDER withSubOrders(Set<CBVorgangSubOrder> subOrders) {
        this.subOrders = subOrders;
        return getActualClass();
    }

    public BUILDER withAutomationErrors(Set<CBVorgangAutomationError> automationErrors) {
        this.automationErrors = automationErrors;
        return getActualClass();
    }

    public BUILDER withStatusClosed() {
        return withStatus(CBVorgang.STATUS_CLOSED).withReturnLBZ("123").withReturnRealDate(new Date())
                .withSubmittedAt(new Date()).withReturnOk(Boolean.TRUE).withAnsweredAt(new Date());
    }

    public BUILDER withReturnMaxBruttoBitrate(String returnMaxBruttoBitrate) {
        this.returnMaxBruttoBitrate = returnMaxBruttoBitrate;
        return getActualClass();
    }

    public BUILDER withWiedervorlageAm(Date wiedervorlageAm) {
        this.wiedervorlageAm = wiedervorlageAm;
        return getActualClass();
    }

    public BUILDER withWiedervorlageAm(LocalDateTime wiedervorlageAm) {
        final Date dt = DateConverterUtils.asDate(wiedervorlageAm);
        return this.withWiedervorlageAm(dt);
    }

    public BUILDER withAnbieterwechselTkg46(Boolean anbieterwechselTkg46) {
        this.anbieterwechselTkg46 = anbieterwechselTkg46;
        return getActualClass();
    }

    public BUILDER withAutomation(Boolean automation) {
        this.automation = automation;
        return getActualClass();
    }

    public BUILDER withKlaerfall(Boolean isKlaerfall) {
        this.klaerfall = isKlaerfall;
        return getActualClass();
    }

    public BUILDER withKlaerfallBemerkung(String klaerfallBemerkung) {
        this.klaerfallBemerkung = klaerfallBemerkung;
        return getActualClass();
    }

    public BUILDER withGfTypInternRefId(Long gfTypInternRefId) {
        this.gfTypInternRefId = gfTypInternRefId;
        return getActualClass();
    }

    public BUILDER withTalRealisierungsZeitfenster(TalRealisierungsZeitfenster talRealisierungsZeitfenster) {
        this.talRealisierungsZeitfenster = talRealisierungsZeitfenster;
        return getActualClass();
    }

}
