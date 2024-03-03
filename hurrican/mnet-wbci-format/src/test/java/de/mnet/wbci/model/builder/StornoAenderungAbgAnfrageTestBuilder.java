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
import de.mnet.wbci.model.StornoAenderungAbgAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciRequestStatus;

public class StornoAenderungAbgAnfrageTestBuilder<GF extends WbciGeschaeftsfall> extends
        StornoAenderungAbgAnfrageBuilder<GF> implements
        WbciTestBuilder<StornoAenderungAbgAnfrage<GF>> {

    @Override
    public StornoAenderungAbgAnfrage<GF> buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        StornoTestBuilder.enrich(wbciRequest, wbciCdmVersion, gfTyp);
        if (wbciRequest.getStornoGrund() == null) {
            withStornoGrund("Some reason");
        }
        return build();
    }

    @Override
    public StornoAenderungAbgAnfrageTestBuilder<GF> withWbciGeschaeftsfall(GF wbciGeschaeftsfall) {
        super.withWbciGeschaeftsfall(wbciGeschaeftsfall);
        return this;
    }

    @Override
    public StornoAenderungAbgAnfrageTestBuilder<GF> withVorabstimmungsIdRef(String vorabstimmungsIdRef) {
        return (StornoAenderungAbgAnfrageTestBuilder<GF>) super.withVorabstimmungsIdRef(vorabstimmungsIdRef);
    }

    @Override
    public StornoAenderungAbgAnfrageTestBuilder<GF> withUpdatedAt(LocalDateTime updatedAt) {
        super.withUpdatedAt(updatedAt);
        return this;
    }

    @Override
    public StornoAenderungAbgAnfrageTestBuilder<GF> withEndkunde(PersonOderFirma personOderFirma) {
        super.withEndkunde(personOderFirma);
        return this;
    }

    @Override
    public StornoAenderungAbgAnfrageTestBuilder<GF> withStandort(Standort standort) {
        super.withStandort(standort);
        return this;
    }

    @Override
    public StornoAenderungAbgAnfrageTestBuilder<GF> withRequestStatus(WbciRequestStatus requestStatus) {
        super.withRequestStatus(requestStatus);
        return this;
    }

    @Override
    public StornoAenderungAbgAnfrageTestBuilder<GF> withAenderungsId(String aenderungsId) {
        super.withAenderungsId(aenderungsId);
        return this;
    }

}
