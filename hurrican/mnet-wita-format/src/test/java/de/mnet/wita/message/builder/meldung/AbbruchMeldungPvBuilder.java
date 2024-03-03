/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2011 18:21:38
 */
package de.mnet.wita.message.builder.meldung;

import de.mnet.wita.message.meldung.AbbruchMeldungPv;
import de.mnet.wita.message.meldung.position.MeldungsPosition;

public class AbbruchMeldungPvBuilder extends
        MessageBuilder<AbbruchMeldungPv, AbbruchMeldungPvBuilder, MeldungsPosition> {

    @Override
    public AbbruchMeldungPv build() {
        AbbruchMeldungPv abbm = new AbbruchMeldungPv();
        addCommonFields(abbm);
        abbm.setExterneAuftragsnummer(externeAuftragsnummer);
        abbm.setKundenNummer(kundennummer);
        abbm.setVertragsNummer(vertragsnummer);
        abbm.getMeldungsPositionen().addAll(getMeldungspositionen());
        return abbm;
    }

    @Override
    protected MeldungsPosition getDefaultMeldungsPosition() {
        return new MeldungsPosition("200", "OK");
    }

}
