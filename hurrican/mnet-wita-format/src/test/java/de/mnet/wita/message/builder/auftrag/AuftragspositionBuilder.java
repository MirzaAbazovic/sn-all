/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.05.2011 09:54:11
 */
package de.mnet.wita.message.builder.auftrag;

import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static de.mnet.wita.message.auftrag.Auftragsposition.ProduktBezeichner.*;
import static de.mnet.wita.message.auftrag.Produkt.*;

import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.AktionsCode;
import de.mnet.wita.message.auftrag.Auftragsposition;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;

public class AuftragspositionBuilder {

    private GeschaeftsfallTyp geschaeftsfallTyp = LEISTUNGS_AENDERUNG;
    private GeschaeftsfallProdukt geschaeftsfallProdukt = new GeschaeftsfallProdukt();

    public Auftragsposition build() {
        Auftragsposition auftragsPosition = new Auftragsposition();
        auftragsPosition.setProdukt(TAL);
        auftragsPosition.setProduktBezeichner(HVT_2N);
        auftragsPosition.setGeschaeftsfallProdukt(geschaeftsfallProdukt);

        if (geschaeftsfallTyp.equals(LEISTUNGS_AENDERUNG) || geschaeftsfallTyp.equals(LEISTUNGSMERKMAL_AENDERUNG)) {
            auftragsPosition.setAktionsCode(AktionsCode.AENDERUNG);
        }
        return auftragsPosition;
    }

    public AuftragspositionBuilder withGeschaeftsfallTyp(GeschaeftsfallTyp geschaeftsfallTyp) {
        this.geschaeftsfallTyp = geschaeftsfallTyp;
        return this;
    }

    public AuftragspositionBuilder withGeschaeftsfallProdukt(GeschaeftsfallProdukt geschaeftsfallProdukt) {
        this.geschaeftsfallProdukt = geschaeftsfallProdukt;
        return this;
    }

}
