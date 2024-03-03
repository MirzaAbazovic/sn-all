/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created:
 */
package de.mnet.wita.bpm.tasks;

import static de.mnet.wita.message.MeldungsType.*;

import java.util.*;
import com.google.common.collect.ImmutableList;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.wita.bpm.converter.usertask.AbbmPvUserTaskConverter;
import de.mnet.wita.bpm.converter.usertask.AbmPvUserTaskConverter;
import de.mnet.wita.bpm.variables.WitaActivitiVariableUtils;
import de.mnet.wita.exceptions.MessageOutOfOrderException;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.AbbruchMeldungPv;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;

/**
 * Activiti-Task, der PV WITA Messages entgegennimmt.
 */
public class ProcessPvMessageTask extends AbstractProcessingWitaTask {

    private static final Logger LOGGER = Logger.getLogger(ProcessPvMessageTask.class);

    @Autowired
    private AbmPvUserTaskConverter abmPvUserTaskConverter;
    @Autowired
    private AbbmPvUserTaskConverter abbmPvUserTaskConverter;

    @Override
    protected void processMessage(DelegateExecution execution) throws Exception {
        final MeldungsType meldungsType = WitaActivitiVariableUtils.extractMeldungsType(execution);
        final Long meldungsId = WitaActivitiVariableUtils.extractMeldungsId(execution);

        LOGGER.info(String.format("Received Meldung with meldungsId = %s and MeldungsType = %s",
                meldungsId, meldungsType));

        try {
            if (ABM_PV.equals(meldungsType)) {
                final AuftragsBestaetigungsMeldungPv abmPv =
                        mwfEntityDao.findById(meldungsId, AuftragsBestaetigungsMeldungPv.class);
                abmPvUserTaskConverter.write(abmPv);
            }
            if (ABBM_PV.equals(meldungsType)) {
                final AbbruchMeldungPv abbmPv =
                        mwfEntityDao.findById(meldungsId, AbbruchMeldungPv.class);
                abbmPvUserTaskConverter.write(abbmPv);
            }
        }
        catch (Exception e) {
            workflowTaskService.setWorkflowToError(execution, e.getMessage(), e);
        }

        if (ENTM_PV.equals(meldungsType)) {
            throw new MessageOutOfOrderException("ERLM_PV sollte vor ENTM_PV kommen! Business Key: "
                    + execution.getProcessBusinessKey());
        }

        final List<MeldungsType> expectedMeldungsTypes = ImmutableList.of(ENTM_PV, ERLM_PV, ABBM_PV, ABM_PV, VZM_PV);
        if (!expectedMeldungsTypes.contains(meldungsType)) {
            final String msg = String.format("Wrong MeldungsType: %s; Expected one of: %s", meldungsType, expectedMeldungsTypes);

            workflowTaskService.setWorkflowToError(execution, msg);
            LOGGER.error(msg);
        }
    }
}
