/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.08.13
 */
package de.mnet.wbci.acceptance.common.builder;

import de.mnet.wbci.model.Anrede;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.FirmaBuilder;

/**
 *
 */
public class FirmaKftBuilder extends FirmaBuilder {

    public FirmaKftBuilder(WbciCdmVersion wbciCdmVersion) {
        withAnrede(Anrede.FIRMA);
        withFirmename("XCompany");
        withFirmennamenZusatz("GmbH");
    }
}
