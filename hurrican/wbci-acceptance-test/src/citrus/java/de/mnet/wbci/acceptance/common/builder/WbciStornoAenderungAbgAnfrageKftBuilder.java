/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 24.09.13 
 */
package de.mnet.wbci.acceptance.common.builder;

import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.builder.StornoAenderungAbgAnfrageBuilder;

public class WbciStornoAenderungAbgAnfrageKftBuilder<GF extends WbciGeschaeftsfall> extends StornoAenderungAbgAnfrageBuilder<GF> {
    public WbciStornoAenderungAbgAnfrageKftBuilder(WbciCdmVersion wbciCdmVersion) {
        withStornoGrund("Sag ich nicht");
    }
}
