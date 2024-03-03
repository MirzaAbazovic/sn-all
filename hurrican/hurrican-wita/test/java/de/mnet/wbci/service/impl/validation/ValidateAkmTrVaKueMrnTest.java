/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.10.13
 */
package de.mnet.wbci.service.impl.validation;

import static de.mnet.wbci.TestGroups.*;

import org.testng.annotations.Test;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.validation.groups.V1MeldungVaKueMrn;

@Test(groups = UNIT)
@SuppressWarnings("unchecked")
public class ValidateAkmTrVaKueMrnTest extends ValidateAkmTr {

    @Override
    public Class<?> getErrorGroup() {
        return V1MeldungVaKueMrn.class;
    }

    @Override
    public Class<?> getWarnGroup() {
        return null;
    }

    @Override
    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return GeschaeftsfallTyp.VA_KUE_MRN;
    }

}
