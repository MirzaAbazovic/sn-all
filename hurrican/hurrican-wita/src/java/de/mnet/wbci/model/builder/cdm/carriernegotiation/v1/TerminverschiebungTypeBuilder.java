/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import javax.xml.datatype.*;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonOderFirmaType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.TerminverschiebungType;

public class TerminverschiebungTypeBuilder extends AbstractAnfrageTypeBuilder<TerminverschiebungType> {

    public TerminverschiebungTypeBuilder() {
        objectType = OBJECT_FACTORY.createTerminverschiebungType();
    }

    public TerminverschiebungTypeBuilder withAenderungsId(String aenderungsId) {
        objectType.setAenderungsId(aenderungsId);
        return this;
    }

    public TerminverschiebungTypeBuilder withVorabstimmungsIdRef(String vorabstimmungsIdRef) {
        objectType.setVorabstimmungsIdRef(vorabstimmungsIdRef);
        return this;
    }

    public TerminverschiebungTypeBuilder withName(PersonOderFirmaType name) {
        objectType.setName(name);
        return this;
    }

    public TerminverschiebungTypeBuilder withNeuerKundenwunschtermin(XMLGregorianCalendar neuerKundenwunschtermin) {
        objectType.setNeuerKundenwunschtermin(neuerKundenwunschtermin);
        return this;
    }

}
