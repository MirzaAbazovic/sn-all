/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.11.2014 09:30
 */
package de.mnet.hurrican.e2e.taifun.order.workflow;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.*;
import javax.annotation.*;
import javax.inject.*;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.mnet.hurrican.e2e.common.BaseHurricanE2ETest;
import de.mnet.hurricanweb.order.workflow.types.TerminateTechOrdersType;
import de.mnet.hurricanweb.order.workflow.types.TerminateTechOrdersTypeAcknowledgement;
import de.mnet.hurricanweb.order.workflow.types.TerminateTechOrdersTypeAcknowledgementDocument;
import de.mnet.hurricanweb.order.workflow.types.TerminateTechOrdersTypeDocument;

@Test(groups = BaseTest.E2E)
public class TerminateTechOrdersTest extends BaseHurricanE2ETest {

    public static final long AUFTRAG_NO_ORIG = 7630L;

    @Resource(name = "taifunOrderWorkflowWebServiceTemplate")
    protected WebServiceTemplate wsTemplate;

    @Inject
    protected CCAuftragService auftragService;

    @Test
    public void testKuendigeTvMvAuftrag() throws Exception {
        cleanupTestData();
        final Auftrag auftrag = createTestauftrag();
        final TerminateTechOrdersTypeDocument reqDoc = createRequest();

        final TerminateTechOrdersTypeAcknowledgement ack = sendAndReceive(reqDoc);

        assertThat(ack.getOrderNo(), equalTo(AUFTRAG_NO_ORIG));
        assertThat((Iterable<Long>) ack.getHurricanServiceNos(), contains(auftrag.getAuftragId()));
    }

    private TerminateTechOrdersTypeAcknowledgement sendAndReceive(TerminateTechOrdersTypeDocument reqDoc) {
        return ((TerminateTechOrdersTypeAcknowledgementDocument) wsTemplate.marshalSendAndReceive(reqDoc))
                .getTerminateTechOrdersTypeAcknowledgement();
    }

    private TerminateTechOrdersTypeDocument createRequest() {
        final TerminateTechOrdersTypeDocument reqDoc = TerminateTechOrdersTypeDocument.Factory.newInstance();
        final TerminateTechOrdersType terminateTechOrdersType = reqDoc.addNewTerminateTechOrdersType();
        terminateTechOrdersType.setOrderNo(AUFTRAG_NO_ORIG);
        terminateTechOrdersType.setTerminationDate(new GregorianCalendar());
        return reqDoc;
    }

    private Auftrag createTestauftrag() throws StoreException {
        final AuftragTechnik auftragTechnik = new AuftragTechnik();
        final AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragNoOrig(AUFTRAG_NO_ORIG);
        auftragDaten.setProdId(Produkt.PROD_ID_TV_SIGNALLIEFERUNG_MV);
        auftragDaten.setBearbeiter("test");
        auftragDaten.setAuftragStatusId(AuftragStatus.IN_BETRIEB);
        return auftragService.createAuftrag(AUFTRAG_NO_ORIG, auftragDaten, auftragTechnik, -1L, null);
    }

    private void cleanupTestData() throws FindException, StoreException {
        for (final AuftragDaten ad : auftragService.findAllAuftragDaten4OrderNoOrigTx(AUFTRAG_NO_ORIG)) {
            ad.setGueltigBis(new Date());
            auftragService.saveAuftragDaten(ad, false);
        }
    }

}
