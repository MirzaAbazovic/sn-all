/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.14
 */
package de.augustakom.hurrican.model.cc.ffm;

import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

/**
 * Enum zur Definition, wie bzw. welche Daten fuer eine FFM {@link WorkforceOrder} aggregiert werden sollen.
 */
public enum FfmAggregationStrategy {

    HEADER_ONLY_WITH_TIMESLOT,
    TECHNICAL_PARAMS_INCLUDED_WITH_TIMESLOT,
    HOUSING,

}
