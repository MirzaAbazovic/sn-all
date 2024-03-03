/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.13
 */
package de.mnet.wbci.model.builder;

import static org.apache.commons.lang.StringUtils.*;

import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;

/**
 *
 */
public abstract class StornoTestBuilder {

    public static void enrich(StornoAnfrage stornoAnfrage, WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp) {
        WbciRequestTestBuilder.enrich(stornoAnfrage, wbciCdmVersion, gfTyp);
        if (isEmpty(stornoAnfrage.getAenderungsId())) {
            stornoAnfrage.setAenderungsId(IdGenerator.generateStornoId(CarrierCode.MNET));
        }
        if (isEmpty(stornoAnfrage.getVorabstimmungsIdRef())) {
            stornoAnfrage.setVorabstimmungsIdRef(stornoAnfrage.getWbciGeschaeftsfall().getVorabstimmungsId());
        }
    }

}
