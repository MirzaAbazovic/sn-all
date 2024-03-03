/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.model.builder;

import static org.apache.commons.lang.StringUtils.*;

import java.time.*;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciRequestStatus;

public class TerminverschiebungsAnfrageTestBuilder<GF extends WbciGeschaeftsfall> extends TerminverschiebungsAnfrageBuilder<GF> implements
        WbciTestBuilder<TerminverschiebungsAnfrage<GF>> {

    @Override
    public TerminverschiebungsAnfrage<GF> buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        WbciRequestTestBuilder.enrich(wbciRequest, wbciCdmVersion, gfTyp);
        if (isEmpty(wbciRequest.getAenderungsId())) {
            withAenderungsId(IdGenerator.generateTvId(CarrierCode.MNET));
        }
        if (isEmpty(wbciRequest.getVorabstimmungsIdRef())) {
            withVorabstimmungsIdRef(wbciRequest.getWbciGeschaeftsfall().getVorabstimmungsId());
        }
        if (wbciRequest.getTvTermin() == null) {
            withTvTermin(DateCalculationHelper.addWorkingDays(LocalDate.now(), 7));
        }
        return build();
    }

    @Override
    public TerminverschiebungsAnfrageTestBuilder<GF> withWbciGeschaeftsfall(GF wbciGeschaeftsfall) {
        super.withWbciGeschaeftsfall(wbciGeschaeftsfall);
        return this;
    }

    @Override
    public TerminverschiebungsAnfrageTestBuilder<GF> withVorabstimmungsIdRef(String vorabstimmungsIdRef) {
        return (TerminverschiebungsAnfrageTestBuilder<GF>) super.withVorabstimmungsIdRef(vorabstimmungsIdRef);
    }

    @Override
    public TerminverschiebungsAnfrageTestBuilder<GF> withAenderungsId(String aenderungsId) {
        return (TerminverschiebungsAnfrageTestBuilder<GF>) super.withAenderungsId(aenderungsId);
    }

    @Override
    public TerminverschiebungsAnfrageTestBuilder<GF> withTvTermin(LocalDate tvTermin) {
        return (TerminverschiebungsAnfrageTestBuilder<GF>) super.withTvTermin(tvTermin);
    }

    @Override
    public TerminverschiebungsAnfrageTestBuilder<GF> withEndkunde(PersonOderFirma endkunde) {
        return (TerminverschiebungsAnfrageTestBuilder<GF>) super.withEndkunde(endkunde);
    }

    @Override
    public TerminverschiebungsAnfrageTestBuilder<GF> withUpdatedAt(LocalDateTime updatedAt) {
        super.withUpdatedAt(updatedAt);
        return this;
    }

    @Override
    public TerminverschiebungsAnfrageTestBuilder<GF> withRequestStatus(WbciRequestStatus requestStatus) {
        super.withRequestStatus(requestStatus);
        return this;
    }

    @Override
    public TerminverschiebungsAnfrageTestBuilder<GF> withIoType(IOType ioType) {
        return (TerminverschiebungsAnfrageTestBuilder<GF>) super.withIoType(ioType);
    }

    @Override
    public TerminverschiebungsAnfrageTestBuilder<GF> withCreationDate(LocalDateTime creationDate) {
        super.withCreationDate(creationDate);
        return this;
    }
}
