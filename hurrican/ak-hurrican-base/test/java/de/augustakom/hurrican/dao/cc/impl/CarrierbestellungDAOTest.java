package de.augustakom.hurrican.dao.cc.impl;

import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.view.CuDAVorschau;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class CarrierbestellungDAOTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    private CarrierbestellungDAOImpl carrierbestellungDAO;

    @DataProvider
    public Object[][] findAuftragDatenByBaugruppeDP() {
        return new Object[][] {
                {new Date()},
                { Date.from(LocalDateTime.now().minusMonths(1).atZone(ZoneId.systemDefault()).toInstant()) },
                { Date.from(LocalDateTime.now().plusMonths(1).atZone(ZoneId.systemDefault()).toInstant()) },
        };
    }

    @Test(dataProvider = "findAuftragDatenByBaugruppeDP")
    public void testFindAuftragDatenByBaugruppe(Date vorgabeSCV) {
        List<CuDAVorschau> cuDAVorschauList = carrierbestellungDAO.createCuDAVorschau(vorgabeSCV);
        assertFalse(cuDAVorschauList.isEmpty());
        for (CuDAVorschau cuDAVorschau : cuDAVorschauList) {
            assertTrue(cuDAVorschau.getVorgabeSCV().after(vorgabeSCV));
            assertTrue(cuDAVorschau.getAnzahlCuDA().intValue() > 0);
        }
    }

}
