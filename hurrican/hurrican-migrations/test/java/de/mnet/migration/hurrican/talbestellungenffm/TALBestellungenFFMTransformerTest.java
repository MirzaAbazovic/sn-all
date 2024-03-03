package de.mnet.migration.hurrican.talbestellungenffm;

import static org.mockito.Mockito.*;

import java.util.*;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.Assert;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.ffm.FFMService;

/**
 * Unit test for {@link TALBestellungenFFMTransformer}
 */
public class TALBestellungenFFMTransformerTest {
    private TALBestellungenFFMTransformer transformer;

    @Mock
    private BAService baServiceMock;
    @Mock
    private FFMService ffmService;
    @Mock
    private VerlaufAbteilung verlaufAbteilung;
    @Mock
    private Verlauf verlauf;


    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        transformer = new TALBestellungenFFMTransformer();
        FieldUtils.writeField(transformer, "baService", baServiceMock, true);
        FieldUtils.writeField(transformer, "ffmService", ffmService, true);
        FieldUtils.writeField(transformer, "migIdHolder", new TALBestellungenMigIdHolder(), true);
    }

    @DataProvider
    public Object[][] dataProviderHasOpenFFMAbteilung() {
        // @formatter:off
       return new Object[][] {
                { null, null,       false },
                { null, new Date(), false },
                { 1L,   new Date(), false },
                { 1L,   null,       true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderHasOpenFFMAbteilung")
    public void testHasOpenFFMAbteilung(Long ffmVerlaufId, Date ffmDatumErledigt, boolean expected) throws Exception {
        TALBestellungenFFM row = new TALBestellungenFFM();
        row.ffmVerlaufId = ffmVerlaufId;
        row.ffmDatumErledigt = ffmDatumErledigt;
        Assert.assertTrue(transformer.hasOpenFFMAbteilung(row) == expected);
    }

    @Test
    public void testMigrateFFM() throws Exception {
        when(baServiceMock.findVerlauf(anyLong())).thenReturn(verlauf);
        final TALBestellungenFFM row = new TALBestellungenFFM();
        row.verlaufId = 1L;
        row.ffmVerlaufId = 1L;
        row.ffmDatumErledigt = null;
        final Optional<TALBestellungenFFMTransformer.FfmMigrationResult> res = transformer.migrateFFM(row);
        Assert.assertTrue(res.isPresent());
        verify(ffmService).deleteOrder(any(Verlauf.class));
        verify(ffmService).createAndSendOrder(any(Verlauf.class));
    }
}
