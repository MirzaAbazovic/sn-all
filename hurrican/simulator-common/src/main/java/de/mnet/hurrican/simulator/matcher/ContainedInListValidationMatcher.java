/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.01.2015
 */
package de.mnet.hurrican.simulator.matcher;

import java.util.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.validation.matcher.ValidationMatcher;
import org.apache.commons.lang.StringUtils;

/**
 * Special validation matcher implementation, which checks whether a given value matches one of the expected values
 * within the provided list. The default separator used for parsing the control value string is a comma.
 * <p/>
 * Another separator can be specified by the user on following way: controlValue1|controlValue2|controlValue3(|)
 *
 */
public class ContainedInListValidationMatcher implements ValidationMatcher {

    private static final String DEFAULT_SEPARATOR = ",";

    @Override
    public void validate(String fieldName, String valueIn, String controlIn, TestContext context) throws ValidationException {
        String value = valueIn;
        String control = controlIn;

        if (StringUtils.isEmpty(value) || StringUtils.isEmpty(control)) {
            throw new ValidationException(String.format("%s failed for field '%s'. Both the value to validate and "
                    + "the control value should not be empty or null!", this.getClass().getSimpleName(), fieldName));
        }
        value = value.trim();
        control = control.trim();
        String separatorToUse = DEFAULT_SEPARATOR;
        if (control.contains("(")) {
            int startSeparatorIndex = control.indexOf('(');
            int endSeparatorIndex = control.indexOf(')');
            separatorToUse = control.substring(startSeparatorIndex + 1, endSeparatorIndex);
            control = control.substring(0, startSeparatorIndex).trim();
        }
        String[] controlValues = StringUtils.split(control, separatorToUse);
        if (controlValues == null || controlValues.length == 0) {
            throw new ValidationException(String.format("%s failed for field '%s'. No control values has been "
                    + "specified!", this.getClass().getSimpleName(), fieldName));
        }
        if (!convertToListAndTrim(controlValues).contains(value)) {
            throw new ValidationException(String.format("%s failed for field '%s'. The provided value '%s' is not "
                    + "contained in the list of control values '%s'!", this.getClass().getSimpleName(), fieldName, value, Arrays.toString(controlValues)));
        }
    }

    private List<String> convertToListAndTrim(String[] values) {
        List<String> asList = Arrays.asList(values);
        List<String> trimmedList = new ArrayList<>(asList.size());
        for (String value : asList) {
            trimmedList.add(value.trim());
        }
        return trimmedList;
    }

}
