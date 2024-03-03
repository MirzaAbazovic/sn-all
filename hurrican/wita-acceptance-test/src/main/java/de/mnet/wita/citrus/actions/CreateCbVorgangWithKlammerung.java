/*

 * Copyright (c) 2015 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 17.11.2015

 */

package de.mnet.wita.citrus.actions;


import static org.testng.Assert.*;

import java.util.*;
import com.consol.citrus.context.TestContext;
import org.testng.collections.Maps;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.exmodules.archive.ArchiveDocumentDto;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.model.CbVorgangData;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaTalOrderService;

/**
 * Created by zieglerch on 17.11.2015.
 */
public class CreateCbVorgangWithKlammerung extends AbstractWitaWorkflowTestAction {

    private Long cbVorgangTyp = CBVorgang.TYP_NEU;

    private final WitaTalOrderService talOrderService;
    private CreatedData[] createdDatas;

    public CreateCbVorgangWithKlammerung(Long cbVorgangTyp, WitaTalOrderService talOrderService, CreatedData... createdDatas) {
        super("createCbVorgaenge");
        this.talOrderService = talOrderService;
        this.createdDatas = createdDatas;
        this.cbVorgangTyp = cbVorgangTyp;
    }

    @Override
    public void doExecute(AcceptanceTestWorkflow workflow, TestContext testContext) {
        List<WitaCBVorgang> cbVorgang = createCBVorgang(cbVorgangTyp, createdDatas);
        workflow.cbVorgangId = cbVorgang.get(0).getId();
    }

    public List<WitaCBVorgang> createCBVorgang(Long cbVorgangTyp, CreatedData... createdDatas) {

        CreatedData firstCreatedData = createdDatas[0];
        Carrierbestellung carrierbestellung = firstCreatedData.carrierbestellung;
        AKUser user = firstCreatedData.user;

        List<Long> auftragIds = new ArrayList<>();
        Map<Long, Set<de.augustakom.common.tools.lang.Pair<ArchiveDocumentDto, String>>> archiveDocuments = Maps.newHashMap();
        for (CreatedData createdData : createdDatas) {
            auftragIds.add(createdData.auftrag.getAuftragId());
            archiveDocuments.put(createdData.auftrag.getAuftragId(), createdData.archiveDocuments);
        }
        final Date createdDataVorgameMnet = DateConverterUtils.asDate(firstCreatedData.vorgabeMnet);

        // @formatter:off
        CbVorgangData cbvData = new CbVorgangData()
                .addAuftragIds(auftragIds)
                .withCbId(carrierbestellung.getId())
                .withCarrierId(Carrier.ID_DTAG)
                .withVorgabe(createdDataVorgameMnet)
                .withCbVorgangTyp(cbVorgangTyp)
                .withUser(user)
                .withArchiveDocuments(archiveDocuments)
                .withProjektKenner(firstCreatedData.projektKenner)
                .withVorabstimmungsId(firstCreatedData.vorabstimmungsId);
        // @formatter:on

        List<CBVorgang> result = null;
        try {
            result = talOrderService.createCBVorgang(cbvData);
        }
        catch (StoreException e) {
            e.printStackTrace();
        }
        assertNotNull(result, "CBVorgang for WITA not created!");
        assertEquals(result.get(0).getCbId(), carrierbestellung.getId(), "CB-Id not as expected!");
        return (List<WitaCBVorgang>) (List<?>) result;
    }
}
