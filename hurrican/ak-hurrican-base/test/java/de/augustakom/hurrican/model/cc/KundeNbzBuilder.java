/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2009 13:12:08
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.service.iface.IServiceObject;

public class KundeNbzBuilder extends AbstractCCIDModelBuilder<KundeNbzBuilder, KundeNbz> implements IServiceObject {

    @SuppressWarnings("unused")
    private Long kundeNo = randomLong(Integer.MAX_VALUE / 2);
    @SuppressWarnings("unused")
    private String nbz = "TES123";

    public KundeNbzBuilder withKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
        return this;
    }

    public KundeNbzBuilder withNbz(String nbz) {
        this.nbz = nbz;
        return this;
    }
}
