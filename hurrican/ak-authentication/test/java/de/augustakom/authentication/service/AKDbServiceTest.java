/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.04.2010 11:50:47
 */
package de.augustakom.authentication.service;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKDb;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.BaseTest;


public class AKDbServiceTest extends AbstractAuthenticationTest {


    @Test(groups = BaseTest.SERVICE)
    public void testFindAll() throws AKAuthenticationException {
        List<AKDb> dbs = getDbService().findAll();
        assertFalse(dbs.isEmpty(), "No databases found!");
    }
}
