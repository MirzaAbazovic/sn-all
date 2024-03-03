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
import de.augustakom.hurrican.validation.cc.CBVorgangAnswerVorgabeMnetValid.CBVorgangAnswerVorgabeMnetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = CBVorgangAnswerVorgabeMnetValidator.class)
@Documented
public @interface CBVorgangAnswerVorgabeMnetValid {

    String message() default "Es muss ein Vorgabedatum von Mnet angegeben sein!";

    boolean present() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class CBVorgangAnswerVorgabeMnetValidator implements ConstraintValidator<CBVorgangAnswerVorgabeMnetValid, CBVorgang> {

        @Override
        public boolean isValid(CBVorgang cbVorgang, ConstraintValidatorContext context) {
            if (CBVorgangValidationHelper.isAnswerState(cbVorgang) && (cbVorgang.getVorgabeMnet() == null)) {
                return false;
            }
            return true;
        }

        @Override
        public void initialize(CBVorgangAnswerVorgabeMnetValid constraintAnnotation) {
        }

    }

}
