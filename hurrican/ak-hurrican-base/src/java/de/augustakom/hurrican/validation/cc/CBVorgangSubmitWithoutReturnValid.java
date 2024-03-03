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
import de.augustakom.hurrican.validation.cc.CBVorgangSubmitWithoutReturnValid.CBVorgangSubmitWithoutReturnValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CBVorgangSubmitWithoutReturnValidator.class)
@Documented
public @interface CBVorgangSubmitWithoutReturnValid {

    String message() default "Bei dem aktuellen Status darf keine Rueckmeldeart (positiv/negativ) gesetzt sein!";

    boolean present() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class CBVorgangSubmitWithoutReturnValidator implements ConstraintValidator<CBVorgangSubmitWithoutReturnValid, CBVorgang> {

        @Override
        public boolean isValid(CBVorgang cbVorgang, ConstraintValidatorContext context) {
            if (NumberTools.isLess(cbVorgang.getStatus(), CBVorgang.STATUS_ANSWERED)
                    && (cbVorgang.getReturnOk() != null)) {
                return false;
            }
            return true;
        }

        @Override
        public void initialize(CBVorgangSubmitWithoutReturnValid constraintAnnotation) {
        }
    }

}
