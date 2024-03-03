/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.12.14
 */
package de.mnet.hurrican.acceptance.actions;

import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.testng.Assert;

import de.augustakom.hurrican.dao.cc.CCAuftragDAO;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 *
 */
public class AssignPortToAuftragTestAction extends AbstractTestAction {

    private final CCAuftragDAO ccAuftragDAO;
    private final RangierungsService rangierungsService;
    private final EndstellenService endstellenService;
    private final String leiste;
    private final String stift;

    /**
     * Constructor setting the action name field.
     */
    public AssignPortToAuftragTestAction(CCAuftragDAO ccAuftragDAO,
            EndstellenService endstellenService,
            RangierungsService rangierungsService,
            String leiste, String stift) {
        setName("assignPortToAuftrag");
        this.ccAuftragDAO = ccAuftragDAO;
        this.endstellenService = endstellenService;
        this.rangierungsService = rangierungsService;
        this.leiste = leiste;
        this.stift = stift;
    }

    @Override
    public void doExecute(TestContext context) {
        Long hvtIdStandort = Long.parseLong(context.getVariable(VariableNames.HVT_STANDORT_ID));
        Long auftragId = Long.parseLong(context.getVariable(VariableNames.AUFTRAG_ID));

        try {
            Equipment equipment = rangierungsService.findEQByLeisteStift(hvtIdStandort, leiste, stift);
            Assert.assertNotNull(equipment, String.format("Kein Equipment für HvtStandort '%s', Leiste '%s' " +
                    "und Stift '%s' gefunden", hvtIdStandort, leiste, stift));

            Endstelle endstelle = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
            Rangierung rangierung = rangierungsService.findRangierung4Equipment(equipment.getId(), true);
            endstelle.setRangierId(rangierung.getId());
            ccAuftragDAO.store(endstelle);
        }
        catch (FindException e) {
            throw new CitrusRuntimeException(String.format("Ein unerwarteter Fehler ist bei der Suche nach einem Equipment " +
                    "aufgetretten. Folgende Suchparameter wurden benutzt hvtIdStabdort '%s', leiste '%s' " +
                    "und stift '%s'.", hvtIdStandort, leiste, stift), e);
        }
    }
}
