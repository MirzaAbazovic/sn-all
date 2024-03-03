/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.GeschaeftsfallTerminverschiebungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TerminverschiebungType;

/**
 *
 */
public class TerminverschiebungTypeBuilder extends AuftragTypeBuilder {

    private GeschaeftsfallTerminverschiebungType geschaeftsfallTerminverschiebungType;

    @Override
    public TerminverschiebungType build() {
        TerminverschiebungType auftragType = new TerminverschiebungType();
        auftragType.setGeschaeftsfallAenderung(geschaeftsfallTerminverschiebungType);
        return enrich(auftragType);
    }

    public TerminverschiebungTypeBuilder withGeschaeftsfallTerminverschiebungType(GeschaeftsfallTerminverschiebungType geschaeftsfallTerminverschiebungType) {
        this.geschaeftsfallTerminverschiebungType = geschaeftsfallTerminverschiebungType;
        return this;
    }

}
