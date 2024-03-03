/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.2010 10:19:18
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.CBVorgangBuilder;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Carrierbestellung2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.CBVorgangSubOrder;
import de.augustakom.hurrican.model.exmodules.tal.TALSegment;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Test NG Klasse fuer GetDataPhysikCommand
 *
 *
 */
@Test(groups = BaseTest.SERVICE)
public class GetDataPhysikCommandTest extends AbstractHurricanBaseServiceTest {

    private CBVorgang cbVorgang;
    private AuftragTechnik auftragTechnik1;
    private AuftragTechnik auftragTechnik2;
    private Carrierbestellung carrierbestellung;
    private Set<CBVorgangSubOrder> subOrders;

    /**
     * Initialize the tests
     *
     * @throws FindException
     */
    @SuppressWarnings("unused")
    @BeforeMethod(groups = "service", dependsOnMethods = "beginTransactions")
    private void prepareTest() throws FindException {
        EquipmentBuilder dtagEq1Builder = getBuilder(EquipmentBuilder.class)
                .withRangSSType("2N")
                .withRangBucht("0101")
                .withRangLeiste1("01")
                .withRangStift1("88");

        Carrierbestellung2EndstelleBuilder cb2EsBuilder = getBuilder(Carrierbestellung2EndstelleBuilder.class);

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragTechnikBuilder auftragTechnikBuilder1 = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragBuilder);
        auftragTechnik1 = auftragTechnikBuilder1
                .build();

        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder1)
                .withCb2EsBuilder(cb2EsBuilder)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .withRangierungBuilder(getBuilder(RangierungBuilder.class)
                        .withEqOutBuilder(dtagEq1Builder));
        endstelleBuilder.build();

        CarrierbestellungBuilder cbBuilder = getBuilder(CarrierbestellungBuilder.class)
                .withCb2EsBuilder(cb2EsBuilder);
        carrierbestellung = cbBuilder.build();

        EquipmentBuilder dtagEq2Builder = getBuilder(EquipmentBuilder.class)
                .withRangSSType("2H")
                .withRangBucht("0101")
                .withRangLeiste1("02")
                .withRangStift1("99")
                .withUETV(Uebertragungsverfahren.H13);
        Equipment dtagEq2 = dtagEq2Builder.build();

        AuftragBuilder auftragBuilder2 = getBuilder(AuftragBuilder.class);
        AuftragTechnikBuilder auftragTechnikBuilder2 = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragBuilder(auftragBuilder2);
        auftragTechnik2 = auftragTechnikBuilder2
                .build();

        EndstelleBuilder endstelleBuilder2 = getBuilder(EndstelleBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder2)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .withRangierungBuilder(getBuilder(RangierungBuilder.class)
                        .withEqOutBuilder(dtagEq2Builder));
        endstelleBuilder2.build();

        subOrders = new HashSet<CBVorgangSubOrder>();
        subOrders.add(new CBVorgangSubOrder(auftragTechnik2.getAuftragId(), dtagEq2.getDtagVerteilerLeisteStift(), null, null));

        cbVorgang = getBuilder(CBVorgangBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withCbBuilder(cbBuilder)
                .withSubOrders(subOrders)
                .withSubmittedAt(new Date())
                .withAutomation(Boolean.FALSE)
                .build();
    }


    public void testCreateCuDaWithKlammer() throws Exception {
        GetDataPhysikCommand command = (GetDataPhysikCommand) getBean(GetDataPhysikCommand.class.getName());
        command.prepare(AbstractTALDataCommand.KEY_CBVORGANG_ID, cbVorgang.getId());
        command.prepare(AbstractTALCommand.KEY_CARRIERBESTELLUNG_ID, carrierbestellung.getId());
        command.prepare(AbstractTALCommand.KEY_AUFTRAG_ID, auftragTechnik1.getAuftragId());
        command.prepare(AbstractTALCommand.KEY_SUB_ORDERS, subOrders);

        Object result = command.execute();
        assertNotNull(result);
        assertTrue(result instanceof List<?>);

        @SuppressWarnings("unchecked")
        List<TALSegment> talSegments = (List) result;
        assertNotEmpty(talSegments, "Keine TAL-Segmente generiert");
        assertEquals(talSegments.size(), 2, "Anzahl generierter TAL-Segmente ist ungueltig!");

        for (TALSegment talSeg : talSegments) {
            String dtagPort = talSeg.getValueAsString(5);
            if ("01010188".equals(dtagPort)) {
                assertEquals(talSeg.getValueAsString(1), "N");
            }
            else if ("01010299".equals(dtagPort)) {
                assertEquals(talSeg.getValueAsString(1), "J");
                assertEquals(talSeg.getValueAsString(4), "H13");
            }
            else {
                fail("TAL-Segment enthaelt nicht erwartete Daten!");
            }
        }
    }

}


