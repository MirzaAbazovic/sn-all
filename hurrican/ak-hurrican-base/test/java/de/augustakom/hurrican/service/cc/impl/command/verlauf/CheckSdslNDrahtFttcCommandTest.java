package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.SdslNdraht;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;

@Test(groups = { BaseTest.UNIT })
public class CheckSdslNDrahtFttcCommandTest extends BaseTest {

    private static final Long AUFTRAG_ID = Long.MAX_VALUE;

    @InjectMocks
    @Spy
    private CheckSdslNDrahtFttcCommand cut;

    @Mock
    private CCAuftragService auftragService;
    @Mock
    private RangierungsService rangierungsService;
    @Mock
    private ProduktService produktService;
    @Mock
    private CCLeistungsService leistungsService;
    @Mock
    private EndstellenService endstellenService;
    @Mock
    private HVTService hvtService;

    @BeforeMethod
    public void setUp() throws ServiceNotFoundException {
        cut = new CheckSdslNDrahtFttcCommand();
        MockitoAnnotations.initMocks(this);

        doReturn(AUFTRAG_ID).when(cut).getAuftragId();
    }


    @Test
    public void testIsNDrahtNecessaryNoNDraht() throws FindException {
        Produkt produkt = new ProduktBuilder().withSdslNdraht(null).build();
        assertFalse(cut.isNDrahtNecessary(produkt));
    }


    @DataProvider(name = "isNDrahtNecessaryDP")
    public Object[][] isNDrahtNecessaryDP() {
        TechLeistung techLsNoBandwidth = new TechLeistungBuilder().withLongValue(null).build();
        TechLeistung techLsBandwidth25 = new TechLeistungBuilder().withLongValue(25000L).build();
        TechLeistung techLsBandwidth50 = new TechLeistungBuilder().withLongValue(50000L).build();
        TechLeistung techLsBandwidth100 = new TechLeistungBuilder().withLongValue(100000L).build();

        return new Object[][] {
                { Collections.emptyList(), Collections.emptyList(), false },
                { Arrays.asList(techLsNoBandwidth), Arrays.asList(techLsNoBandwidth), false },
                { Arrays.asList(techLsBandwidth25), Arrays.asList(techLsBandwidth25), false },
                { Arrays.asList(techLsBandwidth25), Arrays.asList(techLsBandwidth50), false },
                { Arrays.asList(techLsBandwidth50), Arrays.asList(techLsBandwidth25), false },
                { Arrays.asList(techLsBandwidth50), Arrays.asList(techLsBandwidth50), true },
                { Arrays.asList(techLsBandwidth25), Arrays.asList(techLsNoBandwidth, techLsBandwidth50), false },
                { Arrays.asList(techLsBandwidth25, techLsBandwidth50), Arrays.asList(techLsBandwidth25, techLsBandwidth50), true },
                { Arrays.asList(techLsBandwidth100), Arrays.asList(techLsBandwidth100), true }
        };
    }

    @Test(dataProvider = "isNDrahtNecessaryDP")
    public void testIsNDrahtNecessary(List<TechLeistung> downstream, List<TechLeistung> upstream, boolean expected) throws FindException {
        Produkt produkt = new ProduktBuilder().withSdslNdraht(SdslNdraht.OPTIONAL_BONDING).build();
        when(leistungsService.findTechLeistungen4Auftrag(eq(AUFTRAG_ID), eq(TechLeistung.TYP_DOWNSTREAM), any(Date.class))).thenReturn(downstream);
        when(leistungsService.findTechLeistungen4Auftrag(eq(AUFTRAG_ID), eq(TechLeistung.TYP_UPSTREAM), any(Date.class))).thenReturn(upstream);

        assertEquals(cut.isNDrahtNecessary(produkt), expected);
    }


    @Test(expectedExceptions = HurricanServiceCommandException.class)
    public void testIsFttcThrowsExceptionIfEndstelleBNotPresent() throws FindException, HurricanServiceCommandException {
        when(endstellenService.findEndstelle4Auftrag(anyLong(), eq(Endstelle.ENDSTELLEN_TYP_B))).thenReturn(null);
        cut.isFttc();
    }

    @Test
    public void testIsFttc() throws FindException, HurricanServiceCommandException {
        HVTStandortBuilder hvtStdBuilder = new HVTStandortBuilder()
                .withRandomId().withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ);
        Endstelle endstelle = new EndstelleBuilder()
                .withEndstelleGruppeBuilder(new AuftragTechnik2EndstelleBuilder())
                .withHvtStandortBuilder(hvtStdBuilder).build();

        when(endstellenService.findEndstelle4Auftrag(anyLong(), eq(Endstelle.ENDSTELLEN_TYP_B))).thenReturn(endstelle);
        when(hvtService.findHVTStandort(hvtStdBuilder.get().getId())).thenReturn(hvtStdBuilder.get());
        assertTrue(cut.isFttc());
    }


    @Test
    public void testExecuteNoActionIfNotFttc() throws Exception {
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(1L)
                .withProdId(Produkt.PROD_ID_SDSL_10000)
                .build();

        doReturn(auftragDaten).when(cut).getAuftragDatenTx(anyLong());
        doReturn(false).when(cut).isFttc();
        when(produktService.findProdukt(auftragDaten.getProdId())).thenReturn(new Produkt());
        Object result = cut.execute();

        verify(auftragService, never()).findAuftragDaten4OrderNoOrigTx(anyLong());
        verify(cut, never()).isNDrahtNecessary(any(Produkt.class));
        assertTrue(result instanceof ServiceCommandResult);
        assertTrue(((ServiceCommandResult) result).isOk());
    }

    @Test
    public void testExecuteNoActionIfNDrahtNotNecessary() throws Exception {
        doReturn(new AuftragDaten()).when(cut).getAuftragDatenTx(anyLong());
        doReturn(true).when(cut).isFttc();
        when(produktService.findProdukt(anyLong())).thenReturn(new Produkt());
        Object result = cut.execute();

        verify(auftragService, never()).findAuftragDaten4OrderNoOrigTx(anyLong());
        verify(cut).isNDrahtNecessary(any(Produkt.class));
        assertTrue(result instanceof ServiceCommandResult);
        assertTrue(((ServiceCommandResult) result).isOk());
    }

    private void prepare4Execute(boolean differentBaugruppen, boolean hasRangierung) throws Exception {
        doReturn(new AuftragDaten()).when(cut).getAuftragDatenTx(anyLong());
        when(produktService.findProdukt(anyLong()))
                .thenReturn(new ProduktBuilder().withSdslNdraht(SdslNdraht.OPTIONAL_BONDING).build());
        doReturn(true).when(cut).isFttc();
        doReturn(true).when(cut).isNDrahtNecessary(any(Produkt.class));
        doReturn(hasRangierung).when(cut).hasRangierung(anyLong());
        doReturn(differentBaugruppen).when(cut).hasRangierungOnDifferentBgs(any());

        AuftragDaten ad1 = new AuftragDatenBuilder().withStatusId(AuftragStatus.ERFASSUNG).build();
        AuftragDaten ad2 = new AuftragDatenBuilder().withStatusId(AuftragStatus.ERFASSUNG).build();

        when(auftragService.findAuftragDaten4OrderNoOrigTx(anyLong())).thenReturn(Arrays.asList(ad1, ad2));
    }

    @Test
    public void testExecuteGoodCase() throws Exception {
        prepare4Execute(false, true);

        Object result = cut.execute();

        assertTrue(result instanceof ServiceCommandResult);
        assertTrue(((ServiceCommandResult) result).isOk());
    }


    @Test
    public void testExecuteDifferentBgs() throws Exception {
        prepare4Execute(true, true);

        Object result = cut.execute();

        assertTrue(result instanceof ServiceCommandResult);
        assertFalse(((ServiceCommandResult) result).isOk());
    }


    @Test
    public void testExecuteOnlyOneOrder() throws Exception {
        doReturn(new AuftragDaten()).when(cut).getAuftragDatenTx(anyLong());
        when(produktService.findProdukt(anyLong()))
                .thenReturn(new ProduktBuilder().withSdslNdraht(SdslNdraht.OPTIONAL_BONDING).build());
        doReturn(true).when(cut).isFttc();
        doReturn(true).when(cut).isNDrahtNecessary(any(Produkt.class));
        doReturn(true).when(cut).hasRangierung(anyLong());

        AuftragDaten ad1 = new AuftragDatenBuilder().withStatusId(AuftragStatus.ERFASSUNG).build();

        when(auftragService.findAuftragDaten4OrderNoOrigTx(anyLong())).thenReturn(Arrays.asList(ad1));

        Object result = cut.execute();

        assertTrue(result instanceof ServiceCommandResult);
        assertFalse(((ServiceCommandResult) result).isOk());
    }

    @Test
    public void testExecuteNoRangierung() throws Exception {
        prepare4Execute(false, false);

        Object result = cut.execute();

        assertTrue(result instanceof ServiceCommandResult);
        assertFalse(((ServiceCommandResult) result).isOk());
    }

}
