package de.augustakom.hurrican.service.cc.impl.logindata;

import static org.testng.Assert.*;

import java.util.*;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.cc.impl.logindata.model.voip.LoginDataVoip;

/**
 * Service test for {@link LoginDataVoipGatherer}
 */
@Test(groups = BaseTest.SERVICE)
public class LoginDataVoipGathererTest extends AbstractHurricanBaseServiceTest {

    private LoginDataVoipGatherer loginDataVoipGatherer;

    @BeforeMethod
    public void setup() {
        loginDataVoipGatherer = (LoginDataVoipGatherer) getBean("loginDataVoipGatherer");
        assertNotNull(loginDataVoipGatherer);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetLoginDataVoip_UnknownAuftrag() throws Exception{
        final Optional<LoginDataVoip> loginDataVoipOptional = loginDataVoipGatherer.getLoginDataVoip(12345556L);
        assertNotNull(loginDataVoipOptional);
        assertFalse(loginDataVoipOptional.isPresent());
    }

}
