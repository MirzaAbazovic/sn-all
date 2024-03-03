/**
 *
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.dao.cc.KvzSperreDAO;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.KvzSperre;
import de.augustakom.hurrican.model.cc.KvzSperreBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;

/**
 * Unit Test fuer die Implementierung von <code>HVTService</code>.
 */
@Test(groups = { BaseTest.UNIT })
public class HVTServiceImplUnitTest extends BaseTest {

    @Spy
    @InjectMocks
    private HVTServiceImpl cut;
    @Mock
    private KvzSperreDAO kvzSperreDAO;

    @BeforeMethod
    public void setupSut() {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider
    public Object[][] findAnschlussart4HVTStandortProvider() {
        return new Object[][] {
                { HVTStandort.HVT_STANDORT_TYP_HVT, Anschlussart.ANSCHLUSSART_HVT },
                { HVTStandort.HVT_STANDORT_TYP_KVZ, Anschlussart.ANSCHLUSSART_KVZ },
                { HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ, Anschlussart.ANSCHLUSSART_KVZ },
                { HVTStandort.HVT_STANDORT_TYP_FTTB, Anschlussart.ANSCHLUSSART_FTTB },
                { HVTStandort.HVT_STANDORT_TYP_FTTH, Anschlussart.ANSCHLUSSART_FTTH },
                { HVTStandort.HVT_STANDORT_TYP_FTTX_BR, Anschlussart.ANSCHLUSSART_FTTX },
                { HVTStandort.HVT_STANDORT_TYP_FTTX_FC, Anschlussart.ANSCHLUSSART_FTTX },
                { HVTStandort.HVT_STANDORT_TYP_EWSD, null },
                { null, null } };
    }

    @Test(dataProvider = "findAnschlussart4HVTStandortProvider")
    public void testFindAnschlussart4HVTStandort(Long standortTypRefId, Long expectedAnschlussart) throws Exception {
        HVTStandort hvtStandort = new HVTStandort();
        hvtStandort.setStandortTypRefId(standortTypRefId);
        doReturn(hvtStandort).when(cut).findHVTStandort(any(Long.class));

        Long anschlussart = cut.findAnschlussart4HVTStandort(hvtStandort.getId());

        assertEquals(anschlussart, expectedAnschlussart);
    }

    @Test(expectedExceptions = { ValidationException.class })
    public void testValidateKvzSperreExceptionExpected() throws Exception {
        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder().withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ).setPersist(false);
        Endstelle endstelle = new EndstelleBuilder().setPersist(false).build();

        doReturn(hvtStandortBuilder.get()).when(cut).findHVTStandort(anyLong());
        doReturn(new KvzSperreBuilder().setPersist(false).build()).when(cut).findKvzSperre(anyLong(), anyLong());

        cut.validateKvzSperre(endstelle);
    }

    @Test
    public void testValidateKvzSperreExceptionNotExpected() throws Exception {
        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder().withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTB).setPersist(false);
        Endstelle endstelle = new EndstelleBuilder().setPersist(false).build();

        doReturn(hvtStandortBuilder.get()).when(cut).findHVTStandort(anyLong());
        doReturn(new KvzSperreBuilder().setPersist(false).build()).when(cut).findKvzSperre(anyLong(), anyLong());

        cut.validateKvzSperre(endstelle);
    }

    @Test
    public void testCreateKvzSperre() throws Exception {
        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder()
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTB)
                .withHvtGruppeBuilder(new HVTGruppeBuilder().setPersist(false))
                .setPersist(false);

        final HVTStandort hvtStandort = hvtStandortBuilder.get();
        final HVTGruppe hvtGruppe = hvtStandortBuilder.getHvtGruppeBuilder().get();
        doReturn(hvtStandort).when(cut).findHVTStandort(hvtStandort.getId());
        doReturn(hvtGruppe).when(cut).findHVTGruppe4Standort(hvtStandort.getId());

        final String kvz_id = "KVZ_ID";
        final String comment = "test comment";
        cut.createKvzSperre(hvtStandort.getId(), kvz_id, comment);

        ArgumentCaptor<KvzSperre> ac = ArgumentCaptor.forClass(KvzSperre.class);
        verify(kvzSperreDAO).store(ac.capture());
        assertEquals(ac.getValue().getKvzNummer(), kvz_id);
        assertEquals(ac.getValue().getBemerkung(), comment);
        assertEquals(ac.getValue().getAsb(), hvtStandort.getDTAGAsb());
        assertEquals(ac.getValue().getOnkz(), hvtGruppe.getOnkz());
    }

    @Test(expectedExceptions = FindException.class, expectedExceptionsMessageRegExp = "Es konnte kein HVT-Standort fuer die angebebene HVT-Standort-ID '5555' gefunden werden")
    public void testCreateKvzSperreNoKvt() throws Exception {
        doReturn(null).when(cut).findHVTStandort(anyLong());
        doReturn(null).when(cut).findHVTGruppe4Standort(anyLong());

        final String kvz_id = "KVZ_ID";
        final String comment = "test comment";
        cut.createKvzSperre(5555L, kvz_id, comment);
    }
}
