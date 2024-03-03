/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.06.2011 16:04:15
 */
package de.mnet.wita.message.builder;

import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.Storno;

public class StornoBuilder extends MnetWitaRequestBuilder<Storno> {

    public StornoBuilder(GeschaeftsfallTyp geschaeftsfallTyp) {
        this(geschaeftsfallTyp, WitaCdmVersion.getDefault());
    }

    public StornoBuilder(GeschaeftsfallTyp geschaeftsfallTyp, WitaCdmVersion witaCdmVersion) {
        super(geschaeftsfallTyp, witaCdmVersion);
    }

    @Override
    public Storno buildValid() {
        return super.buildValidRequest();
    }

}
