/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.13
 */
package de.mnet.wbci.model.builder;

import java.util.*;

import de.mnet.wbci.model.Leitung;
import de.mnet.wbci.model.MeldungPositionUebernahmeRessourceMeldung;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;

/**
 *
 */
public class UebernahmeRessourceMeldungBuilder extends
        MeldungBuilder<UebernahmeRessourceMeldung, MeldungPositionUebernahmeRessourceMeldung> {

    protected Boolean uebernahme;
    protected Boolean sichererHafen;
    protected String portierungskennungPKIauf;

    protected Set<Leitung> leitungen = new HashSet<>();

    public UebernahmeRessourceMeldung build() {
        UebernahmeRessourceMeldung meldung = new UebernahmeRessourceMeldung();

        meldung.setUebernahme(uebernahme);
        meldung.setSichererHafen(sichererHafen);
        meldung.setPortierungskennungPKIauf(portierungskennungPKIauf);
        meldung.setLeitungen(leitungen);

        super.enrich(meldung);

        return meldung;
    }

    public UebernahmeRessourceMeldungBuilder withUebernahme(Boolean uebernahme) {
        this.uebernahme = uebernahme;
        return this;
    }

    public UebernahmeRessourceMeldungBuilder withSichererhafen(Boolean sichererHafen) {
        this.sichererHafen = sichererHafen;
        return this;
    }

    public UebernahmeRessourceMeldungBuilder withPortierungskennungPKIauf(String portierungskennungPKIauf) {
        this.portierungskennungPKIauf = portierungskennungPKIauf;
        return this;
    }

    public UebernahmeRessourceMeldungBuilder withLeitungen(Set<Leitung> leitungen) {
        this.leitungen = leitungen;
        return this;
    }

    public UebernahmeRessourceMeldungBuilder addLeitung(Leitung leitung) {
        this.leitungen.add(leitung);
        return this;
    }

    public UebernahmeRessourceMeldungBuilder addMeldungPosition(MeldungPositionUebernahmeRessourceMeldung position) {
        return (UebernahmeRessourceMeldungBuilder) super.addMeldungPosition(position);
    }

}
