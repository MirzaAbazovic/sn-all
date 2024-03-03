/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2011 13:16:24
 */
package de.mnet.wita.validators;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.mnet.wita.message.common.portierung.RufnummernBlock;
import de.mnet.wita.message.common.portierung.RufnummernPortierungAnlagenanschluss;
import de.mnet.wita.validators.AnlagenanschlussRufnummerValid.AnlagenanschlussRufnummerValidator;


@Retention(RUNTIME)
@Target({ TYPE })
@Constraint(validatedBy = AnlagenanschlussRufnummerValidator.class)
@Documented
public @interface AnlagenanschlussRufnummerValid {

    String message() default "ONKZ + Durchwahl + Block zusammen koennen max 11 Ziffern haben.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class AnlagenanschlussRufnummerValidator implements
            ConstraintValidator<AnlagenanschlussRufnummerValid, RufnummernPortierungAnlagenanschluss> {

        @Override
        public boolean isValid(RufnummernPortierungAnlagenanschluss anlagenanschluss, ConstraintValidatorContext context) {
            if (anlagenanschluss == null) {
                return true;
            }
            int maxBlockLen = anlagenanschluss.getAbfragestelle().length();
            for (RufnummernBlock block : anlagenanschluss.getRufnummernBloecke()) {
                if (maxBlockLen < block.getVon().length()) {
                    maxBlockLen = block.getVon().length();
                }
            }

            return ((anlagenanschluss.getOnkz().length() + anlagenanschluss.getDurchwahl().length()) + maxBlockLen) <= 11;
        }

        @Override
        public void initialize(AnlagenanschlussRufnummerValid constraintAnnotation) {
            // nothing to do
        }
    }

}
