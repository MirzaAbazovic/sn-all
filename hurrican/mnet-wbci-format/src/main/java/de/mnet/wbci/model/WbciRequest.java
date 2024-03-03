package de.mnet.wbci.model;

import java.time.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_WBCI_REQUEST")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYP", discriminatorType = DiscriminatorType.STRING)
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_WBCI_REQUEST_0", allocationSize = 1)
public abstract class WbciRequest<GF extends WbciGeschaeftsfall> extends WbciEntity implements WbciMessage<RequestTyp> {

    private static final long serialVersionUID = 5000027076944995339L;

    protected static final String COL_NAME_STORNO_GRUND = "STORNO_GRUND";
    protected static final String COL_NAME_AENDERUNGS_ID = "AENDERUNGS_ID";
    protected static final String COL_NAME_STANDORT_ID = "STANDORT_ID";
    protected static final String COL_NAME_ENDKUNDE_ID = "ENDKUNDE_ID";

    public static final String CREATION_DATE = "creationDate";
    public static final String WBCI_GESCHAEFTSFALL = "wbciGeschaeftsfall";
    public static final String IO_TYPE = "ioType";
    public static final String CHANGE_ID = "aenderungsId";
    public static final String LAST_MELDUNG_TYPE = "lastMeldungType";
    public static final String REQUEST_STATUS = "requestStatus";

    private Date creationDate;
    private Date processedAt;
    private Date updatedAt;
    private Date sendAfter;
    private IOType ioType;
    private GF wbciGeschaeftsfall;
    private LocalDate answerDeadline;
    private Boolean isMnetDeadline;

    private WbciRequestStatus requestStatus;

    private String lastMeldungCodes;
    private MeldungTyp lastMeldungType;
    private Date lastMeldungDate;

    // is only set for incoming requests and is not persisted with the request
    private CarrierCode absender;

    @NotNull
//    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Type(type="timestamp")
    @Column(name = "CREATION_DATE")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date mwfCreationDate) {
        this.creationDate = mwfCreationDate;
    }

    //@Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Type(type="timestamp")
    @Column(name = "PROCESSED_AT")
    public Date getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Date processedAt) {
        this.processedAt = processedAt;
    }

    /**
     * Returns the last update date of the request. This date is set when the request is created and whenever a Meldung
     * correlating to the request is sent or received.
     *
     * @return
     */
    @NotNull
//    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Type(type="timestamp")
    @Column(name = "UPDATED_AT")
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    @Column(name = "ANSWER_DEADLINE")
    public LocalDate getAnswerDeadline() {
        return answerDeadline;
    }

    public void setAnswerDeadline(LocalDate answerDeadline) {
        this.answerDeadline = answerDeadline;
    }

    @Column(name = "IS_MNET_DEADLINE")
    public Boolean getIsMnetDeadline() {
        return isMnetDeadline;
    }

    public void setIsMnetDeadline(Boolean isMnetDeadline) {
        this.isMnetDeadline = isMnetDeadline;
    }

    @ManyToOne(targetEntity = WbciGeschaeftsfall.class)
    @Cascade(value = { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    @JoinColumn(name = "GESCHAEFTSFALL_ID")
    @Valid
    public GF getWbciGeschaeftsfall() {
        return wbciGeschaeftsfall;
    }

    public void setWbciGeschaeftsfall(GF wbciGeschaeftsfall) {
        this.wbciGeschaeftsfall = wbciGeschaeftsfall;
    }

    @Override
    @Transient
    public String getVorabstimmungsId() {
        return wbciGeschaeftsfall.getVorabstimmungsId();
    }

    @Override
    @Transient
    public CarrierCode getEKPPartner() {
        return this.getWbciGeschaeftsfall().getEKPPartner();
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "IO_TYPE", length = 10, columnDefinition = "varchar2(10)")
    public IOType getIoType() {
        return ioType;
    }

    public void setIoType(IOType ioType) {
        this.ioType = ioType;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 25, columnDefinition = "varchar2(25)")
    public WbciRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(WbciRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    @Override
    @Transient
    public abstract RequestTyp getTyp();

    /**
     * @return the last meldung codes recorded for this Request.
     */
    @Column(name = "LAST_MELDUNG_CODES")
    @Size(max = 255)
    public String getLastMeldungCodes() {
        return lastMeldungCodes;
    }

    public void setLastMeldungCodes(String lastMeldungCodes) {
        this.lastMeldungCodes = lastMeldungCodes;
    }

    /**
     * @return the {@link MeldungTyp} of the last {@link Meldung} recoreded for this request.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "LAST_MELDUNG_TYPE")
    public MeldungTyp getLastMeldungType() {
        return lastMeldungType;
    }

    public void setLastMeldungType(MeldungTyp lastMeldungType) {
        this.lastMeldungType = lastMeldungType;
    }

    /**
     * @return the {@link LocalDateTime} of the last {@link Meldung} recorded for this request.
     */
//    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Type(type="timestamp")
    @Column(name = "LAST_MELDUNG_DATE")
    public Date getLastMeldungDate() {
        return lastMeldungDate;
    }

    public void setLastMeldungDate(Date lastMeldungDate) {
        this.lastMeldungDate = lastMeldungDate;
    }

    /**
     * {@inheritDoc}
     */
    //@Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Type(type="timestamp")
    @Column(name = "SEND_AFTER")
    @Override
    public Date getSendAfter() {
        return sendAfter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSendAfter(Date sendAfter) {
        this.sendAfter = sendAfter;
    }

    /**
     * <b>Achtung:</b> Feld wird nur fuer eingehende Anfragen verwendet, um der Absender zu identifizieren, der die
     * Anfrage geschickt hat. <br /> Falls die Anfrage nicht einem Geschaeftsfall zugeordnet werden kann (z.B. die VA-ID
     * Hurrican unbekannt ist), muss eine ABBM auf die Anfrage zurueckgeschickt werden (und dafuer braucht man der
     * Absender).
     *
     * @return das CarrierCode des Absenders
     */
    @Transient
    public CarrierCode getAbsender() {
        return absender;
    }

    public void setAbsender(CarrierCode absender) {
        this.absender = absender;
    }

    @Override
    public String toString() {
        return "WbciRequest{" +
                "creationDate=" + creationDate +
                ", processedAt=" + processedAt +
                ", updatedAt=" + updatedAt +
                ", sendAfter=" + sendAfter +
                ", ioType=" + ioType +
                ", wbciGeschaeftsfall=" + wbciGeschaeftsfall +
                ", lastMeldungCodes=" + lastMeldungCodes +
                ", lastMeldungType=" + lastMeldungType +
                ", lastMeldungDate=" + lastMeldungDate +
                ", type='" + getTyp() + '\'' +
                ", requestStatus='" + getRequestStatus() + '\'' +
                ", answerDeadline='" + answerDeadline + '\'' +
                ", isMnetDeadline ='" + isMnetDeadline + '\'' +
                ", toString='" + super.toString() + '\'' +
                '}';
    }
}
