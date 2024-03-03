/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.acceptance.common.builder;

import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.TechnischeRessourceBuilder;

/**
 *
 */
public class TechnischeRessourceKftBuilder extends TechnischeRessourceBuilder {

    public TechnischeRessourceKftBuilder(WbciCdmVersion wbciCdmVersion, IOType ioType) {
        withVertragsnummer("V123456");
        withIdentifizierer("I12345");
        withTnbKennungAbg("D001");
    }

}
