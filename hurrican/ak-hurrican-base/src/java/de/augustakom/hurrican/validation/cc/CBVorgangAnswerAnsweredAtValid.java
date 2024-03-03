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
import de.augustakom.hurrican.validation.cc.CBVorgangAnswerAnsweredAtValid.CBVorgangAnswerAnsweredAtValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CBVorgangAnswerAnsweredAtValidator.class)
@Documented
public @interface CBVorgangAnswerAnsweredAtValid {

    String message() default "Es muss ein Antwortdatum angegeben werden!";

    boolean present() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class CBVorgangAnswerAnsweredAtValidator implements
            ConstraintValidator<CBVorgangAnswerAnsweredAtValid, CBVorgang> {

        @Override
        public boolean isValid(CBVorgang cbVorgang, ConstraintValidatorContext context) {
            if (CBVorgangValidationHelper.isAnswerState(cbVorgang) && !cbVorgang.isStorno()
                    && (cbVorgang.getAnsweredAt() == null)) {
                return false;
            }
            return true;
        }

        @Override
        public void initialize(CBVorgangAnswerAnsweredAtValid constraintAnnotation) {
            // not used
        }
    }
}
