/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.13
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.VoipDn2DnBlockView;
import de.augustakom.hurrican.model.cc.VoipDnBlock;
import de.augustakom.hurrican.model.cc.VoipDnPlan;
import de.augustakom.hurrican.model.cc.VoipDnPlanBuilder;
import de.augustakom.hurrican.model.cc.VoipDnPlanView;
import de.augustakom.hurrican.model.cc.view.AuftragVoipDNViewBuilder;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.model.shared.view.voip.BlockDNView;
import de.augustakom.hurrican.service.cc.VoipDnPlanValidationService;

@Test(groups = BaseTest.UNIT)
public class VoipDnPlanValidationServiceImplUnitTest extends BaseTest {

    private VoipDnPlanValidationService service;

    @BeforeMethod
    public void initService() {
        service = new VoipDnPlanValidationServiceImpl();
    }

    public void testIsValid() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "0", "9999"))
                .setPersist(false)
                .build();

        final List<VoipDnBlock> blocks = ImmutableList.of(
                new VoipDnBlock("0", null, true),
                new VoipDnBlock("1000", "6999", false),
                new VoipDnBlock("700000", "709999", false),
                new VoipDnBlock("7100", "7999", false),
                new VoipDnBlock("8000", "9999", false));

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(new Date())
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("012345", "67890", plan);

        assertTrue(service.allErrorMessages(planView, dnView).isEmpty());
        assertTrue(service.isValid(planView, dnView));
    }

    public void testIsValidWithRangeFrom00() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "00", "29"))
                .setPersist(false)
                .build();

        final List<VoipDnBlock> blocks = ImmutableList.of(
                new VoipDnBlock("0", null, true),
                new VoipDnBlock("10", "19", false),
                new VoipDnBlock("20", "29", false));

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(new Date())
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("012345", "67890", plan);

        assertTrue(service.allErrorMessages(planView, dnView).isEmpty());
        assertTrue(service.isValid(planView, dnView));
    }

    public void testIsValidWithRangeFrom05() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "05", "29"))
                .setPersist(false)
                .build();

        final List<VoipDnBlock> blocks = ImmutableList.of(
                new VoipDnBlock("05", null, true),
                new VoipDnBlock("06", "09", false),
                new VoipDnBlock("10", "19", false),
                new VoipDnBlock("20", "29", false));

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(new Date())
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("012345", "67890", plan);

        assertTrue(service.allErrorMessages(planView, dnView).isEmpty());
        assertTrue(service.isValid(planView, dnView));
    }

    public void testHasExactlyOneZentraleWithNoZentrale() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "0", "6999"))
                .setPersist(false)
                .build();

        final List<VoipDnBlock> blocks = ImmutableList.of(
                new VoipDnBlock("0", null, false),
                new VoipDnBlock("1000", "6999", false));

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(new Date())
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("012345", "67890", plan);

        assertTrue(service.allErrorMessages(planView, dnView).isEmpty());
        assertTrue(service.hasExactlyOneZentrale(planView));
    }

    public void testHasExactlyOneZentraleWithTwoZentrale() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "0", "6999"))
                .setPersist(false)
                .build();

        final List<VoipDnBlock> blocks = ImmutableList.of(
                new VoipDnBlock("0", null, true),
                new VoipDnBlock("1000", "6999", true));

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(new Date())
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("012345", "67890", plan);

        assertFalse(service.allErrorMessages(planView, dnView).isEmpty());
        assertFalse(service.hasExactlyOneZentrale(planView));
    }

    public void testBoundaryMatchesRangeFromTo() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "0", "2999"))
                .setPersist(false)
                .build();

        final List<VoipDnBlock> blocks = ImmutableList.of(
                new VoipDnBlock("0", null, true),
                new VoipDnBlock("1000", "1999", false),
                new VoipDnBlock("2000", "2999", false));

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(new Date())
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("012345", "67890", plan);

        assertTrue(service.allErrorMessages(planView, dnView).isEmpty());
        assertTrue(service.boundaryMatchesRangeFromTo(planView, dnView));
    }

    public void testBoundaryMatchesRangeFromToNot() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "0", "3999"))
                .setPersist(false)
                .build();

        final List<VoipDnBlock> blocks = ImmutableList.of(
                new VoipDnBlock("0", null, true),
                new VoipDnBlock("1000", "1999", false),
                new VoipDnBlock("2000", "2999", false));

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(new Date())
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("012345", "67890", plan);

        assertFalse(service.allErrorMessages(planView, dnView).isEmpty());
        assertFalse(service.boundaryMatchesRangeFromTo(planView, dnView));
    }

    public void testBoundaryMatchesRangeFromToShorterLonger() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "00", "99"))
                .setPersist(false)
                .build();

        final List<VoipDnBlock> blocks = ImmutableList.of(
                new VoipDnBlock("0", null, true),
                new VoipDnBlock("1000", "9999", false));

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(new Date())
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("012345", "67890", plan);

        assertTrue(service.allErrorMessages(planView, dnView).isEmpty());
        assertTrue(service.boundaryMatchesRangeFromTo(planView, dnView));
    }

    // https://jira.m-net.de/browse/HUR-16270
    public void testBoundaryMatchesRangeFromToIncorrectEnd() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "00", "29"))
                .setPersist(false)
                .build();

        final List<VoipDnBlock> blocks = ImmutableList.of(
                new VoipDnBlock("0", null, true),
                new VoipDnBlock("10000", "29000", false));

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(new Date())
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("089", "1891716", plan);

        assertFalse(service.boundaryMatchesRangeFromTo(planView, dnView));
        assertFalse(service.allErrorMessages(planView, dnView).isEmpty());
        assertFalse(service.isValid(planView, dnView));
    }

    public void testBoundaryMatchesRangeFromToEmptyPlan() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "00", "29"))
                .setPersist(false)
                .build();

        final List<VoipDnBlock> blocks = Collections.emptyList();

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(new Date())
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("089", "1891716", plan);

        assertFalse(service.boundaryMatchesRangeFromTo(planView, dnView));
        assertFalse(service.allErrorMessages(planView, dnView).isEmpty());
        assertFalse(service.isValid(planView, dnView));
    }

    public void testGetOverlappingBlocksMultipleOverlaps() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "1000", "9999"))
                .setPersist(false)
                .build();

        final VoipDnBlock b1 = new VoipDnBlock("1000", "6999", false);
        final VoipDnBlock b2 = new VoipDnBlock("6000", "7199", false);
        final VoipDnBlock b3 = new VoipDnBlock("7100", "7999", false);
        final VoipDnBlock b4 = new VoipDnBlock("7900", "9999", false);

        final VoipDn2DnBlockView db1 = new VoipDn2DnBlockView("0", "0", b1);
        final VoipDn2DnBlockView db2 = new VoipDn2DnBlockView("0", "0", b2);
        final VoipDn2DnBlockView db3 = new VoipDn2DnBlockView("0", "0", b3);
        final VoipDn2DnBlockView db4 = new VoipDn2DnBlockView("0", "0", b4);

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withVoipDnBlocks(ImmutableList.of(b1, b2, b3, b4))
                .withGueltigAb(new Date())
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("0", "0", plan);

        assertFalse(service.allErrorMessages(planView, dnView).isEmpty());
        assertEquals(service.getOverlappingBlocks(planView),
                ImmutableList.of(
                        Pair.create(db1, db2),
                        Pair.create(db2, db3),
                        Pair.create(db3, db4))
        );
    }

    public void testGetOverlappingBlocksEndEqStart() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "1000", "7199"))
                .setPersist(false)
                .build();

        final VoipDnBlock b1 = new VoipDnBlock("1000", "6000", false);
        final VoipDnBlock b2 = new VoipDnBlock("6000", "7199", false);

        final VoipDn2DnBlockView db1 = new VoipDn2DnBlockView("0", "0", b1);
        final VoipDn2DnBlockView db2 = new VoipDn2DnBlockView("0", "0", b2);

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withVoipDnBlocks(ImmutableList.of(b1, b2))
                .withGueltigAb(new Date())
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("0", "0", plan);

        assertFalse(service.allErrorMessages(planView, dnView).isEmpty());
        assertEquals(service.getOverlappingBlocks(planView),
                ImmutableList.of(Pair.create(db1, db2)));
    }

    public void testGetOverlappingBlocksNoOverlap() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "0", "7199"))
                .setPersist(false)
                .build();

        final VoipDnBlock b0 = new VoipDnBlock("0", null, true);
        final VoipDnBlock b1 = new VoipDnBlock("1000", "6000", false);
        final VoipDnBlock b2 = new VoipDnBlock("6001", "7199", false);

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withVoipDnBlocks(ImmutableList.of(b1, b2))
                .withGueltigAb(new Date())
                .withVoipDnBlocks(ImmutableList.of(b0, b1, b2))
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("0", "0", plan);

        assertTrue(service.allErrorMessages(planView, dnView).isEmpty());
        assertTrue(service.getOverlappingBlocks(planView).isEmpty());
    }

    public void testGetOverlappingBlocksWithGap() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "0", "7199"))
                .setPersist(false)
                .build();

        final VoipDnBlock b0 = new VoipDnBlock("0", null, true);
        final VoipDnBlock b1 = new VoipDnBlock("1000", "6000", false);
        final VoipDnBlock b2 = new VoipDnBlock("6010", "7199", false);

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withVoipDnBlocks(ImmutableList.of(b1, b2))
                .withGueltigAb(new Date())
                .withVoipDnBlocks(ImmutableList.of(b0, b1, b2))
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("0", "0", plan);

        assertFalse(service.allErrorMessages(planView, dnView).isEmpty());
        assertTrue(service.getOverlappingBlocks(planView).isEmpty());
    }

    public void testGetBlocksWithGapsNoGaps() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "0", "3000"))
                .setPersist(false)
                .build();

        final VoipDnBlock b0 = new VoipDnBlock("0", null, true);
        final VoipDnBlock b1 = new VoipDnBlock("100000", "199999", false);
        final VoipDnBlock b2 = new VoipDnBlock("2000", "3000", false);

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withVoipDnBlocks(ImmutableList.of(b1, b2))
                .withGueltigAb(new Date())
                .withVoipDnBlocks(ImmutableList.of(b0, b1, b2))
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("0", "0", plan);

        assertTrue(service.allErrorMessages(planView, dnView).isEmpty());
        assertTrue(service.getBlocksWithGaps(planView).isEmpty());
    }

    public void testGetBlocksWithGapsMultipleGaps() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "1000", "5000"))
                .setPersist(false)
                .build();

        final VoipDnBlock b1 = new VoipDnBlock("100000", "199999", false);
        final VoipDnBlock b2 = new VoipDnBlock("2500", "3000", false);
        final VoipDnBlock b3 = new VoipDnBlock("3001", "4000", false);
        final VoipDnBlock b4 = new VoipDnBlock("4500", "5000", false);

        final VoipDn2DnBlockView db1 = new VoipDn2DnBlockView("0", "0", b1);
        final VoipDn2DnBlockView db2 = new VoipDn2DnBlockView("0", "0", b2);
        final VoipDn2DnBlockView db3 = new VoipDn2DnBlockView("0", "0", b3);
        final VoipDn2DnBlockView db4 = new VoipDn2DnBlockView("0", "0", b4);

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withVoipDnBlocks(ImmutableList.of(b1, b2, b3, b4))
                .withGueltigAb(new Date())
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("0", "0", plan);

        assertFalse(service.allErrorMessages(planView, dnView).isEmpty());
        assertEquals(service.getBlocksWithGaps(planView),
                ImmutableList.of(
                        Pair.create(db1, db2),
                        Pair.create(db3, db4))
        );
    }

    public void testGetBlocksWithGapsGapAfterZentrale() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "0", "4000"))
                .setPersist(false)
                .build();

        final VoipDnBlock b1 = new VoipDnBlock("0", null, true);
        final VoipDnBlock b2 = new VoipDnBlock("2000", "3000", false);
        final VoipDnBlock b3 = new VoipDnBlock("3001", "4000", false);

        final VoipDn2DnBlockView db1 = new VoipDn2DnBlockView("0", "0", b1);
        final VoipDn2DnBlockView db2 = new VoipDn2DnBlockView("0", "0", b2);

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withVoipDnBlocks(ImmutableList.of(b1, b2, b3))
                .withGueltigAb(new Date())
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("0", "0", plan);

        assertFalse(service.allErrorMessages(planView, dnView).isEmpty());
        assertEquals(service.getBlocksWithGaps(planView),
                ImmutableList.of(Pair.create(db1, db2)));
    }

    // https://jira.m-net.de/browse/HUR-16344
    public void testGetBlocksWithGapsHUR16344() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "00", "29"))
                .setPersist(false)
                .build();

        final VoipDnBlock b1 = new VoipDnBlock("00", null, true);
        final VoipDnBlock b2 = new VoipDnBlock("011", "099", false);
        final VoipDnBlock b3 = new VoipDnBlock("10", "29", false);

        final VoipDn2DnBlockView db1 = new VoipDn2DnBlockView("0", "0", b1);
        final VoipDn2DnBlockView db2 = new VoipDn2DnBlockView("0", "0", b2);

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withVoipDnBlocks(ImmutableList.of(b1, b2, b3))
                .withGueltigAb(new Date())
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("0", "0", plan);

        assertFalse(service.allErrorMessages(planView, dnView).isEmpty());
        assertEquals(service.getBlocksWithGaps(planView),
                ImmutableList.of(Pair.create(db1, db2)));
    }

    public void testGetTooLongBlocks() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "1001", "1234567"))
                .setPersist(false)
                .build();

        final VoipDnBlock b1 = new VoipDnBlock("1001", "1234", false);
        final VoipDnBlock b2 = new VoipDnBlock("2001", "12345", false);
        final VoipDnBlock b3 = new VoipDnBlock("3001", "123456", false);
        final VoipDnBlock b4 = new VoipDnBlock("4001", "1234567", false);

        final String lac = "01234567890";
        final String base = "1234567890";

        final VoipDn2DnBlockView db3 = new VoipDn2DnBlockView(lac, base, b3);
        final VoipDn2DnBlockView db4 = new VoipDn2DnBlockView(lac, base, b4);

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withVoipDnBlocks(ImmutableList.of(b1, b2, b3, b4))
                .withGueltigAb(new Date())
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView(lac, base, plan);

        assertFalse(service.allErrorMessages(planView, dnView).isEmpty());
        assertEquals(service.getTooLongBlocks(planView),
                ImmutableList.of(db3, db4));
    }

    public void testAnf99VerlTeilbereich() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "0", "29"))
                .setPersist(false)
                .build();

        final List<VoipDnBlock> blocks = ImmutableList.of(
                new VoipDnBlock("0", null, true),
                new VoipDnBlock("10", "23", false),
                new VoipDnBlock("240", "289", false),
                new VoipDnBlock("29", null, false));

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(new Date())
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("089", "12345", plan);

        assertTrue(service.allErrorMessages(planView, dnView).isEmpty());
        assertTrue(service.isValid(planView, dnView));
    }

    public void testBlockArity() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "0", "29"))
                .setPersist(false)
                .build();

        final List<VoipDnBlock> blocks = ImmutableList.of(
                new VoipDnBlock("0", null, true),
                new VoipDnBlock("1", "19", false),
                new VoipDnBlock("20", "29", false));

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(new Date())
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("012345", "67890", plan);

        assertFalse(service.getBlocksWithInconsistentArity(planView).isEmpty());
        assertEquals(service.allErrorMessages(planView, dnView).size(), 1);
        assertFalse(service.isValid(planView, dnView));
    }

    public void testAnfangWithAdditionalTrailingZeros() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "00", "20"))
                .setPersist(false)
                .build();

        final List<VoipDnBlock> blocks = ImmutableList.of(
                new VoipDnBlock("0000", "1999", false),
                new VoipDnBlock("20", null, true));

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(new Date())
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("012345", "67890", plan);

        assertTrue(service.allErrorMessages(planView, dnView).isEmpty());
        assertTrue(service.isValid(planView, dnView));
    }

    public void testMinimalPlan() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "00", "99"))
                .setPersist(false)
                .build();

        final List<VoipDnBlock> blocks = ImmutableList.of(
                new VoipDnBlock("0", null, true),
                new VoipDnBlock("1", "9", false));

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(new Date())
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("012345", "67890", plan);

        assertTrue(service.allErrorMessages(planView, dnView).isEmpty());
        assertTrue(service.isValid(planView, dnView));
    }

    public void testPlanWithInvalidChars() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "00", "99"))
                .setPersist(false)
                .build();

        final List<VoipDnBlock> blocks = ImmutableList.of(
                new VoipDnBlock("0 ", null, true),
                new VoipDnBlock("1b", "90", false),
                new VoipDnBlock("91", "99", false));

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(new Date())
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("012345", "67890", plan);

        assertFalse(service.getNonnumericalBlocks(planView).isEmpty());
        assertFalse(service.allErrorMessages(planView, dnView).isEmpty());
        assertFalse(service.isValid(planView, dnView));
    }

    // https://jira.m-net.de/browse/HUR-16266
    public void testPlanWithLongBlocks() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "00", "99"))
                .setPersist(false)
                .build();

        final List<VoipDnBlock> blocks = ImmutableList.of(
                new VoipDnBlock("000000000000", "099999999999", false),
                new VoipDnBlock("1", null, true),
                new VoipDnBlock("20", "99", false));

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(new Date())
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("089", "1891716", plan);

        assertTrue(service.allErrorMessages(planView, dnView).isEmpty());
        assertTrue(service.isValid(planView, dnView));
    }

    public void testPlaeneMitGleichemGueltigAb() {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withBlock(new BlockDNView("", "00", "99"))
                .setPersist(false)
                .build();

        final Date now = new Date();

        final List<VoipDnBlock> blocks = ImmutableList.of(
                new VoipDnBlock("0", null, true),
                new VoipDnBlock("10", "99", false));

        final VoipDnPlan plan1 = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(now)
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlan plan2 = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(now)
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlanView planView1 = new VoipDnPlanView("012345", "67890", plan1);
        final VoipDnPlanView planView2 = new VoipDnPlanView("012345", "67890", plan2);
        dnView.addVoipDnPlanView(planView1);

        assertFalse(service.isValid(planView2, dnView));
        assertFalse(service.hasUniqueGueltigAb(planView2, dnView));
        assertEquals(service.allErrorMessages(planView2, dnView).size(), 1);
    }
}
