/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.04.2011 10:33:02
 */

package de.augustakom.authentication.service.impl;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.authentication.dao.AKUserDAO;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class AKUserServiceImplTest {

    private AKUserServiceImpl userService;

    @BeforeMethod
    public void prepareUserService() {
        userService = new AKUserServiceImpl();
        userService.userDao = mock(AKUserDAO.class);
    }

    public void findUserIdToNames() throws AKAuthenticationException {
        List<AKUser> users = new ArrayList<AKUser>();
        users.add(createAKUser(1L, "Blub", "Tim"));
        users.add(createAKUser(5L, "Blub", "Susi"));
        when(userService.userDao.queryByExample(anyObject(), eq(AKUser.class))).thenReturn(users);

        Map<Long, String> resultMap = userService.findUserIdToNames();

        assertEquals(resultMap.get(1L), "Blub Tim");
        assertEquals(resultMap.get(5L), "Blub Susi");
        assertNull(resultMap.get(2L));
    }

    private AKUser createAKUser(Long id, String name, String firstName) {
        AKUser user = new AKUser();
        user.setId(id);
        user.setName(name);
        user.setFirstName(firstName);
        return user;
    }

}
