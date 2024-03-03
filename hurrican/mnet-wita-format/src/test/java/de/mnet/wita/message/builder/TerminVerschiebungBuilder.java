/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.06.2011 16:04:15
 */
package de.mnet.wita.message.builder;

import java.time.*;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.TerminVerschiebung;

public class TerminVerschiebungBuilder extends MnetWitaRequestBuilder<TerminVerschiebung> {

    private LocalDate termin;

    public TerminVerschiebungBuilder(GeschaeftsfallTyp geschaeftsfallTyp) {
        this(geschaeftsfallTyp, WitaCdmVersion.getDefault());
    }

    public TerminVerschiebungBuilder(GeschaeftsfallTyp geschaeftsfallTyp, WitaCdmVersion witaCdmVersion) {
        super(geschaeftsfallTyp, witaCdmVersion);
    }

    @Override
    public TerminVerschiebung buildValid() {
        if (termin == null) {
            termin = LocalDate.now();
        }
        return build();
    }

    public TerminVerschiebung build() {
        TerminVerschiebung terminVerschiebung = super.buildValidRequest();
        terminVerschiebung.setTermin(termin);
        return terminVerschiebung;
    }

    public TerminVerschiebungBuilder withTermin(LocalDate termin) {
        this.termin = termin;
        return this;
    }

}
