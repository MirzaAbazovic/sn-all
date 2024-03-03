/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.04.2007 08:04:26
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.Multimap;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfile;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.DSLAMProfileChangeReason;
import de.augustakom.hurrican.model.cc.DSLAMProfileMapping;
import de.augustakom.hurrican.model.cc.HWBaugruppenTypBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2DslamProfileBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.DSLAMService;

/**
 * Test fuer die Implementierung von <code>DSLAMService</code>.
 *
 *
 */
@Test(groups = { BaseTest.SERVICE })
public class DSLAMServiceTest extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(DSLAMServiceTest.class);
    private DSLAMService underTest;

    @BeforeMethod
    public void setup() {
        underTest = getCCService(DSLAMService.class);
    }

    public void testFindDSLAMProfiles4Produkt() throws FindException {
        Long prodId = Long.valueOf(430);

        List<DSLAMProfile> result = underTest.findDSLAMProfiles4Produkt(prodId);

        assertNotEmpty(result, "No DSLAM-Profiles found for product " + prodId);
    }

    @Test(enabled = false)
    public void testAssignDSLAMProfile() throws FindException, StoreException {
        // Test muss mit dynamischen Daten aufgebaut werden!!!

        Long auftragId = Long.valueOf(224949);

        DSLAMProfile result = underTest.assignDSLAMProfile(auftragId, DateTools.createDate(2009, 6, 15), null, getSessionId());

        assertNotNull(result, "Es konnte kein DSLAM-Profile ermittelt werden!");
    }

    public void testChangeDSLAMProfile() throws StoreException {
        Auftrag2DSLAMProfile result = underTest.changeDSLAMProfile(Long.valueOf(175783), Long.valueOf(35),
                DateTools.createDate(2007, 3, 8), "UnitTest", DSLAMProfileChangeReason.CHANGE_REASON_ID_INIT, null);

        assertNotNull(result, "Es wurde kein Auftrag2DSLAMProfile Objekt zurueck gegeben!");
    }

    public void findNextHigherDSLAMProfile4DSL18000Auftrag() throws FindException {
        AuftragBuilder ab = getBuilder(AuftragBuilder.class);
        Long baugruppenTyp = getBuilder(HWBaugruppenTypBuilder.class).build().getId();
        Integer bitrateUp = 1000, bitrateDown = 1000;
        //@formatter:off
        DSLAMProfileBuilder pb =
                getBuilder(DSLAMProfileBuilder.class)
                    .withBaugruppenTypId(baugruppenTyp)
                    .withBandwidth(bitrateDown, bitrateUp)
                    .withFastpath(true)
                    .withUetv("ADSL2+")
                    .withEnabledForAutochange(Boolean.TRUE);
        Auftrag2DSLAMProfile a2dslam = getBuilder(Auftrag2DSLAMProfileBuilder.class).withAuftragBuilder(ab).withDSLAMProfileBuilder(pb).build();
        //@formatter:on
        getBuilder(Auftrag2TechLeistungBuilder.class).withAuftragId(a2dslam.getAuftragId()).withTechLeistungId(21L)
                .build();

        DSLAMProfile profileFound = underTest.findNextHigherDSLAMProfile4DSL18000Auftrag(
                a2dslam.getAuftragId(), bitrateUp - 1, bitrateDown - 1);

        assertEquals(profileFound.getBandwidth().getDownstream(), bitrateDown);
        assertEquals(profileFound.getBandwidth().getUpstream(), bitrateUp);
    }

    public void findDSLAMProfiles() throws FindException {
        HWBaugruppenTyp baugruppenTyp = getBuilder(HWBaugruppenTypBuilder.class).build();
        boolean fastpath = true;
        String uetv = "ADSL2+";
        DSLAMProfile dslamProfile = getBuilder(DSLAMProfileBuilder.class).withBaugruppenTypId(baugruppenTyp.getId())
                .withFastpath(fastpath).withUetv(uetv).withEnabledForAutochange(Boolean.TRUE).build();
        DSLAMProfile dslamProfileWithoutFastpath = getBuilder(DSLAMProfileBuilder.class).withBaugruppenTypId(baugruppenTyp.getId())
                .withFastpath(!fastpath).withUetv(uetv).withEnabledForAutochange(Boolean.TRUE).build();
        DSLAMProfile disabledDslamProfile = getBuilder(DSLAMProfileBuilder.class).withBaugruppenTypId(baugruppenTyp.getId())
                .withFastpath(fastpath).withUetv(uetv).withEnabledForAutochange(Boolean.FALSE).build();
        flushAndClear();

        Collection<String> uetvsAllowed = Arrays.asList(uetv, "H13");
        List<DSLAMProfile> foundProfiles = underTest.findDSLAMProfiles(baugruppenTyp.getId(), fastpath, uetvsAllowed);

        flushAndClear();
        assertNotEmpty(foundProfiles);
        assertEquals(foundProfiles.size(), 1);
        assertTrue(foundProfiles.contains(dslamProfile));
        assertFalse(foundProfiles.contains(disabledDslamProfile));
        assertFalse(foundProfiles.contains(dslamProfileWithoutFastpath));
        assertTrue(foundProfiles.get(0).getFastpath());
    }

    public void findDSLAMProfiles_WithoutUetvs() throws FindException {
        HWBaugruppenTyp baugruppenTyp = getBuilder(HWBaugruppenTypBuilder.class).build();
        boolean fastpath = true;
        String uetv = "ADSL2+";
        DSLAMProfile dslamProfile = getBuilder(DSLAMProfileBuilder.class).withBaugruppenTypId(baugruppenTyp.getId())
                .withFastpath(fastpath).withUetv(uetv).withEnabledForAutochange(Boolean.TRUE).build();
        DSLAMProfile nulledDslamProfile = getBuilder(DSLAMProfileBuilder.class)
                .withBaugruppenTypId(baugruppenTyp.getId()).withFastpath(fastpath).withUetv(null)
                .withEnabledForAutochange(Boolean.TRUE).build();

        Collection<String> uetvsAllowed = Collections.emptyList();
        List<DSLAMProfile> foundProfiles = underTest.findDSLAMProfiles(baugruppenTyp.getId(), fastpath, uetvsAllowed);

        assertFalse(foundProfiles.contains(dslamProfile));
        assertTrue(foundProfiles.contains(nulledDslamProfile));
    }

    public void findDslamProfile4AuftragOrCalculateDefaultWithProfileAssigned() throws FindException {
        //@formatter:off
        Auftrag2DSLAMProfile auftrag2Dslamprofile = getBuilder(Auftrag2DSLAMProfileBuilder.class)
                .withGueltigVon(DateTools.createDate(1980, 1, 1))
                .withGueltigBis(DateTools.getHurricanEndDate())
                .build();
        //@formatter:on
        DSLAMProfile dslamProfile = underTest.findDslamProfile4AuftragOrCalculateDefault(
                auftrag2Dslamprofile.getAuftragId(), new Date());
        assertNotNull(dslamProfile);
        assertEquals(dslamProfile.getId(), auftrag2Dslamprofile.getDslamProfileId());
    }

    public void findDslamProfile4AuftragOrCalculateDefaultNoProfileAssigned() throws FindException, StoreException {
        final DSLAMProfileBuilder dslamProfileBuilder = getBuilder(DSLAMProfileBuilder.class);
        final ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class).withRandomId();
        final Produkt2DslamProfileBuilder p2DpBuilder= getBuilder(Produkt2DslamProfileBuilder.class)
                .withProduktBuilder(produktBuilder)
                .withDslamProfileBuilder(dslamProfileBuilder);

        p2DpBuilder.build();

        AuftragDaten auftragDaten = getBuilder(AuftragDatenBuilder.class).withProdId(produktBuilder.get().getProdId()).build();
        DSLAMProfile dslamProfile = underTest.findDslamProfile4AuftragOrCalculateDefault(auftragDaten.getAuftragId(),
                new Date());
        assertNotNull(dslamProfile);
    }

    public void deleteAuftrag2DslamProfile() throws DeleteException {
        Auftrag2DSLAMProfile auftrag2Profile = getBuilder(Auftrag2DSLAMProfileBuilder.class)
                .withAuftragBuilder(getBuilder(AuftragBuilder.class))
                .withDSLAMProfileBuilder(getBuilder(DSLAMProfileBuilder.class))
                .build();
        underTest.deleteAuftrag2DslamProfile(auftrag2Profile.getId());

        // nochmaliger Loeschversuch fuehrt zu Fehler
        try {
            underTest.deleteAuftrag2DslamProfile(auftrag2Profile.getId());
        }
        catch (DeleteException e) {
            return;
        }
        fail();
    }

    public void findDSLAMProfileMappings() throws FindException {
        List<DSLAMProfile> profiles = underTest.findDSLAMProfiles4Produkt(Produkt.PROD_ID_MAXI_DSL_UND_ISDN);
        assertNotEmpty(profiles);

        DSLAMProfile oldDSLAMProfile = profiles.get(0);
        List<DSLAMProfileMapping> mappings = underTest.findDSLAMProfileMappings(oldDSLAMProfile.getName());
        assertNotEmpty(mappings);
    }

    public void findDSLAMProfileMappingCandidates() throws FindException {
        List<DSLAMProfile> profiles = underTest.findDSLAMProfiles4Produkt(Produkt.PROD_ID_MAXI_DSL_UND_ISDN);
        assertNotEmpty(profiles);

        DSLAMProfile oldDSLAMProfile = profiles.get(0);
        Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> mappings = underTest.findDSLAMProfileMappingCandidates(oldDSLAMProfile.getId());
        assertNotEmpty(mappings.entries());
        assertNotEmpty(mappings.get(new Pair(oldDSLAMProfile.getId(), null)));
    }

}
