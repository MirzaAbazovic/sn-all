package de.augustakom.hurrican.dao.billing;

import static org.testng.Assert.*;

import java.util.*;
import javax.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.augustakom.hurrican.model.billing.query.RufnummerQuery;
import de.augustakom.hurrican.model.shared.view.AuftragDNView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class RufnummerDAOTest extends AbstractHurricanBaseServiceTest {
    @Autowired
    @Qualifier("taifunDataSource")
    private DataSource taifunDataSource;
    @Autowired
    private RufnummerDAO rufnummerDAO;


    @Test
    public void testFindAuftragDNViewsByQuery() throws Exception {
        GeneratedTaifunData generatedTaifunData = getTaifunDataFactory().surfAndFonWithDns(1).persist();
        RufnummerQuery queryAuftrag = new RufnummerQuery();
        queryAuftrag.setAuftragNoOrig(generatedTaifunData.getBillingAuftrag().getAuftragNoOrig());
        RufnummerQuery queryDnNo = new RufnummerQuery();
        queryDnNo.setDnNoOrig(generatedTaifunData.getDialNumbers().get(0).getDnNoOrig());
        RufnummerQuery queryKuNo = new RufnummerQuery();
        queryKuNo.setKundeNo(generatedTaifunData.getKunde().getKundeNo());
        RufnummerQuery queryDn = new RufnummerQuery();
        queryDn.setOnKz(generatedTaifunData.getDialNumbers().get(0).getOnKz());
        queryDn.setDnBase(generatedTaifunData.getDialNumbers().get(0).getDnBase());

        Arrays.asList(queryAuftrag, queryDnNo, queryKuNo, queryDn).forEach(q -> {
            List<AuftragDNView> auftragDNViewsByQuery = rufnummerDAO.findAuftragDNViewsByQuery(q);
            assertEquals(auftragDNViewsByQuery.size(), 1);
            auftragDNViewsByQuery.stream().forEach(v -> {
                assertEquals(v.getKundeNo(), generatedTaifunData.getKunde().getKundeNo());
                assertEquals(v.getAuftragNoOrig(), generatedTaifunData.getBillingAuftrag().getAuftragNoOrig());
                assertEquals(v.getDnNoOrig(), generatedTaifunData.getDialNumbers().get(0).getDnNoOrig());
                assertEquals(v.getOnKz(), generatedTaifunData.getDialNumbers().get(0).getOnKz());
                assertEquals(v.getDnBase(), generatedTaifunData.getDialNumbers().get(0).getDnBase());
            });
        });
    }
}