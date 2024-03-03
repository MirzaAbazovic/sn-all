/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.06.2011 15:42:17
 */
package de.mnet.wita.message;

import java.time.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import com.google.common.base.Function;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;

import de.mnet.wita.OutgoingWitaMessage;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.auftrag.AuftragsKenner;
import de.mnet.wita.message.auftrag.Kunde;
import de.mnet.wita.message.auftrag.Projekt;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.common.BsiDelayProtokollEintragSent;
import de.mnet.wita.message.common.BsiProtokollEintragSent;
import de.mnet.wita.validators.WitaVersionValid;
import de.mnet.wita.validators.reflection.WitaCharactersValid;

/**
 * Basisklasse für Aufträge, Stornos und Terminverschiebungen. Meldungen sinde keine MnetWitaRequests.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "requesttype", discriminatorType = DiscriminatorType.STRING)
@Table(name = "T_MWF_REQUEST")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_REQUEST_0", allocationSize = 1)
@WitaCharactersValid
public class MnetWitaRequest extends MwfEntity implements OutgoingWitaMessage, MessageWithSentToBsiFlag {

    private static final long serialVersionUID = 587604707436797590L;

    public static final Function<MnetWitaRequest, String> GET_EXTERNE_AUFTRAGSNUMMER = new Function<MnetWitaRequest, String>() {
        @Override
        public String apply(MnetWitaRequest input) {
            return input.getExterneAuftragsnummer();
        }
    };

    public static final String EXTERNE_AUFTRAGSNR_FIELD = "externeAuftragsnummer";
    private String externeAuftragsnummer;
    public static final String MWF_CREATION_DATE = "mwfCreationDate";
    private Date mwfCreationDate = new Date();
    private Kunde kunde;
    private Kunde besteller;
    public static final String GESCHAEFTSFALL = "geschaeftsfall";
    private Geschaeftsfall geschaeftsfall;
    public static final String CB_VORGANG_ID = "cbVorgangId";
    private Long cbVorgangId;
    private AuftragsKenner auftragsKenner;
    private Projekt projekt;

    public static final String REQUEST_STORNIERT = "requestWurdeStorniert";
    private Boolean requestWurdeStorniert = false;

    public static final String SENT_AT = "sentAt";
    /**
     * Zeitpunkt, wann der Request an die WITA-Queue geschickt wurde. null := noch nicht geschickt
     */
    private Date sentAt;
    /**
     * Flag, ob der Request an BSI als Protokolleintrag gesendet wurde.
     */
    private BsiProtokollEintragSent sentToBsi;
    /**
     * Flag, ob ein vorgehaltener Request an BSI als Protokolleintrag gesendet wurde.
     */
    private BsiDelayProtokollEintragSent delaySentToBsi;
    public static final String DELAY_SENT_TO_BSI_FIELD = "delaySentToBsi";
    private String cdmVersion;
    public static final String EARLIEST_SEND_DATE = "earliestSendDate";
    private Date earliestSendDate;

    public static MnetWitaRequest createEmptyRequestForExample() {
        MnetWitaRequest request = new MnetWitaRequest();
        request.setMwfCreationDate(null);
        request.setRequestWurdeStorniert(null);
        return request;
    }

    @Column(name = "CB_VORGANG_ID")
    @NotNull
    public Long getCbVorgangId() {
        return cbVorgangId;
    }

    public void setCbVorgangId(Long cbVorgangId) {
        this.cbVorgangId = cbVorgangId;
    }

    @NotNull
//    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Type(type="timestamp")
    @Column(name = "MWF_CREATION_DATE")
    public Date getMwfCreationDate() {
        return mwfCreationDate;
    }

    public void setMwfCreationDate(Date mwfCreationDate) {
        this.mwfCreationDate = mwfCreationDate;
    }

    /**
     * Returns the earliest send date for the request. If the {@code earliestSendDate} is null or lies in the past, then
     * the request can be sent, if it hasn't already been sent.
     *
     * @return
     */
//    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Type(type="timestamp")
    @Column(name = "EARLIEST_SEND_DATE")
    public Date getEarliestSendDate() {
        return earliestSendDate;
    }

    /**
     * Sets the earliest send date for the request. Setting the date to null or a date in the past will allow the
     * request to be sent.
     *
     * @param earliestSendDate
     */
    public void setEarliestSendDate(Date earliestSendDate) {
        this.earliestSendDate = earliestSendDate;
    }

    @Override
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "EXTERNE_AUFTRAGSNUMMER")
    public String getExterneAuftragsnummer() {
        return externeAuftragsnummer;
    }

    public void setExterneAuftragsnummer(String externeAuftragsnummer) {
        this.externeAuftragsnummer = externeAuftragsnummer;
    }

    @NotNull
    @Valid
    @ManyToOne
    @Cascade(value = { CascadeType.SAVE_UPDATE })
    public Kunde getKunde() {
        return kunde;
    }

    public void setKunde(Kunde kunde) {
        this.kunde = kunde;
    }

    @Valid
    @ManyToOne
    @Cascade(value = { CascadeType.SAVE_UPDATE })
    public Kunde getBesteller() {
        return besteller;
    }

    public void setBesteller(Kunde besteller) {
        this.besteller = besteller;
    }

    @NotNull
    @Valid
    @ManyToOne(targetEntity = Geschaeftsfall.class)
    @Cascade(value = { CascadeType.SAVE_UPDATE })
    public Geschaeftsfall getGeschaeftsfall() {
        return geschaeftsfall;
    }

    public void setGeschaeftsfall(Geschaeftsfall geschaeftsfall) {
        this.geschaeftsfall = geschaeftsfall;
    }

    @OneToOne(cascade = { javax.persistence.CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name = "AUFTRAGSKENNER_ID", nullable = true)
    @Valid
    public AuftragsKenner getAuftragsKenner() {
        return auftragsKenner;
    }

    public void setAuftragsKenner(AuftragsKenner auftragsKenner) {
        this.auftragsKenner = auftragsKenner;
    }

    @OneToOne(cascade = { javax.persistence.CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name = "PROJEKT_ID", nullable = true)
    @Valid
    public Projekt getProjekt() {
        return projekt;
    }

    public void setProjekt(Projekt projekt) {
        this.projekt = projekt;
    }

//    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Type(type="timestamp")
    @Column(name = "SENT_AT")
    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    @Column(name = "REQUEST_WURDE_STORNIERT")
    public Boolean getRequestWurdeStorniert() {
        return requestWurdeStorniert;
    }

    public void setRequestWurdeStorniert(Boolean requestWurdeStorniert) {
        this.requestWurdeStorniert = requestWurdeStorniert;
    }

    @Override
    @Enumerated(EnumType.STRING)
    @Column(name = "SENT_TO_BSI")
    public BsiProtokollEintragSent getSentToBsi() {
        return sentToBsi;
    }

    @Override
    public void setSentToBsi(BsiProtokollEintragSent sentToBsi) {
        this.sentToBsi = sentToBsi;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "DELAY_SENT_TO_BSI")
    public BsiDelayProtokollEintragSent getDelaySentToBsi() {
        return delaySentToBsi;
    }

    public void setDelaySentToBsi(BsiDelayProtokollEintragSent delaySentToBsi) {
        this.delaySentToBsi = delaySentToBsi;
    }

    @NotNull
    @WitaVersionValid
    @Transient
    @Override
    public WitaCdmVersion getCdmVersion() {
        return WitaCdmVersion.getCdmVersion(cdmVersion);
    }

    public void setCdmVersion(WitaCdmVersion version) {
        this.cdmVersion = version.getVersion();
    }

    @NotNull
    @Column(name = "CDM_VERSION")
    private String getCdmVersionAsString() {
        return cdmVersion;
    }

    private void setCdmVersionAsString(String cdmVersionAsString) {
        this.cdmVersion = cdmVersionAsString;
    }

    @Transient
    public MeldungsType getMeldungsTyp() {
        return null;
    }

    @Transient
    public boolean isAuftrag() {
        return false;
    }

    @Transient
    public boolean isTv() {
        return false;
    }

    @Transient
    public boolean isStorno() {
        return false;
    }

    @Transient
    public String getMeldungstypForIoArchive() {
        MeldungsType meldungsType = getMeldungsTyp();
        if (meldungsType == null) {
            return null;
        }
        return meldungsType.getLongName();
    }

    @Override
    public String toString() {
        return "MnetWitaRequest [externeAuftragsnummer=" + externeAuftragsnummer + ", cdmVersion=" + cdmVersion + ", mwfCreationDate="
                + mwfCreationDate + ", kunde=" + kunde + ", geschaeftsfall=" + geschaeftsfall + ", cbVorgangId="
                + cbVorgangId + ", auftragsKenner=" + auftragsKenner + ", projekt=" + projekt + ", sentAt=" + sentAt
                + ", sentToBsi=" + sentToBsi + ", earliestSendDate=" + earliestSendDate + "]";
    }
}
