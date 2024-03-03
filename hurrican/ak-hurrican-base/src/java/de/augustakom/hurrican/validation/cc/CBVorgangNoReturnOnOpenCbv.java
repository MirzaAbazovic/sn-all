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

import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.validation.cc.CBVorgangNoReturnOnOpenCbv.CBVorgangNoReturnOnOpenCbvValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CBVorgangNoReturnOnOpenCbvValidator.class)
@Documented
public @interface CBVorgangNoReturnOnOpenCbv {

    String message() default "Bei dem aktuellen Status darf keine Rueckmeldeart (positiv/negativ) gesetzt sein!";

    boolean present() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class CBVorgangNoReturnOnOpenCbvValidator implements
            ConstraintValidator<CBVorgangNoReturnOnOpenCbv, CBVorgang> {

        @Override
        public boolean isValid(CBVorgang cbVorgang, ConstraintValidatorContext context) {
            // Beim Status Answered oder Closed muss auch ReturnOk gefuellt sein
            if (!CBVorgang.STATUS_STORNO.equals(cbVorgang.getStatus())
                    && !CBVorgangValidationHelper.isAnswerState(cbVorgang) && (cbVorgang.getReturnOk() != null)) {
                return false;
            }
            return true;
        }

        @Override
        public void initialize(CBVorgangNoReturnOnOpenCbv constraintAnnotation) {
        }

    }

}
