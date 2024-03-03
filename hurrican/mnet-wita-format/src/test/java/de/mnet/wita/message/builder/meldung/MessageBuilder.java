/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.07.2011 09:53:11
 */
package de.mnet.wita.message.builder.meldung;

import java.time.*;
import java.util.*;
import com.google.common.collect.Lists;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.AktionsCode;
import de.mnet.wita.message.auftrag.Auftragsposition.ProduktBezeichner;
import de.mnet.wita.message.builder.TestAnlage;
import de.mnet.wita.message.builder.meldung.position.MeldungsPositionBuilder;
import de.mnet.wita.message.builder.meldung.position.ProduktPositionBuilder;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.common.Anlagentyp;
import de.mnet.wita.message.common.BsiProtokollEintragSent;
import de.mnet.wita.message.common.EmailStatus;
import de.mnet.wita.message.common.SmsStatus;
import de.mnet.wita.message.common.Uebertragungsverfahren;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.message.meldung.position.ProduktPosition;

public abstract class MessageBuilder<MELDUNG extends Meldung<?>, BUILDER extends MessageBuilder<MELDUNG, BUILDER, POSITION>, POSITION extends MeldungsPosition> {

    GeschaeftsfallTyp geschaeftsfallTyp = GeschaeftsfallTyp.BEREITSTELLUNG;
    AenderungsKennzeichen aenderungsKennzeichen = AenderungsKennzeichen.STANDARD;
    String externeAuftragsnummer = "12345";
    String kundennummer = "2345678901";
    String kundennummerBesteller = "N123456789";
    String vertragsnummer = "987654";
    private LocalDateTime versandZeitstempel = LocalDateTime.now();
    private final Set<POSITION> meldungspositionen = new HashSet<>();
    final Set<ProduktPosition> produktPositionen = new HashSet<>();
    final List<Anlage> anlagen = Lists.newArrayList();
    Long id;
    private BsiProtokollEintragSent sentToBsi = null;
    private WitaCdmVersion witaCdmVersion;
    private SmsStatus smsStatus;
    private EmailStatus emailStatus;

    MessageBuilder() {
        this.witaCdmVersion = WitaCdmVersion.getDefault();
    }

    public abstract MELDUNG build();

    void addCommonFields(MELDUNG meldung) {
        meldung.setGeschaeftsfallTyp(geschaeftsfallTyp);
        meldung.setAenderungsKennzeichen(aenderungsKennzeichen);
        meldung.setId(id);
        meldung.setVersandZeitstempel(Date.from(versandZeitstempel.atZone(ZoneId.systemDefault()).toInstant()));
        meldung.setSentToBsi(sentToBsi);
        meldung.setCdmVersion(witaCdmVersion);
        meldung.setSmsStatus(smsStatus);
        meldung.setEmailStatus(emailStatus);
    }

    public BUILDER addTestAnlage(TestAnlage testAnlage, Anlagentyp anlagentyp) {
        Anlage anlage = new Anlage();
        anlage.setDateiname(testAnlage.resourceFileName);
        anlage.setDateityp(testAnlage.dateityp);
        anlage.setAnlagentyp(anlagentyp);
        anlage.setInhalt(testAnlage.getAnlageInhalt());
        this.anlagen.add(anlage);
        return returnThisUnchecked();
    }

    public BUILDER addAnlagen(int quantity) {
        for (int i = 0; i < quantity; i++) {
            addTestAnlage(TestAnlage.SIMPLE, Anlagentyp.SONSTIGE);
        }
        return returnThisUnchecked();
    }

    public BUILDER withId(Long id) {
        this.id = id;
        return returnThisUnchecked();
    }

    public BUILDER withGeschaeftsfallTyp(GeschaeftsfallTyp geschaeftsfallTyp) {
        this.geschaeftsfallTyp = geschaeftsfallTyp;
        return returnThisUnchecked();
    }

    public BUILDER withAenderungsKennzeichen(AenderungsKennzeichen aenderungsKennzeichen) {
        this.aenderungsKennzeichen = aenderungsKennzeichen;
        return returnThisUnchecked();
    }

    public BUILDER withExterneAuftragsnummer(String externeAuftragsnummer) {
        this.externeAuftragsnummer = externeAuftragsnummer;
        return returnThisUnchecked();
    }

    public BUILDER withKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
        return returnThisUnchecked();
    }

    public BUILDER withKundennummerBesteller(String kundennummerBesteller) {
        this.kundennummerBesteller = kundennummerBesteller;
        return returnThisUnchecked();
    }

    public BUILDER withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return returnThisUnchecked();
    }

    public BUILDER withVersandZeitstempel(LocalDateTime zeitstempel) {
        this.versandZeitstempel = zeitstempel;
        return returnThisUnchecked();
    }

    public BUILDER withWitaVersion(WitaCdmVersion witaCdmVersion) {
        this.witaCdmVersion = witaCdmVersion;
        return returnThisUnchecked();
    }

    public BUILDER withSmsStatus(SmsStatus smsStatus) {
        this.smsStatus = smsStatus;
        return returnThisUnchecked();
    }

    public BUILDER withEmailStatus(EmailStatus emailStatus) {
        this.emailStatus = emailStatus;
        return returnThisUnchecked();
    }

    public BUILDER addMeldungsposition(POSITION meldungsposition) {
        this.meldungspositionen.add(meldungsposition);
        return returnThisUnchecked();
    }

    public BUILDER addAllMeldungsposition(Collection<POSITION> meldungspositionen) {
        this.meldungspositionen.addAll(meldungspositionen);
        return returnThisUnchecked();
    }

    public BUILDER addMeldungspositionen(int quantity) {
        for (int i = 0; i < quantity; i++) {
            addMeldungsposition((POSITION) new MeldungsPositionBuilder(witaCdmVersion).buildValid());
        }
        return returnThisUnchecked();
    }

    Set<POSITION> getMeldungspositionen() {
        if (meldungspositionen.isEmpty()) { // add default if no meldungsposition set
            Set<POSITION> result = new HashSet<>();
            result.add(getDefaultMeldungsPosition());
            return result;
        }
        return meldungspositionen;
    }

    protected abstract POSITION getDefaultMeldungsPosition();

    Set<ProduktPosition> getProduktpositionen() {
        if (produktPositionen.isEmpty()) {
            Set<ProduktPosition> result = new HashSet<>();
            result.add(getDefaultProduktPositionen());
            return result;
        }
        return produktPositionen;
    }

    public BUILDER addProduktPosition(ProduktPosition produktPosition) {
        this.produktPositionen.add(produktPosition);
        return returnThisUnchecked();
    }

    public BUILDER addAllProduktPositionen(Collection<ProduktPosition> produktPositionen) {
        this.produktPositionen.addAll(produktPositionen);
        return returnThisUnchecked();
    }

    public BUILDER addProduktPositionen(int quantity) {
        Collection<ProduktPosition> produktPositions = new ArrayList<>(quantity);
        for (int i = 0; i < quantity; i++) {
            produktPositions.add(new ProduktPositionBuilder(WitaCdmVersion.V1).buildValid());
        }
        this.produktPositionen.addAll(produktPositions);
        return returnThisUnchecked();
    }

    ProduktPosition getDefaultProduktPositionen() {
        ProduktPosition pp = new ProduktPosition(AktionsCode.AENDERUNG, ProduktBezeichner.HVT_2H);
        pp.setUebertragungsVerfahren(Uebertragungsverfahren.H04);
        return pp;
    }

    public BUILDER withSentToBsi(BsiProtokollEintragSent sentToBsi) {
        this.sentToBsi = sentToBsi;
        return returnThisUnchecked();
    }

    private BUILDER returnThisUnchecked() {
        return (BUILDER) this;
    }

}
