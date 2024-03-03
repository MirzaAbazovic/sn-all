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
import de.augustakom.hurrican.validation.cc.CBVorgangAnswerReturnValid.CBVorgangAnswerReturnValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CBVorgangAnswerReturnValidator.class)
@Documented
public @interface CBVorgangAnswerReturnValid {

    String message() default "Die Art der Rueckmeldung (positiv/negativ) der TAL-Bestellung muss angegeben werden!";

    boolean present() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class CBVorgangAnswerReturnValidator implements
            ConstraintValidator<CBVorgangAnswerReturnValid, CBVorgang> {

        @Override
        public boolean isValid(CBVorgang cbVorgang, ConstraintValidatorContext context) {
            if (CBVorgangValidationHelper.isAnswerState(cbVorgang) && !cbVorgang.isStorno()
                    && (cbVorgang.getReturnOk() == null)) {
                return false;
            }
            return true;
        }

        @Override
        public void initialize(CBVorgangAnswerReturnValid constraintAnnotation) {
            // not used
        }
    }
}
