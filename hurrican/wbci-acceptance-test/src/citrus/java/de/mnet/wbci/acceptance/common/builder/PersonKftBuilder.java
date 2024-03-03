/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.08.13
 */
package de.mnet.wbci.acceptance.common.builder;

import de.mnet.wbci.model.Anrede;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.PersonBuilder;

/**
 *
 */
public class PersonKftBuilder extends PersonBuilder {

    public PersonKftBuilder(WbciCdmVersion wbciCdmVersion) {
        withAnrede(Anrede.HERR);
        withVorname("John");
        withNachname("McFly");
    }
}
