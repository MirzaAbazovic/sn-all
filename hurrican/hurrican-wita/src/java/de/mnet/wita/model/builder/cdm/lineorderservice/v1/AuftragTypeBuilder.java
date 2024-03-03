/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KennerType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KundeType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class AuftragTypeBuilder implements LineOrderTypeBuilder<AuftragType> {

    private String externeAuftragsnummer;
    private KundeType kunde;
    private KundeType besteller;
    private Geschaeftsfall geschaeftsfall;
    private KennerType kenner;

    @Override
    public AuftragType build() {
        return enrich(new AuftragType());
    }

    protected <A extends AuftragType> A enrich(A auftragType) {
        auftragType.setBesteller(besteller);
        auftragType.setExterneAuftragsnummer(externeAuftragsnummer);
        auftragType.setGeschaeftsfall(geschaeftsfall);
        auftragType.setKenner(kenner);
        auftragType.setKunde(kunde);
        return auftragType;
    }

    public AuftragTypeBuilder withExterneAuftragsnummer(String externeAuftragsnummer) {
        this.externeAuftragsnummer = externeAuftragsnummer;
        return this;
    }

    public AuftragTypeBuilder withKunde(KundeType kunde) {
        this.kunde = kunde;
        return this;
    }

    public AuftragTypeBuilder withBesteller(KundeType besteller) {
        this.besteller = besteller;
        return this;
    }

    public AuftragTypeBuilder withGeschaeftsfall(Geschaeftsfall geschaeftsfall) {
        this.geschaeftsfall = geschaeftsfall;
        return this;
    }

    public AuftragTypeBuilder withKenner(KennerType kenner) {
        this.kenner = kenner;
        return this;
    }

}
