package de.augustakom.hurrican.service.cc.ffm.command;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragIntern;
import de.augustakom.hurrican.model.cc.AuftragInternBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.AuftragInternService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;

/**
 *
 */
public class AggregateFfmHeaderLocation4InterneArbeitCommandTest extends AbstractAggregateFfmCommandTest {

    @Mock
    private EndstellenService endstellenService;
    @Mock
    private CCKundenService ccKundenService;
    @Mock
    private CCAuftragService auftragService;
    @Mock
    private KundenService billingKundenService;
    @Mock
    private HVTService hvtService;
    @Mock
    private AuftragInternService auftragInternService;


    @InjectMocks
    @Spy
    private AggregateFfmHeaderLocation4InterneArbeitCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new AggregateFfmHeaderLocation4InterneArbeitCommand();
        prepareFfmCommand(testling);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteFailure() throws Exception {
        when(auftragInternService.findByAuftragId(auftragDaten.getAuftragId())).thenThrow(new FFMServiceException("failure"));

        Object result = testling.execute();

        assertNotNull(result);
        assertTrue(result instanceof ServiceCommandResult);
        assertFalse(((ServiceCommandResult) result).isOk());
        verify(auftragInternService).findByAuftragId(auftragDaten.getAuftragId());
    }

    @DataProvider
    public Object[][] executeNoServiceRaumDP() {
        return new Object[][] {
                { null },
                { new AuftragInternBuilder().setPersist(false).withHvtStandortId(null).build() },
                { new AuftragInternBuilder().setPersist(false).withHvtStandortId(99L).build() },
        };
    }

    /**
     * kein Service-Raum bzw. Service Raum mit unvollstaendiger Adresse.
     *
     * @param auftragIntern
     * @throws Exception
     */
    @Test(dataProvider = "executeNoServiceRaumDP")
    public void testExecuteNoServiceRaum(AuftragIntern auftragIntern) throws Exception {
        when(auftragInternService.findByAuftragId(auftragDaten.getAuftragId())).thenReturn(auftragIntern);
        when(auftragService.findAuftragById(anyLong())).thenReturn(
                new AuftragBuilder().setPersist(false).withId(auftragDaten.getAuftragId()).build());
        if(auftragIntern != null && auftragIntern.getHvtStandortId() != null) {
            when(hvtService.findHVTGruppe4Standort(anyLong())).thenReturn(new HVTGruppeBuilder().setPersist(false).withHausNr(null).build());
        }

        Object result = testling.execute();

        assertNotNull(result);
        assertTrue(result instanceof ServiceCommandResult);
        assertTrue(((ServiceCommandResult) result).isOk());
        verify(auftragInternService).findByAuftragId(auftragDaten.getAuftragId());
        verify(billingKundenService).getAdresse4Kunde(any());
    }

    @Test
    public void testExecute() throws Exception {
        long hvtStandortId = 1L;
        AuftragIntern auftragIntern =
                new AuftragInternBuilder().setPersist(false)
                        .withHvtStandortId(hvtStandortId)
                        .build();
        when(auftragInternService.findByAuftragId(auftragDaten.getAuftragId())).thenReturn(auftragIntern);
        String ort = "Augsburg";
        String plz = "12345";
        String strasse = "Curt-Frenzel Str.";
        String hausNr = "10";
        HVTGruppe hvtGruppe =
                new HVTGruppeBuilder().setPersist(false)
                        .withOrt(ort)
                        .withPlz(plz)
                        .withStrasse(strasse)
                        .withHausNr(hausNr)
                        .build();
        when(hvtService.findHVTGruppe4Standort(hvtStandortId)).thenReturn(hvtGruppe);

        Object result = testling.execute();

        assertNotNull(result);
        assertTrue(result instanceof ServiceCommandResult);
        assertTrue(((ServiceCommandResult) result).isOk());
        verify(auftragInternService).findByAuftragId(auftragDaten.getAuftragId());
        verify(hvtService).findHVTGruppe4Standort(hvtStandortId);

        assertNotNull(workforceOrder.getLocation());
        assertNull(workforceOrder.getLocation().getCountry());
        assertEquals(workforceOrder.getLocation().getCity(), ort);
        assertEquals(workforceOrder.getLocation().getZipCode(), plz);
        assertEquals(workforceOrder.getLocation().getStreet(), strasse);
        assertNull(workforceOrder.getLocation().getFloor());
        assertEquals(workforceOrder.getLocation().getHouseNumber(), hausNr);
    }

}
