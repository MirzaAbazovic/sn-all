package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.AuftragDatenDAO;
import de.augustakom.hurrican.dao.cc.AuftragTechnikDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ProduktService;

@Test(groups = { BaseTest.UNIT })
public class CCAuftragStatusServiceHelperImplTest extends BaseTest {
    @Mock
    AuftragDatenDAO auftragDatenDAO;
    @Mock
    AuftragTechnikDAO auftragTechnikDAO;
    @Mock
    private CCAuftragService ccAuftragService;
    @Mock
    private ProduktService produktService;

    @InjectMocks
    @Spy
    private CCAuftragStatusServiceHelperImpl sut;

    final long auftragId = 1234L;
    final Long prodId = 1234L;
    final Date kuendigungsDatum = new Date();

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    private Produkt getProduktMock(Long kuendigungStatus) {
        Produkt produktMock = Mockito.mock(Produkt.class);
        when(produktMock.getKuendigungStatusId()).thenReturn(kuendigungStatus);
        return produktMock;
    }

    private AuftragDaten getAuftragDatenMock(Long prodId) {
        AuftragDaten adMock = Mockito.mock(AuftragDaten.class);
        when(adMock.getProdId()).thenReturn(prodId);
        return adMock;
    }

    private AuftragTechnik getAuftragTechnikMock() {
        AuftragTechnik atMock = Mockito.mock(AuftragTechnik.class);
        return atMock;
    }

    private void setUpKuendigeAuftragTest(AuftragDaten ad, AuftragTechnik at, Produkt p) throws FindException {
        when(ccAuftragService.findAuftragDatenByAuftragIdTx(auftragId)).thenReturn(ad);
        when(ccAuftragService.findAuftragTechnikByAuftragIdTx(auftragId)).thenReturn(at);
        when(produktService.findProdukt(prodId)).thenReturn(p);
    }

    @Test
    public void shouldUpdateAuftragDatenAndAuftragTechnik() throws Exception {
        Produkt produktMock = getProduktMock(null);
        AuftragDaten adMock = getAuftragDatenMock(prodId);
        AuftragTechnik atMock = getAuftragTechnikMock();

        setUpKuendigeAuftragTest(adMock, atMock, produktMock);

        sut.kuendigeAuftragReqNew(auftragId, kuendigungsDatum, null);

        verify(adMock).setStatusId(AuftragStatus.KUENDIGUNG_ERFASSEN);
        verify(adMock).setKuendigung(kuendigungsDatum);
        verify(adMock, never()).setBearbeiter(anyString());
        verify(auftragDatenDAO).store(adMock);

        verify(atMock).setAuftragsart(BAVerlaufAnlass.KUENDIGUNG);
        verify(atMock).setProjectResponsibleUserId(null);
        verify(auftragTechnikDAO).store(atMock);
    }

    @Test(expectedExceptions = StoreException.class, expectedExceptionsMessageRegExp = "Der Auftrag mit der ID 1234 .*")
    public void shouldThrowExAsAuftragDatenNotFound() throws Exception {
        Produkt produktMock = getProduktMock(null);
        AuftragTechnik atMock = getAuftragTechnikMock();

        setUpKuendigeAuftragTest(null, atMock, produktMock);

        sut.kuendigeAuftragReq(auftragId, kuendigungsDatum, null);
    }

    @Test(expectedExceptions = StoreException.class, expectedExceptionsMessageRegExp = "Der Auftrag mit der ID 1234 .*")
    public void shouldThrowExAsAuftragTechnikNotFound() throws Exception {
        Produkt produktMock = getProduktMock(null);
        AuftragDaten adMock = getAuftragDatenMock(prodId);

        setUpKuendigeAuftragTest(adMock, null, produktMock);

        sut.kuendigeAuftragReq(auftragId, kuendigungsDatum, null);
    }

    @Test(expectedExceptions = StoreException.class, expectedExceptionsMessageRegExp = "Es wurde keine Auftrags-ID.*")
    public void shouldThrowExAsAuftragIdNotSet() throws Exception {
        sut.kuendigeAuftragReqNew(null, kuendigungsDatum, null);
    }

    @Test(expectedExceptions = StoreException.class, expectedExceptionsMessageRegExp = "Kuendigungsdatum wurde nicht angegeben.*")
    public void shouldThrowExAsKuendigungsDatumNotSet() throws Exception {
        sut.kuendigeAuftragReqNew(auftragId, null, null);
    }
}