package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.evolving.wsdl.sa.v1.types.ServiceRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ReferenceService;


@Test(groups = BaseTest.UNIT)
public class CPSSendAsyncTxCommandTest {

    @BeforeMethod
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    PhysikService physikService;

    @Mock
    ReferenceService referenceService;

    @Spy
    @InjectMocks
    CPSSendAsyncTxCommand cut;

    @Test
    public void testBuildRequestPayloadForWholesaleAuftragHasAppKeyVbz() throws Exception {
        final AuftragBuilder auftragBuilder = new AuftragBuilder()
                .withRandomId();
        final AuftragDaten auftragDaten = createWholesaleAuftrag(auftragBuilder);
        final CPSTransaction cpsTx = createCpsTx(auftragBuilder, auftragDaten);
        final VerbindungsBezeichnung vbz = createVbz();
        final Reference referenceSOPrio = createRefSoPrio(cpsTx);
        final Reference referenceSOType = createRefSoType(cpsTx);

        mockForBuildRequestPayload(auftragBuilder, auftragDaten, cpsTx, vbz, referenceSOPrio, referenceSOType);

        final ServiceRequest result = cut.buildRequestPayload();

        assertApplicationKey(vbz.getVbz(), result);
    }

    private void assertApplicationKey(String expectedAppKey, ServiceRequest result) {
        assertThat(result.getApplicationKeys().sizeOfApplicationKeyArray(), equalTo(1));
        assertThat(result.getApplicationKeys().getApplicationKeyArray(0).getName(), equalTo(CPSTransaction.CPS_APPLICATION_KEY_TAIFUNNUMBER));
        assertThat(result.getApplicationKeys().getApplicationKeyArray(0).getValue(), equalTo(expectedAppKey));
    }

    @Test
    public void testBuildRequestPayloadForRetailAuftragHasAppKeyTaifunNr() throws Exception {
        final AuftragBuilder auftragBuilder = new AuftragBuilder()
                .withRandomId();
        final AuftragDaten auftragDaten = createRetailAuftrag(auftragBuilder);
        final CPSTransaction cpsTx = createCpsTx(auftragBuilder, auftragDaten);
        final VerbindungsBezeichnung vbz = createVbz();
        final Reference referenceSOPrio = createRefSoPrio(cpsTx);
        final Reference referenceSOType = createRefSoType(cpsTx);

        mockForBuildRequestPayload(auftragBuilder, auftragDaten, cpsTx, vbz, referenceSOPrio, referenceSOType);

        final ServiceRequest result = cut.buildRequestPayload();

        assertApplicationKey(auftragDaten.getAuftragNoOrig().toString(), result);

    }


    private void mockForBuildRequestPayload(AuftragBuilder auftragBuilder, AuftragDaten auftragDaten, CPSTransaction cpsTx, VerbindungsBezeichnung vbz, Reference referenceSOPrio, Reference referenceSOType) throws FindException, de.augustakom.common.service.exceptions.ServiceNotFoundException {
        doReturn(auftragDaten).when(cut).getAuftragDatenTx(auftragBuilder.getId());
        doReturn(referenceService).when(cut).getCCService(ReferenceService.class);
        when(physikService.findVerbindungsBezeichnungByAuftragIdTx(auftragBuilder.getId())).thenReturn(vbz);
        when(referenceService.findReference(cpsTx.getServiceOrderPrio())).thenReturn(referenceSOPrio);
        when(referenceService.findReference(cpsTx.getServiceOrderType())).thenReturn(referenceSOType);
        cut.cpsTx = cpsTx;
    }

    private Reference createRefSoType(CPSTransaction cpsTx) {
        return new ReferenceBuilder()
                .withId(cpsTx.getServiceOrderPrio())
                .withStrValue(RandomTools.createString())
                .build();
    }

    private Reference createRefSoPrio(CPSTransaction cpsTx) {
        return new ReferenceBuilder()
                .withId(cpsTx.getServiceOrderPrio())
                .withIntValue(RandomTools.createInteger())
                .build();
    }

    private VerbindungsBezeichnung createVbz() {
        return new VerbindungsBezeichnungBuilder()
                .withRandomId()
                .withVbz(RandomTools.createString())
                .build();
    }

    private CPSTransaction createCpsTx(AuftragBuilder auftragBuilder, AuftragDaten auftragDaten) {
        return new CPSTransactionBuilder()
                .withRandomId()
                .withServiceOrderType(CPSTransaction.TX_SOURCE_HURRICAN_ORDER)
                .withAuftragBuilder(auftragBuilder)
                .withOrderNoOrig(auftragDaten.getAuftragNoOrig())
                .withServiceOrderData(RandomTools.createString().getBytes())
                .build();
    }

    private AuftragDaten createWholesaleAuftrag(AuftragBuilder auftragBuilder) {
        return createAuftrag(auftragBuilder, RandomTools.createString(), null);
    }

    private AuftragDaten createRetailAuftrag(AuftragBuilder auftragBuilder) {
        return createAuftrag(auftragBuilder, null, RandomTools.createLong());
    }

    private AuftragDaten createAuftrag(AuftragBuilder auftragBuilder, String wholesaleID, final Long taifunNr) {
        return new AuftragDatenBuilder()
                .withRandomId()
                .withAuftragBuilder(auftragBuilder)
                .withWholesaleAuftragsId(wholesaleID)
                .withAuftragNoOrig(taifunNr)
                .build();
    }

}