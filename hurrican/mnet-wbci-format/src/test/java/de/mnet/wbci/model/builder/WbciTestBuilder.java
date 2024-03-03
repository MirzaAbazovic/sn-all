package de.mnet.wbci.model.builder;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;

public interface WbciTestBuilder<T> extends WbciBuilder<T> {

    /**
     * This should return a valid T with respect to the CDM version and geschaeftsfall type
     *
     * @param wbciCdmVersion version of the atlas cdm
     * @param gfTyp
     * @return
     */
    T buildValid(WbciCdmVersion wbciCdmVersion, GeschaeftsfallTyp gfTyp);
}
