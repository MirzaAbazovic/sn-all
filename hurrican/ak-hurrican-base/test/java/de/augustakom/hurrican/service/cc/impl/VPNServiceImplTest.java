/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.09.2004 08:00:27
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.springframework.orm.hibernate4.HibernateSystemException;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.VPNBuilder;
import de.augustakom.hurrican.model.cc.VPNKonfiguration;
import de.augustakom.hurrican.model.cc.VPNKonfigurationBuilder;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.VPNService;


/**
 * Test-NG fuer VPNService.
 *
 *
 */
@Test(groups = BaseTest.SERVICE)
public class VPNServiceImplTest extends AbstractHurricanBaseServiceTest {

    public void testFindVPNByVpnNr() throws FindException {
        Long vpnNr = Long.valueOf(123456);
        getBuilder(VPNBuilder.class)
                .withVpnNr(vpnNr)
                .withDatum(new Date())
                .withKundeNo(Long.valueOf(500000001))
                .build();

        VPN vpn = getCCService(VPNService.class).findVPNByVpnNr(vpnNr);
        assertNotNull(vpn, "VPN not found!");
    }

    public void testSaveVPN() throws StoreException, FindException {
        Long vpnNr = Long.valueOf(11);
        VPN vpnToSave = getBuilder(VPNBuilder.class)
                .withVpnNr(vpnNr)
                .withDatum(new Date())
                .withKundeNo(Long.valueOf(500000001))
                .setPersist(false)
                .build();

        getCCService(VPNService.class).saveVPN(vpnToSave);
        assertNotNull(vpnToSave.getId(), "ID not generated!");

        VPN vpn = getCCService(VPNService.class).findVPNByVpnNr(vpnNr);
        assertNotNull(vpn, "saved VPN could not be loaded!");
    }

    @Test(expectedExceptions = StoreException.class)
    public void testSaveVPNInvalidData() throws HibernateSystemException, StoreException {
        VPN vpnToSave = getBuilder(VPNBuilder.class)
                .withDatum(new Date())
                .withVpnNr(null)
                .setPersist(false)
                .build();

        getCCService(VPNService.class).saveVPN(vpnToSave);

        fail("Should NOT be able to save this one because of NOT_NULL fields in hibernate configuration!");
    }

    public void testAddAuftrag2VPN() throws StoreException, FindException {
        Long vpnNr = Long.valueOf(11);
        VPN vpnToSave = getBuilder(VPNBuilder.class)
                .withVpnNr(vpnNr)
                .withDatum(new Date())
                .withKundeNo(Long.valueOf(500000001))
                .build();

        ProduktBuilder prodBuilder = getBuilder(ProduktBuilder.class)
                .withVbzKindOfUseProduct("X")
                .withVbzKindOfUseType("Y");

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(getBuilder(AuftragDatenBuilder.class)
                        .withProdBuilder(prodBuilder));

        VerbindungsBezeichnungBuilder vbzBuilder = getBuilder(VerbindungsBezeichnungBuilder.class)
                .withRandomUniqueCode()
                .withKindOfUseProduct(prodBuilder.get().getVbzKindOfUseProduct())
                .withKindOfUseType(prodBuilder.get().getVbzKindOfUseType());

        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withVerbindungsBezeichnungBuilder(vbzBuilder)
                .withAuftragBuilder(auftragBuilder);

        getCCService(VPNService.class).addAuftrag2VPN(vpnToSave.getId(), auftragTechnikBuilder.get());
        assertNotNull(auftragTechnikBuilder.get().getVpnId(), "AuftragTechnik.VPN_ID not set!");

        // VerbindungsBezeichnung pruefen
        VerbindungsBezeichnung verbindungsBezeichnung = getCCService(PhysikService.class).findVerbindungsBezeichnungByAuftragIdTx(auftragTechnikBuilder.get().getAuftragId());
        assertNotNull(verbindungsBezeichnung, "VerbindungsBezeichnung not found!");
        assertTrue(verbindungsBezeichnung.getVbz().startsWith(VerbindungsBezeichnung.KindOfUseType.V.name()), "Prefix of VerbindungsBezeichnung is not valid!");
    }

    public void testRemoveAuftragFromVPN() throws StoreException, FindException {
        Long vpnNr = Long.valueOf(11);
        VPNBuilder vpnBuilder = getBuilder(VPNBuilder.class)
                .withVpnNr(vpnNr)
                .withDatum(new Date())
                .withKundeNo(Long.valueOf(500000001));

        ProduktBuilder prodBuilder = getBuilder(ProduktBuilder.class)
                .withVbzKindOfUseProduct("X")
                .withVbzKindOfUseType("Y");

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(getBuilder(AuftragDatenBuilder.class)
                        .withProdBuilder(prodBuilder));

        VerbindungsBezeichnungBuilder vbzBuilder = getBuilder(VerbindungsBezeichnungBuilder.class)
                .withRandomUniqueCode()
                .withKindOfUseProduct("V")
                .withKindOfUseType(prodBuilder.get().getVbzKindOfUseType());

        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withVPNBuilder(vpnBuilder)
                .withVerbindungsBezeichnungBuilder(vbzBuilder)
                .withAuftragBuilder(auftragBuilder);

        getCCService(VPNService.class).removeAuftragFromVPN(auftragTechnikBuilder.get());
        assertNull(auftragTechnikBuilder.get().getVpnId(), "AuftragTechnik.VPN_ID still assigned to VPN!");

        // VerbindungsBezeichnung pruefen
        VerbindungsBezeichnung verbindungsBezeichnung = getCCService(PhysikService.class).findVerbindungsBezeichnungByAuftragIdTx(auftragTechnikBuilder.get().getAuftragId());
        assertNotNull(verbindungsBezeichnung, "VerbindungsBezeichnung not found!");
        assertTrue(verbindungsBezeichnung.getVbz().startsWith("X"), "Prefix of VerbindungsBezeichnung is not valid!");
    }

    public void findVPNKonfiguration4Auftrag() throws Exception {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        final Long auftragId = auftragBuilder.get().getId();
        VPNKonfiguration vpnKonfigurationToFind = getBuilder(VPNKonfigurationBuilder.class).withAuftragBuilder(auftragBuilder).build();
        getBuilder(VPNKonfigurationBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withGueltigBis(new Date()).build();

        VPNKonfiguration vpnKonfigurationFound = getCCService(VPNService.class).findVPNKonfiguration4Auftrag(auftragId);

        assertEquals(vpnKonfigurationToFind, vpnKonfigurationFound);
    }

    public void saveVPNKonfigurationWithHistory() throws Exception {
        VPNKonfiguration vpnKonfigToHistorize = getBuilder(VPNKonfigurationBuilder.class).build();

        VPNKonfiguration vpnKonfigNew = getCCService(VPNService.class).saveVPNKonfiguration(vpnKonfigToHistorize, true);

        assertNotEquals(vpnKonfigToHistorize.getId(), vpnKonfigNew.getId());
        assertTrue(DateTools.isHurricanEndDate(vpnKonfigNew.getGueltigBis()));
        assertTrue(DateTools.isDateEqual(vpnKonfigNew.getGueltigVon(), new Date()));
    }

}


