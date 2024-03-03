/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.2015
 */
package de.mnet.wita.citrus.actions;

import static org.testng.Assert.*;

import java.util.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.google.common.collect.Maps;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.model.CbVorgangData;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaTalOrderService;

/**
 * Creates new master and child CbVorgang with Klammerung. Master external order id is saved as test variable {@link de.mnet.wita.citrus.VariableNames#MASTER_EXTERNAL_ORDER_ID}.
 *
 */
public class CreateMasterChildCbVorgangTestAction extends AbstractWitaTestAction {

    private WitaCBVorgang master = null;
    private WitaCBVorgang child = null;

    private final AcceptanceTestWorkflow workflowMaster;
    private final AcceptanceTestWorkflow workflowChild;
    private final CreatedData createdDataMaster;
    private final CreatedData createdDataChild;
    private Long cbVorgangTyp = CBVorgang.TYP_NEU;

    private final WitaTalOrderService talOrderService;

    public CreateMasterChildCbVorgangTestAction(Long cbVorgangTyp, AcceptanceTestWorkflow workflowMaster, CreatedData createdDataMaster,
            AcceptanceTestWorkflow workflowChild, CreatedData createdDataChild, WitaTalOrderService talOrderService) {
        super("createCbVorgaenge");

        this.createdDataMaster = createdDataMaster;
        this.createdDataChild = createdDataChild;
        this.talOrderService = talOrderService;
        this.workflowMaster = workflowMaster;
        this.workflowChild = workflowChild;
        this.cbVorgangTyp = cbVorgangTyp;
    }

    @Override
    public void doExecute(TestContext testContext) {
        try {
            List<WitaCBVorgang> cbVorgaenge = createCbVorgaenge();
            for (WitaCBVorgang cbVorgang : cbVorgaenge) {
                if (cbVorgang.getAuftragId().equals(createdDataMaster.auftrag.getAuftragId())) {
                    workflowMaster.cbVorgangId = cbVorgang.getId();
                    master = cbVorgang;
                }
                else if (cbVorgang.getAuftragId().equals(createdDataChild.auftrag.getAuftragId())) {
                    workflowChild.cbVorgangId = cbVorgang.getId();
                    child = cbVorgang;
                }
            }

            assertNotNull(master, "Konnte keinen CBVorgang fuer den Haupt-Auftrag ermitteln");
            assertNotNull(child, "Konnte keinen CBVorgang fuer den Sub-Auftrag ermitteln");

            testContext.setVariable(VariableNames.MASTER_EXTERNAL_ORDER_ID, String.format("%s", master.getCarrierRefNr()));
        } catch (Exception e) {
            throw new CitrusRuntimeException("Failed to create CbVorgang data", e);
        }
    }

    private List<WitaCBVorgang> createCbVorgaenge() throws StoreException {
        Map<Long, Set<Pair<ArchiveDocumentDto, String>>> archiveDocuments = Maps.newHashMap();
        archiveDocuments.put(createdDataMaster.auftrag.getAuftragId(), createdDataMaster.archiveDocuments);

        Map<Long, Set<Pair<ArchiveDocumentDto, String>>> archiveDocumentsChild = Maps.newHashMap();
        archiveDocuments.put(createdDataChild.auftrag.getAuftragId(), createdDataChild.archiveDocuments);

        CbVorgangData cbvData = new CbVorgangData()
                .addAuftragId(createdDataMaster.auftrag.getAuftragId())
                .withCbId(createdDataMaster.carrierbestellung.getId())
                .withCarrierId(Carrier.ID_DTAG)
                .withVorgabe(DateConverterUtils.asDate(createdDataMaster.vorgabeMnet.toLocalDate()))
                .withCbVorgangTyp(cbVorgangTyp)
                .withUser(createdDataMaster.user)
                .withArchiveDocuments(archiveDocuments)
                .withAutomation(Boolean.FALSE);

        if (createdDataChild != null) {
            cbvData.addAuftragId(createdDataChild.auftrag.getAuftragId());
        }

        List<CBVorgang> result = talOrderService.createCBVorgang(cbvData);
        assertNotNull(result, "CBVorgaenge for WITA not created!");
        assertEquals(result.size(), 2);

        return (List<WitaCBVorgang>) (List<?>) result;
    }
}
