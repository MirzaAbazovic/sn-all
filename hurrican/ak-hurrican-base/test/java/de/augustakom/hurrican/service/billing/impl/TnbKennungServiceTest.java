package de.augustakom.hurrican.service.billing.impl;

import static org.testng.Assert.*;

import java.io.*;
import javax.annotation.*;
import ch.ergon.taifun.ws.messages.GetDialNumberInfoResponseDocument;
import ch.ergon.taifun.ws.messages.GetDialNumberInfoResponseType;
import org.springframework.core.io.InputStreamResource;
import org.springframework.ws.test.client.MockWebServiceServer;
import org.springframework.ws.test.client.RequestMatchers;
import org.springframework.ws.test.client.ResponseCreators;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.ws.MnetWebServiceTemplate;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.TnbKennungService;

/**
 * service test fuer die Ermittelung der Portierungskennung aus der Taifun-Auftragsnummer
 * 03.03.2016.
 */
@Test(groups = { BaseTest.SERVICE })
public class TnbKennungServiceTest extends AbstractHurricanBaseServiceTest {

    @Resource(name = "billingWSTemplate")
    private MnetWebServiceTemplate billingWsTemplate;

    public void thatPortierungsKennungIsRetrievedFromTaifun() throws FindException, ServiceCommandException, IOException {
        TnbKennungService tnbKennungService = getBillingService(TnbKennungService.class);
        mockWS(tnbKennungService);
        String tnbKennung = tnbKennungService.getTnbKennungFromTaifunWebservice(123L);
        assertEquals(tnbKennung, "test");
    }

    private void mockWS(TnbKennungService bws) throws ServiceCommandException, IOException {
        MockWebServiceServer mockServer = MockWebServiceServer.createServer(billingWsTemplate);
        GetDialNumberInfoResponseDocument respDoc = GetDialNumberInfoResponseDocument.Factory
                .newInstance();
        GetDialNumberInfoResponseType resp = GetDialNumberInfoResponseType.Factory.newInstance();
        resp.setPortingIdentifier("test");
        respDoc.setGetDialNumberInfoResponse(resp);
        mockServer.expect(RequestMatchers.anything()).andRespond(
                ResponseCreators.withPayload(new InputStreamResource(respDoc.newInputStream())));
    }

}