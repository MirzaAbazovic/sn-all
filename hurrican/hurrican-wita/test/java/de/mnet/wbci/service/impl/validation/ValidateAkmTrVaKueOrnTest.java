/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.10.13
 */
package de.mnet.wbci.service.impl.validation;

import static de.mnet.wbci.TestGroups.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.validation.groups.V1MeldungVaKueOrn;

@Test(groups = UNIT)
@SuppressWarnings("unchecked")
public class ValidateAkmTrVaKueOrnTest extends ValidateAkmTr {

    @Override
    public Class<?> getErrorGroup() {
        return V1MeldungVaKueOrn.class;
    }

    @Override
    public Class<?> getWarnGroup() {
        return null;
    }

    @Override
    public GeschaeftsfallTyp getGeschaeftsfallTyp() {
        return GeschaeftsfallTyp.VA_KUE_ORN;
    }

    @DataProvider
    public Object[][] pkiAufOrn() {
        return new Object[][] {
                { "PKI-AUF", 1 },
                { "", 1 },
                { null, 0 },
        };
    }

    @Test(dataProvider = "pkiAufOrn")
    @Override
    public void testCheckPkiAufEmpty(String pkiAuf, int constraints) throws Exception {
        super.testCheckPkiAufEmpty(pkiAuf, constraints);
    }
}
