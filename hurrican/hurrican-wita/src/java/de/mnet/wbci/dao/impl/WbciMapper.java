/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 06.02.2017

 */

package de.mnet.wbci.dao.impl;


import java.math.*;

import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * Mapping methods for WBCI domain objects.
 *
 * Created by zieglerch on 06.02.2017.
 */
class WbciMapper {

    @SuppressWarnings("unchecked")
    <T> T mapObject(Object value, Class<T> clazz) {
        if (value != null) {
            // special handling is needed for MeldungTyp
            if (clazz.isAssignableFrom(MeldungTyp.class)) {
                return (T) MeldungTyp.valueOf((String) value);
            }
            // special handling is needed for WbciRequestStatus
            if (clazz.isAssignableFrom(WbciRequestStatus.class)) {
                return (T) WbciRequestStatus.valueOf((String) value);
            }
            // special handling is needed for WbciGeschaeftsfallStatus
            if (clazz.isAssignableFrom(WbciGeschaeftsfallStatus.class)) {
                return (T) WbciGeschaeftsfallStatus.valueOf((String) value);
            }
            // special handling is needed for RequestTyp
            if (clazz.isAssignableFrom(RequestTyp.class)) {
                return (T) RequestTyp.buildFromShortName((String) value);
            }
            // special handling is needed for CarrierCode
            if (clazz.isAssignableFrom(CarrierCode.class)) {
                return (T) CarrierCode.valueOf((String) value);
            }
            // special handling is needed for KundenTyp
            if (clazz.isAssignableFrom(KundenTyp.class)) {
                return (T) KundenTyp.valueOf((String) value);
            }
            if (clazz.isAssignableFrom(Technologie.class)) {
                return (T) Technologie.valueOf((String) value);
            }
            // special handling is needed for GeschaeftsfallTyp
            if (clazz.isAssignableFrom(GeschaeftsfallTyp.class)) {
                return (T) GeschaeftsfallTyp.buildFromName((String) value);
            }
            // special handling is needed for Boolean
            if (clazz.isAssignableFrom(Boolean.class)) {
                return (T) Boolean.valueOf(((BigDecimal) value).intValue() == 1);
            }
            // special handling is needed for BigDecimal
            if (value instanceof BigDecimal) {
                return (T) Long.valueOf(((BigDecimal) value).longValue());
            }
            return (T) value;
        }

        return null;
    }
}
