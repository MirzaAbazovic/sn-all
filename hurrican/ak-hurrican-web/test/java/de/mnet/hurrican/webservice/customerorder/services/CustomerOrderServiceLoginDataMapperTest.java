package de.mnet.hurrican.webservice.customerorder.services;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.hurrican.model.cc.IpMode;
import de.augustakom.hurrican.service.cc.impl.logindata.model.LoginData;
import de.augustakom.hurrican.service.cc.impl.logindata.model.internet.LoginDataInternet;
import de.augustakom.hurrican.service.cc.impl.logindata.model.voip.LoginDataVoip;
import de.augustakom.hurrican.service.cc.impl.logindata.model.voip.LoginDataVoipDn;
import de.mnet.esb.cdm.customer.customerorderservice.v1.CustomerOrderLogin;

/**
 * UT for {@link CustomerOrderServiceLoginDataMapper}
 */
public class CustomerOrderServiceLoginDataMapperTest {

    @Test
    public void testMapCustomerOrderLogin() throws Exception {
        final Long taifunOrderId = 123345L;

        final Integer pbitDaten = 0;
        final Integer pbitVoip = 5;

        final LoginDataInternet loginDataInternet = new LoginDataInternet("pppUser", "@mdsl.mnet-online.de", "pppPassword", IpMode.DUAL_STACK, pbitDaten,
                pbitVoip, 40, null, "aftrAdress", 0, 5);
        final LoginDataVoipDn loginDataVoipDn = new LoginDataVoipDn("sipHauptrufnummer", "sipDomain", "sipPassword");
        final LoginDataVoip loginDataVoip = new LoginDataVoip(Arrays.asList(loginDataVoipDn));
        final LoginData loginData = new LoginData(loginDataInternet, loginDataVoip);


        final CustomerOrderServiceLoginDataMapper mapper = new CustomerOrderServiceLoginDataMapper();
        final CustomerOrderLogin customerOrderLogin = mapper.mapCustomerOrderLogin(loginData, taifunOrderId);

        assertNotNull(customerOrderLogin);
        assertEquals(customerOrderLogin.getCustomerOrderId(), taifunOrderId.toString());
        assertNotNull(customerOrderLogin.getLogin());
        assertFalse(customerOrderLogin.getLogin().isEmpty());
        assertEquals(customerOrderLogin.getLogin().size(), 2);
    }

}
