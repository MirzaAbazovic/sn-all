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

import de.mnet.wita.message.common.portierung.EinzelanschlussRufnummer;
import de.mnet.wita.validators.EinzelanschlussRufnummerValid.EinzelanschlussRufnummerValidator;


@Retention(RUNTIME)
@Target({ TYPE })
@Constraint(validatedBy = EinzelanschlussRufnummerValidator.class)
@Documented
public @interface EinzelanschlussRufnummerValid {

    String message() default "ONKZ + Rufnummer koennen max 11 Ziffern haben.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class EinzelanschlussRufnummerValidator implements
            ConstraintValidator<EinzelanschlussRufnummerValid, EinzelanschlussRufnummer> {

        @Override
        public boolean isValid(EinzelanschlussRufnummer einzelanschlussRufnummer, ConstraintValidatorContext context) {
            if (einzelanschlussRufnummer == null) {
                return true;
            }
            return (einzelanschlussRufnummer.getOnkz().length() + einzelanschlussRufnummer.getRufnummer().length()) <= 11;
        }

        @Override
        public void initialize(EinzelanschlussRufnummerValid constraintAnnotation) {
            // nothing to do
        }
    }

}
