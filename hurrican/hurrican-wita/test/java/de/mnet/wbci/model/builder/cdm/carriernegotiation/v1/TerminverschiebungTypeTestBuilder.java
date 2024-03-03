/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import static org.apache.commons.lang.StringUtils.*;

import java.time.*;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.TerminverschiebungType;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationRequestTypeTestBuilder;

/**
 *
 */
public class TerminverschiebungTypeTestBuilder extends TerminverschiebungTypeBuilder implements
        CarrierNegotiationRequestTypeTestBuilder<TerminverschiebungType> {
    @Override
    public TerminverschiebungType buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        AbstractAnfrageTypeTestBuilder.enrichTestData(objectType);

        if (isEmpty(objectType.getAenderungsId())) {
            withAenderungsId("DEU.DTAG.T123456789");
        }

        if (isEmpty(objectType.getVorabstimmungsIdRef())) {
            withVorabstimmungsIdRef("DEU.DTAG.V123456789");
        }

        if (objectType.getName() == null) {
            withName(new PersonOderFirmaTypeTestBuilder().buildValid());
        }

        if (objectType.getNeuerKundenwunschtermin() == null) {
            withNeuerKundenwunschtermin(DateConverterUtils.toXmlGregorianCalendar(LocalDateTime.now().plusDays(10)));
        }
        return build();
    }
}
