package de.augustakom.hurrican.dao.cc.ffm.impl;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.ffm.FfmDAO;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.enums.FfmTyp;
import de.augustakom.hurrican.model.cc.ffm.FfmFeedbackMaterial;
import de.augustakom.hurrican.model.cc.ffm.FfmFeedbackMaterialBuilder;
import de.augustakom.hurrican.model.cc.ffm.FfmProductMapping;
import de.augustakom.hurrican.model.cc.ffm.FfmProductMappingBuilder;
import de.augustakom.hurrican.model.cc.ffm.FfmQualification;
import de.augustakom.hurrican.model.cc.ffm.FfmQualificationBuilder;
import de.augustakom.hurrican.model.cc.ffm.FfmQualificationMapping;
import de.augustakom.hurrican.model.cc.ffm.FfmQualificationMappingBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class FfmDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    private FfmDAO ffmDAO;

    @Test
    public void testSaveAndFindFfmProductMapping() {
        FfmProductMappingBuilder ffmProductMappingBuilder = new FfmProductMappingBuilder()
                .withProduktId(Produkt.PROD_ID_FTTX_TELEFONIE)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .withBaFfmTyp(FfmTyp.NEU);

        FfmProductMapping ffmProductMapping = ffmDAO.store(ffmProductMappingBuilder.build());
        Assert.assertNotNull(ffmProductMapping.getId());

        FfmProductMapping result = ffmDAO.findById(ffmProductMapping.getId(), FfmProductMapping.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getFfmActivityType(), ffmProductMapping.getFfmActivityType());
    }

    @Test
    public void testSaveAndFindFfmFeedbackMaterial() {
        FfmFeedbackMaterialBuilder materialBuilder = new FfmFeedbackMaterialBuilder().setPersist(false);

        FfmFeedbackMaterial material = ffmDAO.store(materialBuilder.get());
        ffmDAO.flushSession();
        Assert.assertNotNull(material.getId());

        FfmFeedbackMaterial result = ffmDAO.findById(material.getId(), FfmFeedbackMaterial.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getWorkforceOrderId(), material.getWorkforceOrderId());
        Assert.assertEquals(result.getMaterialId(), material.getMaterialId());
        Assert.assertEquals(result.getSerialNumber(), material.getSerialNumber());
        Assert.assertEquals(result.getQuantity(), material.getQuantity());
        Assert.assertEquals(result.getProcessed(), Boolean.FALSE);
    }

    @Test
    public void testFindQualificationsByProduct() throws Exception {
        FfmQualification ffmQualification = ffmDAO.store(new FfmQualificationBuilder().build());

        FfmQualificationMappingBuilder ffmQualificationMappingBuilder = new FfmQualificationMappingBuilder()
                .withProductId(Produkt.PROD_ID_FTTX_TELEFONIE)
                .withFfmQualification(ffmQualification);

        FfmQualificationMapping ffmQualificationMapping = ffmDAO.store(ffmQualificationMappingBuilder.build());
        ffmDAO.flushSession();
        Assert.assertNotNull(ffmQualificationMapping.getId());

        List<FfmQualificationMapping> result = ffmDAO.findQualificationsByProduct(ffmQualificationMapping.getProductId());
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1L);
        Assert.assertNotNull(result.get(0).getFfmQualification());
        Assert.assertNotNull(result.get(0).getProductId());
        Assert.assertEquals(result.get(0).getProductId(), ffmQualificationMapping.getProductId());
        Assert.assertEquals(result.get(0).getFfmQualification().getQualification(), ffmQualificationMapping.getFfmQualification().getQualification());

        result = ffmDAO.findQualificationsByProduct(0L);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 0L);
    }


    @Test
    public void testFindQualifications4Vpn() throws Exception {
        FfmQualification ffmQualification = ffmDAO.store(new FfmQualificationBuilder().build());

        FfmQualificationMappingBuilder ffmQualificationMappingBuilder = new FfmQualificationMappingBuilder()
                .withVpn(Boolean.TRUE)
                .withFfmQualification(ffmQualification);

        FfmQualificationMapping ffmQualificationMapping = ffmDAO.store(ffmQualificationMappingBuilder.build());
        ffmDAO.flushSession();
        Assert.assertNotNull(ffmQualificationMapping.getId());

        List<FfmQualificationMapping> result = ffmDAO.findQualifications4Vpn();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains(ffmQualificationMapping));
    }


    @Test
    public void testFindQualificationsByLeistung() throws Exception {
        FfmQualification ffmQualification = ffmDAO.store(new FfmQualificationBuilder().build());

        FfmQualificationMappingBuilder ffmQualificationMappingBuilder = new FfmQualificationMappingBuilder()
                .withTechLeistungId(TechLeistung.ID_ADDITIONAL_IP)
                .withFfmQualification(ffmQualification);

        FfmQualificationMapping ffmQualificationMapping = ffmDAO.store(ffmQualificationMappingBuilder.build());
        ffmDAO.flushSession();
        Assert.assertNotNull(ffmQualificationMapping.getId());

        List<FfmQualificationMapping> result = ffmDAO.findQualificationsByLeistung(ffmQualificationMapping.getTechLeistungId());
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1L);
        Assert.assertNotNull(result.get(0).getFfmQualification());
        Assert.assertNotNull(result.get(0).getTechLeistungId());
        Assert.assertEquals(result.get(0).getTechLeistungId(), ffmQualificationMapping.getTechLeistungId());
        Assert.assertEquals(result.get(0).getFfmQualification().getQualification(), ffmQualificationMapping.getFfmQualification().getQualification());

        result = ffmDAO.findQualificationsByLeistung(0L);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 0L);
    }

    @Test
    public void testFindQualificationsByStandortRef() throws Exception {
        FfmQualification ffmQualification = ffmDAO.store(new FfmQualificationBuilder().build());

        FfmQualificationMappingBuilder ffmQualificationMappingBuilder = new FfmQualificationMappingBuilder()
                .withStandortRefId(HVTStandort.HVT_STANDORT_TYP_TV)
                .withFfmQualification(ffmQualification);

        FfmQualificationMapping ffmQualificationMapping = ffmDAO.store(ffmQualificationMappingBuilder.build());
        ffmDAO.flushSession();
        Assert.assertNotNull(ffmQualificationMapping.getId());

        List<FfmQualificationMapping> result = ffmDAO.findQualificationsByStandortRef(ffmQualificationMapping.getStandortRefId());
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1L);
        Assert.assertNotNull(result.get(0).getFfmQualification());
        Assert.assertNotNull(result.get(0).getStandortRefId());
        Assert.assertEquals(result.get(0).getStandortRefId(), ffmQualificationMapping.getStandortRefId());
        Assert.assertEquals(result.get(0).getFfmQualification().getQualification(), ffmQualificationMapping.getFfmQualification().getQualification());

        result = ffmDAO.findQualificationsByStandortRef(0L);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 0L);
    }
}