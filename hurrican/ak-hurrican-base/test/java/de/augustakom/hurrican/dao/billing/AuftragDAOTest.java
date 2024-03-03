package de.augustakom.hurrican.dao.billing;

import static org.testng.Assert.*;

import java.util.*;
import javax.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.billing.view.BAuftragView;
import de.augustakom.hurrican.model.cc.query.AuftragSAPQuery;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class AuftragDAOTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    @Qualifier("taifunDataSource")
    private DataSource taifunDataSource;
    @Autowired
    private AuftragDAO auftragDAO;

    @Test
    public void testFindAuftraege4SAP() throws Exception {
        GeneratedTaifunData generatedTaifunData = getTaifunDataFactory()
                .withSAPId()
                .surfAndFonWithDns(1).persist();
        String sapId = generatedTaifunData.getBillingAuftrag().getSapId();
        AuftragSAPQuery query = new AuftragSAPQuery();
        query.setSapId(sapId);
        List<BAuftrag> auftraege = auftragDAO.findAuftraege4SAP(query);
        assertEquals(auftraege.size(), 1);
        assertEquals(auftraege.get(0).getAuftragNoOrig(), generatedTaifunData.getBillingAuftrag().getAuftragNoOrig());
        assertEquals(auftraege.get(0).getSapId(), sapId);
    }

    @Test
    public void testFindAuftragLeistungView() throws Exception {
        GeneratedTaifunData generatedTaifunData = getTaifunDataFactory()
                .surfAndFonWithDns(1).persist();
        List<BAuftragLeistungView> auftragLeistungView = auftragDAO.findAuftragLeistungView(
                generatedTaifunData.getKunde().getKundeNo(),
                generatedTaifunData.getBillingAuftrag().getAuftragNoOrig(),
                true, true, false
        );
        assertEquals(auftragLeistungView.size(), 2); // two in case of different leistungen
        auftragLeistungView.stream().forEach(v -> {
            assertEquals(v.getKundeNo(), generatedTaifunData.getKunde().getKundeNo());
            assertEquals(v.getAuftragNoOrig(), generatedTaifunData.getBillingAuftrag().getAuftragNoOrig());
        });
        assertNotEquals(auftragLeistungView.get(0).getLeistungName(), auftragLeistungView.get(1).getLeistungName());
        assertEquals(auftragLeistungView.get(0).getOeName(), auftragLeistungView.get(1).getOeName());
    }

    @Test
    public void testfindAuftragViews() throws Exception {
        GeneratedTaifunData generatedTaifunData = getTaifunDataFactory()
                .surfAndFonWithDns(1).persist();
        List<BAuftragView> auftragViews = auftragDAO.findAuftragViews(
                generatedTaifunData.getKunde().getKundeNo());
        assertEquals(auftragViews.size(), 1);
        auftragViews.stream().forEach(v -> {
            assertEquals(v.getKundeNo(), generatedTaifunData.getKunde().getKundeNo());
            assertEquals(v.getAuftragNoOrig(), generatedTaifunData.getBillingAuftrag().getAuftragNoOrig());
        });
    }

    @Test
    public void findDebitorNrForAuftragNo() throws Exception {
        GeneratedTaifunData generatedTaifunData = getTaifunDataFactory()
                .surfAndFonWithDns(1).persist();
        String debitorNr = auftragDAO.findDebitorNrForAuftragNo(generatedTaifunData.getBillingAuftrag().getAuftragNoOrig());
        assertEquals(debitorNr, generatedTaifunData.getRInfo().getExtDebitorId());
    }

}
