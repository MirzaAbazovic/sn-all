/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.02.14
 */
package de.mnet.hurrican.atlas.simulator.wita.actions;

import java.time.*;
import java.time.format.*;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;

/**
 *
 */
public abstract class AbstractWitaTestAction extends AbstractTestAction {

    /**
     * Constructor setting the action name field.
     *
     * @param actionName
     */
    public AbstractWitaTestAction(String actionName) {
        super();
        setName(actionName);
    }

    @Override
    public void execute(TestContext context) {
        try {
            super.execute(context);
        }
        catch (Error e) { // NOSONAR squid:S1181 ; errors should cause to fail the test!
            throw new CitrusRuntimeException(e.getMessage(), e);
        }
    }

    protected LocalDateTime getDateTimeFromContext(TestContext context, String variableName) {
        final DateTimeFormatter dateTimeFormatter =
                new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").toFormatter();
        return LocalDateTime.of(LocalDate.parse(context.getVariable(variableName), dateTimeFormatter), LocalTime.MIN);
    }

}
