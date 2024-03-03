/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.13
 */
package de.mnet.wbci.model.builder;

import java.time.*;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.StornoAufhebungAbgAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciRequestStatus;

public class StornoAufhebungAbgAnfrageTestBuilder<GF extends WbciGeschaeftsfall> extends
        StornoAufhebungAbgAnfrageBuilder<GF> implements
        WbciTestBuilder<StornoAufhebungAbgAnfrage<GF>> {

    @Override
    public StornoAufhebungAbgAnfrage<GF> buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        StornoTestBuilder.enrich(wbciRequest, wbciCdmVersion, gfTyp);
        if (wbciRequest.getStornoGrund() == null) {
            withStornoGrund("Some reason");
        }
        return build();
    }

    @Override
    public StornoAufhebungAbgAnfrageTestBuilder<GF> withEndkunde(PersonOderFirma personOderFirma) {
        super.withEndkunde(personOderFirma);
        return this;
    }

    @Override
    public StornoAufhebungAbgAnfrageTestBuilder<GF> withStandort(Standort standort) {
        super.withStandort(standort);
        return this;
    }

    @Override
    public StornoAufhebungAbgAnfrageTestBuilder<GF> withRequestStatus(WbciRequestStatus requestStatus) {
        super.withRequestStatus(requestStatus);
        return this;
    }

    @Override
    public StornoAufhebungAbgAnfrageTestBuilder<GF> withUpdatedAt(LocalDateTime updatedAt) {
        super.withUpdatedAt(updatedAt);
        return this;
    }
}
