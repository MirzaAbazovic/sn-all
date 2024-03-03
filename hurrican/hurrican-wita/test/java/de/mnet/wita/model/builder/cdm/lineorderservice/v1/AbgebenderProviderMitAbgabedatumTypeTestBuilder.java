/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.time.*;

import de.augustakom.common.service.holiday.DateCalculationHelper;

/**
 *
 */
public class AbgebenderProviderMitAbgabedatumTypeTestBuilder extends AbgebenderProviderMitAbgabedatumTypeBuilder {

    public AbgebenderProviderMitAbgabedatumTypeTestBuilder() {
        super();
        withAbgabedatum(DateCalculationHelper.addWorkingDays(LocalDate.now(), 20));
    }

}
