/**
 * # * Copyright (c) 2009 - M-net Telekommunikations GmbH All rights reserved. -------------------------------------------------------
 * File created: 20.10.2009 14:13:39
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateJdbcException;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.dao.cc.CCAddressDAO;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Ansprechpartner.Typ;
import de.augustakom.hurrican.model.cc.AnsprechpartnerBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.CCAddressBuilder;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.view.CCAnsprechpartnerView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;


/**
 * Testst den AnsprechpartnerService
 *
 *
 */
@Test(groups = BaseTest.SERVICE)
public class AnsprechpartnerServiceTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    CCAddressDAO ccAddressDao;

    public void testSaveAnsprechpartner() throws StoreException, ValidationException {
        AnsprechpartnerService ansprechpartnerService = getCCService(AnsprechpartnerService.class);

        AnsprechpartnerBuilder ansprechpartnerBuilder = getBuilder(AnsprechpartnerBuilder.class)
                .withPreferred(Boolean.TRUE)
                .setPersist(false);
        ansprechpartnerBuilder.getAddressBuilder().setPersist(false);
        Ansprechpartner ansprechpartner = ansprechpartnerBuilder.build();

        ansprechpartnerService.saveAnsprechpartner(ansprechpartner);
        assertNotNull(ansprechpartner.getId(), "ID of Ansprechpartner not set");
    }

    public void testDeleteAnsprechpartner() throws DeleteException, FindException {
        AnsprechpartnerService ansprechpartnerService = getCCService(AnsprechpartnerService.class);

        AnsprechpartnerBuilder ansprechpartnerBuilder = getBuilder(AnsprechpartnerBuilder.class);
        Ansprechpartner ansprechpartner = ansprechpartnerBuilder.build();

        ansprechpartnerService.deleteAnsprechpartner(ansprechpartner);

        List<Ansprechpartner> result = ansprechpartnerService.findAnsprechpartner(
                Ansprechpartner.Typ.forRefId(ansprechpartner.getTypeRefId()), ansprechpartner.getAuftragId());
        assertEmpty(result, "Ansprechpartner found, but should be deleted");

        CCAddress address = null;
        address = ccAddressDao.findById(ansprechpartner.getAddress().getId(), CCAddress.class);
        assertNotNull(address, "Address not found, but should still be there!");
    }

    public void testSaveSecondPreferred() throws StoreException, FindException, ValidationException {
        AnsprechpartnerService ansprechpartnerService = getCCService(AnsprechpartnerService.class);

        AnsprechpartnerBuilder ansprechpartnerBuilder = getBuilder(AnsprechpartnerBuilder.class)
                .withPreferred(Boolean.TRUE)
                .setPersist(false);
        Ansprechpartner ansprechpartner = ansprechpartnerBuilder.build();

        try {
            ansprechpartnerService.saveAnsprechpartner(ansprechpartner);
        }
        catch (Exception e) {
            fail("Should not throw exception here");
        }
        assertNotNull(ansprechpartner.getId(), "ID of Ansprechpartner not set");

        ansprechpartner = ansprechpartnerBuilder.build();
        try {
            ansprechpartnerService.saveAnsprechpartner(ansprechpartner);
            flushAndClear();
            fail("Should NOT be able to save this one because of CHECK constraint in the database!");
        }
        catch (HibernateJdbcException e) {
            Assert.assertTrue(e.getRootCause().getMessage().contains("two preferred Ansprechpartner not allowed for same Auftrag"));
        }
    }

    public void testFindAnsprechpartner() throws FindException {
        AnsprechpartnerService ansprechpartnerService = getCCService(AnsprechpartnerService.class);
        AnsprechpartnerBuilder ansprechpartnerBuilder = getBuilder(AnsprechpartnerBuilder.class);
        ansprechpartnerBuilder.build();
        ansprechpartnerBuilder
                .withType(Typ.IPSEC_C2S)
                .withReferenceBuilder(getBuilder(ReferenceBuilder.class).withId(Typ.IPSEC_C2S.refId()).setPersist(false))
                .build();

        List<Ansprechpartner> result = ansprechpartnerService.findAnsprechpartner(Typ.IPSEC_C2S,
                ansprechpartnerBuilder.getAuftragBuilder().get().getId());
        assertEquals(result.size(), 1, "Ansprechpartner not found or too many found!");

        result = ansprechpartnerService.findAnsprechpartner(null,
                ansprechpartnerBuilder.getAuftragBuilder().get().getId());
        assertEquals(result.size(), 2, "Not all Ansprechpartner found!");

        result = ansprechpartnerService.findAnsprechpartner(Typ.ENDSTELLE_A,
                ansprechpartnerBuilder.getAuftragBuilder().get().getId());
        assertEmpty(result, "Ansprechpartner found!");
    }


    public void testFindPreferredAnsprechpartner() throws FindException {
        AnsprechpartnerService ansprechpartnerService = getCCService(AnsprechpartnerService.class);
        AnsprechpartnerBuilder ansprechpartnerBuilder = getBuilder(AnsprechpartnerBuilder.class)
                .withType(Typ.KUNDE)
                .withReferenceBuilder(getBuilder(ReferenceBuilder.class).withId(Typ.KUNDE.refId()).setPersist(false))
                .withPreferred(Boolean.FALSE);
        ansprechpartnerBuilder.build();

        Ansprechpartner result = ansprechpartnerService.findPreferredAnsprechpartner(Typ.KUNDE,
                ansprechpartnerBuilder.getAuftragBuilder().get().getId());
        assertNull(result, "Ansprechpartner found!");

        ansprechpartnerBuilder
                .withPreferred(Boolean.TRUE)
                .build();

        result = ansprechpartnerService.findPreferredAnsprechpartner(Typ.KUNDE,
                ansprechpartnerBuilder.getAuftragBuilder().get().getId());
        assertNotNull(result, "Ansprechpartner not found!");
    }

    public void testCopyAnsprechpartner() throws StoreException, FindException {
        AnsprechpartnerService ansprechpartnerService = getCCService(AnsprechpartnerService.class);
        AnsprechpartnerBuilder ansprechpartnerBuilder = getBuilder(AnsprechpartnerBuilder.class)
                .withType(Typ.KUNDE)
                .withReferenceBuilder(getBuilder(ReferenceBuilder.class).withId(Typ.KUNDE.refId()).setPersist(false))
                .withPreferred(Boolean.TRUE);
        ansprechpartnerBuilder.build();

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);

        ansprechpartnerService.copyAnsprechpartner(Typ.KUNDE, ansprechpartnerBuilder.get().getAuftragId(), auftragBuilder.get().getAuftragId());

        Ansprechpartner copy = ansprechpartnerService.findPreferredAnsprechpartner(Typ.KUNDE, auftragBuilder.get().getAuftragId());
        assertNotNull(copy, "Ansprechpartner wurde nicht kopiert!");
        assertTrue(NumberTools.notEqual(copy.getId(), ansprechpartnerBuilder.get().getId()), "Copy has same ID!");
        assertTrue(NumberTools.equal(copy.getAddress().getId(), ansprechpartnerBuilder.get().getAddress().getId()), "Copy does not have same Address.ID!");
    }

    public void copyAnsprechpartner() throws StoreException {
        AnsprechpartnerService ansprechpartnerService = getCCService(AnsprechpartnerService.class);
        AnsprechpartnerBuilder ansprechpartnerBuilder = getBuilder(AnsprechpartnerBuilder.class)
                .withType(Typ.KUNDE)
                .withReferenceBuilder(getBuilder(ReferenceBuilder.class).withId(Typ.KUNDE.refId()).setPersist(false))
                .withPreferred(Boolean.TRUE);
        ansprechpartnerBuilder.build();
        Ansprechpartner source = ansprechpartnerBuilder.get();

        Ansprechpartner copy = ansprechpartnerService.copyAnsprechpartner(source, Ansprechpartner.Typ.HOTLINE_SERVICE, CCAddress.ADDRESS_TYPE_HOTLINE_SERVICE, Boolean.FALSE);

        assertEquals(source.getAuftragId(), copy.getAuftragId());
        assertFalse(source.getId().equals(copy.getId()));
        assertFalse(source.getAddress().getId().equals(copy.getAddress().getId()));
        assertEquals(copy.getTypeRefId(), Ansprechpartner.Typ.HOTLINE_SERVICE.refId());
        assertEquals(copy.getAddress().getAddressType(), CCAddress.ADDRESS_TYPE_HOTLINE_SERVICE);

    }

    /*
     * Test fuer den Fall, dass keine Kundennummer eingeliefert wird.
     */
    @Test(expectedExceptions = FindException.class)
    public void testFindAnsprechpartnerByKundeNoAndBuendelNoNoIdsGiven() throws FindException {
        AnsprechpartnerService ansprechpartnerService = getCCService(AnsprechpartnerService.class);
        ansprechpartnerService.findAnsprechpartnerByKundeNoAndBuendelNo(null, null);
    }

    /*
     * Test fuer den folgenden Fall: Kunde hat einen Auftrag mit genau einem Ansprechpartner.
     */
    public void testFindAnsprechpartnerByKundeNoAndBuendelNoOnlyKundeNoGivenOneAnsprechpartner() throws FindException {
        Long kundeNo = 123L;
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withKundeNo(kundeNo)
                .withAuftragDatenBuilder(getBuilder(AuftragDatenBuilder.class));
        AnsprechpartnerBuilder ansprechpartnerBuilder = getBuilder(AnsprechpartnerBuilder.class)
                .withAuftragBuilder(auftragBuilder);
        ansprechpartnerBuilder.build();

        AnsprechpartnerService ansprechpartnerService = getCCService(AnsprechpartnerService.class);
        List<CCAnsprechpartnerView> current = ansprechpartnerService.findAnsprechpartnerByKundeNoAndBuendelNo(kundeNo, null);
        assertNotEmpty(current);
        assertEquals(current.size(), 1);
    }

    /*
     * Test fuer den folgenden Fall: Kunde hat einen Auftrag mit zwei Ansprechpartnern.
     */
    public void testFindAnsprechpartnerByKundeNoAndBuendelNoOnlyKundeNoGivenTwoAnsprechpartner() throws FindException {
        Long kundeNo = 123L;
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withKundeNo(kundeNo)
                .withAuftragDatenBuilder(getBuilder(AuftragDatenBuilder.class));
        AnsprechpartnerBuilder ansprechpartnerBuilder1 = getBuilder(AnsprechpartnerBuilder.class)
                .withType(Typ.ENDSTELLE_A)
                .withAuftragBuilder(auftragBuilder);
        ansprechpartnerBuilder1.build();
        AnsprechpartnerBuilder ansprechpartnerBuilder2 = getBuilder(AnsprechpartnerBuilder.class)
                .withType(Typ.ENDSTELLE_B)
                .withAuftragBuilder(auftragBuilder);
        ansprechpartnerBuilder2.build();

        AnsprechpartnerService ansprechpartnerService = getCCService(AnsprechpartnerService.class);
        List<CCAnsprechpartnerView> current = ansprechpartnerService.findAnsprechpartnerByKundeNoAndBuendelNo(kundeNo, null);
        assertNotEmpty(current);
        assertEquals(current.size(), 2);
    }

    /*
     * Test fuer den folgenden Fall: 2 Auftraege mit derselben Buendelnummer in den Auftragsdaten. Jeder Auftrag hat
     * einen anderen Ansprechpartner. Beide Auftraege sind demselben Kunden zugeordnet.
     */
    public void testFindAnsprechpartnerByKundeNoAndBuendelNoKundeNoAndBuendelNoGiven() throws FindException {
        Long kundeNo = 123L;
        Integer buendelNo = 123;
        AuftragDatenBuilder auftragDatenBuilder1 = getBuilder(AuftragDatenBuilder.class)
                .withBemerkungen("AD1")
                .withBuendelNr(buendelNo);
        AuftragDatenBuilder auftragDatenBuilder2 = getBuilder(AuftragDatenBuilder.class)
                .withBemerkungen("AD2")
                .withBuendelNr(buendelNo);
        AuftragBuilder auftragBuilder1 = getBuilder(AuftragBuilder.class)
                .withKundeNo(kundeNo)
                .withAuftragDatenBuilder(auftragDatenBuilder1);
        AuftragBuilder auftragBuilder2 = getBuilder(AuftragBuilder.class)
                .withKundeNo(kundeNo)
                .withAuftragDatenBuilder(auftragDatenBuilder2);
        AnsprechpartnerBuilder ansprechpartnerBuilder1 = getBuilder(AnsprechpartnerBuilder.class)
                .withType(Typ.ENDSTELLE_A)
                .withAuftragBuilder(auftragBuilder1);
        ansprechpartnerBuilder1.build();
        AnsprechpartnerBuilder ansprechpartnerBuilder2 = getBuilder(AnsprechpartnerBuilder.class)
                .withType(Typ.ENDSTELLE_B)
                .withAuftragBuilder(auftragBuilder2);
        ansprechpartnerBuilder2.build();

        AnsprechpartnerService ansprechpartnerService = getCCService(AnsprechpartnerService.class);
        List<CCAnsprechpartnerView> current = ansprechpartnerService.findAnsprechpartnerByKundeNoAndBuendelNo(kundeNo, buendelNo);
        assertNotEmpty(current);
        assertEquals(current.size(), 2);
    }

    public void testCopyAnsprechpartnerSameAuftragIdAndSameAddressDifferentPreferredFlag() throws StoreException {
        AnsprechpartnerBuilder ansprechpartnerBuilder = getBuilder(AnsprechpartnerBuilder.class)
                .withType(Typ.KUNDE)
                .withPreferred(Boolean.TRUE)
                .withAuftragBuilder(getBuilder(AuftragBuilder.class))
                .withAddressBuilder(getBuilder(CCAddressBuilder.class));
        ansprechpartnerBuilder.build();

        Ansprechpartner old = ansprechpartnerBuilder.get();
        AnsprechpartnerService ansprechpartnerService = getCCService(AnsprechpartnerService.class);
        Ansprechpartner copy = ansprechpartnerService.copyAnsprechpartner(old.getId(), old.getAuftragId());

        assertNotNull(copy);
        assertEquals(old.getTypeRefId(), copy.getTypeRefId());
        assertEquals(old.getAuftragId(), copy.getAuftragId());
        assertEquals(old.getAddress().getId(), copy.getAddress().getId());
        assertTrue(old.getPreferred());
        assertFalse(copy.getPreferred());
    }
}