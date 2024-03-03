/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.04.2015
 */
package de.mnet.hurrican.atlas.simulator.wita.actions;

import java.util.*;
import java.util.stream.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.hurrican.atlas.simulator.wita.WitaLineOrderVariableNames;

/**
 *
 */
public class WaitForMnetWitaRequestSentByExtOrderNoAction<T extends MnetWitaRequest> extends AbstractWitaTestAction {

    private static final Logger LOG = LoggerFactory.getLogger(WaitForMnetWitaRequestSentByExtOrderNoAction.class);

    private final MwfEntityService mwfEntityService;
    private final Class<T> type;

    public WaitForMnetWitaRequestSentByExtOrderNoAction(MwfEntityService mwfEntityService, Class<T> type) {
        super("waitForMnetWitaRequestSentByExtOrderNoAction");
        this.mwfEntityService = mwfEntityService;
        this.type = type;
    }

    @Override
    public void doExecute(TestContext context) {
        String id = context.getVariable(WitaLineOrderVariableNames.EXTERNAL_ORDER_ID);
        LOG.info(String.format("Searching for a sent MnetWitaRequest for %s '%s' and type '%s'",
                WitaLineOrderVariableNames.EXTERNAL_ORDER_ID, id, type.getCanonicalName()));
        List<T> mwfEntities = mwfEntityService.findMwfEntitiesByProperty(type, MnetWitaRequest.EXTERNE_AUFTRAGSNR_FIELD, id);
        List<T> sentMwfEntities =
                mwfEntities.stream()
                        .filter(input -> input.getSentAt() != null)
                        .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(sentMwfEntities)) {
            throw new CitrusRuntimeException(
                    String.format("Failed to found sent MnetWitaRequest entry for %s '%s' and meldungsType '%s' expected %s but " +
                            "got %s entries", WitaLineOrderVariableNames.EXTERNAL_ORDER_ID, id, type.getCanonicalName(), 1, sentMwfEntities.size()));
        }
        else {
            LOG.info(String.format("MnetWitaRequest entry for %s '%s' and type '%s' found!",
                    WitaLineOrderVariableNames.EXTERNAL_ORDER_ID, id, type.getCanonicalName()));
        }
    }

}
