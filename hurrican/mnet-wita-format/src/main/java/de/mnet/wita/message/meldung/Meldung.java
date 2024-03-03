/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2011 16:12:09
 */
package de.mnet.wita.message.meldung;

import static com.google.common.collect.Sets.*;

import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import com.google.common.base.Function;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.MessageWithSentToBsiFlag;
import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.common.BsiProtokollEintragSent;
import de.mnet.wita.message.common.EmailStatus;
import de.mnet.wita.message.common.SmsStatus;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;
import de.mnet.wita.message.meldung.attribute.AufnehmenderProvider;
import de.mnet.wita.message.meldung.converter.MeldungsCodeConverter;
import de.mnet.wita.message.meldung.position.AbgebenderProvider;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.message.meldung.position.ProduktPosition;
import de.mnet.wita.validators.WitaVersionValid;
import de.mnet.wita.validators.getters.HasVertragsNummer;
import de.mnet.wita.validators.groups.V1;
import de.mnet.wita.validators.groups.WorkflowV1;

/**
 * Basis-Objekt fuer eine WITA-Meldungen.
 * <p/>
 * Regeln fuer Validierung: <ul> <li>Attribute, die zum Addressieren des Workflows benötigt werden (Auftragstyp,
 * externeAuftragsnummer, Vertragsnummer): Diese Attribute werden von der Default Validation Group gecheckt, Violation
 * fuehrt zu Eintrag in DLQ </li> <li>Alle anderen Attribute dürfen nur in eigener Validation Group gecheckt werden,
 * Violation fuehrt zu Workflow Error State.</li> </ul>
 *
 * @param <POS> die passende Meldungsposition-Implementierung
 */
@Entity()
@Table(name = "T_MWF_MELDUNG")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = Meldung.MELDUNGS_TYPE_FIELD, discriminatorType = DiscriminatorType.STRING)
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_MELDUNG_0", allocationSize = 1)
public abstract class Meldung<POS extends MeldungsPosition> extends MwfEntity implements WitaMessage,
        HasVertragsNummer, MessageWithSentToBsiFlag {

    private static final long serialVersionUID = -3282309657056062588L;

    public static final Function<Meldung<?>, String> GET_EXTERNE_AUFTRAGSNUMMER = new Function<Meldung<?>, String>() {
        @Override
        public String apply(Meldung<?> input) {
            return input.getExterneAuftragsnummer();
        }
    };

    public static final String EXTERNE_AUFTRAGSNR_FIELD = "externeAuftragsnummer";
    public static final String VERTRAGS_NUMMER_FIELD = "vertragsNummer";
    public static final String MELDUNGS_TYPE_FIELD = "meldungstyp";
    public static final String SMS_STATUS_FIELD = "smsStatus";
    public static final String EMAIL_STATUS_FIELD = "emailStatus";


    private GeschaeftsfallTyp geschaeftsfallTyp;
    private AenderungsKennzeichen aenderungsKennzeichen;
    private String kundenNummer;
    private String vertragsNummer;
    private String anschlussOnkz;
    private String anschlussRufnummer;
    private Leitung leitung;
    private String externeAuftragsnummer;
    private String kundennummerBesteller;
    private AbgebenderProvider abgebenderProvider;
    private AufnehmenderProvider aufnehmenderProvider;
    private RufnummernPortierung rufnummernPortierung;
    private Set<POS> meldungsPositionen = new HashSet<>();
    private List<ProduktPosition> produktPositionen;
    private Set<Anlage> anlagen = newHashSet();
    private Date versandZeitstempel;
    private BsiProtokollEintragSent sentToBsi;
    private String cdmVersion;
    private String vorabstimmungsId;
    private SmsStatus smsStatus;
    private EmailStatus emailStatus;

    @Enumerated(EnumType.STRING)
    @Column(length = 40, columnDefinition = "varchar2(40)")
    @NotNull
    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return geschaeftsfallTyp;
    }

    public void setGeschaeftsfallTyp(GeschaeftsfallTyp geschaeftsfallTyp) {
        this.geschaeftsfallTyp = geschaeftsfallTyp;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 20, columnDefinition = "varchar2(20)")
    @NotNull
    public AenderungsKennzeichen getAenderungsKennzeichen() {
        return aenderungsKennzeichen;
    }

    public void setAenderungsKennzeichen(AenderungsKennzeichen aenderungsKennzeichen) {
        this.aenderungsKennzeichen = aenderungsKennzeichen;
    }

    @Valid
    @Size.List({
            @Size(groups = WorkflowV1.class, min = 1, max = 99, message = "Die Anzahl der gesetzten Meldungspositionen muss zwischen {min} und {max} sein.")
    })
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = MeldungsPosition.class)
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 1000)
    @JoinColumn(name = "MELDUNG_ID")
    public Set<POS> getMeldungsPositionen() {
        return meldungsPositionen;
    }

    private void setMeldungsPositionen(Set<POS> meldungsPositionen) {
        this.meldungsPositionen = meldungsPositionen;
    }

    public void addMeldungsPosition(POS abmMeldungsPosition) {
        meldungsPositionen.add(abmMeldungsPosition);
    }

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "RUFNUMMERN_PORTIERUNG_ID")
    public RufnummernPortierung getRufnummernPortierung() {
        return rufnummernPortierung;
    }

    public void setRufnummernPortierung(RufnummernPortierung rufnummernPortierung) {
        this.rufnummernPortierung = rufnummernPortierung;
    }

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "AUFNEHMENDER_PROVIDER_ID")
    public AufnehmenderProvider getAufnehmenderProvider() {
        return aufnehmenderProvider;
    }

    public void setAufnehmenderProvider(AufnehmenderProvider aufnehmenderProvider) {
        this.aufnehmenderProvider = aufnehmenderProvider;
    }

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    public Leitung getLeitung() {
        return leitung;
    }

    public void setLeitung(Leitung leitung) {
        this.leitung = leitung;
    }

    /**
     * is persisted by {@link DiscriminatorColumn} in {@link Meldung}
     */
    @Valid
    @NotNull
    @Transient
    public abstract MeldungsType getMeldungsTyp();

    @Column(name = "KUNDEN_NUMMER")
    @Size(min = 10, max = 10, message = "Die Kundennummer muss aus 10 Zeichen bestehen.")
    public String getKundenNummer() {
        return kundenNummer;
    }

    public void setKundenNummer(String kundenNummer) {
        this.kundenNummer = kundenNummer;
    }

    @Override
    @Column(name = "VERTRAGS_NUMMER")
    @Size(min = 1, max = 10, message = "Vertragsnummer muss mindestens aus einem und maximal 10 Zeichen bestehen.")
    public String getVertragsNummer() {
        return vertragsNummer;
    }

    public void setVertragsNummer(String vertragsNummer) {
        this.vertragsNummer = vertragsNummer;
    }

    @Column(name = "ANSCHLUSS_ONKZ")
    public String getAnschlussOnkz() {
        return anschlussOnkz;
    }

    public void setAnschlussOnkz(String anschlussOnkz) {
        this.anschlussOnkz = anschlussOnkz;
    }

    @Column(name = "ANSCHLUSS_RUFNUMMER")
    public String getAnschlussRufnummer() {
        return anschlussRufnummer;
    }

    public void setAnschlussRufnummer(String anschlussRufnummer) {
        this.anschlussRufnummer = anschlussRufnummer;
    }

    @Column(name = "KUNDEN_NUMMER_BESTELLER")
    public String getKundennummerBesteller() {
        return kundennummerBesteller;
    }

    public void setKundennummerBesteller(String kundennummerBesteller) {
        this.kundennummerBesteller = kundennummerBesteller;
    }

    public boolean hasKundennummerBesteller() {
        return kundennummerBesteller != null;
    }

    @Size(min = 1, max = 30)
    @Column(name = "EXT_AUFTRAGS_NR")
    public String getExterneAuftragsnummer() {
        return externeAuftragsnummer;
    }

    public void setExterneAuftragsnummer(String externeAuftragsnummer) {
        this.externeAuftragsnummer = externeAuftragsnummer;
    }

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ABGEBENDER_PROVIDER_ID")
    public AbgebenderProvider getAbgebenderProvider() {
        return abgebenderProvider;
    }

    public void setAbgebenderProvider(AbgebenderProvider abgebenderProvider) {
        this.abgebenderProvider = abgebenderProvider;
    }

    @Valid
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 1000)
    @JoinColumn(name = "MELDUNG_ID")
    public List<ProduktPosition> getProduktPositionen() {
        if (produktPositionen == null) {
            produktPositionen = new ArrayList<>();
        }

        return produktPositionen;
    }

    private void setProduktPositionen(List<ProduktPosition> produktPositionen) {
        this.produktPositionen = produktPositionen;
    }

    @Valid
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 1000)
    @JoinColumn(name = "MELDUNG_ID")
    public Set<Anlage> getAnlagen() {
        if (anlagen == null) {
            return Collections.emptySet();
        }
        return anlagen;
    }

    /**
     * Required by Hibernate
     */
    private void setAnlagen(Set<Anlage> anlagen) {
        this.anlagen = anlagen;
    }

    @NotNull
    @Column(name = "VERSAND_ZEITSTEMPEL")
    //    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @Type(type = "timestamp")
    public Date getVersandZeitstempel() {
        return versandZeitstempel;
    }

    public void setVersandZeitstempel(Date versandZeitstempel) {
        this.versandZeitstempel = versandZeitstempel;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "SENT_TO_BSI")
    @Override
    public BsiProtokollEintragSent getSentToBsi() {
        return sentToBsi;
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

    @Size(groups = V1.class, min = 16, max = 21)
    @Column(name = "VORABSTIMMUNGSID")
    public String getVorabstimmungsId() {
        return vorabstimmungsId;
    }

    public void setVorabstimmungsId(String vorabstimmungsId) {
        this.vorabstimmungsId = vorabstimmungsId;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "SMS_STATUS", length = 25, columnDefinition = "varchar2(25)")
    public SmsStatus getSmsStatus() {
        return smsStatus;
    }

    public void setSmsStatus(SmsStatus smsStatus)
    {
        this.smsStatus = smsStatus;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "EMAIL_STATUS", length = 25, columnDefinition = "varchar2(25)")
    public EmailStatus getEmailStatus() {
        return emailStatus;
    }

    public void setEmailStatus(EmailStatus emailStatus) {
        this.emailStatus = emailStatus;
    }

    @Override
    public void setSentToBsi(BsiProtokollEintragSent sentToBsi) {
        this.sentToBsi = sentToBsi;
    }

    /**
     * Gets all Meldungscodes from a WITA Message
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
     * Gets all Meldungstexts from a WITA Message
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

    @Transient
    public boolean isStatusOpenForMail() {
        return EmailStatus.OFFEN.equals(this.getEmailStatus());
    }


    @Transient
    public boolean isStatusOpenForSMS() {
        return SmsStatus.OFFEN.equals(this.getSmsStatus());
    }

    @Override
    public String toString() {
        return "Meldung [geschaeftsfallTyp=" + geschaeftsfallTyp + ", cdmVersion=" + cdmVersion + ", aenderungsKennzeichen=" + aenderungsKennzeichen
                + ", kundenNummer=" + kundenNummer + ", vertragsNummer=" + vertragsNummer + ", anschlussOnkz="
                + anschlussOnkz + ", anschlussRufnummer=" + anschlussRufnummer + ", leitung=" + leitung
                + ", externeAuftragsnummer=" + externeAuftragsnummer + ", kundennummerBesteller="
                + kundennummerBesteller + ", abgebenderProvider=" + abgebenderProvider + ", aufnehmenderProvider="
                + aufnehmenderProvider + ", rufnummernPortierung=" + rufnummernPortierung + ", meldungsPositionen="
                + meldungsPositionen + ", produktPositionen=" + produktPositionen + ", anlagen=" + anlagen
                + ", versandZeitstempel=" + versandZeitstempel + ", sentToBsi=" + sentToBsi + ", vorabstimmungsId=" + vorabstimmungsId + "]";
    }

}
