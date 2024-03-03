/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.02.2013 09:54:24
 */
package de.mnet.hurrican.webservice.tvprovider;

import static com.google.common.collect.ImmutableList.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.ProduktGruppeBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AuftragTvService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.mnet.hurrican.tvprovider.GetTvAvailabilityInformationRequest;
import de.mnet.hurrican.tvprovider.GetTvAvailabilityInformationResponse;
import de.mnet.hurrican.tvprovider.TvAvailabilityInformationType;
import de.mnet.hurrican.webservice.tvprovider.TvProviderEndpoint.AuftragEndstelle;

@Test(groups = { BaseTest.UNIT })
public class TvProviderEndpointTest {

    @InjectMocks
    @Spy
    private TvProviderEndpoint underTest;

    @Mock
    private EndstellenService endstellenService;
    @Mock
    private ProduktService produktService;
    @Mock
    private AuftragTvService auftragTvService;

    private final long geoId = 1234L;

    private final Produkt produkt = new ProduktBuilder()
            .withRandomId()
            .withAnschlussart("asdf")
            .withProduktGruppeId(4231L)
            .build();
    private final ProduktGruppe produktGruppe = new ProduktGruppeBuilder()
            .withId(produkt.getProduktGruppeId())
            .build();
    private final Endstelle endstelle = new EndstelleBuilder()
            .withRandomId()
            .withGeoIdBuilder(new GeoIdBuilder().withId(geoId))
            .build();
    private final AuftragDaten auftragDaten = new AuftragDatenBuilder()
            .withRandomId()
            .withGueltigBis(DateTools.getHurricanEndDate())
            .withProdId(produkt.getId())
            .build();

    @BeforeMethod
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    public void testCreateTvAvailabilityInfo() throws Exception {
        when(produktService.findProdukt(produkt.getProdId())).thenReturn(produkt);
        when(produktService.findProduktGruppe(produkt.getProduktGruppeId())).thenReturn(produktGruppe);

        final TvAvailabilityInformationType result = underTest.createTvAvailabilityInfo(new AuftragEndstelle(
                auftragDaten, endstelle));

        assertThat(result.getBillingOrderNo(), equalTo(auftragDaten.getAuftragNoOrig()));
        assertThat(result.getGeoId(), equalTo(endstelle.getGeoId()));
        assertThat(result.getOrderState(), equalTo(TvProviderEndpoint.getStatus(auftragDaten)));
        assertThat(result.getProductGroup(), equalTo(produktGruppe.getProduktGruppe()));
        assertThat(result.getProductName(), equalTo(produkt.getAnschlussart()));
    }

    public void testAuftragEndstellen() throws FindException {
        final AuftragDaten auftrag1 = new AuftragDatenBuilder().withRandomId().withRandomAuftragId()
                .withAuftragNoOrig(null).build();
        final AuftragDaten auftrag2 = new AuftragDatenBuilder().withRandomId().withRandomAuftragId()
                .withAuftragNoOrig(2L).build();
        final AuftragDaten auftrag3 = new AuftragDatenBuilder().withRandomId().withRandomAuftragId()
                .withAuftragNoOrig(3L).withStatusId(AuftragStatus.ERFASSUNG).withProdId(10L).build();
        final AuftragDaten auftrag4 = new AuftragDatenBuilder().withRandomId().withRandomAuftragId()
                .withAuftragNoOrig(4L).withStatusId(AuftragStatus.IN_BETRIEB).withProdId(10L).build();
        final AuftragDaten auftrag5 = new AuftragDatenBuilder().withRandomId().withRandomAuftragId()
                .withAuftragNoOrig(5L).withStatusId(AuftragStatus.IN_BETRIEB).withProdId(10L).build();
        final AuftragDaten auftrag6 = new AuftragDatenBuilder().withRandomId().withRandomAuftragId()
                .withAuftragNoOrig(6L).withStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT).withProdId(10L).build();
        final AuftragDaten auftrag7 = new AuftragDatenBuilder().withRandomId().withRandomAuftragId()
                .withAuftragNoOrig(7L).withStatusId(AuftragStatus.STORNO).withProdId(11L).build();

        final AuftragEndstelle ae2 = new AuftragEndstelle(auftrag2, endstelle);
        final AuftragEndstelle ae3 = new AuftragEndstelle(auftrag3, endstelle);
        final AuftragEndstelle ae5 = new AuftragEndstelle(auftrag5, endstelle);
        final AuftragEndstelle ae6 = new AuftragEndstelle(auftrag6, endstelle);

        when(auftragTvService.findTvAuftraege(geoId)).thenReturn(Arrays.asList(auftrag1, auftrag2, auftrag3,
                auftrag4, auftrag5, auftrag6, auftrag7));
        when(endstellenService.findEndstelle4Auftrag(anyLong(), eq(Endstelle.ENDSTELLEN_TYP_B))).thenReturn(endstelle);

        List<AuftragEndstelle> auftragEndstellen = underTest.getAuftragEndstellen(geoId);

        List<AuftragEndstelle> expected = of(ae6, ae5, ae3, ae2);
        assertThat(auftragEndstellen, equalTo(expected));
    }

    public void testGetTvAvailabilityInformationWhenGeoIdNotFound() throws Exception {
        final GetTvAvailabilityInformationRequest request = new GetTvAvailabilityInformationRequest();
        request.setGeoId(geoId);
        //noinspection unchecked
        when(auftragTvService.findTvAuftraege(geoId)).thenReturn(Collections.EMPTY_LIST);
        GetTvAvailabilityInformationResponse tvAvailabilityInformation = underTest.getTvAvailabilityInformation(request);
        assertNotNull(tvAvailabilityInformation.getTvAvailabilityInformation());
        assertTrue(tvAvailabilityInformation.getTvAvailabilityInformation().isEmpty());
    }

    @Test(expectedExceptions = { TvProviderTechnicalException.class })
    public void testGetTvAvailabilityInformationWhenUnexpectedExceptionIsThrown() throws Exception {
        final GetTvAvailabilityInformationRequest request = new GetTvAvailabilityInformationRequest();
        request.setGeoId(geoId);
        when(auftragTvService.findTvAuftraege(anyLong())).thenThrow(new RuntimeException(""));
        underTest.getTvAvailabilityInformation(request);
    }

}
