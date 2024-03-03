/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2011 09:36:34
 */
package de.augustakom.hurrican.validation.cc;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.validation.cc.CBVorgangSubmitDateValid.CBVorgangSubmitDateValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CBVorgangSubmitDateValidator.class)
@Documented
public @interface CBVorgangSubmitDateValid {

    String message() default "Der CBVorgang benoetigt im aktuellen Status einen Zeitstempel fuer die Uebermittlung.";

    boolean present() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class CBVorgangSubmitDateValidator implements ConstraintValidator<CBVorgangSubmitDateValid, CBVorgang> {

        @Override
        public boolean isValid(CBVorgang cbVorgang, ConstraintValidatorContext context) {
            if (NumberTools.isGreaterOrEqual(cbVorgang.getStatus(), CBVorgang.STATUS_SUBMITTED)
                    && (cbVorgang.getSubmittedAt() == null)) {
                return false;
            }
            return true;
        }

        @Override
        public void initialize(CBVorgangSubmitDateValid constraintAnnotation) {
        }
    }

}
