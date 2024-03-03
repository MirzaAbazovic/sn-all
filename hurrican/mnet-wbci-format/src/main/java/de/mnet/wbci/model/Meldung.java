package de.mnet.wbci.model;

import java.time.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import de.mnet.wbci.converter.MeldungsCodeConverter;
import de.mnet.wbci.validation.groups.V1Meldung;

/**
 * Base type for notification messages in WBCI.
 *
 * @param <POS> respective notification item entry type
 *
 */
@Entity(name = "de.mnet.wbci.model.Meldung")
@Table(name = "T_WBCI_MELDUNG")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = Meldung.MELDUNG_TYPE_FIELD, discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("DEFAULT")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_WBCI_MELDUNG_0", allocationSize = 1)
public abstract class Meldung<POS extends MeldungPosition> extends WbciEntity implements WbciMessage<MeldungTyp> {

    private static final long serialVersionUID = -4015350102286267153L;

    public static final String MELDUNG_TYPE_FIELD = "typ";
    public static final String WBCI_GESCHAEFTSFALL = "wbciGeschaeftsfall";

    private final MeldungTyp typ;
    private CarrierCode absender;
    private WbciVersion wbciVersion;
    private Date processedAt;
    private Date sendAfter;
    private IOType ioType;
    /**
     * Notification items
     */
    private Set<POS> meldungsPositionen = new HashSet<>();
    private WbciGeschaeftsfall wbciGeschaeftsfall;
    private String vorabstimmungsIdRef;

    /**
     * Constructor using meldung typ.
     *
     * @param typ
     */
    public Meldung(MeldungTyp typ) {
        this.typ = typ;
    }

    /**
     * default constructor
     */
    public Meldung() {
        this(MeldungTyp.DEFAULT);
    }

    @NotNull(groups = V1Meldung.class, message = "darf nicht leer sein")
    @ManyToOne(targetEntity = WbciGeschaeftsfall.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "GESCHAEFTSFALL_ID")
    @Valid
    public WbciGeschaeftsfall getWbciGeschaeftsfall() {
        return wbciGeschaeftsfall;
    }

    public void setWbciGeschaeftsfall(WbciGeschaeftsfall wbciGeschaeftsfall) {
        this.wbciGeschaeftsfall = wbciGeschaeftsfall;
    }

    @Override
    @Transient
    public String getVorabstimmungsId() {
        return wbciGeschaeftsfall != null ? wbciGeschaeftsfall.getVorabstimmungsId() : getVorabstimmungsIdRef();
    }

    @Size(min = 1, message = "Es muss mindestens eine Meldungsposition gesetzt sein.")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = MeldungPosition.class)
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 1000)
    @JoinColumn(name = "MELDUNG_ID")
    @Valid
    public Set<POS> getMeldungsPositionen() {
        return meldungsPositionen;
    }

    public void setMeldungsPositionen(Set<POS> meldungsPositionen) {
        this.meldungsPositionen = meldungsPositionen;
    }

    /**
     * <b>Achtung:</b> Feld wird nur fuer eingehende Meldungen verwendet, um die Referenz auf die Vorabstimmung zu
     * definieren, auf die sich die Meldung bezieht.
     *
     * @return
     */
    @Transient
    public String getVorabstimmungsIdRef() {
        return vorabstimmungsIdRef;
    }

    public void setVorabstimmungsIdRef(String vorabstimmungsIdRef) {
        this.vorabstimmungsIdRef = vorabstimmungsIdRef;
    }

    /**
     * is persisted by {@link DiscriminatorColumn} in {@link Meldung}
     */
    @Override
    @NotNull
    @Transient
    public MeldungTyp getTyp() {
        return typ;
    }

    @Enumerated(EnumType.STRING)
    public CarrierCode getAbsender() {
        return absender;
    }

    public void setAbsender(CarrierCode absender) {
        this.absender = absender;
    }

    @Enumerated(EnumType.STRING)
    public WbciVersion getWbciVersion() {
        return wbciVersion;
    }

    public void setWbciVersion(WbciVersion wbciVersion) {
        this.wbciVersion = wbciVersion;
    }

//    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "PROCESSED_AT")
    @Type(type="timestamp")
    public Date getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Date processedAt) {
        this.processedAt = processedAt;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "IO_TYPE", length = 10, columnDefinition = "varchar2(10)")
    @Override
    public IOType getIoType() {
        return ioType;
    }

    @Override
    public void setIoType(IOType ioType) {
        this.ioType = ioType;
    }

    /**
     * Gets all Meldungscodes from a WBCI Message
     *
     * @return
     */
    @Transient
    public String getMeldungsCodes() {
        if (getMeldungsPositionen() != null) {
            return MeldungsCodeConverter.meldungsPositionToCodeString(meldungsPositionen);
        }
        return null;
    }

    /**
     * Gets all Meldungstexts from a WBCI Message
     *
     * @return
     */
    @Transient
    public String getMeldungsTexte() {
        if (getMeldungsPositionen() != null) {
            return MeldungsCodeConverter.meldungsPositionToTextString(meldungsPositionen);
        }
        return null;
    }

    @Override
    @Transient
    public CarrierCode getEKPPartner() {
        return this.getWbciGeschaeftsfall().getEKPPartner();
    }

    @Transient
    public boolean containsMeldungsCodes(MeldungsCode... meldungsCodes) {
        if (getMeldungsPositionen() != null) {
            return MeldungsCodeConverter.retrieveMeldungCodes(meldungsPositionen)
                    .containsAll(new HashSet<>(Arrays.asList(meldungsCodes)));
        }
        return false;
    }

    /**
     * Iterates through all MeldungPositions, checking for a MeldungPositions with an ADA MeldungsCode.
     * @return true if any ADA MeldungsCodes are found, otherwise false.
     */
    @Transient
    public boolean hasADAMeldungsCode() {
        Set<POS> positionen = getMeldungsPositionen();
        if (positionen != null) {
            for (POS position : positionen) {
                for (MeldungsCode adaCode : MeldungsCode.getADACodes()) {
                    if (adaCode.equals(position.getMeldungsCode())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Erstellt aus allen hinterlegten Meldungspositionen einen String in der Form [Meldungscode] - [Meldungstext], z.B.
     * "8001 - Auftragsbestaetigung (ZWA)"
     *
     * @return
     */
    @Transient
    public String extractMeldungspositionen() {
        StringBuilder result = new StringBuilder();
        if (getMeldungsPositionen() != null) {
            for (MeldungPosition meldungPos : meldungsPositionen) {
                if (result.length() > 0) {
                    result.append(System.lineSeparator());
                }
                result.append(String.format("%s - %s", meldungPos.getMeldungsCode().getCode(),
                        meldungPos.getMeldungsText()));
            }
        }
        return result.toString();
    }

    /**
     * {@inheritDoc}
     */
//    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Column(name = "SEND_AFTER")
    @Type(type="timestamp")
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

    @Override
    public String toString() {
        return "Meldung [meldungstyp=" + getTyp()
                + ", meldungspositionen=" + getMeldungsPositionen()
                + ", absender=" + getAbsender()
                + ", processedAt=" + processedAt
                + ", sendAfter=" + sendAfter
                + ", ioType=" + ioType
                + ", wbciGeschaeftsfall=" + wbciGeschaeftsfall
                + ", wbciVersion=" + wbciVersion + "]";
    }

}
