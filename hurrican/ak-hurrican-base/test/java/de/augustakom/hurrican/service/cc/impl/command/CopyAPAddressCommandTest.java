/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.03.2011 07:32:39
 */
package de.augustakom.hurrican.service.cc.impl.command;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.AdresseBuilder;
import de.augustakom.hurrican.model.billing.view.EndstelleAnsprechpartnerView;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.AnsprechpartnerBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.CCAddressBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.EndstellenService;

/**
 * Unit-Test fuer CopyAPAddressCommand
 */
@Test(groups = { BaseTest.UNIT })
public class CopyAPAddressCommandTest extends BaseTest {

    private CopyAPAddressCommand cut;
    private EndstellenService endstellenServiceMock;
    private CCKundenService kundenServiceMock;
    private BillingAuftragService billingAuftragServiceMock;
    private AvailabilityService availabilityServiceMock;
    private AnsprechpartnerService ansprechpartnerServiceMock;

    private Endstelle endstelle;
    private Adresse billingAddressToCopy;
    private CCAddress createdAddress;
    private CCAddress createdAddressAnsprechpartner;
    private EndstelleAnsprechpartnerView endstelleAnsprechpartner;
    private Ansprechpartner ansprechpartner;

    @BeforeMethod
    public void setUp() throws FindException {
        cut = new CopyAPAddressCommand();

        endstellenServiceMock = mock(EndstellenService.class);
        cut.setEndstellenService(endstellenServiceMock);

        kundenServiceMock = mock(CCKundenService.class);
        cut.setKundenService(kundenServiceMock);

        billingAuftragServiceMock = mock(BillingAuftragService.class);
        cut.setBillingAuftragService(billingAuftragServiceMock);

        availabilityServiceMock = mock(AvailabilityService.class);
        cut.setAvailabilityService(availabilityServiceMock);

        ansprechpartnerServiceMock = mock(AnsprechpartnerService.class);
        cut.setAnsprechpartnerService(ansprechpartnerServiceMock);

        AuftragDaten auftragDaten = new AuftragDatenBuilder().withRandomId().setPersist(false).build();
        cut.setAuftragDaten(auftragDaten);

        endstelle = new EndstelleBuilder().setPersist(false).build();
        when(endstellenServiceMock.findEndstelle4Auftrag(auftragDaten.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B))
                .thenReturn(endstelle);

        billingAddressToCopy = new AdresseBuilder().withGeoId(Long.valueOf(9)).setPersist(false).build();
        when(billingAuftragServiceMock.findAnschlussAdresse4Auftrag(any(Long.class), any(String.class))).thenReturn(
                billingAddressToCopy);

        createdAddress = new CCAddressBuilder().withRandomId().setPersist(false).build();
        when(kundenServiceMock.getCCAddress4BillingAddress(billingAddressToCopy)).thenReturn(createdAddress);

        createdAddressAnsprechpartner = createdAddress;

        endstelleAnsprechpartner = new EndstelleAnsprechpartnerView();
        when(billingAuftragServiceMock.findEndstelleAnsprechpartner(any(Long.class))).thenReturn(endstelleAnsprechpartner);

        when(kundenServiceMock.getCCAddressAnsprechpartner(createdAddress, endstelleAnsprechpartner)).thenReturn(createdAddressAnsprechpartner);

        ansprechpartner = new AnsprechpartnerBuilder().withRandomId().setPersist(false).build();
        when(ansprechpartnerServiceMock.createPreferredAnsprechpartner(createdAddressAnsprechpartner, auftragDaten.getAuftragId())).thenReturn(ansprechpartner);
    }

    @Test
    public void copyAccessPointSuccess() throws HurricanServiceCommandException, StoreException, ValidationException,
            FindException, ServiceNotFoundException {
        cut.copyAccessPoint(Endstelle.ENDSTELLEN_TYP_B);

        verify(kundenServiceMock, times(1)).saveCCAddress(createdAddress);
        verify(availabilityServiceMock, times(1)).findOrCreateGeoId(any(Long.class), any(Long.class));
        verify(endstellenServiceMock, times(1)).saveEndstelle(endstelle);
        assertEquals(endstelle.getGeoId(), billingAddressToCopy.getGeoId(), "GeoID not copied!");
        assertEquals(endstelle.getEndstelle(), billingAddressToCopy.getCombinedStreetData());
        assertEquals(endstelle.getName(), billingAddressToCopy.getCombinedNameData());
        assertEquals(endstelle.getOrt(), billingAddressToCopy.getOrt());
        assertEquals(endstelle.getPlz(), billingAddressToCopy.getPlzTrimmed());
    }

    @Test
    public void copyAccessPointWithoutGeoIdInBillingSystem() throws HurricanServiceCommandException, StoreException,
            ValidationException, FindException, ServiceNotFoundException {
        billingAddressToCopy.setGeoId(null);
        cut.copyAccessPoint(Endstelle.ENDSTELLEN_TYP_B);

        verify(kundenServiceMock, times(1)).saveCCAddress(createdAddress);
        verify(availabilityServiceMock, times(0)).findOrCreateGeoId(any(Long.class), any(Long.class));
        verify(endstellenServiceMock, times(1)).saveEndstelle(endstelle);
        assertEquals(endstelle.getGeoId(), null, "GeoID is defined but should not!");
    }

    @Test
    public void copyAccessPointWithOrtszusatz() throws Exception {
        billingAddressToCopy.setOrtsteil("Ortsteil");
        cut.copyAccessPoint(Endstelle.ENDSTELLEN_TYP_B);
        assertEquals(endstelle.getOrt(), billingAddressToCopy.getOrt() + " - " + billingAddressToCopy.getOrtsteil());
    }

    @Test
    public void copyAccessPointSaveAnsprechpartner() throws Exception {
        billingAddressToCopy.setGeoId(null);

        createdAddressAnsprechpartner.setEmail("ansprechpartner@mailinator.com");
        createdAddressAnsprechpartner.setTelefon("+49 89 123422");
        createdAddressAnsprechpartner.setFax("+49 89 32142");
        createdAddressAnsprechpartner.setHandy("+49 mobil 1234");

        endstelleAnsprechpartner.setName("Huber");
        endstelleAnsprechpartner.setVorname("Sepp");
        endstelleAnsprechpartner.setEmail("ansprechpartner@mailinator.com");
        endstelleAnsprechpartner.setTelefon("+49 89 123422");
        endstelleAnsprechpartner.setFax("+49 89 32142");
        endstelleAnsprechpartner.setMobil("+49 mobil 1234");

        cut.copyAccessPoint(Endstelle.ENDSTELLEN_TYP_B);

        verify(kundenServiceMock).getCCAddressAnsprechpartner(createdAddress, endstelleAnsprechpartner);
        verify(ansprechpartnerServiceMock).saveAnsprechpartner(ansprechpartner);
        assertEquals(ansprechpartner.getPreferred().booleanValue(), true);
        assertEquals(createdAddressAnsprechpartner.getFormatName(), billingAddressToCopy.getFormatName());
        assertEquals(createdAddressAnsprechpartner.getName(), billingAddressToCopy.getName());
        assertEquals(createdAddressAnsprechpartner.getEmail(), endstelleAnsprechpartner.getEmail());
    }
}
