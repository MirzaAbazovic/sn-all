/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.2015
 */
package de.mnet.wita.citrus.actions;

import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.google.common.collect.ImmutableList;

import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.model.CbVorgangData;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaTalOrderService;

/**
 * Creates new Bereitstellung and Kuendigung CbVorgang for HvtKvz Umzug. Bereitstellung external order id is saved as test variable {@link de.mnet.wita.citrus.VariableNames#MASTER_EXTERNAL_ORDER_ID} and
 * Bereitstellung CbVorgang Id is saved as test variable {@link de.mnet.wita.citrus.VariableNames#CB_VORGANG_ID}.
 *
 *
 */
public class CreateHvtKvzCbVorgangTestAction extends AbstractWitaTestAction {

    private WitaCBVorgang bereitstellung = null;
    private WitaCBVorgang kuendigung = null;

    private final AcceptanceTestWorkflow workflowNeu;
    private final AcceptanceTestWorkflow workflowKue;
    private final CreatedData createdDataNeu;
    private final CreatedData createdDataKue;

    private final WitaTalOrderService talOrderService;

    /**
     * Constructor using fields.
     * @param workflowNeu
     * @param createdDataNeu
     * @param workflowKue
     * @param createdDataKue
     * @param talOrderService
     */
    public CreateHvtKvzCbVorgangTestAction(AcceptanceTestWorkflow workflowNeu, CreatedData createdDataNeu,
            AcceptanceTestWorkflow workflowKue, CreatedData createdDataKue, WitaTalOrderService talOrderService) {
        super("createHvtKvzCbVorgaenge");

        this.createdDataNeu = createdDataNeu;
        this.createdDataKue = createdDataKue;
        this.talOrderService = talOrderService;
        this.workflowNeu = workflowNeu;
        this.workflowKue = workflowKue;
    }

    @Override
    public void doExecute(TestContext testContext) {
        try {
            List<WitaCBVorgang> cbVorgaenge = createCbVorgaenge();
            for (WitaCBVorgang cbVorgang : cbVorgaenge) {
                if (cbVorgang.getTyp().equals(WitaCBVorgang.TYP_NEU)) {
                    workflowNeu.cbVorgangId = cbVorgang.getId();
                    bereitstellung = cbVorgang;
                }
                else if (cbVorgang.getTyp().equals(WitaCBVorgang.TYP_KUENDIGUNG)) {
                    workflowKue.cbVorgangId = cbVorgang.getId();
                    kuendigung = cbVorgang;
                }
            }

            assertNotNull(bereitstellung, "Konnte keinen CBVorgang fuer die Bereitstellung ermitteln");
            assertNotNull(kuendigung, "Konnte keinen CBVorgang fuer die Kuendigung ermitteln");

            testContext.setVariable(VariableNames.MASTER_EXTERNAL_ORDER_ID, String.format("%s", bereitstellung.getCarrierRefNr()));
            testContext.setVariable(VariableNames.CB_VORGANG_ID, String.format("%s", bereitstellung.getId()));

            if (kuendigung != null) {
                testContext.setVariable(VariableNames.KUENDIGUNG_EXTERNAL_ORDER_ID, String.format("%s", kuendigung.getCarrierRefNr()));
            }
        } catch (Exception e) {
            throw new CitrusRuntimeException("Failed to create CbVorgang data", e);
        }
    }

    private List<WitaCBVorgang> createCbVorgaenge() throws StoreException {
        CbVorgangData cbvData = new CbVorgangData()
                .addAuftragIds(ImmutableList.of(createdDataNeu.auftrag.getAuftragId()))
                .withCbId(createdDataNeu.carrierbestellung.getId())
                .withAuftragId4HvtToKvz(createdDataKue.auftrag.getAuftragId())
                .withCarrierId(Carrier.ID_DTAG)
                .withVorgabe(DateConverterUtils.asDate(createdDataNeu.vorgabeMnet))
                .withCbVorgangTyp(CBVorgang.TYP_HVT_KVZ)
                .withUser(createdDataNeu.user)
                .withAutomation(Boolean.FALSE);

        List<CBVorgang> result = talOrderService.createHvtKvzCBVorgaenge(cbvData);
        assertNotNull(result, "CBVorgaenge for WITA not created!");
        assertEquals(result.size(), 2);

        return (List<WitaCBVorgang>) (List<?>) result;
    }
}
