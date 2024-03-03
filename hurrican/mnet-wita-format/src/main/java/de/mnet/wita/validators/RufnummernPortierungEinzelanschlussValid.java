/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.07.2011 09:36:34
 */
package de.mnet.wita.validators;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.mnet.wita.message.common.portierung.RufnummernPortierungEinzelanschluss;
import de.mnet.wita.validators.RufnummernPortierungEinzelanschlussValid.RufnummernPortierungEinzelanschlussValidator;

@Retention(RUNTIME)
@Target({ TYPE })
@Constraint(validatedBy = RufnummernPortierungEinzelanschlussValidator.class)
public @interface RufnummernPortierungEinzelanschlussValid {

    String message() default "Entweder alle Rufnummern müssen ausgewählt sein, oder die Liste darf nicht leer sein.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class RufnummernPortierungEinzelanschlussValidator implements
            ConstraintValidator<RufnummernPortierungEinzelanschlussValid, RufnummernPortierungEinzelanschluss> {

        @Override
        public boolean isValid(RufnummernPortierungEinzelanschluss portierung, ConstraintValidatorContext context) {
            if (portierung == null) {
                return true;
            }

            return !portierung.getRufnummern().isEmpty();
        }

        @Override
        public void initialize(RufnummernPortierungEinzelanschlussValid constraintAnnotation) {
            // nothing to do
        }
    }
}
