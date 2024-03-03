/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.06.2011 16:36:34
 */
package de.mnet.wita.message.builder.meldung;

import java.time.*;

import de.mnet.wita.message.auftrag.BestandsSuche;
import de.mnet.wita.message.auftrag.StandortKundeKorrektur;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.common.portierung.RufnummernBlock;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.position.AnschlussPortierungKorrekt;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.message.meldung.position.Positionsattribute;

public class AbbruchMeldungBuilder extends MessageBuilder<AbbruchMeldung, AbbruchMeldungBuilder, MeldungsPosition> {

    private boolean addPositionsattribute = false;
    private boolean anschlussPortierungKorrekt = false;

    @Override
    public AbbruchMeldung build() {
        AbbruchMeldung abbm = new AbbruchMeldung(externeAuftragsnummer, kundennummer);
        addCommonFields(abbm);
        abbm.setVertragsNummer(vertragsnummer);
        abbm.setKundennummerBesteller(kundennummerBesteller);
        abbm.setGeschaeftsfallTyp(geschaeftsfallTyp);
        abbm.setAenderungsKennzeichen(aenderungsKennzeichen);
        abbm.getMeldungsPositionen().addAll(getMeldungspositionen());
        if (addPositionsattribute) {
            addPositionsattribute(abbm);
        }
        return abbm;
    }

    private void addPositionsattribute(AbbruchMeldung abbm) {
        Positionsattribute positionsattribute = new Positionsattribute();
        positionsattribute.setAlternativprodukt("TAL Blubber");
        positionsattribute.setErledigungsterminOffenerAuftrag(LocalDate.now());
        positionsattribute.setFehlauftragsnummer("123456");

        StandortKundeKorrektur standortKundeKorrektur = new StandortKundeKorrektur();
        standortKundeKorrektur.setGebaeudeteilName("Gebauede 5");
        standortKundeKorrektur.setHausnummer("12");
        standortKundeKorrektur.setHausnummernZusatz("a");
        standortKundeKorrektur.setLand("DE");
        standortKundeKorrektur.setOrtsname("Eichstätt");
        standortKundeKorrektur.setOrtsteil("Bayern");
        standortKundeKorrektur.setPostleitzahl("86485");
        standortKundeKorrektur.setStrassenname("Blubberstraße");
        positionsattribute.setStandortKundeKorrektur(standortKundeKorrektur);

        if (anschlussPortierungKorrekt) {
            AnschlussPortierungKorrekt anschlussPortierungKorrekt = new AnschlussPortierungKorrekt();
            anschlussPortierungKorrekt.setOnkzDurchwahlAbfragestelle(new BestandsSuche());
            anschlussPortierungKorrekt.getOnkzDurchwahlAbfragestelle().setAnlagenAbfrageStelle("123");
            anschlussPortierungKorrekt.getOnkzDurchwahlAbfragestelle().setAnlagenDurchwahl("0");
            anschlussPortierungKorrekt.getOnkzDurchwahlAbfragestelle().setAnlagenOnkz("821");
            RufnummernBlock rufnummernBlock = new RufnummernBlock();
            rufnummernBlock.setVon("0");
            rufnummernBlock.setBis("99");
            anschlussPortierungKorrekt.getRufnummernbloecke().add(rufnummernBlock);
            positionsattribute.setAnschlussPortierungKorrekt(anschlussPortierungKorrekt);
        }

        positionsattribute.addDoppeladerBelegt(new LeitungsBezeichnung("1234", "2345", "3456", "1234567890"));

        for (MeldungsPosition pos : abbm.getMeldungsPositionen()) {
            pos.setPositionsattribute(positionsattribute);
        }
    }

    @Override
    protected MeldungsPosition getDefaultMeldungsPosition() {
        return new MeldungsPosition("200", "OK");
    }

    public AbbruchMeldungBuilder withPositionsattribute() {
        this.addPositionsattribute = true;
        return this;
    }

    public AbbruchMeldungBuilder withAnschlussPortierungKorrekt() {
        this.anschlussPortierungKorrekt = true;
        return this;
    }
}
