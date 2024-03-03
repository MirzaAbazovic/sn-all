/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.08.13
 */
package de.mnet.wbci.acceptance.common.builder;

import de.mnet.wbci.model.Portierungszeitfenster;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageBuilder;

/**
 *
 */
public class RufnummernportierungAnlageKftBuilder extends RufnummernportierungAnlageBuilder {

    public RufnummernportierungAnlageKftBuilder(WbciCdmVersion wbciCdmVersion) {
        withAbfragestelle("0");
        withDurchwahlnummer("123456");
        withOnkz("089");
        withPortierungszeitfenster(Portierungszeitfenster.ZF2);

        addRufnummernblock(new RufnummernBlockKftBuilder(wbciCdmVersion).build());
    }
}
