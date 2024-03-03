/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.acceptance.common.builder;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.AbbruchmeldungTechnRessourceBuilder;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungTechnRessourceTestBuilder;

public class AbbruchmeldungTechnRessourceKftBuilder extends AbbruchmeldungTechnRessourceBuilder {

    public AbbruchmeldungTechnRessourceKftBuilder(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp, IOType ioType) {
        AbstractMeldungKftBuilder.withMeldungMetaData(this, ioType);
        addMeldungPosition(new MeldungPositionAbbruchmeldungTechnRessourceTestBuilder().buildValid(wbciCdmVersion, gfTyp));
    }

}
