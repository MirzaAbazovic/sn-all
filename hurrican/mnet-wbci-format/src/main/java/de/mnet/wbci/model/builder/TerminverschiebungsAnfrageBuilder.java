/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.model.builder;

import java.time.*;

import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.PersonOderFirma;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;

public class TerminverschiebungsAnfrageBuilder<GF extends WbciGeschaeftsfall> extends WbciRequestBuilder<TerminverschiebungsAnfrage<GF>, GF> {

    public TerminverschiebungsAnfrageBuilder() {
        wbciRequest = new TerminverschiebungsAnfrage<>();
    }

    @Override
    public TerminverschiebungsAnfrage<GF> build() {
        return wbciRequest;
    }

    public TerminverschiebungsAnfrageBuilder<GF> withVorabstimmungsIdRef(String vorabstimmungsIdRef) {
        wbciRequest.setVorabstimmungsIdRef(vorabstimmungsIdRef);
        return this;
    }

    public TerminverschiebungsAnfrageBuilder<GF> withAenderungsId(String aenderungsId) {
        wbciRequest.setAenderungsId(aenderungsId);
        return this;
    }

    public TerminverschiebungsAnfrageBuilder<GF> withTvTermin(LocalDate tvTermin) {
        wbciRequest.setTvTermin(tvTermin != null ? tvTermin : null);
        return this;
    }

    public TerminverschiebungsAnfrageBuilder<GF> withEndkunde(PersonOderFirma endkunde) {
        wbciRequest.setEndkunde(endkunde);
        return this;
    }

    @Override
    public TerminverschiebungsAnfrageBuilder<GF> withWbciGeschaeftsfall(GF wbciGeschaeftsfall) {
        return (TerminverschiebungsAnfrageBuilder<GF>) super.withWbciGeschaeftsfall(wbciGeschaeftsfall);
    }

    @Override
    public TerminverschiebungsAnfrageBuilder<GF> withIoType(IOType ioType) {
        return (TerminverschiebungsAnfrageBuilder<GF>) super.withIoType(ioType);
    }
}
