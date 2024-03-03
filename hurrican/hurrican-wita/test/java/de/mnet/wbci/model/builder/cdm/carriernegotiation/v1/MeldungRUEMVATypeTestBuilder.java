/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import static org.apache.commons.lang.StringUtils.*;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungRUEMVAType;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.CarrierNegotiationMeldungTypeTestBuilder;

public class MeldungRUEMVATypeTestBuilder extends MeldungRUEMVATypeBuilder implements
        CarrierNegotiationMeldungTypeTestBuilder<MeldungRUEMVAType> {
    @Override
    public MeldungRUEMVAType buildValid(GeschaeftsfallEnumType geschaeftsfallEnumType) {
        AbstractMeldungTypeTestBuilder.enrichTestData(objectType, geschaeftsfallEnumType);
        if (objectType.getPosition() == null || objectType.getPosition().size() == 0) {
            withPosition(new MeldungsPositionRUEMVAMeldungTypeTestBuilder().buildValid(geschaeftsfallEnumType));
        }
        if (objectType.getRessource() == null || objectType.getRessource().size() == 0) {
            withRessource(new TechnischeRessourceTypeTestBuilder().buildValid(geschaeftsfallEnumType));
        }
        if (objectType.getRufnummernPortierung() == null) {
            withRufnummernportierungMeldung(new RufnummernportierungMeldungTypeEinzelTestBuilder().buildValid(
                    geschaeftsfallEnumType));
        }
        if (isEmpty(objectType.getTechnologie())) {
            withTechnologie(Technologie.TAL_ISDN);
        }
        if (objectType.getWechseltermin() == null) {
            withWechselTermin(DateConverterUtils.toXmlGregorianCalendar(DateCalculationHelper.getDateInWorkingDaysFromNow(8).toLocalDate()));
        }
        return build();
    }

}
