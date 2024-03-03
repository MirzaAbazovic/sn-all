/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.01.2013 09:36:07
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.AdresseBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.GeoId2TechLocationBuilder;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IPAddressBuilder;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.NiederlassungBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.IPAddressService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.ReferenceService;

public abstract class IPAbstractCommandTest extends BaseTest {

    @Mock
    ProduktService produktService;
    @Mock
    IPAddressService ipAddressService;
    @Mock
    ReferenceService referenceService;
    @Mock
    CCAuftragService auftragService;
    @Mock
    NiederlassungService niederlassungService;
    @Mock
    BillingAuftragService billingAuftragService;
    @Mock
    AvailabilityService availabilityService;
    @Mock
    HVTService hvtService;

    private Produkt produkt;
    private AuftragBuilder auftragBuilder;
    private AuftragDaten auftragDaten;
    private Reference ipPurpose;
    private Reference ipLocation;
    private Niederlassung niederlassung;
    private IPAddress ipAddress;
    private Adresse adresse;
    private GeoId geoId;
    private GeoId2TechLocation geoId2TechLocation;
    private HVTStandortBuilder hvtStandortBuilder;
    private HVTGruppe hvtGruppe;

    protected abstract AbstractIpCommand cut();

    protected abstract String refType();

    @BeforeMethod
    void setUp() throws Exception {
        initMocks(this);

        createIpPurpose();
        createProdukt();
        auftragBuilder = new AuftragBuilder().withRandomId();
        createAuftragDaten();
        createIpLocation();
        createNiederlassung();
        createIpAddress();
        geoId = new GeoIdBuilder().withId(1234L).build();
        createAdresse();
        hvtStandortBuilder = new HVTStandortBuilder().withRandomId();
        createGeoId2TechLocation();
        createHvtGruppe();

        doReturn(auftragDaten.getAuftragId()).when(cut()).getAuftragId();

        when(auftragService.findAuftragDatenByAuftragIdTx(any(Long.class)))
                .thenReturn(auftragDaten);
        when(referenceService.findReferencesByType(eq(refType()), eq(Boolean.FALSE)))
                .thenReturn(ImmutableList.of(ipPurpose));
        when(produktService.findProdukt4Auftrag(eq(auftragDaten.getAuftragId())))
                .thenReturn(produkt);
        whenAssignIpThenReturnIpAddress();
        when(billingAuftragService.findAnschlussAdresse4Auftrag(auftragDaten.getAuftragNoOrig(),
                Endstelle.ENDSTELLEN_TYP_B)).thenReturn(adresse);
        when(availabilityService.findGeoId(adresse.getGeoId())).thenReturn(geoId);
        when(availabilityService.findPossibleGeoId2TechLocations(geoId, produkt.getId()))
                .thenReturn(ImmutableList.of(geoId2TechLocation));
        when(hvtService.findHVTGruppe4Standort(geoId2TechLocation.getHvtIdStandort()))
                .thenReturn(hvtGruppe);
        when(niederlassungService.findNiederlassung(hvtGruppe.getNiederlassungId())).thenReturn(niederlassung);
    }

    private void createGeoId2TechLocation() {
        geoId2TechLocation = new GeoId2TechLocationBuilder().withHvtStandortBuilder(hvtStandortBuilder).build();
    }

    private void whenAssignIpThenReturnIpAddress() throws StoreException {
        if (cut().isV4()) {
            when(ipAddressService.assignIPV4(eq(auftragDaten.getAuftragId()), eq(ipPurpose),
                    eq(produkt.getIpNetmaskSizeV4()), eq(ipLocation), anyLong()))
                    .thenReturn(ipAddress);
        }
        else {
            when(ipAddressService.assignIPV6(eq(auftragDaten.getAuftragId()), eq(ipPurpose),
                    eq(produkt.getIpNetmaskSizeV6()), eq(ipLocation), anyLong()))
                    .thenReturn(ipAddress);
        }
    }

    private void createHvtGruppe() {
        //@formatter:off
        hvtGruppe = new HVTGruppeBuilder()
            .withRandomId()
            .withNiederlassungId(815L)
            .build();
        //@formatter:on
    }

    private void createAdresse() {
        //@formatter:off
        adresse = new AdresseBuilder()
            .withRandomAdresseNo()
            .withGeoId(geoId.getId())
            .build();
        //@formatter:on
    }

    private void createIpAddress() {
        //@formatter:off
        ipAddress = new IPAddressBuilder()
                .withRandomId()
                .build();
        //@formatter:on
    }

    private void createNiederlassung() {
        //@formatter:off
        niederlassung = new NiederlassungBuilder()
                .withRandomId()
                .withIpLocation(ipLocation)
                .build();
        //@formatter:on
    }

    private void createIpLocation() {
        //@formatter:off
        ipLocation = new ReferenceBuilder()
                .withRandomId()
                .withType(Reference.REF_TYPE_IP_BACKBONE_LOCATION_TYPE)
                .build();
        //@formatter:on
    }

    private void createIpPurpose() {
        //@formatter:off
        ipPurpose = new ReferenceBuilder()
                .withRandomId()
                .withType(refType())
                .build();
        //@formatter:on
    }

    private void createAuftragDaten() {
        //@formatter:off
        auftragDaten = new AuftragDatenBuilder()
                .withProdId(produkt.getId())
                .withAuftragBuilder(auftragBuilder)
                .withInbetriebnahme(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .build();
        //@formatter:on
    }

    private void createProdukt() {
        //@formatter:off
        produkt = new ProduktBuilder()
                .withRandomId()
                .withIpNetmaskSizeV6(48)
                .withIpNetmaskSizeV4(16)
                .withIpPurposeV4Editable(Boolean.FALSE)
                .withIpPurposeV4(ipPurpose)
                .build();
        //@formatter:on
    }

    @Test
    public void testExecute() throws Exception {
        Object result = cut().execute();
        assertThat(result, instanceOf(ServiceCommandResult.class));
        ServiceCommandResult commandResult = (ServiceCommandResult) result;
        assertThat(commandResult.isOk(), equalTo(Boolean.TRUE));
        verify(ipAddressService).saveIPAddress(eq(ipAddress), anyLong());
    }
}


