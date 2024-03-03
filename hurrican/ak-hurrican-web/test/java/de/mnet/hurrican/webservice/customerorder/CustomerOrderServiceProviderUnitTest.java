/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.12.13
 */
package de.mnet.hurrican.webservice.customerorder;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.util.*;
import javax.xml.ws.*;
import com.google.common.collect.ImmutableList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.mnet.esb.cdm.customer.customerorderservice.v1.ContactPerson;
import de.mnet.esb.cdm.customer.customerorderservice.v1.ESBFault;

@Test(groups = BaseTest.UNIT)
public class CustomerOrderServiceProviderUnitTest extends BaseTest {

    @Spy
    @InjectMocks
    CustomerOrderServiceProvider sut;

    @Mock
    CarrierService carrierService;

    @Mock
    KundenService kundenService;

    @Mock
    CCAuftragService ccAuftragService;

    @Mock
    AnsprechpartnerService ansprechpartnerService;

    @Mock
    EndstellenService endstellenService;

    @BeforeMethod
    public void initTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetCustomerOrder() throws ESBFault, FindException {
        Carrierbestellung cb = mockCarrierbestellung();

        AuftragDaten ad = mockAuftragDaten(cb);

        Auftrag a = mockAuftrag(ad);

        mockEndstelle(cb);

        Ansprechpartner ap = mockAnsprechpartnerWithAddress(a.getAuftragId());

        Kunde k = mockKunde(a.getKundeNo());

        Holder<String> customerIdHolder = new Holder<>();
        Holder<String> customerOrderIdHolder = new Holder<>();
        Holder<List<ContactPerson>> contactPersonsHolder = new Holder<>();

        sut.getOrderDetails(cb.getVtrNr(), customerIdHolder, customerOrderIdHolder, contactPersonsHolder);

        assertEquals(customerIdHolder.value, String.valueOf(k.getKundeNo()));
        assertEquals(customerOrderIdHolder.value, String.valueOf(ad.getAuftragNoOrig()));

        List<ContactPerson> contactPersons = contactPersonsHolder.value;
        assertThat(contactPersons, hasSize(2));
        ContactPerson endstelle = contactPersons.get(0);
        ContactPerson customer = contactPersons.get(1);

        assertThat(endstelle.getRole(), equalTo("ENDSTELLE"));
        CCAddress apAddress = ap.getAddress();
        assertThat(endstelle.getLastName(), equalTo(apAddress.getName()));
        assertThat(endstelle.getFirstName(), equalTo(apAddress.getVorname()));
        assertThat(endstelle.getCommunication().getEMail(), equalTo(apAddress.getEmail()));
        assertThat(endstelle.getCommunication().getFax(), equalTo(apAddress.getFax()));
        assertThat(endstelle.getCommunication().getMobile(), equalTo(apAddress.getHandy()));
        assertThat(endstelle.getCommunication().getPhone(), equalTo(apAddress.getTelefon()));

        assertThat(customer.getRole(), equalTo("CUSTOMER"));
        assertThat(customer.getLastName(), equalTo(k.getName()));
        assertThat(customer.getFirstName(), equalTo(k.getVorname()));
        assertThat(customer.getCommunication().getEMail(), equalTo(k.getEmail()));
        assertThat(customer.getCommunication().getFax(), equalTo(k.getRnFax()));
        assertThat(customer.getCommunication().getMobile(), equalTo(k.getRnMobile()));
        assertThat(customer.getCommunication().getPhone(), equalTo(k.getRnGeschaeft()));
    }

    @Test
    public void shouldReturnFaultGettingCustomerOrderWithNoMatchingCarrierbestellung() throws ESBFault, FindException {
        final String lineOrderId = "123";
        try {
            sut.getOrderDetails(lineOrderId, new Holder<>(), new Holder<>(), new Holder<>());
            fail("was expecting fault");
        }
        catch (ESBFault fault) {
            assertEquals(fault.getFaultInfo().getErrorCode(), CustomerOrderServiceProvider.ESB_CUST_ORD_001);
            assertEquals(fault.getFaultInfo().getErrorMessage(), String.format("No matching Carrier Order found for lineId=%s", lineOrderId));
        }
    }

    @Test
    public void shouldReturnFaultIfNoOrderFoundMatchingCarrierbestellung() throws ESBFault, FindException {
        Carrierbestellung cb = mockCarrierbestellung();

        Mockito.when(carrierService.findAuftragDaten4CB(cb.getId())).thenReturn(Collections.emptyList());

        try {
            sut.getOrderDetails(cb.getVtrNr(), new Holder<>(), new Holder<>(), new Holder<>());
            fail("was expecting fault");
        }
        catch (ESBFault fault) {
            assertEquals(fault.getFaultInfo().getErrorCode(), CustomerOrderServiceProvider.ESB_CUST_ORD_001);
            assertEquals(fault.getFaultInfo().getErrorMessage(), String.format("No matching Order found for lineId=%s, carrierbestellungId=%s", cb.getVtrNr(), cb.getId()));
        }
    }

    @DataProvider
    private Object[][] generateAuftraege() throws FindException {
        AuftragDaten ad1 = createAuftragDaten(1L);
        AuftragDaten ad2 = createAuftragDaten(2L);
        AuftragDaten ad3 = createAuftragDaten(3L);
        AuftragDaten ad4 = createAuftragDaten(null);

        // @formatter:off
       return new Object[][] {
                { Arrays.asList(), null },
                { Arrays.asList(ad1), ad1 },
                { Arrays.asList(ad3, ad1, ad2), ad3},
                { Arrays.asList(ad4), null},
                { Arrays.asList(ad4, ad1), ad1},
        };
        // @formatter:on
    }

    @Test(dataProvider = "generateAuftraege")
    public void shouldFilterAuftraege(List<AuftragDaten> auftraege, AuftragDaten expectedAuftrag) throws ESBFault, FindException {
        final Long cbId = 1L;
        Mockito.when(carrierService.findAuftragDaten4CB(cbId)).thenReturn(auftraege);

        Optional<AuftragDaten> auftragDaten = sut.filterAuftragDaten(auftraege);
        if(expectedAuftrag != null) {
            Assert.assertTrue(auftragDaten.isPresent());
            Assert.assertEquals(auftragDaten.get(), expectedAuftrag);
        } else {
            Assert.assertFalse(auftragDaten.isPresent());
        }
    }

    private Kunde mockKunde(Long kundenNr) throws FindException {
        Kunde k = new Kunde();
        k.setKundeNo(kundenNr);
        k.setName(RandomTools.createString());
        k.setVorname(RandomTools.createString());
        k.setEmail(RandomTools.createString());
        k.setRnFax(RandomTools.createString());
        k.setRnMobile(RandomTools.createString());
        k.setRnGeschaeft(RandomTools.createString());
        Mockito.when(kundenService.findKunde(kundenNr)).thenReturn(k);
        return k;
    }

    private Ansprechpartner mockAnsprechpartnerWithAddress(Long auftragId) throws FindException {
        CCAddress address = new CCAddress();
        address.setName(RandomTools.createString());
        Ansprechpartner ap = new Ansprechpartner();
        address.setName(RandomTools.createString());
        address.setVorname(RandomTools.createString());
        address.setEmail(RandomTools.createString());
        address.setFax(RandomTools.createString());
        address.setHandy(RandomTools.createString());
        address.setTelefon(RandomTools.createString());
        ap.setAddress(address);
        Mockito.when(ansprechpartnerService.findAnsprechpartner(Ansprechpartner.Typ.ENDSTELLE_B, auftragId))
                .thenReturn(ImmutableList.of(ap));
        return ap;
    }

    private void mockEndstelle(Carrierbestellung cb) throws FindException {
        Endstelle e = new Endstelle();
        e.setEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B);
        Mockito.when(endstellenService.findEndstellen4Carrierbestellung(cb))
                .thenReturn(ImmutableList.of(e));
    }

    private Auftrag mockAuftrag(AuftragDaten ad) throws FindException {
        Auftrag a = new Auftrag();
        a.setAuftragId(ad.getAuftragId());
        a.setKundeNo(RandomTools.createLong());
        Mockito.when(ccAuftragService.findAuftragById(ad.getAuftragId())).thenReturn(a);
        return a;
    }

    private AuftragDaten mockAuftragDaten(Carrierbestellung cb) throws FindException {
        AuftragDaten ad = createAuftragDaten(RandomTools.createLong());
        Mockito.when(carrierService.findAuftragDaten4CB(cb.getId())).thenReturn(ImmutableList.of(ad));
        return ad;
    }

    private AuftragDaten createAuftragDaten(Long auftragNoOrig) throws FindException {
        AuftragDaten ad = new AuftragDaten();
        ad.setAuftragId(RandomTools.createLong());
        ad.setAuftragNoOrig(auftragNoOrig);
        return ad;
    }

    private Carrierbestellung mockCarrierbestellung() {
        Carrierbestellung cb = new Carrierbestellung();
        cb.setId(RandomTools.createLong());
        cb.setVtrNr(RandomTools.createString());
        Mockito.when(carrierService.findCBsByNotExactVertragsnummer(cb.getVtrNr())).thenReturn(
                ImmutableList.of(cb));
        return cb;
    }

}
