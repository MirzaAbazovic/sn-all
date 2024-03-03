package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.IntAccountBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class IntAccountDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    private IntAccountDAOImpl daoImpl;

    @Test
    public void testFindAuftragAccountEinwahlViews() throws Exception {
        IntAccountBuilder intAccountBuilder = getBuilder(IntAccountBuilder.class)
                .withLiNr(IntAccount.LINR_EINWAHLACCOUNT)
                .withRandomAccount();
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withStatusId(AuftragStatus.IN_BETRIEB)
                .withProdId(Produkt.PROD_ID_SDSL_10000);
        Auftrag auftrag = getBuilder(AuftragBuilder.class)
                .withKundeNo(1020304050L)
                .withAuftragDatenBuilder(auftragDatenBuilder)
                .withAuftragTechnikBuilder(getBuilder(AuftragTechnikBuilder.class)
                        .withIntAccountBuilder(intAccountBuilder))
                .build();

        flushAndClear();

        List<AuftragIntAccountView>views = daoImpl.findAuftragAccountViews(auftrag.getKundeNo(),
                Collections.singletonList(ProduktGruppe.AK_SDSL));

        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftrag.getAuftragId());
    }

    @Test
    public void testFindAuftragAccountAbrechnungViews() throws Exception {
        IntAccountBuilder intAccountBuilder = getBuilder(IntAccountBuilder.class)
                .withLiNr(IntAccount.LINR_ABRECHNUNGSACCOUNT)
                .withRandomAccount();
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withStatusId(AuftragStatus.IN_BETRIEB)
                .withProdId(Produkt.PROD_ID_AKSDSL_512);
        Auftrag auftrag = getBuilder(AuftragBuilder.class)
                .withKundeNo(1020304050L)
                .withAuftragDatenBuilder(auftragDatenBuilder)
                .withAuftragTechnikBuilder(getBuilder(AuftragTechnikBuilder.class)
                        .withIntAccountBuilder(intAccountBuilder))
                .build();

        flushAndClear();

        List<AuftragIntAccountView>views = daoImpl.findAuftragAccountViews(auftrag.getKundeNo(),
                Collections.singletonList(ProduktGruppe.AK_SDSL));

        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getAuftragId(), auftrag.getAuftragId());
    }
}