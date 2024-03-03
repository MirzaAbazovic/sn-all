/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2011 14:48:15
 */
package de.mnet.wita.bpm.tasks;

import static de.mnet.wita.bpm.WitaTaskVariables.*;

import javax.annotation.*;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.mnet.wita.bpm.WorkflowTaskService;
import de.mnet.wita.bpm.converter.MwfCbVorgangConverterService;
import de.mnet.wita.bpm.variables.WitaActivitiVariableUtils;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.exceptions.WitaBpmException;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.MwfEntityService;

/**
 * Abstrakte Klasse fuer alle Wita (Java) Tasks.
 */
public abstract class AbstractWitaTask implements JavaDelegate {

    private static final Logger LOGGER = Logger.getLogger(AbstractWitaTask.class);

    @Autowired
    MwfEntityDao mwfEntityDao;

    @Autowired
    MwfEntityService mwfEntityService;

    @Autowired
    WorkflowTaskService workflowTaskService;

    @Autowired
    MwfCbVorgangConverterService mwfCbVorgangConverterService;

    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierElTALService")
    CarrierElTALService carrierElTalService;

    protected WitaCBVorgang getCbVorgang(DelegateExecution execution) {
        try {
            Long cbVorgangId = (Long) execution.getVariable(CB_VORGANG_ID.id);
            WitaCBVorgang cbVorgang = (WitaCBVorgang) carrierElTalService.findCBVorgang(cbVorgangId);
            if (cbVorgang == null) {
                throw new WitaDataAggregationException("CBVorgang must be set");
            }
            return cbVorgang;
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new WitaBpmException("Fehler bei der Ermittlung des CB-Vorgangs: " + e.getMessage(), e);
        }
    }

    protected WitaCBVorgang getCbVorgangOrNull(DelegateExecution execution) {
        try {
            Long cbVorgangId = (Long) execution.getVariable(CB_VORGANG_ID.id);
            if (cbVorgangId == null) {
                return null;
            }
            return (WitaCBVorgang) carrierElTalService.findCBVorgang(cbVorgangId);
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new WitaBpmException("Fehler bei der Ermittlung des CB-Vorgangs: " + e.getMessage(), e);
        }
    }

    protected void verifyMeldungsType(DelegateExecution execution, MeldungsType expected) {
        MeldungsType meldungsTyp = WitaActivitiVariableUtils.extractMeldungsType(execution);
        if (!expected.equals(meldungsTyp)) {
            workflowTaskService.setWorkflowToError(execution, "Wrong MeldungsTyp: " + meldungsTyp + "; Expected MeldungsType: " + expected);
        }
    }

}
