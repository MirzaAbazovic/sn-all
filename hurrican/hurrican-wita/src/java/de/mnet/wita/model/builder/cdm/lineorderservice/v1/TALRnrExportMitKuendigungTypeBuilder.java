/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ErweiterteBestandssucheType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.RufnummernportierungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.StandortAType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALRnrExportMitKuendigungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class TALRnrExportMitKuendigungTypeBuilder implements LineOrderTypeBuilder<TALRnrExportMitKuendigungType> {

    private StandortAType standortA;
    private ErweiterteBestandssucheType bestandssuche;
    private RufnummernportierungType rufnummerPortierung;

    @Override
    public TALRnrExportMitKuendigungType build() {
        TALRnrExportMitKuendigungType talLeistungsaenderungType = new TALRnrExportMitKuendigungType();
        talLeistungsaenderungType.setStandortA(standortA);
        talLeistungsaenderungType.setBestandssuche(bestandssuche);
        talLeistungsaenderungType.setRufnummernPortierung(rufnummerPortierung);
        return talLeistungsaenderungType;
    }

    public TALRnrExportMitKuendigungTypeBuilder withBestandssuche(ErweiterteBestandssucheType bestandssuche) {
        this.bestandssuche = bestandssuche;
        return this;
    }

    public TALRnrExportMitKuendigungTypeBuilder withRufnummerPortierung(RufnummernportierungType rufnummerPortierung) {
        this.rufnummerPortierung = rufnummerPortierung;
        return this;
    }

    public TALRnrExportMitKuendigungTypeBuilder withStandortA(StandortAType standortA) {
        this.standortA = standortA;
        return this;
    }

}
