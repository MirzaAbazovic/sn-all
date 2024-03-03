/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2011 09:36:34
 */
package de.mnet.wita.validators.geschaeftsfall;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.validators.geschaeftsfall.UnterpositionSet.UnterpositionSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = UnterpositionSetValidator.class)
@Documented
public @interface UnterpositionSet {
    String message() default "Es muss mindestens eine Unterposition zur Auftragposition vorhanden sein.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class UnterpositionSetValidator implements ConstraintValidator<UnterpositionSet, Geschaeftsfall> {

        @Override
        public boolean isValid(Geschaeftsfall geschaeftsfall, ConstraintValidatorContext context) {
            return geschaeftsfall.getAuftragsPosition().getPosition() != null;
        }

        @Override
        public void initialize(UnterpositionSet constraintAnnotation) {
            // nothing to do
        }
    }

}
