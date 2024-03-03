package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.Leistung4DnBuilder;
import de.augustakom.hurrican.model.cc.LeistungsbuendelBuilder;
import de.augustakom.hurrican.model.cc.dn.Lb2Leistung;
import de.augustakom.hurrican.model.cc.view.LeistungInLeistungsbuendelView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class DNLeistungDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    private DNLeistungDAOImpl daoImpl;

    @Autowired
    @Qualifier("cc.sessionFactory")
    private SessionFactory sessionFactory;

    @Test
    public void testFindAllLb2Leistung() throws Exception {
        Leistung4DnBuilder leistung4DnBuilder = getBuilder(Leistung4DnBuilder.class)
                .withRandomId()
                .withLeistung("TestLeistung");

        LeistungsbuendelBuilder leistungsbuendelBuilder = getBuilder(LeistungsbuendelBuilder.class)
                .withRandomId();

        leistung4DnBuilder.build();
        leistungsbuendelBuilder.build();

        Lb2Leistung lb2L = new Lb2Leistung();
        lb2L.setGueltigVon(new Date());
        lb2L.setGueltigBis(DateTools.getHurricanEndDate());
        lb2L.setLbId(leistungsbuendelBuilder.get().getId());
        lb2L.setLeistungId(leistung4DnBuilder.get().getId());
        lb2L.setOeNo(0L);
        lb2L.setStandard(Boolean.FALSE);
        lb2L.setVerwendenVon(DateTools.getActualSQLDate());
        lb2L.setVerwendenBis(DateTools.getHurricanEndDate());
        lb2L.setDefParamValue("TestLeistung");

        sessionFactory.getCurrentSession().save(lb2L);

        flushAndClear();

        List<LeistungInLeistungsbuendelView> views = daoImpl.findAllLb2Leistung(leistungsbuendelBuilder.get().getId());
        Assert.assertFalse(views.isEmpty());
    }

    @Test
    public void testUpdateLB2Leistung() throws Exception {
        Leistung4DnBuilder leistung4DnBuilder = getBuilder(Leistung4DnBuilder.class)
                .withRandomId()
                .withLeistung("TestLeistung");

        LeistungsbuendelBuilder leistungsbuendelBuilder = getBuilder(LeistungsbuendelBuilder.class)
                .withRandomId();

        leistung4DnBuilder.build();
        leistungsbuendelBuilder.build();

        Lb2Leistung lb2L = new Lb2Leistung();
        lb2L.setGueltigVon(new Date());
        lb2L.setGueltigBis(DateTools.createDate(2009, 0, 1));
        lb2L.setLbId(leistungsbuendelBuilder.get().getId());
        lb2L.setLeistungId(leistung4DnBuilder.get().getId());
        lb2L.setOeNo(0L);
        lb2L.setStandard(Boolean.FALSE);
        lb2L.setVerwendenVon(DateTools.getActualSQLDate());
        lb2L.setVerwendenBis(DateTools.createDate(2009, 0, 1));
        lb2L.setDefParamValue("TestLeistung");

        sessionFactory.getCurrentSession().save(lb2L);

        flushAndClear();

        List<LeistungInLeistungsbuendelView> views = daoImpl.findAllLb2Leistung(leistungsbuendelBuilder.get().getId());
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getVerwendenBis().getTime(), DateTools.createDate(2009, 0, 1).getTime());

        daoImpl.updateLB2Leistung(DateTools.getHurricanEndDate(), lb2L.getId());

        views = daoImpl.findAllLb2Leistung(leistungsbuendelBuilder.get().getId());
        Assert.assertFalse(views.isEmpty());
        Assert.assertEquals(views.get(0).getVerwendenBis().getTime(), DateTools.getHurricanEndDate().getTime());
    }

}
