/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.2011 11:09:11
 */
package de.mnet.wita.message.builder.meldung.position;

import java.util.*;
import com.google.common.base.Strings;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.builder.AbstractWitaBuilder;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.message.meldung.position.LeitungsAbschnitt;

public class LeitungBuilder extends AbstractWitaBuilder<Leitung> {

    private LeitungsBezeichnung leitungsBezeichnung;
    private List<LeitungsAbschnitt> leitungsabschnitte = Collections.emptyList();
    private String maxBruttoBitRate;
    private String schleifenwiderstand;

    public LeitungBuilder(WitaCdmVersion witaCdmVersion) {
        super(witaCdmVersion);
    }

    public LeitungBuilder() {
        this(WitaCdmVersion.getDefault());
    }

    public Leitung buildValid() {
        if (leitungsBezeichnung == null) {
            leitungsBezeichnung = new LeitungsBezeichnung("22", "22", "33", Strings.padStart("123", 10, '0'));
        }
        //        if (leitungsabschnitte == null) leitungsabschnitte = Collections.singletonList(new LeitungsAbschnitt(150, "200", "20"));
        //        if (maxBruttoBitRate == null) maxBruttoBitRate = "2000";
        //        if (schleifenwiderstand == null) schleifenwiderstand = "100";
        return build();
    }

    public Leitung build() {
        Leitung leitung = new Leitung();
        leitung.setMaxBruttoBitrate(maxBruttoBitRate);
        leitung.setSchleifenWiderstand(schleifenwiderstand);
        leitung.setLeitungsBezeichnung(leitungsBezeichnung);
        leitung.setLeitungsAbschnitte(leitungsabschnitte);
        return leitung;
    }

    public LeitungBuilder withLeitungsbezeichnung(LeitungsBezeichnung leitungsBezeichnung) {
        this.leitungsBezeichnung = leitungsBezeichnung;
        return this;
    }

    public LeitungBuilder withLeitungsAbschnittList(List<LeitungsAbschnitt> leitungsabschnitte) {
        this.leitungsabschnitte = leitungsabschnitte;
        return this;
    }

    public LeitungBuilder withMaxBruttoBitrate(String maxBruttoBitRate) {
        this.maxBruttoBitRate = maxBruttoBitRate;
        return this;
    }

    public LeitungBuilder withSchleifenwiderstand(String schleifenwiderstand) {
        this.schleifenwiderstand = schleifenwiderstand;
        return this;
    }

    public LeitungBuilder withLeitungsabschnitte(int quantity) {
        this.leitungsabschnitte = new ArrayList<>(quantity);
        for (int i = 0; i < quantity; i++) {
            this.leitungsabschnitte.add(new LeitungAbschnittBuilder(getWitaVersion()).buildValid());
        }
        return this;
    }

}
