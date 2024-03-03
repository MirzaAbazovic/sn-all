/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.14
 */
package de.mnet.wbci.citrus.actions;

import java.util.*;
import javax.annotation.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import de.augustakom.common.tools.exceptions.ExceptionLogEntryContext;
import de.augustakom.hurrican.model.exceptions.ExceptionLogEntry;
import de.augustakom.hurrican.service.exceptions.ExceptionLogService;
import de.mnet.wbci.citrus.VariableNames;

/**
 * Citrus Test-Action um zu ueberpruefen, ob ein ExceptionLog-Eintrag geschrieben wurde.
 */
public class AssertExceptionLogEntryCreatedAction extends AbstractWbciTestAction {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AssertExceptionLogEntryCreatedAction.class);

    private static final int EXCEPTIONLOG_MAX_RESULTS = 100;

    private ExceptionLogService exceptionLogService;
    private ExceptionLogEntryContext exceptionLogEntryContext;

    public AssertExceptionLogEntryCreatedAction(ExceptionLogService exceptionLogService,
            ExceptionLogEntryContext exceptionLogEntryContext) {
        super("assertExceptionLogEntryCreatedAction");
        this.exceptionLogService = exceptionLogService;
        this.exceptionLogEntryContext = exceptionLogEntryContext;
    }

    @Override
    public void doExecute(TestContext context) {
        final Long latestExceptionLogEntryId = Long.valueOf(context.getVariable(VariableNames.LATEST_EXCEPTION_LOG_ENTRY_ID));
        if (latestExceptionLogEntryId == null) {
            throw new CitrusRuntimeException(String.format("Variable '%s' is null, the test variable hast to be set in the TestContext", VariableNames.LATEST_EXCEPTION_LOG_ENTRY_ID));
        }

        final List<ExceptionLogEntry> exceptionLogEntries = exceptionLogService.findNewExceptionLogEntries(
                exceptionLogEntryContext, EXCEPTIONLOG_MAX_RESULTS);

        Collection<ExceptionLogEntry> result = Collections2.filter(exceptionLogEntries,
                new Predicate<ExceptionLogEntry>() {
                    @Override
                    public boolean apply(@Nullable ExceptionLogEntry input) {
                        Long id = input.getId();
                        LOGGER.info(String.format("'%s' < '%s'", latestExceptionLogEntryId, id));
                        return latestExceptionLogEntryId < id;
                    }
                });

        if (CollectionUtils.isEmpty(result)) {
            throw new CitrusRuntimeException(String.format("Failed to find and validate exception log entry with context '%s'", exceptionLogEntryContext.identifier));
        }
    }
}
