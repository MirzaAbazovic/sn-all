/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.06.2007 09:29:59
 */
package de.augustakom.hurrican.model.cc.tal;

import java.time.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.iface.ICBVorgangStatusModel;
import de.augustakom.hurrican.validation.cc.CBVorgangAnswerAnsweredAtValid;
import de.augustakom.hurrican.validation.cc.CBVorgangAnswerOkValid;
import de.augustakom.hurrican.validation.cc.CBVorgangAnswerReturnValid;
import de.augustakom.hurrican.validation.cc.CBVorgangAnswerVorgabeMnetValid;
import de.augustakom.hurrican.validation.cc.CBVorgangCBNotNullExceptRexMkValid;
import de.augustakom.hurrican.validation.cc.CBVorgangNoReturnOnOpenCbv;
import de.augustakom.hurrican.validation.cc.CBVorgangSubmitDateValid;
import de.augustakom.hurrican.validation.cc.CBVorgangSubmitWithoutReturnValid;
import de.mnet.common.tools.DateConverterUtils;

/**
 * Modell fuer die Protokollierung von el. TAL-Bestellungen. <br> Das Modell wird sowohl als Basis fuer die el.
 * TAL-Bestellung Richtung DTAG verwendet, als auch fuer interne TAL-Bestellungen. <br>
 */
@Entity
@Table(name = "T_CB_VORGANG")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CBV_TYPE")
@DiscriminatorValue("ESAA_INTERN")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_CB_VORGANG_0", allocationSize = 1)
@CBVorgangSubmitDateValid
@CBVorgangSubmitWithoutReturnValid
@CBVorgangAnswerOkValid
@CBVorgangAnswerAnsweredAtValid
@CBVorgangAnswerReturnValid
@CBVorgangAnswerVorgabeMnetValid
@CBVorgangNoReturnOnOpenCbv
@CBVorgangCBNotNullExceptRexMkValid
public class CBVorgang extends AbstractCCIDModel implements CCAuftragModel, ICBVorgangStatusModel, CBVorgangReturnModel {

    public static final String KLAERFALL_BEMERKUNGS_SEPARATOR = "----------------------";
    private static final long serialVersionUID = -2187360346092030418L;

    /**
     * Wert fuer 'typ', der eine Neu-Bestellung definiert.
     */
    public static final Long TYP_NEU = 8000L;
    /**
     * Wert fuer 'typ', der eine Kuendigung definiert.
     */
    public static final Long TYP_KUENDIGUNG = 8001L;
    /**
     * Wert fuer 'typ', der eine Stornierung definiert. Nur für ESAA und interne Bestellungen relevant
     */
    public static final Long TYP_STORNO = 8002L;
    /**
     * Wert fuer 'typ', der eine TAL-Nutzungsaenderung definiert.
     */
    public static final Long TYP_NUTZUNGSAENDERUNG = 8003L;
    /**
     * Wert fuer 'typ', der einen TAL-Anbieterwechsel definiert.
     */
    public static final Long TYP_ANBIETERWECHSEL = 8004L;
    /**
     * Wert fuer 'typ', der einen TAL-Portwechsel (LAE, LMAE, SER-POW) definiert.
     */
    public static final Long TYP_PORTWECHSEL = 8005L;
    /**
     * Wert fuer 'typ', der einen REX-MK definiert.
     */
    public static final Long TYP_REX_MK = 8006L;
    /**
     * Wert fuer 'typ', der einen Wechsel von HVt nach KVz TAL definiert.
     */
    public static final Long TYP_HVT_KVZ = 8007L;

    /**
     * Geschaeftsfaelle für ESAA und interne Bestellungen
     */
    public static final ImmutableList<Long> ESAA_AND_INTERN_TYPEN = ImmutableList.of(TYP_NEU, TYP_KUENDIGUNG,
            TYP_STORNO, TYP_NUTZUNGSAENDERUNG);

    /**
     * Geschaeftsfaelle für WITA-Bestellungen
     */
    public static final ImmutableList<Long> WITA_TYPEN = ImmutableList.of(TYP_NEU, TYP_KUENDIGUNG,
            TYP_ANBIETERWECHSEL, TYP_PORTWECHSEL);
    /**
     * Geschaeftsfaelle für WITA-Bestellungen an KVz Standorten
     */
    public static final ImmutableList<Long> WITA_KVZ_TYPEN = ImmutableList.of(TYP_NEU, TYP_KUENDIGUNG, TYP_ANBIETERWECHSEL, TYP_HVT_KVZ);

    /**
     * Geschaeftsfaelle fuer WITA-Bestellungen, die eine TAL bestellen (nicht kuendigen!)
     */
    public static final ImmutableList<Long> BESTELL_TYPEN = ImmutableList.of(TYP_NEU, TYP_ANBIETERWECHSEL,
            TYP_PORTWECHSEL, TYP_NUTZUNGSAENDERUNG);

    /**
     * Status-ID, wenn der Vorgang eingestellt wurde (8100).
     */
    public static final Long STATUS_SUBMITTED = 8100L;

    /**
     * Status-ID, wenn der Vorgang an den Carrier uebermittelt wurde (8200).
     */
    public static final Long STATUS_TRANSFERRED = 8200L;

    /**
     * Status-ID, wenn der Carrier eine Rueckmeldung geliefert hat (8300).
     */
    public static final Long STATUS_ANSWERED = 8300L;

    /**
     * Status-ID fuer komplett geschlossene CB-Vorgaenge (8400).
     */
    public static final Long STATUS_CLOSED = 8400L;

    /**
     * Status-ID fuer stornierte CB-Vorgaenge (8499). Nur für ESAA und evtl. interne Bestllungen relevant
     */
    public static final Long STATUS_STORNO = 8499L;

    static final int RETURN_BEMERKUNGEN_SIZE = 2048;

    public static final Predicate<CBVorgang> CB_ID_NULL_PREDICATE = new Predicate<CBVorgang>() {

        @Override
        public boolean apply(CBVorgang input) {
            return input.getCbId() == null;
        }
    };

    public static final Predicate<CBVorgang> IS_REXMK_PREDICATE = new Predicate<CBVorgang>() {
        @Override
        public boolean apply(CBVorgang input) {
            return input.isRexMk();
        }
    };

    public static final Function<CBVorgang, Long> GET_CB_ID = new Function<CBVorgang, Long>() {
        @Override
        public Long apply(CBVorgang input) {
            return input.getCbId();
        }
    };

    private Long cbId;
    private Long auftragId;
    private Long carrierId;
    public static final String TYP = "typ";
    private Long typ;
    private Long usecaseId;
    private Boolean vierDraht;
    public static final String STATUS = "status";
    private Long status;
    private String bezeichnungMnet;
    private Date vorgabeMnet;
    private String montagehinweis;
    private Boolean anbieterwechselTkg46;
    /**
     * Business Key fuer WITA-Workflows
     */
    private String carrierRefNr;
    public static final String RETURN_OK = "returnOk";
    private Boolean returnOk;
    private String returnLBZ;
    private String returnVTRNR;
    private String returnAQS;
    private String returnLL;
    private Boolean returnKundeVorOrt;
    private Date returnRealDate;
    private String returnBemerkung;
    private Date submittedAt;
    private Date answeredAt;
    private Long userId;
    // @Transient
    public String terminReservierungsId;
    private AKUser bearbeiter;
    private String carrierBearbeiter;
    private String carrierKennungAbs;
    private Long exmId;
    private Long exmRetFehlertyp;
    private String statusBemerkung;
    /**
     * Ist nur bei H16 TALs gesetzt.
     */
    private String returnMaxBruttoBitrate;
    private Set<CBVorgangSubOrder> subOrders = new HashSet<>();
    private Date wiedervorlageAm;
    public static final String AUTOMATION = "automation";
    private Boolean automation;
    private Set<CBVorgangAutomationError> automationErrors = new HashSet<>();
    private Boolean klaerfall = Boolean.FALSE;
    private String klaerfallBemerkung;
    private Long gfTypInternRefId;
    private TalRealisierungsZeitfenster talRealisierungsZeitfenster;

    public CBVorgang() {
        // required by Hibernate
    }

    @Transient
    public boolean isAenderung() {
        return NumberTools.equal(getTyp(), TYP_PORTWECHSEL);
    }

    @Transient
    public boolean isAnbieterwechsel() {
        return NumberTools.equal(getTyp(), TYP_ANBIETERWECHSEL);
    }

    @Transient
    public boolean isNeuschaltung() {
        return NumberTools.equal(getTyp(), TYP_NEU);
    }

    @Transient
    public boolean isRexMk() {
        return NumberTools.equal(getTyp(), TYP_REX_MK);
    }

    /**
     * Prueft, ob es sich bei dem CB-Vorgang um eine Kuendigung handelt.
     *
     * @return true, wenn der Vorgang eine Kuendigung ist.
     */
    @Transient
    public boolean isKuendigung() {
        return NumberTools.equal(getTyp(), TYP_KUENDIGUNG);
    }

    /**
     * Prueft, ob es sich bei dem CB-Vorgang um eine Stornierung handelt.
     *
     * @return true, wenn der Vorgang eine Stornierung ist.
     */
    @Transient
    public boolean isStorno() {
        return NumberTools.equal(getTyp(), TYP_STORNO);
    }

    /**
     * Prueft, ob fuer den CB-Vorgang eine Antwort vom Carrier vorliegt.
     *
     * @return true, wenn der Carrier den CB-Vorgang beantwortet hat.
     */
    @Transient
    public boolean hasAnswer() {
        boolean answer = NumberTools.equal(getStatus(), STATUS_ANSWERED);
        if (!answer && getAnsweredAt() != null) {
            answer = true;
        }
        return answer;
    }

    @Transient
    @Override
    public boolean hasAutomationErrors() {
        return automationErrors != null && !automationErrors.isEmpty();
    }

    @Column(name = "CB_ID")
    public Long getCbId() {
        return cbId;
    }

    public void setCbId(Long cbId) {
        this.cbId = cbId;
    }

    @Column(name = "CARRIER_ID")
    @NotNull
    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    @Column(name = "TYP")
    @NotNull
    public Long getTyp() {
        return typ;
    }

    public void setTyp(Long typ) {
        this.typ = typ;
    }

    @Override
    @Column(name = "STATUS")
    @NotNull
    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    @Column(name = "BEZEICHNUNG_MNET")
    public String getBezeichnungMnet() {
        return bezeichnungMnet;
    }

    public void setBezeichnungMnet(String bezeichnungMnet) {
        this.bezeichnungMnet = bezeichnungMnet;
    }

    @Override
    @Column(name = "RET_LBZ")
    public String getReturnLBZ() {
        return returnLBZ;
    }

    @Override
    public void setReturnLBZ(String returnLBZ) {
        this.returnLBZ = returnLBZ;
    }

    @Override
    @Column(name = "RET_VTRNR")
    public String getReturnVTRNR() {
        return returnVTRNR;
    }

    @Override
    public void setReturnVTRNR(String returnVTRNR) {
        this.returnVTRNR = returnVTRNR;
    }

    @Override
    @Column(name = "RET_AQS")
    public String getReturnAQS() {
        return returnAQS;
    }

    @Override
    public void setReturnAQS(String returnAQS) {
        this.returnAQS = returnAQS;
    }

    @Override
    @Column(name = "RET_LL")
    public String getReturnLL() {
        return returnLL;
    }

    @Override
    public void setReturnLL(String returnLL) {
        this.returnLL = returnLL;
    }

    @Column(name = "RET_KUNDE_VOR_ORT")
    public Boolean getReturnKundeVorOrt() {
        return returnKundeVorOrt;
    }

    public void setReturnKundeVorOrt(Boolean returnKundeVorOrt) {
        this.returnKundeVorOrt = returnKundeVorOrt;
    }

    @Column(name = "RET_REAL_DATE")
    @Temporal(TemporalType.DATE)
    public Date getReturnRealDate() {
        return returnRealDate;
    }

    public void setReturnRealDate(Date returnRealDate) {
        this.returnRealDate = returnRealDate;
    }

    @Column(name = "RET_BEMERKUNG")
    public String getReturnBemerkung() {
        return returnBemerkung;
    }

    public void setReturnBemerkung(String returnBemerkung) {
        if (returnBemerkung != null && returnBemerkung.length() > RETURN_BEMERKUNGEN_SIZE) {
            returnBemerkung = returnBemerkung.substring(0, RETURN_BEMERKUNGEN_SIZE);
        }
        this.returnBemerkung = returnBemerkung;
    }

    @Column(name = "VORGABE_MNET")
    @Temporal(TemporalType.DATE)
    @NotNull
    public Date getVorgabeMnet() {
        return vorgabeMnet;
    }

    public void setVorgabeMnet(Date vorgabeMnet) {
        this.vorgabeMnet = vorgabeMnet;
    }

    /**
     * Gibt an, ob der Carrier die Bestellung positiv (TRUE) oder negativ (FALSE) zurueck gemeldet hat.
     *
     * @return Returns the returnOk.
     */
    @Override
    @Column(name = "RET_OK")
    public Boolean getReturnOk() {
        return returnOk;
    }

    public void setReturnOk(Boolean returnOk) {
        this.returnOk = returnOk;
    }

    @Column(name = "SUBMITTED_AT")
    @Temporal(TemporalType.DATE)
    public Date getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Date submittedAt) {
        this.submittedAt = submittedAt;
    }

    @Column(name = "ANSWERED_AT")
    @Temporal(TemporalType.DATE)
    public Date getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(Date answeredAt) {
        this.answeredAt = answeredAt;
    }

    @Override
    @Column(name = "AUFTRAG_ID")
    @NotNull
    public Long getAuftragId() {
        return auftragId;
    }

    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * ID des (M-net) Users, der den CB-Vorgang ausgeloest hat.
     *
     * @return Returns the userId.
     */
    @Column(name = "USER_ID")
    @NotNull
    public Long getUserId() {
        return userId;
    }

    /**
     * Nur für Hibernate. Aus Anwendung heraus sollte {@link #setBearbeiter(AKUser)} verwendet werden.
     *
     * @param userId
     */
    private void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Der Bearbeiter des Tasks. Gespeichert wird nur die ID, siehe {@link #getUserId()}.<br> <b>ACHTUNG:</b> Muss
     * manuell im DAO nachgeladen werden.
     *
     * @return
     */
    @Transient
    public AKUser getBearbeiter() {
        return bearbeiter;
    }

    /**
     * Setzt den Bearbeiter (transient). Intern wird auch die (persistente) userId gesetzt.
     *
     * @param bearbeiter
     */
    public void setBearbeiter(AKUser bearbeiter) {
        this.bearbeiter = bearbeiter;
        setUserId(bearbeiter == null ? null : bearbeiter.getId());
    }

    /**
     * Gibt den Namen des Bearbeiters beim Ziel-Carrier an, der den Vorgang bearbeitet hat.
     *
     * @return Returns the carrierBearbeiter.
     */
    @Column(name = "CARRIER_BEARBEITER")
    public String getCarrierBearbeiter() {
        return carrierBearbeiter;
    }

    public void setCarrierBearbeiter(String carrierBearbeiter) {
        this.carrierBearbeiter = carrierBearbeiter;
    }

    /**
     * ESAA: Bemerkung Mnet, Wita Montagehinweis
     */
    @Column(name = "MONTAGEHINWEIS")
    public String getMontagehinweis() {
        return montagehinweis;
    }

    public void setMontagehinweis(String montagehinweis) {
        this.montagehinweis = montagehinweis;
    }

    /**
     * Transient TerminReservierungsId because it is mandatory for KFT acceptance tests however we do not use it in
     * productive code at the moment.
     *
     * @return
     */
    @Transient
    public String getTerminReservierungsId() {
        return terminReservierungsId;
    }

    public void setTerminReservierungsId(String terminReservierungsId) {
        this.terminReservierungsId = terminReservierungsId;
    }

    @Column(name = "ANBIETERWECHSEL_TKG46")
    @NotNull
    public Boolean getAnbieterwechselTkg46() {
        return anbieterwechselTkg46;
    }

    public void setAnbieterwechselTkg46(Boolean abw) {
        this.anbieterwechselTkg46 = abw;
    }

    @Column(name = "USECASE_ID")
    public Long getUsecaseId() {
        return usecaseId;
    }

    public void setUsecaseId(Long usecaseId) {
        this.usecaseId = usecaseId;
    }

    /**
     * Gibt die Carrier-Kennung des absendenden Carrier an.
     *
     * @return Returns the carrierKennungAbs.
     */
    @Column(name = "CARRIER_KENNUNG_ABS")
    public String getCarrierKennungAbs() {
        return carrierKennungAbs;
    }

    public void setCarrierKennungAbs(String carrierKennungAbs) {
        this.carrierKennungAbs = carrierKennungAbs;
    }

    /**
     * Referenz-ID aus dem externen TAL-Bestellungs Modul (aktuell MNETCALL).
     *
     * @return Returns the exmId.
     */
    @Column(name = "EXM_ID")
    public Long getExmId() {
        return exmId;
    }

    public void setExmId(Long exmId) {
        this.exmId = exmId;
    }

    /**
     * Rueckmelde-Typ aus dem externen TAL-Bestellungs Modul (aktuell MNETCALL). Der Typ definiert detailliert, ob die
     * RM positiv od. negativ ist.
     *
     * @return Returns the exmRetFehlertyp.
     */
    @Column(name = "EXM_RET_FEHLERTYP")
    public Long getExmRetFehlertyp() {
        return exmRetFehlertyp;
    }

    public void setExmRetFehlertyp(Long exmRetFehlertyp) {
        this.exmRetFehlertyp = exmRetFehlertyp;
    }

    @Column(name = "CARRIER_REF_NR")
    public String getCarrierRefNr() {
        return carrierRefNr;
    }

    public void setCarrierRefNr(String carrierRefNr) {
        this.carrierRefNr = carrierRefNr;
    }

    @Column(name = "STATUS_BEMERKUNG")
    public String getStatusBemerkung() {
        return statusBemerkung;
    }

    public void setStatusBemerkung(String statusBemerkung) {
        this.statusBemerkung = statusBemerkung;
    }

    @Column(name = "VIER_DRAHT")
    public Boolean getVierDraht() {
        return vierDraht;
    }

    public void setVierDraht(Boolean vierDraht) {
        this.vierDraht = vierDraht;
    }

    @Transient
    public boolean isVierDraht() {
        return Boolean.TRUE.equals(vierDraht);
    }

    @OneToMany(fetch = FetchType.EAGER)
    @Cascade({ CascadeType.DELETE_ORPHAN, CascadeType.ALL })
    @JoinColumn(name = "CB_VORGANG_ID")
    @Fetch(value = FetchMode.JOIN)
    @BatchSize(size = 100)
    public Set<CBVorgangSubOrder> getSubOrders() {
        return subOrders;
    }

    public void setSubOrders(Set<CBVorgangSubOrder> subOrders) {
        this.subOrders = subOrders;
    }

    public void addSubOrder(CBVorgangSubOrder toAdd) {
        subOrders.add(toAdd);
    }

    @Column(name = "RETURN_MAX_BRUTTO_BITRATE")
    public String getReturnMaxBruttoBitrate() {
        return returnMaxBruttoBitrate;
    }

    public void setReturnMaxBruttoBitrate(String returnMaxBruttoBitrate) {
        this.returnMaxBruttoBitrate = returnMaxBruttoBitrate;
    }


    @Column(name = "WIEDERVORLAGE_AM")
    //    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getWiedervorlageAm() {
        return wiedervorlageAm;
    }

    public void setWiedervorlageAm(Date wiedervorlageAm) {
        this.wiedervorlageAm = wiedervorlageAm;
    }

    @Transient
    public LocalDateTime getWiedervorlageAmAsLocalDateTime() {
        return getWiedervorlageAm() != null ? Instant.ofEpochMilli(getWiedervorlageAm().getTime()).atZone((ZoneId.systemDefault())).toLocalDateTime() : null;
    }

    public void setWiedervorlageAm(LocalDateTime wiedervorlageAmLD) {
        final Date dt = wiedervorlageAmLD != null ? DateConverterUtils.asDate(wiedervorlageAmLD) : null;
        this.setWiedervorlageAm(dt);
    }

    @Column(name = "KLAERFALL")
    public Boolean isKlaerfall() {
        return klaerfall;
    }

    public void setKlaerfall(Boolean klaerfall) {
        this.klaerfall = klaerfall;
    }

    @Column(name = "KLAERFALL_BEMERKUNG")
    public String getKlaerfallBemerkung() {
        return klaerfallBemerkung;
    }

    public void setKlaerfallBemerkung(String klaerfallBemerkung) {
        this.klaerfallBemerkung = klaerfallBemerkung;
    }

    @Column(name = "AUTOMATION")
    @NotNull
    public Boolean getAutomation() {
        return automation;
    }

    public void setAutomation(Boolean automation) {
        this.automation = automation;
    }


    @Column(name = "GF_TYP_INTERN_REF_ID")
    public Long getGfTypInternRefId() {
        return gfTypInternRefId;
    }

    public void setGfTypInternRefId(Long gfTypInternRefId) {
        this.gfTypInternRefId = gfTypInternRefId;
    }

    @OneToMany(fetch = FetchType.EAGER)
    @Cascade({ CascadeType.DELETE_ORPHAN, CascadeType.ALL })
    @JoinColumn(name = "CBVORGANG_ID", nullable = false)
    @Fetch(value = FetchMode.JOIN)
    @BatchSize(size = 1000)
    public Set<CBVorgangAutomationError> getAutomationErrors() {
        return automationErrors;
    }

    public void setAutomationErrors(Set<CBVorgangAutomationError> automationErrors) {
        this.automationErrors = automationErrors;
    }

    public void addAutomationError(Exception e) {
        if (automationErrors == null) {
            automationErrors = new HashSet<>();
        }
        automationErrors.add(new CBVorgangAutomationError(e));
    }

    @Column(name = "TAL_REAL_TIMESLOT")
    @Enumerated(EnumType.STRING)
    public TalRealisierungsZeitfenster getTalRealisierungsZeitfenster() {
        return talRealisierungsZeitfenster;
    }

    public void setTalRealisierungsZeitfenster(TalRealisierungsZeitfenster talRealisierungsZeitfenster) {
        this.talRealisierungsZeitfenster = talRealisierungsZeitfenster;
    }

    /**
     * Setzt den Status des CBVorgangs auf geschlossen
     */
    public void close() {
        setStatus(STATUS_CLOSED);
    }

    /**
     * Setzt die Status Felder (Status, AnsweredAt, ReturnOK) fuer einen offenen CBVorgang
     */
    public void open(Long newStatus) {
        setStatus(newStatus);
        setAnsweredAt(null);
        setReturnOk(null);
    }

    /**
     * Setzt die Status Felder (Status, AnsweredAt, ReturnOK) fuer beantwortet
     *
     * @param returnOk positive Rueckmeldung: true, negative Rueckmeldung: false
     */
    public void answer(boolean returnOk) {
        setStatus(CBVorgang.STATUS_ANSWERED);
        setAnsweredAt(new Date());
        setReturnOk(returnOk);
    }

    /**
     * Wurde der Vorgang schon beantwortet?
     */
    @Transient
    public boolean isAnswered() {
        return getAnsweredAt() != null;
    }

    /**
     * Is der Vorgang im Status ANSWERED?
     */
    @Transient
    public boolean isInStatusAnswered() {
        return STATUS_ANSWERED.equals(status);
    }

    /**
     * Ist der Vorgang geschlossen?
     */
    @Transient
    public boolean isClosed() {
        return STATUS_CLOSED.equals(getStatus());
    }

    /**
     * Wurde schon ein Realisierungsdatum gesetzt?
     */
    @Transient
    public boolean hasReturnRealDate() {
        return getReturnRealDate() != null;
    }

    /**
     * Ist das Realisierungsdatum unterschiedlich zum Kundenwunschtermin bei positiver Rueckmeldung?
     */
    @Transient
    public boolean returnRealDateDiffers() {
        return BooleanTools.nullToFalse(getReturnOk())
                && !isStorno()
                && (!DateTools.isDateEqual(getVorgabeMnet(), getReturnRealDate()) || DateTools.isDateBefore(
                getReturnRealDate(), new Date()));
    }

    @Transient
    public boolean isTurnOnInPast() {
        return DateTools.isDateBefore(this.getReturnRealDate(), new Date());
    }

    /**
     * Entfernt 'leere' Werte aus einem String mit der Leitungslaenge bzw. dem Aderquerschnitt. <br> Beispiel: <br>
     * 55/1342/0000/0000/0000/0000/0000/0000/0000/0000 wird zu 55/1342
     *
     * @param toStrip
     * @return
     */
    public static String stripLlOrAqs(String toStrip) {
        if (StringUtils.isNotBlank(toStrip)) {
            String[] subst = StringUtils.split(toStrip, "/");
            if (subst != null) {
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < subst.length; i++) {
                    String sub = subst[i];
                    if (!StringUtils.containsOnly(sub, "0")) {
                        if (i > 0) {
                            result.append("/");
                        }
                        result.append(sub);
                    }
                }
                return result.toString();
            }
        }
        return null;
    }
}
