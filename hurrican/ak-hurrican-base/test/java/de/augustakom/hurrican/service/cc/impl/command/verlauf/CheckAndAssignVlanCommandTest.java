/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.05.2012 16:45:51
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

/**
 *
 */
@Test(groups = BaseTest.SERVICE)
public class CheckAndAssignVlanCommandTest extends AbstractHurricanBaseServiceTest {
    @Autowired
    AssignVlanCommand sut;

    private Auftrag auftrag;

    // private Product

    @BeforeMethod
    public void beforeMethod() {
        auftrag = getBuilder(AuftragBuilder.class).withAuftragTechnikBuilder(
                getBuilder(AuftragTechnikBuilder.class).withEndstelleBuilder(
                        getBuilder(EndstelleBuilder.class).withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B))
        ).build();
        sut.prepare(AbstractVerlaufCheckCommand.KEY_AUFTRAG_ID, auftrag.getAuftragId());
        sut.prepare(AbstractVerlaufCheckCommand.KEY_PRODUKT, auftrag.getAuftragId());
    }

    public void execute() throws Exception {
        sut.execute();
    }
}


