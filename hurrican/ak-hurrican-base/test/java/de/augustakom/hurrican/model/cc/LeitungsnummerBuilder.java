/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.02.2010
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.ReferencedEntityId;
import de.augustakom.hurrican.model.cc.Leitungsnummer.Typ;

public class LeitungsnummerBuilder extends AbstractCCIDModelBuilder<LeitungsnummerBuilder, Leitungsnummer> {

    @ReferencedEntityId("auftragId")
    private AuftragBuilder auftragBuilder = null;

    private Typ typ = Typ.SONST;
    private String leitungsnummer = "Leitungsnummer";

    public LeitungsnummerBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public LeitungsnummerBuilder withTyp(Typ typ) {
        this.typ = typ;
        return this;
    }

    public LeitungsnummerBuilder withLeitungsnummer(String leitungsnummer) {
        this.leitungsnummer = leitungsnummer;
        return this;
    }

    public AuftragBuilder getAuftragBuilder() {
        return auftragBuilder;
    }

    public Typ getTyp() {
        return typ;
    }

    public String getLeitungsnummer() {
        return leitungsnummer;
    }
}
