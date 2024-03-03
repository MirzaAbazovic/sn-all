/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.08.2011 15:42:01
 */
package de.mnet.wita.validators.meldung;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import javax.validation.*;

import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.validators.BasePresenceValidator;
import de.mnet.wita.validators.meldung.AnschlussSet.AnschlussSetValidator;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = AnschlussSetValidator.class)
@Documented
public @interface AnschlussSet {

    String message() default "";

    boolean mandatory() default true;

    boolean permitted() default true;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    public static class AnschlussSetValidator extends BasePresenceValidator implements
            ConstraintValidator<AnschlussSet, Meldung<?>> {

        @Override
        public boolean isValid(Meldung<?> meldungToValidate, ConstraintValidatorContext context) {
            return checkPresence(context, meldungToValidate.getAnschlussOnkz())
                    && checkPresence(context, meldungToValidate.getAnschlussRufnummer());
        }

        @Override
        public void initialize(AnschlussSet constraintAnnotation) {
            permitted = constraintAnnotation.permitted();
            mandatory = constraintAnnotation.mandatory();
            errorMsgIfRequired = "Anschluss ONKZ und Rufnummer müssen gesetzt sein.";
            errorMsgIfNotAllowed = "Anschluss ONKZ und Rufnummer darf nicht gesetzt sein";
        }
    }
}
