/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2011 15:29:21
 */
package de.mnet.wita.message.builder.common.portierung;

import java.util.*;

import de.mnet.wita.message.common.portierung.EinzelanschlussRufnummer;
import de.mnet.wita.message.common.portierung.RufnummernBlock;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;
import de.mnet.wita.message.common.portierung.RufnummernPortierungAnlagenanschluss;
import de.mnet.wita.message.common.portierung.RufnummernPortierungEinzelanschluss;

public class RufnummernPortierungBuilder {

    private String onkz = "89";

    public RufnummernPortierung buildAuftragPortierung(boolean einzelAnschluss) {
        RufnummernPortierung portierung = build(einzelAnschluss);
        portierung.setPortierungsKenner("D052-089");
        return portierung;
    }

    public RufnummernPortierung buildMeldungPortierung(boolean einzelAnschluss) {
        RufnummernPortierung portierung = build(einzelAnschluss);
        portierung.setPortierungsKenner("D052-089");
        return portierung;
    }

    public RufnummernPortierungBuilder withOnkz(String onkz) {
        this.onkz = onkz;
        return this;
    }

    private RufnummernPortierung build(boolean einzelAnschluss) {
        RufnummernPortierung rufnummernPortierung;
        if (einzelAnschluss) {
            rufnummernPortierung = createRufnummernEinzelAnschluss();
        }
        else {
            rufnummernPortierung = createRufnummernAnlagenAnschluss();
        }
        return rufnummernPortierung;
    }

    public RufnummernPortierungEinzelanschluss createRufnummernEinzelAnschluss() {
        RufnummernPortierungEinzelanschluss res = new RufnummernPortierungEinzelanschluss();

        EinzelanschlussRufnummer rufnummer = new EinzelanschlussRufnummer();
        rufnummer.setOnkz(onkz);
        rufnummer.setRufnummer("123");
        List<EinzelanschlussRufnummer> rufnummern = Collections.singletonList(rufnummer);
        res.getRufnummern().addAll(rufnummern);
        return res;
    }

    public RufnummernPortierungAnlagenanschluss createRufnummernAnlagenAnschluss() {
        RufnummernPortierungAnlagenanschluss rufnummernPortierung = new RufnummernPortierungAnlagenanschluss();
        rufnummernPortierung.setDurchwahl("1234");
        rufnummernPortierung.setAbfragestelle("2345");
        rufnummernPortierung.setOnkz(onkz);

        RufnummernBlock rufnummernBlock = new RufnummernBlock();
        rufnummernBlock.setVon("100");
        rufnummernBlock.setBis("299");

        rufnummernPortierung.getRufnummernBloecke().add(rufnummernBlock);

        return rufnummernPortierung;
    }
}


