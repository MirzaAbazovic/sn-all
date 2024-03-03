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

import de.mnet.wita.validators.VertragsnummerSet.VertragsnummerSetValidator;
import de.mnet.wita.validators.getters.HasVertragsNummer;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = VertragsnummerSetValidator.class)
@Documented
public @interface VertragsnummerSet {
    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class VertragsnummerSetValidator extends BasePresenceValidator implements
            ConstraintValidator<VertragsnummerSet, HasVertragsNummer> {

        @Override
        public boolean isValid(HasVertragsNummer toValidate, ConstraintValidatorContext context) {
            String vertragsNummer = toValidate.getVertragsNummer();
            return checkPresence(context, vertragsNummer);
        }

        @Override
        public void initialize(VertragsnummerSet constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            errorMsgIfRequired = "Die Vertragsnummer muss gesetzt sein.";
            errorMsgIfNotAllowed = "Die Vertragsnummer darf nicht gesetzt sein";
        }
    }

}
