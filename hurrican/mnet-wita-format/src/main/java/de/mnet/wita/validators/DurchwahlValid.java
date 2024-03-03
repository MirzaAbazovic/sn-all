/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2011 13:11:04
 */
package de.mnet.wita.validators;


import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;
import org.apache.commons.lang.StringUtils;

import de.mnet.wita.validators.DurchwahlValid.DurchwahlValidator;


@Retention(RUNTIME)
@Target({ METHOD, FIELD })
@Constraint(validatedBy = DurchwahlValidator.class)
@Documented
public @interface DurchwahlValid {

    String message() default "Durchwahl muss aus 1-8 Ziffern bestehen.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class DurchwahlValidator implements
            ConstraintValidator<DurchwahlValid, String> {

        @Override
        public boolean isValid(String durchwahl, ConstraintValidatorContext context) {
            if (durchwahl == null) {
                return true;
            }

            // @formatter:off
            return StringUtils.isNumeric(durchwahl)
                && (StringUtils.length(durchwahl) >= 1)
                && (StringUtils.length(durchwahl) <= 8);
            // @formatter:on
        }

        @Override
        public void initialize(DurchwahlValid constraintAnnotation) {
            // nothing to do
        }
    }
}

