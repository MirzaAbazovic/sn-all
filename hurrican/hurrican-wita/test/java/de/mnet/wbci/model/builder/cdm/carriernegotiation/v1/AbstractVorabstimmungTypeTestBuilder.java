/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.VorabstimmungType;

/**
 *
 */
public abstract class AbstractVorabstimmungTypeTestBuilder {

    public static <M extends VorabstimmungType> void enrichTestData(M objectType, GeschaeftsfallEnumType geschaeftsfall) {
        AbstractAnfrageTypeTestBuilder.enrichTestData(objectType);

        if (objectType.getVorabstimmungsId() == null) {
            objectType.setVorabstimmungsId("DEU.DTAG.V000000001");
        }

        if (objectType.getKundenwunschtermin() == null) {
            objectType.setKundenwunschtermin(DateConverterUtils.toXmlGregorianCalendar(DateCalculationHelper.getDateInWorkingDaysFromNow(14)));
        }

        if (objectType.getEndkunde() == null) {
            objectType.setEndkunde(new PersonOderFirmaTypeTestBuilder().buildValid());
        }
    }

}
