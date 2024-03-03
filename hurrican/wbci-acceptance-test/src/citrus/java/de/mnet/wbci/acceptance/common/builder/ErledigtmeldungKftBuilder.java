/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.13
 */
package de.mnet.wbci.acceptance.common.builder;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.WbciVersion;
import de.mnet.wbci.model.builder.ErledigtmeldungBuilder;

/**
 *
 */
public class ErledigtmeldungKftBuilder extends ErledigtmeldungBuilder {

    public ErledigtmeldungKftBuilder(IOType ioType) {
        AbstractMeldungKftBuilder.withMeldungMetaData(this, ioType);
        withWbciVersion(WbciVersion.V2);
        withWechseltermin(DateCalculationHelper.getDateInWorkingDaysFromNow(14).toLocalDate());
        withStornoIdRef(null);
    }

    public ErledigtmeldungKftBuilder withMeldungsCodes(MeldungsCode... meldungsCodes) {
        return (ErledigtmeldungKftBuilder) super.withMeldungsCodes(meldungsCodes);
    }

    public ErledigtmeldungKftBuilder withoutWechseltermin() {
        withWechseltermin(null);
        return this;
    }

}
