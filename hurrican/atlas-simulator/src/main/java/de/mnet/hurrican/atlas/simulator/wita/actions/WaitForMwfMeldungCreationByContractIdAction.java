/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.04.2015
 */
package de.mnet.hurrican.atlas.simulator.wita.actions;

import java.util.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderVariableNames;

/**
 *
 */
public class WaitForMwfMeldungCreationByContractIdAction<T extends Meldung> extends AbstractWitaTestAction {

    private static final Logger LOG = LoggerFactory.getLogger(WaitForMnetWitaRequestSentByExtOrderNoAction.class);

    private final MwfEntityService mwfEntityService;
    private final Class<T> type;

    public WaitForMwfMeldungCreationByContractIdAction(MwfEntityService mwfEntityService, Class<T> type) {
        super("waitForMwfMeldungCreationByContractIdAction");
        this.mwfEntityService = mwfEntityService;
        this.type = type;
    }

    @Override
    public void doExecute(TestContext context) {
        String id = context.getVariable(WitaLineOrderVariableNames.CONTRACT_ID);
        LOG.info(String.format("Searching for a Meldung for %s '%s' and type '%s'",
                WitaLineOrderVariableNames.EXTERNAL_ORDER_ID, id, type.getCanonicalName()));
        List<T> meldungen = mwfEntityService.findMwfEntitiesByProperty(type, Meldung.VERTRAGS_NUMMER_FIELD, id);

        if (CollectionUtils.isEmpty(meldungen)) {
            throw new CitrusRuntimeException(
                    String.format("Failed to found Meldung entry for %s '%s' and meldungsType '%s' expected %s but " +
                            "got %s entries", WitaLineOrderVariableNames.CONTRACT_ID, id, type.getCanonicalName(), 1, meldungen.size()));
        }
        else {
            LOG.info(String.format("Meldung entry for %s '%s' and type '%s' found!",
                    WitaLineOrderVariableNames.CONTRACT_ID, id, type.getCanonicalName()));
        }
    }

}
