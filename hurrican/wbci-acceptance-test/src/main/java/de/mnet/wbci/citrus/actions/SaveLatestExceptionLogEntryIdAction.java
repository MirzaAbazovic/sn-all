/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.05.14
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;
import com.consol.citrus.context.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import de.augustakom.common.tools.exceptions.ExceptionLogEntryContext;
import de.augustakom.hurrican.model.exceptions.ExceptionLogEntry;
import de.augustakom.hurrican.service.exceptions.ExceptionLogService;
import de.mnet.wbci.citrus.VariableNames;

/**
 *
 */
public class SaveLatestExceptionLogEntryIdAction extends AbstractWbciTestAction {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SaveLatestExceptionLogEntryIdAction.class);

    private static final int EXCEPTIONLOG_MAX_RESULTS = 1;

    private ExceptionLogService exceptionLogService;
    private ExceptionLogEntryContext exceptionLogEntryContext;

    public SaveLatestExceptionLogEntryIdAction(ExceptionLogService exceptionLogService,
            ExceptionLogEntryContext exceptionLogEntryContext) {
        super("saveLatestExceptionLogEntryIdAction");
        this.exceptionLogService = exceptionLogService;
        this.exceptionLogEntryContext = exceptionLogEntryContext;
    }

    @Override
    public void doExecute(TestContext context) {
        List<ExceptionLogEntry> exceptionLogEntries = exceptionLogService.findNewExceptionLogEntries(
                exceptionLogEntryContext, EXCEPTIONLOG_MAX_RESULTS);

        Long lastExceptionId;
        if (!CollectionUtils.isEmpty(exceptionLogEntries)) {
            lastExceptionId = exceptionLogEntries.get(0).getId();
        }
        else {
            lastExceptionId = 0L;
        }
        context.setVariable(VariableNames.LATEST_EXCEPTION_LOG_ENTRY_ID, lastExceptionId);
        LOGGER.info(String.format("%s='%s'", VariableNames.LATEST_EXCEPTION_LOG_ENTRY_ID, lastExceptionId));
    }
}
