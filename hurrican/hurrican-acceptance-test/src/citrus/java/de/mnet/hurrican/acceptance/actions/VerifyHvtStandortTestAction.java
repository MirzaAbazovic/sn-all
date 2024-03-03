/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.11.2014
 */
package de.mnet.hurrican.acceptance.actions;

import static org.testng.Assert.*;

import java.util.*;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import org.testng.Assert;

import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 *
 */
public class VerifyHvtStandortTestAction extends AbstractTestAction {

    private HVTService hvtService;
    private Long standortTypRefId;

    public VerifyHvtStandortTestAction(HVTService hvtService, Long standortTypRefId) {
        setName("verifyHvtStandort");
        this.hvtService = hvtService;
        this.standortTypRefId = standortTypRefId;
    }

    @Override
    public void doExecute(TestContext context) {
        String ortsteil = context.getVariable(VariableNames.ORTSTEIL);
        try {
            HVTGruppe hvtGruppe = hvtService.findHVTGruppeByBezeichnung(ortsteil);
            List<HVTStandort> hvtStandorte = hvtService.findHVTStandorte4Gruppe(hvtGruppe.getId(), true);
            assertNotNull(hvtStandorte);
            assertEquals(hvtStandorte.size(), 1);
            HVTStandort hvtStandort = hvtStandorte.get(0);
            assertEquals(hvtStandort.getStandortTypRefId(), standortTypRefId);
        }
        catch (FindException e) {
            e.printStackTrace();
            Assert.fail(String.format("Ein unerwarteter Fehler ist bei der Suche nach einer HvtGruppe bzw. nach " +
                    "einem HvtStandort aufgetretten. der Ortsteil der HvtGruppe lautet '%s'.", ortsteil));
        }
    }

}
