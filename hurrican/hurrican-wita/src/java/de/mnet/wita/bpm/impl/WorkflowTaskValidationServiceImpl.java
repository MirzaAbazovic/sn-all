/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2011 10:12:13
 */
package de.mnet.wita.bpm.impl;

import java.util.*;
import javax.validation.*;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.bpm.WorkflowTaskService;
import de.mnet.wita.bpm.WorkflowTaskValidationService;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.validators.ValidationHelper;
import de.mnet.wita.validators.reflection.WitaCharacterValidator;

/**
 * Abstraction-Layer fuer die Validierung von Workflow Tasks.
 */
@CcTxRequired
public class WorkflowTaskValidationServiceImpl implements WorkflowTaskValidationService {

    private static final Logger LOGGER = Logger.getLogger(WorkflowTaskService.class);
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private static WitaCharacterValidator characterValidator = new WitaCharacterValidator();


    @Override
    public <T extends MnetWitaRequest> void validateMwfOutput(T request) throws ValidationException {
        final Set<ConstraintViolation<T>> violations = validator.validate(request,
                ValidationHelper.getDefaultValidationGroups(request.getCdmVersion()));
        if (!violations.isEmpty()) {
            // if there are violations, throw a exception with all of the violation messages
            throw new ValidationException(violationsToString(violationsToStringSet(violations)));
        }
    }

    @Override
    public String validateMwfInput(Meldung<?> meldung) {
        LOGGER.info("validate MWF Input: " + meldung);

        Set<String> violations = violationsToStringSet(validator.validate(meldung,
                ValidationHelper.getAllValidationGroups(meldung.getCdmVersion())));

        // Validate characters in addition as they should not prevent saving the message
        violations = Sets.union(violations, characterValidator.validate(meldung));

        if (!violations.isEmpty()) {
            String errorMsg = violationsToString(violations);
            LOGGER.info("set workflow to error: " + errorMsg + " due to validation violations");
            return errorMsg;
        }
        return null;
    }

    private String violationsToString(Set<String> violations) {
        return StringUtils.join(violations, SystemUtils.LINE_SEPARATOR);
    }

    private <T extends WitaMessage> Set<String> violationsToStringSet(Set<ConstraintViolation<T>> violations) {
        Set<String> res = new HashSet<>();
        for (ConstraintViolation<T> violation : violations) {
            res.add(violation.getPropertyPath().toString() + " " + violation.getMessage());
        }
        return res;
    }
}
