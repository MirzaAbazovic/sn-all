/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.10.13
 */
package de.mnet.wbci.acceptance.common.builder;

import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageBuilder;
import de.mnet.wbci.model.builder.WbciRequestTestBuilder;

/**
 *
 */
public class VorabstimmungsAnfrageKftBuilder<GF extends WbciGeschaeftsfall> extends VorabstimmungsAnfrageBuilder<GF> {

    public VorabstimmungsAnfrageKftBuilder(WbciCdmVersion wbciCdmVersion, GF wbciGeschaeftsfall, IOType ioType) {
        withWbciGeschaeftsfall(wbciGeschaeftsfall);
        withIoType(ioType);

        WbciRequestTestBuilder.enrich(wbciRequest, wbciCdmVersion, wbciGeschaeftsfall.getTyp());
    }
}
