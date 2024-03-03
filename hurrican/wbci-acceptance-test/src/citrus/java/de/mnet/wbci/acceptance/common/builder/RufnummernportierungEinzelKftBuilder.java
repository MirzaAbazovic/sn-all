/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.08.13
 */
package de.mnet.wbci.acceptance.common.builder;

import de.mnet.wbci.model.Portierungszeitfenster;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.RufnummernportierungEinzelnBuilder;

/**
 *
 */
public class RufnummernportierungEinzelKftBuilder extends RufnummernportierungEinzelnBuilder {

    public RufnummernportierungEinzelKftBuilder(WbciCdmVersion wbciCdmVersion) {
        withAlleRufnummernPortieren(true);
        withPortierungszeitfenster(Portierungszeitfenster.ZF2);
        addRufnummer(new RufnummerOnkzKftBuilder(wbciCdmVersion).build());
    }
}
