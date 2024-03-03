/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.03.2005 09:50:22
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.LeistungBuilder;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.Leistung2DNBuilder;
import de.augustakom.hurrican.model.cc.Leistung2ParameterBuilder;
import de.augustakom.hurrican.model.cc.Leistung4DnBuilder;
import de.augustakom.hurrican.model.cc.LeistungParameterBuilder;
import de.augustakom.hurrican.model.cc.Leistungsbuendel2ProduktBuilder;
import de.augustakom.hurrican.model.cc.LeistungsbuendelBuilder;
import de.augustakom.hurrican.model.cc.dn.DNLeistungsView;
import de.augustakom.hurrican.model.cc.dn.Leistung2DN;
import de.augustakom.hurrican.model.cc.dn.Leistung2Parameter;
import de.augustakom.hurrican.model.cc.dn.Leistung4Dn;
import de.augustakom.hurrican.model.cc.dn.LeistungParameter;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel2Produkt;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.LeistungService;
import de.augustakom.hurrican.service.cc.CCRufnummernService;


/**
 * JUnit Test-Case fuer <code>CCRufnummernService</code>.
 *
 *
 */
@Test(groups = BaseTest.SERVICE)
public class CCRufnummernServiceTest extends AbstractHurricanBaseServiceTest {

    @InjectMocks
    private CCRufnummernService service;

    @Mock
    private LeistungService leistungService;

    @BeforeMethod
    public void setup() {
        service = getCCService(CCRufnummernService.class);
        MockitoAnnotations.initMocks(this);
    }

    public void testFindLeistung2DN() throws Exception {
        Leistung2DN l2dn = getBuilder(Leistung2DNBuilder.class)
                .withLeistung4DnId(Leistung4Dn.DN_SERVICE_AGRU)
                .withScvRealisierung(new Date())
                .build();

        List<Long> dnServiceIDs = new ArrayList<Long>();
        dnServiceIDs.add(Leistung4Dn.DN_SERVICE_AGRU);

        List<Leistung2DN> result = service.findLeistung2DN(l2dn.getDnNo(), dnServiceIDs, new Date());
        assertNotEmpty(result, "No DN services of expected type found!");
    }


    public void testFindLeistungsbuendel2Produkt() throws FindException {
        Long leistungNoOrig = Long.valueOf(100);
        getBuilder(Leistungsbuendel2ProduktBuilder.class)
                .withLeistungsbuendelBuilder(getBuilder(LeistungsbuendelBuilder.class).withRandomId())
                .withLeistungNoOrig(leistungNoOrig)
                .build();
        Leistungsbuendel2Produkt leistungsbuendel2Produkt = getBuilder(Leistungsbuendel2ProduktBuilder.class)
                .withLeistungsbuendelBuilder(getBuilder(LeistungsbuendelBuilder.class).withRandomId())
                .withLeistungNoOrig(leistungNoOrig)
                .withRandomProtokollLeistungNoOrig()
                .build();

        List<Leistungsbuendel2Produkt> result = service.findLeistungsbuendel2Produkt(leistungNoOrig);
        assertNotEmpty(result);
        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getProtokollLeistungNoOrig(), leistungsbuendel2Produkt.getProtokollLeistungNoOrig());
        assertNull(result.get(1).getProtokollLeistungNoOrig());
    }


    public void testFindDNLeistungen() throws Exception {
        LeistungsbuendelBuilder leistungsbuendelBuilder = getBuilder(LeistungsbuendelBuilder.class).withRandomId();

        Leistung4DnBuilder leistung4DnBuilder = getBuilder(Leistung4DnBuilder.class).withRandomId();
        Leistung2DN leistung2Dn = getBuilder(Leistung2DNBuilder.class)
                .withLeistung4DnBuilder(leistung4DnBuilder)
                .withDnNo(Long.valueOf(1))
                .withLeistungsbuendelBuilder(leistungsbuendelBuilder)
                .build();

        List<DNLeistungsView> result = service.findDNLeistungen(Arrays.asList(leistung2Dn.getDnNo()), leistungsbuendelBuilder.get().getId());
        assertNotEmpty(result, "Es konnten keine Rufnummernleistungen ermittelt werden.");
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getDnNo(), leistung2Dn.getDnNo());
    }

    public void testFindSignedParameter2Leistung() throws Exception {
        LeistungParameterBuilder leistungParameterBuilder = getBuilder(LeistungParameterBuilder.class);
        Leistung4DnBuilder leistung4dnBuilder = getBuilder(Leistung4DnBuilder.class).withRandomId();
        Leistung2Parameter leistung2Parameter = getBuilder(Leistung2ParameterBuilder.class)
                .withLeistungId(leistung4dnBuilder.get().getId())
                .withLeistungParameterBuilder(leistungParameterBuilder)
                .build();

        List<LeistungParameter> result = service.findSignedParameter2Leistung(leistung2Parameter.getLeistungId());
        assertNotEmpty(result, "Keine Parameter zur Leistung gefunden!");
        assertEquals(result.size(), 1);
    }

    public void testFindLeistung2DnByExample() throws Exception {
        Leistung2DN example = getBuilder(Leistung2DNBuilder.class)
                .withDnNo(Long.valueOf(1))
                .withLeistungsbuendelBuilder(getBuilder(LeistungsbuendelBuilder.class))
                .build();

        List<Leistung2DN> result = service.findLeistung2DnByExample(example);

        assertNotEmpty(result, "Keine DN-Leistungen gefunden!");
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), example.getId());
    }

    @SuppressWarnings("unused")
    public void testFindLeistung2DN4CPSTx() throws Exception {
        CPSTransactionBuilder cpsTxBuilder = getBuilder(CPSTransactionBuilder.class);
        Leistung2DN l2dnCreated = getBuilder(Leistung2DNBuilder.class)
                .withCpsTxCreationBuilder(cpsTxBuilder)
                .build();
        Leistung2DN l2dnCancelled = getBuilder(Leistung2DNBuilder.class)
                .withCpsTxCancelBuilder(cpsTxBuilder)
                .build();
        Leistung2DN l2dnNoCpsTx = getBuilder(Leistung2DNBuilder.class)
                .build();

        List<Leistung2DN> result = service.findLeistung2DN4CPSTx(cpsTxBuilder.get().getId());

        assertNotEmpty(result);
        assertEquals(result.size(), 2);
        for (Leistung2DN l2dn : result) {
            if (l2dn.getId().equals(l2dnNoCpsTx.getId())) {
                fail("Leistung2DN without Cps-Tx ID should not be found!");
            }
        }
    }

    @DataProvider(name = "leistung2DnDataProvider")
    protected Object[][] leistung2DnDataProvider() {
        Date provisioningDate = DateTools.createDate(2099, 0, 1);  // sinnloses Datum verwenden, um Datenbestand und somit nicht gewollte Ergebnisse zu umgehen
        Date tomorrow = DateTools.changeDate(provisioningDate, Calendar.DAY_OF_MONTH, 1);
        return new Object[][] {
                // erstes Field dient lediglich der Identifikation eines fehlgeschlagenen Tests!
                //number(String)
                //     provisioningDateForQuery(Date)
                //                       provisioningDate(Date)
                //                                         scvRealisierung(Date)
                //                                                           ewsdRealisierung(Date)
                //                                                                             cpsTxCreation(boolean)
                //                                                                                    scvKuendigung(Date)
                //                                                                                                      ewsdKuendigung(Date)
                //                                                                                                                        cpsTxCancel(boolean)
                //                                                                                                                               expectResult(boolean)
                { "1", provisioningDate, provisioningDate, provisioningDate, null, false, null, null, false, true },  // noch zu erzeugen
                { "2", provisioningDate, provisioningDate, null, null, false, provisioningDate, null, false, true },  // noch zu kuendigen
                { "3", provisioningDate, provisioningDate, provisioningDate, provisioningDate, true, null, null, false, false },  // bereits eingerichtet
                { "4", provisioningDate, provisioningDate, null, null, false, provisioningDate, provisioningDate, true, false },  // bereits gekuendigt
                { "5", provisioningDate, provisioningDate, tomorrow, null, false, null, null, false, false },  // wg. Datum nicht zu beruecksichtigen!
        };
    }

    @Test(dataProvider = "leistung2DnDataProvider")
    public void testFindUnProvisionedDNServices(String number, Date provisioningDateForQuery, Date provisioningDate, Date scvRealisierung, Date ewsdRealisierung, boolean cpsTxCreation, Date scvKuendigung, Date ewsdKuendigung, boolean cpsTxCancel, boolean expectResult) throws Exception {
        CPSTransactionBuilder cpsTxBuilder = getBuilder(CPSTransactionBuilder.class);

        Leistung2DN leistung2Dn = getBuilder(Leistung2DNBuilder.class)
                .withScvRealisierung(scvRealisierung)
                .withEwsdRealisierung(ewsdRealisierung)
                .withCpsTxCreationBuilder((cpsTxCreation) ? cpsTxBuilder : null)
                .withScvKuendigung(scvKuendigung)
                .withEwsdKuendigung(ewsdKuendigung)
                .withCpsTxCancelBuilder((cpsTxCancel) ? cpsTxBuilder : null)
                .build();

        List<Leistung2DN> result = service.findUnProvisionedDNServices(provisioningDateForQuery);

        if (expectResult) {
            assertNotEmpty(result);
            assertEquals(result.get(0).getId(), leistung2Dn.getId());
        }
        else {
            assertEmpty(result, "");
        }
    }


    public void findLeistungsbuendel4Auftrag() throws FindException {
        Leistung billingLeistung = getBuilder(LeistungBuilder.class).withLeistungNoOrig(Long.valueOf(666)).build();
        LeistungsbuendelBuilder leistungsbuendelBuilder = getBuilder(LeistungsbuendelBuilder.class);
        Leistungsbuendel2Produkt leistungsbuendel2Produkt = getBuilder(Leistungsbuendel2ProduktBuilder.class)
                .withLeistungNoOrig(billingLeistung.getLeistungNoOrig())
                .withLeistungsbuendelBuilder(leistungsbuendelBuilder)
                .build();

        when(leistungService.findProductLeistung4Auftrag(any(Long.class), any(String.class))).thenReturn(
                billingLeistung);

        Leistungsbuendel result = service.findLeistungsbuendel4Auftrag(null, leistungService);
        assertNotNull(result);
        assertEquals(result.getId(), leistungsbuendel2Produkt.getLbId());
        verify(leistungService, times(0)).findLeistungen4Auftrag(any(Long.class));
    }

    public void findLeistungsbuendel4AuftragMitProtokollLeistung() throws FindException {
        Leistung billingLeistung = getBuilder(LeistungBuilder.class).withLeistungNoOrig(Long.valueOf(666)).build();
        LeistungsbuendelBuilder leistungsbuendelBuilder = getBuilder(LeistungsbuendelBuilder.class);
        Leistungsbuendel2Produkt leistungsbuendel2Produkt = getBuilder(Leistungsbuendel2ProduktBuilder.class)
                .withLeistungNoOrig(billingLeistung.getLeistungNoOrig())
                .withLeistungsbuendelBuilder(leistungsbuendelBuilder)
                .withRandomProtokollLeistungNoOrig()
                .build();

        Leistung protokollLeistung = getBuilder(LeistungBuilder.class).withLeistungNoOrig(leistungsbuendel2Produkt.getProtokollLeistungNoOrig()).build();

        when(leistungService.findProductLeistung4Auftrag(any(Long.class), any(String.class))).thenReturn(
                billingLeistung);
        when(leistungService.findLeistungen4Auftrag(any(Long.class))).thenReturn(Arrays.asList(billingLeistung, protokollLeistung));

        Leistungsbuendel result = service.findLeistungsbuendel4Auftrag(Long.valueOf(1), leistungService);
        assertNotNull(result);
        assertEquals(result.getId(), leistungsbuendel2Produkt.getLbId());
        verify(leistungService, times(1)).findLeistungen4Auftrag(any(Long.class));
    }

    public void kuendigeLeistung4Rufnummern() throws Exception {
        final Leistung4DnBuilder leistung4DnBuilder = getBuilder(Leistung4DnBuilder.class);
        final Rufnummer rufnummer1 = getBuilder(RufnummerBuilder.class).withRandomDnNo().build();
        final Rufnummer rufnummer2 = getBuilder(RufnummerBuilder.class).withRandomDnNo().build();
        final Date kuendigungScv = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        //@formatter:off
        final Leistung2DN leistung2Dn1 = getBuilder(Leistung2DNBuilder.class)
                .withLeistung4DnBuilder(leistung4DnBuilder)
                .withDnNo(rufnummer1.getDnNo())
                .build();
        final Leistung2DN leistung2Dn2 = getBuilder(Leistung2DNBuilder.class)
                .withLeistung4DnBuilder(leistung4DnBuilder)
                .withDnNo(rufnummer2.getDnNo())
                .build();
        // ist bereits gekündigt, da kuendigungScv gesetzt wurde und darf damit nicht nochmal
        // gekündigt werden (= kuendigungScv auf jetzt setzen)
        final Leistung2DN leistung2Dn3 = getBuilder(Leistung2DNBuilder.class)
                .withLeistung4DnBuilder(leistung4DnBuilder)
                .withDnNo(rufnummer2.getDnNo())
                .withScvKuendigung(kuendigungScv)
                .build();
        //@formatter:on
        final Date kuendigungAm = new Date();
        final String username = "asdf";

        service.kuendigeLeistung4Rufnummern(leistung4DnBuilder.get().getId(), kuendigungAm, username,
                ImmutableList.of(rufnummer1, rufnummer2));

        assertThat(leistung2Dn1.getScvKuendigung(), equalTo(kuendigungAm));
        assertThat(leistung2Dn1.getScvUserKuendigung(), equalTo(username));
        assertThat(leistung2Dn2.getScvKuendigung(), equalTo(kuendigungAm));
        assertThat(leistung2Dn2.getScvUserKuendigung(), equalTo(username));
        assertThat(leistung2Dn3.getScvKuendigung(), equalTo(kuendigungScv));
    }

}
