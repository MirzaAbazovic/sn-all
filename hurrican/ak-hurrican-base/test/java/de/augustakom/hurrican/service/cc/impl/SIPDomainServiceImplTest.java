/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.06.2011 16:39:26
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.model.cc.EGType2SIPDomain;
import de.augustakom.hurrican.model.cc.EGType2SIPDomainBuilder;
import de.augustakom.hurrican.model.cc.EGTypeBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2SIPDomain;
import de.augustakom.hurrican.model.cc.Produkt2SIPDomainBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HWSwitchService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.SIPDomainService;

@Test(groups = { BaseTest.SERVICE })
public class SIPDomainServiceImplTest extends AbstractHurricanBaseServiceTest {

    private static final String SIP_DOMAIN_PRIVATE = "privat.sip.m-online.net";
    private static final String VOIP_SWITCH = "MUC03";
    private SIPDomainService cut;

    @BeforeMethod
    public void initVoIPService() {
        cut = getCCService(SIPDomainService.class);
    }

    public void testSaveProdukt2SIPDomain() throws StoreException, FindException {
        ReferenceService referenceService = getCCService(ReferenceService.class);
        Reference refSIPDomain = referenceService.findReference(Reference.REF_TYPE_SIP_DOMAIN_TYPE, SIP_DOMAIN_PRIVATE);
        HWSwitchService hwSwitchService = getCCService(HWSwitchService.class);
        HWSwitch refSwitchKennung = hwSwitchService.findSwitchByName(VOIP_SWITCH);

        Produkt produkt = getBuilder(ProduktBuilder.class).build();
        Produkt2SIPDomain produkt2SIPDomain = new Produkt2SIPDomain();
        produkt2SIPDomain.setProdId(produkt.getId());
        produkt2SIPDomain.setSipDomainRef(refSIPDomain);
        produkt2SIPDomain.setHwSwitch(refSwitchKennung);

        cut.saveProdukt2SIPDomain(produkt2SIPDomain, -1L);
    }

    @Test
    public void testDeleteProdukt2SIPDomain() throws FindException {
        ReferenceService referenceService = getCCService(ReferenceService.class);
        Reference refSIPDomain = referenceService.findReference(Reference.REF_TYPE_SIP_DOMAIN_TYPE, SIP_DOMAIN_PRIVATE);
        HWSwitchService hwSwitchService = getCCService(HWSwitchService.class);
        HWSwitch refSwitchKennung = hwSwitchService.findSwitchByName(VOIP_SWITCH);

        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class);
        Produkt2SIPDomain produkt2SIPDomain = getBuilder(Produkt2SIPDomainBuilder.class)
                .withProduktBuilder(produktBuilder)
                .withHwSwitch(refSwitchKennung)
                .withSIPDomainRef(refSIPDomain)
                .build();

        cut.deleteProdukt2SIPDomain(produkt2SIPDomain);
    }

    @Test
    public void testFindProdukt2SIPDomains4Produkt() throws FindException {
        ReferenceService referenceService = getCCService(ReferenceService.class);
        Reference refSIPDomain = referenceService.findReference(Reference.REF_TYPE_SIP_DOMAIN_TYPE, SIP_DOMAIN_PRIVATE);
        HWSwitchService hwSwitchService = getCCService(HWSwitchService.class);
        HWSwitch refSwitchKennung = hwSwitchService.findSwitchByName(VOIP_SWITCH);

        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class);
        getBuilder(Produkt2SIPDomainBuilder.class)
                .withProduktBuilder(produktBuilder)
                .withHwSwitch(refSwitchKennung)
                .withSIPDomainRef(refSIPDomain)
                .build();

        List<Produkt2SIPDomain> result = cut.findProdukt2SIPDomains(produktBuilder.get().getId(),
                refSwitchKennung, null);

        assertTrue(CollectionTools.isNotEmpty(result), "SIP Domäne konnte nicht ermittelt werden!");
    }

    @Test
    public void testFindEGType2SIPDomains4Produkt() throws FindException {
        ReferenceService referenceService = getCCService(ReferenceService.class);
        Reference refSIPDomain = referenceService.findReference(Reference.REF_TYPE_SIP_DOMAIN_TYPE, SIP_DOMAIN_PRIVATE);
        HWSwitchService hwSwitchService = getCCService(HWSwitchService.class);
        HWSwitch refSwitchKennung = hwSwitchService.findSwitchByName(VOIP_SWITCH);

        EGTypeBuilder egTypeBuilder = getBuilder(EGTypeBuilder.class);
        getBuilder(EGType2SIPDomainBuilder.class)
                .withEGTypeBuilder(egTypeBuilder)
                .withHwSwitch(refSwitchKennung)
                .withSIPDomainRef(refSIPDomain)
                .build();

        List<EGType2SIPDomain> result = cut.findEGType2SIPDomains(egTypeBuilder.get(), refSwitchKennung);

        assertTrue(CollectionTools.isNotEmpty(result), "SIP Domäne konnte nicht ermittelt werden!");
    }
}
